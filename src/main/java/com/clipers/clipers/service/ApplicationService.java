package com.clipers.clipers.service;

import com.clipers.clipers.dto.UserDTO;
import com.clipers.clipers.entity.Job;
import com.clipers.clipers.entity.JobApplication;
import com.clipers.clipers.entity.JobMatch;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.JobApplicationRepository;
import com.clipers.clipers.repository.JobMatchRepository;
import com.clipers.clipers.repository.JobRepository;
import com.clipers.clipers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobMatchRepository jobMatchRepository;
    private final AIMatchingService aiMatchingService;

    @Autowired
    public ApplicationService(JobApplicationRepository jobApplicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository,
                              JobMatchRepository jobMatchRepository,
                              AIMatchingService aiMatchingService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.aiMatchingService = aiMatchingService;
    }

    public void applyToJob(String jobId, String candidateUserId) {
        log.info("[ApplicationService] applyToJob jobId={} candidateUserId={}", jobId, candidateUserId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Empleo no encontrado"));
        User candidate = userRepository.findById(candidateUserId)
                .orElseThrow(() -> new RuntimeException("Usuario candidato no encontrado"));

        if (candidate.getRole() != User.Role.CANDIDATE) {
            throw new RuntimeException("Solo candidatos pueden postular");
        }

        Optional<JobApplication> existing = jobApplicationRepository.findByJobIdAndCandidateId(jobId, candidateUserId);
        if (existing.isPresent()) {
            // idempotente: no duplicar
            log.info("[ApplicationService] applyToJob ya existía la postulación, se mantiene idempotencia");
            return;
        }

        JobApplication application = new JobApplication(job, candidate);
        jobApplicationRepository.save(application);
        log.info("[ApplicationService] applyToJob guardada postulación id={} ", application.getId());
    }

    public List<JobApplication> getApplicantsForJob(String jobId) {
        log.info("[ApplicationService] getApplicantsForJob jobId={}", jobId);
        List<JobApplication> apps = jobApplicationRepository.findByJobId(jobId);
        log.info("[ApplicationService] getApplicantsForJob retornando {} aplicaciones", apps != null ? apps.size() : 0);
        return apps;
    }

    public List<JobApplication> getApplicationsForCandidate(String candidateUserId) {
        log.info("[ApplicationService] getApplicationsForCandidate candidateUserId={}", candidateUserId);
        List<JobApplication> apps = jobApplicationRepository.findByCandidateId(candidateUserId);
        log.info("[ApplicationService] getApplicationsForCandidate retornando {} aplicaciones", apps != null ? apps.size() : 0);
        return apps;
    }

    public List<JobMatch> rankApplicantsForJob(String jobId) {
        log.info("[ApplicationService] rankApplicantsForJob jobId={}", jobId);
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);
        List<String> candidateIds = new ArrayList<>();
        for (JobApplication ja : applications) {
            candidateIds.add(ja.getCandidate().getId());
        }

        var response = aiMatchingService.matchBatchCandidates(candidateIds, jobId);
        if (response == null || response.getMatches() == null) {
            log.warn("[ApplicationService] AI matching retornó vacío para jobId={}", jobId);
            return List.of();
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Empleo no encontrado"));

        List<JobMatch> saved = new ArrayList<>();
        response.getMatches().forEach(r -> {
            User user = userRepository.findById(r.getCandidateId()).orElse(null);
            if (user == null) return;
            Optional<JobMatch> existing = jobMatchRepository.findByUserIdAndJobId(user.getId(), jobId);
            JobMatch jm = existing.orElseGet(() -> new JobMatch(job, user, r.getCompatibilityScore(), r.getExplanation()));
            jm.setScore(r.getCompatibilityScore());
            jm.setExplanation(r.getExplanation());
            jm.setMatchedSkills(r.getMatchedSkills());
            saved.add(jobMatchRepository.save(jm));
        });
        // ordenar por score desc
        saved.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        log.info("[ApplicationService] rankApplicantsForJob guardados/actualizados {} JobMatch", saved.size());
        return saved;
    }
}
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationService.class);