package com.clipers.clipers.service;

import com.clipers.clipers.dto.matching.*;
import com.clipers.clipers.entity.*;
import com.clipers.clipers.repository.JobMatchRepository;
import com.clipers.clipers.repository.JobRepository;
import com.clipers.clipers.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para integración con MicroSelectIA API (Matching con IA)
 * 
 * IMPORTANTE: Este servicio se conecta ÚNICAMENTE a la API de MATCHING (MicroSelectIA)
 * NO confundir con CliperService que maneja la API de extracción de videos
 * 
 * Responsabilidades:
 * 1. Construir DTOs desde entidades (User/ATSProfile → CandidateDTO, Job → JobForMatchingDTO)
 * 2. Llamar a MicroSelectIA API (/api/match/batch)
 * 3. Procesar resultados y guardar en JobMatch con datos de IA
 */
@Service
@Transactional
public class AIMatchingService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIMatchingService.class);
    
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobMatchRepository jobMatchRepository;
    private final RestTemplate restTemplate;
    
    // URL de MicroSelectIA API (desplegada en Render)
    @Value("${ai.matching.service.url:https://microselectia.onrender.com}")
    private String aiMatchingServiceUrl;
    
    @Value("${ai.matching.timeout:30000}")
    private int aiMatchingTimeout;
    
    @Value("${ai.matching.min-score-threshold:0.3}")
    private double minScoreThreshold;
    
    @Autowired
    public AIMatchingService(JobRepository jobRepository,
                             UserRepository userRepository,
                             JobMatchRepository jobMatchRepository,
                             RestTemplate restTemplate) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.restTemplate = restTemplate;
    }
    
    /**
     * Construye CandidateDTO desde User + ATSProfile
     * Extrae toda la información del perfil ATS para enviar a la IA
     */
    public CandidateDTO buildCandidateDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        CandidateDTO candidate = new CandidateDTO();
        candidate.setId(user.getId());
        candidate.setName(user.getFirstName() + " " + user.getLastName());
        candidate.setEmail(user.getEmail());
        
        // Extraer datos del ATSProfile si existe
        ATSProfile atsProfile = user.getAtsProfile();
        if (atsProfile != null) {
            candidate.setSummary(atsProfile.getSummary());
            candidate.setLocation(atsProfile.getLocation()); // Nuevo campo de ubicación
            
            // Convertir Education
            if (atsProfile.getEducation() != null) {
                List<EducationDTO> educationList = atsProfile.getEducation().stream()
                    .map(edu -> new EducationDTO(
                        edu.getInstitution(),
                        edu.getDegree(),
                        edu.getField(),
                        edu.getStartDate() != null ? edu.getStartDate().toString() : null,
                        edu.getEndDate() != null ? edu.getEndDate().toString() : null
                    ))
                    .collect(Collectors.toList());
                candidate.setEducation(educationList);
            }
            
            // Convertir Experience y calcular años de experiencia
            if (atsProfile.getExperience() != null) {
                List<ExperienceDTO> experienceList = atsProfile.getExperience().stream()
                    .map(exp -> new ExperienceDTO(
                        exp.getCompany(),
                        exp.getPosition(),
                        exp.getDescription(),
                        exp.getStartDate() != null ? exp.getStartDate().toString() : null,
                        exp.getEndDate() != null ? exp.getEndDate().toString() : null
                    ))
                    .collect(Collectors.toList());
                candidate.setExperience(experienceList);
                
                // Calcular años de experiencia total
                Integer totalYears = calculateTotalExperienceYears(atsProfile.getExperience());
                candidate.setExperienceYears(totalYears);
            }
            
            // Extraer Skills
            if (atsProfile.getSkills() != null) {
                List<String> skillNames = atsProfile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
                candidate.setSkills(skillNames);
            }
            
            // Extraer Languages
            if (atsProfile.getLanguages() != null) {
                List<String> languageNames = atsProfile.getLanguages().stream()
                    .map(lang -> lang.getName() + " (" + lang.getLevel() + ")")
                    .collect(Collectors.toList());
                candidate.setLanguages(languageNames);
            }
        }
        
        return candidate;
    }
    
    /**
     * Construye JobForMatchingDTO desde Job entity
     * Extrae toda la información del job para enviar a la IA
     */
    public JobForMatchingDTO buildJobDTO(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        
        JobForMatchingDTO jobDTO = new JobForMatchingDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setRequirements(job.getRequirements());
        jobDTO.setSkills(job.getSkills());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setType(job.getType() != null ? job.getType().name() : null);
        jobDTO.setSalaryMin(job.getSalaryMin());
        jobDTO.setSalaryMax(job.getSalaryMax());
        
        return jobDTO;
    }
    
    /**
     * Método principal: procesa matching de un job con todos los candidatos disponibles
     * usando la API de IA MicroSelectIA
     * 
     * Flujo:
     * 1. Obtener job de la BD
     * 2. Obtener candidatos con ATSProfile
     * 3. Construir DTOs
     * 4. Llamar a API /api/match/batch
     * 5. Procesar respuesta y guardar resultados
     * 
     * @param jobId ID del job a procesar
     * @return BatchMatchResponseDTO con resultados de la IA
     */
    public BatchMatchResponseDTO processJobMatchingWithAI(String jobId) {
        logger.info("🚀 Iniciando matching con IA para job: {}", jobId);
        
        try {
            // 1. Obtener Job
            Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job no encontrado: " + jobId));
            
            // 2. Obtener candidatos con ATSProfile
            List<User> candidates = userRepository.findCandidatesWithATSProfile();
            logger.info("📋 Encontrados {} candidatos con perfil ATS", candidates.size());
            
            if (candidates.isEmpty()) {
                logger.warn("⚠️ No hay candidatos disponibles para matching");
                return createEmptyResponse(jobId);
            }
            
            // 3. Construir DTOs
            JobForMatchingDTO jobDTO = buildJobDTO(job);
            List<CandidateDTO> candidateDTOs = candidates.stream()
                .map(this::buildCandidateDTO)
                .collect(Collectors.toList());
            
            // 4. Llamar a API de IA
            BatchMatchRequestDTO request = new BatchMatchRequestDTO(candidateDTOs, jobDTO);
            BatchMatchResponseDTO response = callMicroSelectIABatchMatch(request);
            
            // 5. Guardar resultados en base de datos
            if (response != null && response.getRankedCandidates() != null) {
                saveMatchResults(jobId, response.getRankedCandidates());
                logger.info("✅ Matching completado: {} candidatos rankeados", response.getTotalCandidates());
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("❌ Error en matching con IA para job {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Error al procesar matching con IA: " + e.getMessage(), e);
        }
    }
    
    /**
     * Llama al endpoint /api/match/batch de MicroSelectIA
     * 
     * POST https://microselectia.onrender.com/api/match/batch
     * Body: { candidates: [...], job: {...} }
     */
    private BatchMatchResponseDTO callMicroSelectIABatchMatch(BatchMatchRequestDTO request) {
        try {
            String endpoint = aiMatchingServiceUrl + "/api/match/batch";
            logger.info("🔗 Llamando a MicroSelectIA API: {}", endpoint);
            logger.info("📤 Enviando {} candidatos para matching", request.getCandidates().size());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<BatchMatchRequestDTO> httpRequest = new HttpEntity<>(request, headers);
            
            ResponseEntity<BatchMatchResponseDTO> response = restTemplate.postForEntity(
                endpoint,
                httpRequest,
                BatchMatchResponseDTO.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Respuesta exitosa de MicroSelectIA API");
                return response.getBody();
            } else {
                logger.error("❌ Error en respuesta de MicroSelectIA: {}", response.getStatusCode());
                return null;
            }
            
        } catch (Exception e) {
            logger.error("❌ Error al llamar a MicroSelectIA API: {}", e.getMessage(), e);
            // No lanzar excepción, permitir fallback a algoritmo local
            return null;
        }
    }
    
    /**
     * Guarda los resultados del matching de IA en la tabla job_matches
     * Actualiza o crea JobMatch con datos enriquecidos de la IA
     */
    public void saveMatchResults(String jobId, List<RankedMatchResultDTO> rankedResults) {
        logger.info("💾 Guardando {} resultados de matching en BD", rankedResults.size());
        
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job no encontrado: " + jobId));
        
        for (RankedMatchResultDTO result : rankedResults) {
            try {
                // Buscar usuario
                User user = userRepository.findById(result.getCandidateId())
                    .orElse(null);
                
                if (user == null) {
                    logger.warn("⚠️ Usuario no encontrado: {}", result.getCandidateId());
                    continue;
                }
                
                // Buscar o crear JobMatch
                JobMatch jobMatch = jobMatchRepository
                    .findByUserIdAndJobId(user.getId(), jobId)
                    .orElse(new JobMatch(job, user, result.getCompatibilityScore(), ""));
                
                // Actualizar con datos de la IA
                jobMatch.setScore(result.getCompatibilityScore());
                
                // Construir explicación detallada
                String aiExplanation = buildExplanation(result);
                jobMatch.setExplanation(aiExplanation);
                
                // Extraer skills matched si existe breakdown
                if (result.getBreakdown() != null && result.getBreakdown().getMatchedSkills() != null) {
                    jobMatch.setMatchedSkills(result.getBreakdown().getMatchedSkills());
                }
                
                // Guardar en BD
                jobMatchRepository.save(jobMatch);
                logger.debug("✅ JobMatch guardado: user={}, rank={}, score={}",
                    user.getEmail(), result.getRank(), result.getCompatibilityScore());
                
            } catch (Exception e) {
                logger.error("❌ Error al guardar match para candidato {}: {}",
                    result.getCandidateId(), e.getMessage());
            }
        }
    }
    
    /**
     * Construye explicación legible desde RankedMatchResultDTO
     */
    private String buildExplanation(RankedMatchResultDTO result) {
        StringBuilder explanation = new StringBuilder();
        explanation.append(String.format("Rank: %d | Score: %.2f%% | Quality: %s\n",
            result.getRank(),
            result.getMatchPercentage(),
            result.getMatchQuality()));
        
        if (result.getBreakdown() != null) {
            MatchBreakdownDTO breakdown = result.getBreakdown();
            explanation.append("\nDesglose:\n");
            if (breakdown.getSkillsScore() != null) {
                explanation.append(String.format("- Skills: %.2f%%\n", breakdown.getSkillsScore() * 100));
            }
            if (breakdown.getExperienceScore() != null) {
                explanation.append(String.format("- Experience: %.2f%%\n", breakdown.getExperienceScore() * 100));
            }
            if (breakdown.getEducationScore() != null) {
                explanation.append(String.format("- Education: %.2f%%\n", breakdown.getEducationScore() * 100));
            }
            if (breakdown.getSemanticScore() != null) {
                explanation.append(String.format("- Semantic: %.2f%%\n", breakdown.getSemanticScore() * 100));
            }
            
            if (breakdown.getExplanation() != null) {
                explanation.append("\n").append(breakdown.getExplanation());
            }
        }
        
        return explanation.toString();
    }
    
    /**
     * Crea respuesta vacía cuando no hay candidatos
     */
    private BatchMatchResponseDTO createEmptyResponse(String jobId) {
        BatchMatchResponseDTO response = new BatchMatchResponseDTO();
        response.setJobId(jobId);
        response.setRankedCandidates(new ArrayList<>());
        response.setTotalCandidates(0);
        return response;
    }
    
    /**
     * Calcula el total de años de experiencia a partir de una lista de experiencias
     * @param experiences Lista de experiencias del candidato
     * @return Total de años de experiencia (redondeado)
     */
    private Integer calculateTotalExperienceYears(List<Experience> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return 0;
        }
        
        long totalMonths = experiences.stream()
            .mapToLong(exp -> {
                LocalDate start = exp.getStartDate();
                LocalDate end = exp.getEndDate() != null ? exp.getEndDate() : LocalDate.now();
                
                if (start == null) return 0;
                
                return ChronoUnit.MONTHS.between(start, end);
            })
            .sum();
        
        // Convertir meses a años (redondeando)
        return Math.toIntExact(totalMonths / 12);
    }
}
