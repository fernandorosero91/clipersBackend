package com.clipers.clipers.service;

import com.clipers.clipers.dto.matching.*;
import com.clipers.clipers.entity.Job;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.Education;
import com.clipers.clipers.entity.Experience;
import com.clipers.clipers.repository.JobRepository;
import com.clipers.clipers.repository.UserRepository;
import com.clipers.clipers.repository.ATSProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIMatchingService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ATSProfileRepository atsProfileRepository;

    @Value("${microselectia.enabled:true}")
    private boolean microEnabled;

    @Value("${microselectia.url:https://microselectiaserv.onrender.com/}")
    private String microUrl;

    @Value("${microselectia.fallback-to-local:true}")
    private boolean fallbackToLocal;

    public AIMatchingService(RestTemplate restTemplate,
                             UserRepository userRepository,
                             JobRepository jobRepository,
                             ATSProfileRepository atsProfileRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.atsProfileRepository = atsProfileRepository;
    }

    public HealthResponseDTO checkHealth() {
        try {
            return restTemplate.getForObject(microUrl + "/health", HealthResponseDTO.class);
        } catch (Exception e) {
            HealthResponseDTO dto = new HealthResponseDTO();
            dto.setStatus("unavailable");
            dto.setModelLoaded(false);
            return dto;
        }
    }

    public BatchMatchResponseDTO matchBatchCandidates(List<String> candidateIds, String jobId) {
        if (!microEnabled) {
            return buildLocalBatchMatch(candidateIds, jobId);
        }
        try {
            JobDTO jobDTO = fetchJobData(jobId);
            List<CandidateDTO> candidates = candidateIds.stream()
                    .map(this::fetchCandidateData)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            BatchMatchRequestDTO request = new BatchMatchRequestDTO();
            request.setJob(jobDTO);
            request.setCandidates(candidates);

            BatchMatchResponseDTO response = restTemplate.postForObject(
                    microUrl + "/api/match/batch",
                    request,
                    BatchMatchResponseDTO.class
            );
            if (response == null && fallbackToLocal) {
                return buildLocalBatchMatch(candidateIds, jobId);
            }
            return response;
        } catch (RestClientException e) {
            if (fallbackToLocal) {
                return buildLocalBatchMatch(candidateIds, jobId);
            }
            throw e;
        }
    }

    public ExplainMatchResponseDTO explainMatch(String candidateId, String jobId) {
        if (!microEnabled) return null;
        try {
            ExplainMatchRequestDTO req = new ExplainMatchRequestDTO();
            req.setCandidate(fetchCandidateData(candidateId));
            req.setJob(fetchJobData(jobId));
            req.setIncludeSuggestions(true);
            return restTemplate.postForObject(
                    microUrl + "/api/match/explain",
                    req,
                    ExplainMatchResponseDTO.class
            );
        } catch (RestClientException e) {
            return null;
        }
    }

    // ===== Helpers to build DTOs from local data =====
    public CandidateDTO fetchCandidateData(String candidateId) {
        Optional<User> userOpt = userRepository.findById(candidateId);
        if (userOpt.isEmpty()) return null;
        User user = userOpt.get();
        CandidateDTO dto = new CandidateDTO();
        dto.setId(user.getId());
        dto.setName(user.getFirstName() + " " + user.getLastName());

        ATSProfile ats = user.getAtsProfile();
        if (ats == null) {
            ats = atsProfileRepository.findByUserId(user.getId()).orElse(null);
        }
        if (ats != null) {
            dto.setSummary(ats.getSummary());
            dto.setSkills(extractSkillsFromATS(ats));
            dto.setExperienceYears(calculateExperienceYears(ats));
            dto.setEducation(mapEducation(ats));
            dto.setLanguages(ats.getLanguages() != null
                    ? ats.getLanguages().stream().map(l -> l.getName()).collect(Collectors.toList())
                    : Collections.emptyList());
            dto.setLocation(null);
        } else {
            dto.setSkills(Collections.emptyList());
            dto.setExperienceYears(0);
            dto.setEducation(Collections.emptyList());
            dto.setLanguages(Collections.emptyList());
        }
        return dto;
    }

    public JobDTO fetchJobData(String jobId) {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return null;
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkills(extractJobSkills(job));
        dto.setRequirements(extractJobRequirements(job));
        dto.setLocation(job.getLocation());
        dto.setType(job.getType() != null ? job.getType().name() : null);
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setMinExperienceYears(1); // default, ajustar si hay campo específico
        return dto;
    }

    private List<String> extractSkillsFromATS(ATSProfile ats) {
        if (ats.getSkills() == null) return Collections.emptyList();
        return ats.getSkills().stream().map(s -> s.getName().toLowerCase()).collect(Collectors.toList());
    }

    private int calculateExperienceYears(ATSProfile ats) {
        if (ats.getExperience() == null || ats.getExperience().isEmpty()) return 0;
        int years = 0;
        for (Experience exp : ats.getExperience()) {
            LocalDate start = exp.getStartDate();
            LocalDate end = exp.getEndDate() != null ? exp.getEndDate() : LocalDate.now();
            years += Period.between(start, end).getYears();
        }
        return Math.max(years, 0);
    }

    private List<EducationDTO> mapEducation(ATSProfile ats) {
        if (ats.getEducation() == null) return Collections.emptyList();
        List<EducationDTO> list = new ArrayList<>();
        for (Education e : ats.getEducation()) {
            EducationDTO dto = new EducationDTO();
            dto.setInstitution(e.getInstitution());
            dto.setDegree(e.getDegree());
            dto.setField(e.getField());
            dto.setStartYear(e.getStartDate() != null ? e.getStartDate().getYear() : null);
            dto.setEndYear(e.getEndDate() != null ? e.getEndDate().getYear() : null);
            list.add(dto);
        }
        return list;
    }

    private List<String> extractJobSkills(Job job) {
        return job.getSkills() != null ? job.getSkills() : Collections.emptyList();
    }

    private List<String> extractJobRequirements(Job job) {
        return job.getRequirements() != null ? job.getRequirements() : Collections.emptyList();
    }

    // ===== Fallback simple por skills + experiencia =====
    private BatchMatchResponseDTO buildLocalBatchMatch(List<String> candidateIds, String jobId) {
        JobDTO job = fetchJobData(jobId);
        List<RankedMatchResultDTO> results = new ArrayList<>();
        for (String candidateId : candidateIds) {
            CandidateDTO cand = fetchCandidateData(candidateId);
            if (cand == null) continue;
            double score = localScore(cand, job);
            RankedMatchResultDTO r = new RankedMatchResultDTO();
            r.setCandidateId(cand.getId());
            r.setCandidateName(cand.getName());
            r.setCompatibilityScore(score);
            r.setMatchPercentage((int) Math.round(score * 100));
            r.setMatchedSkills(intersection(cand.getSkills(), job.getSkills()));
            r.setExplanation("Local fallback: skills+experience");
            results.add(r);
        }
        results.sort(Comparator.comparing(RankedMatchResultDTO::getCompatibilityScore).reversed());
        int rank = 1;
        for (RankedMatchResultDTO r : results) r.setRank(rank++);

        BatchMatchResponseDTO response = new BatchMatchResponseDTO();
        response.setJobId(jobId);
        response.setJobTitle(job != null ? job.getTitle() : null);
        response.setTotalCandidates(results.size());
        response.setMatches(results);
        response.setAverageScore(results.isEmpty() ? 0.0 : results.stream().mapToDouble(RankedMatchResultDTO::getCompatibilityScore).average().orElse(0.0));
        response.setTopSkillsMatched(job != null ? job.getSkills() : Collections.emptyList());
        return response;
    }

    private double localScore(CandidateDTO c, JobDTO j) {
        if (c == null || j == null) return 0.0;
        List<String> jobSkills = j.getSkills() != null ? j.getSkills() : Collections.emptyList();
        List<String> candSkills = c.getSkills() != null ? c.getSkills() : Collections.emptyList();
        int common = intersection(candSkills, jobSkills).size();
        double skillsScore = jobSkills.isEmpty() ? 0.5 : ((double) common / jobSkills.size());
        double expScore = c.getExperienceYears() != null && c.getExperienceYears() >= (j.getMinExperienceYears() != null ? j.getMinExperienceYears() : 1) ? 0.8 : 0.4;
        return Math.min(1.0, skillsScore * 0.6 + expScore * 0.4);
    }

    private List<String> intersection(List<String> a, List<String> b) {
        Set<String> setA = a.stream().map(String::toLowerCase).collect(Collectors.toSet());
        Set<String> setB = b.stream().map(String::toLowerCase).collect(Collectors.toSet());
        setA.retainAll(setB);
        return new ArrayList<>(setA);
    }
}