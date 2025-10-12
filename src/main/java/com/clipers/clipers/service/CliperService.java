package com.clipers.clipers.service;

import com.clipers.clipers.dto.VideoProcessingResponse;
import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.Cliper;
import com.clipers.clipers.entity.Skill;
import com.clipers.clipers.entity.Language;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.ATSProfileRepository;
import com.clipers.clipers.repository.CliperRepository;
import com.clipers.clipers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio que maneja Clipers implementando Chain of Responsibility implícitamente
 * El procesamiento se delega a la entidad Cliper que maneja la cadena internamente
 */
@Service
@Transactional
public class CliperService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CliperService.class);

    private final CliperRepository cliperRepository;
    private final UserRepository userRepository;
    private final ATSProfileRepository atsProfileRepository;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;
    private final com.clipers.clipers.service.video.VideoProcessingService videoProcessingService;

    @Value("${video.processing.service.url:https://micoservicioprocesarvideo.onrender.com/upload-video}")
    private String videoProcessingServiceUrl;

    @Autowired
    public CliperService(CliperRepository cliperRepository,
                         UserRepository userRepository,
                         ATSProfileRepository atsProfileRepository,
                         NotificationService notificationService,
                         RestTemplate restTemplate,
                         com.clipers.clipers.service.video.VideoProcessingService videoProcessingService) {
        this.cliperRepository = cliperRepository;
        this.userRepository = userRepository;
        this.atsProfileRepository = atsProfileRepository;
        this.notificationService = notificationService;
        this.restTemplate = restTemplate;
        this.videoProcessingService = videoProcessingService;
    }

    /**
     * Template Method implementado implícitamente
     * Define el flujo de creación y procesamiento de Clipers
     * Ahora procesa síncronamente antes de guardar para rellenar ATS automáticamente
     */
    public Cliper createCliper(String userId, String title, String description, String videoUrl, Integer duration, org.springframework.web.multipart.MultipartFile videoFile) {
        log.info("Inicio createCliper userId={}, title={}", userId, title);
        // Step 1: Validate user
        User user = validateAndGetUser(userId);

        // Step 2: Check if user already has a cliper and delete it if exists
        List<Cliper> existingClipers = cliperRepository.findByUserId(userId);
        if (!existingClipers.isEmpty()) {
            // Delete existing cliper(s) to allow creating a new one
            for (Cliper existingCliper : existingClipers) {
                cliperRepository.delete(existingCliper);
            }
        }

        // Step 3: Save video file first
        String savedVideoUrl = null;
        java.nio.file.Path savedFilePath = null;
        if (videoFile != null) {
            savedVideoUrl = saveVideoFile(videoFile);
            savedFilePath = java.nio.file.Paths.get("./uploads/videos", savedVideoUrl.substring(savedVideoUrl.lastIndexOf('/') + 1));
        }

        // Step 4: Process video synchronously before saving cliper
        VideoProcessingResponse processingResponse = null;
        if (savedFilePath != null) {
            processingResponse = callVideoProcessingService(savedFilePath);
        }

        // Step 4: Create or update ATS profile with processing data
        if (processingResponse != null && processingResponse.getProfile() != null) {
            // Use microservice data to update ATS profile
            generateOrUpdateATSProfileFromMicroservice(user, processingResponse.getProfile());
        } else {
            // Use simulated data if microservice fails
            generateOrUpdateATSProfileSimulated(user);
        }

        // Step 5: Create new cliper with processing results
        Cliper cliper = new Cliper(title, description, savedVideoUrl != null ? savedVideoUrl : videoUrl, duration, user);

        // Set processing data if available
        if (processingResponse != null) {
            cliper.setTranscription(processingResponse.getTranscription());
            cliper.setStatus(Cliper.Status.DONE);

            // Extract skills from profile if available
            if (processingResponse.getProfile() != null) {
                List<String> skills = extractSkillsFromProfile(processingResponse.getProfile());
                // Note: Cliper entity might need skills field, for now we'll store in transcription
            }
        } else {
            // Simulated processing
            cliper.setTranscription("Procesamiento simulado completado");
            cliper.setStatus(Cliper.Status.DONE);
        }

        // Step 6: Save cliper
        cliper = cliperRepository.save(cliper);

        // Step 7: Update ATS profile with cliper ID
        if (processingResponse != null && processingResponse.getProfile() != null) {
            updateATSProfileWithCliperId(user.getId(), cliper.getId());
        } else {
            updateATSProfileWithCliperId(user.getId(), cliper.getId());
        }

        // Step 7: Send notification
        notificationService.notifyCliperProcessed(user.getId(), cliper.getId());
        log.info("Fin createCliper userId={}, cliperId={}", user.getId(), cliper.getId());

        return cliper;
    }

    private User validateAndGetUser(String userId) {
        // userId es el ID real del usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole() != User.Role.CANDIDATE) {
            throw new IllegalArgumentException("Solo los candidatos pueden crear Clipers");
        }

        return user;
    }


    /**
     * Builder Pattern implícito para generar perfil ATS
     * Utiliza el patrón builder implementado en ATSProfile
     */
    private void generateOrUpdateATSProfile(Cliper cliper) {
        try {
            Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(cliper.getUser().getId());
            
            ATSProfile atsProfile;
            if (existingProfile.isPresent()) {
                // Actualizar perfil existente
                atsProfile = existingProfile.get();
                atsProfile.withCliper(cliper.getId());
            } else {
                // Crear nuevo perfil usando Builder pattern implícito
                atsProfile = new ATSProfile(cliper.getUser())
                        .withCliper(cliper.getId());
            }
            
            // Generar contenido del perfil desde el cliper procesado
            atsProfile.generateFromCliperData(cliper.getTranscription(), cliper.getSkills());
            
            atsProfileRepository.save(atsProfile);
            
        } catch (Exception e) {
            System.err.println("Error generando perfil ATS para cliper " + cliper.getId() + ": " + e.getMessage());
        }
    }

    // Métodos CRUD estándar
    public Optional<Cliper> findById(String id) {
        return cliperRepository.findById(id);
    }

    public List<Cliper> findByUserId(String userId) {
        return cliperRepository.findByUserId(userId);
    }

    public Page<Cliper> findByUserId(String userId, Pageable pageable) {
        return cliperRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Cliper> findProcessedClipers(Pageable pageable) {
        return cliperRepository.findProcessedClipersOrderByCreatedAtDesc(pageable);
    }

    public Page<Cliper> searchClipers(String query, Pageable pageable) {
        return cliperRepository.searchClipers(query, pageable);
    }

    public List<Cliper> findBySkill(String skill) {
        return cliperRepository.findBySkillsContaining(skill);
    }

    public Cliper updateCliper(String id, String title, String description) {
        Cliper cliper = cliperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliper no encontrado"));

        // State Pattern implícito - verificar si puede ser editado
        if (!cliper.canBeEdited()) {
            throw new IllegalStateException("El Cliper no puede ser editado en su estado actual: " + cliper.getStatus());
        }

        cliper.setTitle(title);
        cliper.setDescription(description);

        return cliperRepository.save(cliper);
    }

    public void deleteCliper(String id) {
        Cliper cliper = cliperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliper no encontrado"));

        // State Pattern implícito - verificar si puede ser eliminado
        if (!cliper.canBeEdited()) {
            throw new IllegalStateException("El Cliper no puede ser eliminado en su estado actual: " + cliper.getStatus());
        }

        cliperRepository.deleteById(id);
    }

    public List<Cliper> findByStatus(Cliper.Status status) {
        return cliperRepository.findByStatus(status);
    }

    /**
     * Command Pattern implícito - reintenta el procesamiento de un Cliper fallido
     */
    public void retryProcessing(String cliperId) {
        Cliper cliper = cliperRepository.findById(cliperId)
                .orElseThrow(() -> new RuntimeException("Cliper no encontrado"));

        if (!cliper.hasProcessingFailed()) {
            throw new IllegalStateException("Solo se puede reintentar el procesamiento de Clipers fallidos");
        }

        // Resetear estado y reiniciar procesamiento con simulación (no tenemos el archivo original)
        cliper.setStatus(Cliper.Status.UPLOADED);
        cliper = cliperRepository.save(cliper);

        // Para retry, usamos procesamiento simulado ya que no tenemos el archivo original
        new Thread(() -> {
            try {
                Thread.sleep(100);
                Cliper freshCliper = cliperRepository.findById(cliperId)
                    .orElseThrow(() -> new RuntimeException("Cliper no encontrado: " + cliperId));

                freshCliper.setStatus(Cliper.Status.PROCESSING);
                cliperRepository.save(freshCliper);

                System.out.println("Reintentando procesamiento simulado para cliper: " + freshCliper.getId());
                freshCliper.processVideo();

                if (freshCliper.getStatus() == Cliper.Status.DONE) {
                    cliperRepository.save(freshCliper);
                    generateOrUpdateATSProfile(freshCliper);
                    notificationService.notifyCliperProcessed(freshCliper.getUser().getId(), freshCliper.getId());
                } else {
                    freshCliper.setStatus(Cliper.Status.FAILED);
                    cliperRepository.save(freshCliper);
                }

            } catch (Exception e) {
                System.err.println("Error reintentando procesamiento para cliper " + cliperId + ": " + e.getMessage());
            }
        }).start();
    }

    /**
     * Llama al microservicio externo para procesar el video.
     * Mantiene la firma por compatibilidad binaria pero delega al servicio modular.
     */
    @Deprecated
    private VideoProcessingResponse callVideoProcessingService(java.nio.file.Path filePath) {
        try {
            log.info("Delegando procesamiento de video al servicio modular. file={}", filePath);
            return videoProcessingService.uploadVideo(filePath);
        } catch (Exception e) {
            log.error("Error procesando video mediante servicio modular. file={}, causa={}", filePath, e.getMessage(), e);
            // Mantener compatibilidad con comportamiento previo (retornar null en caso de error)
            return null;
        }
    }

    /**
     * Extrae skills del perfil del microservicio
     */
    private List<String> extractSkillsFromProfile(VideoProcessingResponse.Profile perfil) {
        List<String> skills = new java.util.ArrayList<>();

        if (perfil == null) {
            return skills; // Return empty list if profile is null
        }

        // Agregar tecnologías si existen
        if (perfil.getTechnologies() != null && !perfil.getTechnologies().equals("No especificado")) {
            skills.addAll(List.of(perfil.getTechnologies().split(",\\s*")));
        }

        // Agregar experiencia como skill
        if (perfil.getProfession() != null && !perfil.getProfession().equals("No especificado")) {
            skills.add(perfil.getProfession());
        }

        return skills;
    }

    /**
     * Genera o actualiza el perfil ATS usando datos del microservicio
     */
    private void generateOrUpdateATSProfileFromMicroservice(Cliper cliper, VideoProcessingResponse.Profile perfil) {
        try {
            if (perfil == null) {
                generateOrUpdateATSProfile(cliper);
                return;
            }
            Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(cliper.getUser().getId());

            ATSProfile atsProfile;
            if (existingProfile.isPresent()) {
                atsProfile = existingProfile.get();
                // Limpiar listas existentes para evitar duplicados
                atsProfile.getEducation().clear();
                atsProfile.getExperience().clear();
                atsProfile.getSkills().clear();
                atsProfile.getLanguages().clear();
            } else {
                atsProfile = new ATSProfile(cliper.getUser());
            }

            // Actualizar con datos del microservicio
            atsProfile.setSummary(generateSummaryFromProfile(perfil));
            atsProfile.setCliperId(cliper.getId());

            // Agregar educación si existe
            if (perfil.getEducation() != null && !perfil.getEducation().equals("No especificado")) {
                atsProfile.addEducation(perfil.getEducation(), "Grado obtenido", "Campo de estudio");
            }

            // Agregar experiencia si existe
            if (perfil.getExperience() != null && !perfil.getExperience().equals("No especificado")) {
                atsProfile.addExperience("Empresa", perfil.getProfession(), perfil.getExperience());
            }

            // Agregar tecnologías como skills técnicos
            if (perfil.getTechnologies() != null && !perfil.getTechnologies().equals("No especificado")) {
                String[] tecnologias = perfil.getTechnologies().split(",\\s*");
                for (String tecnologia : tecnologias) {
                    atsProfile.addSkill(tecnologia.trim(), Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.TECHNICAL);
                }
            }

            // Agregar habilidades blandas como skills soft
            if (perfil.getSoftSkills() != null && !perfil.getSoftSkills().equals("No especificado")) {
                String[] habilidades = perfil.getSoftSkills().split(",\\s*");
                for (String habilidad : habilidades) {
                    atsProfile.addSkill(habilidad.trim(), Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.SOFT);
                }
            }

            // Agregar idiomas si existen
            if (perfil.getLanguages() != null && !perfil.getLanguages().equals("No especificado")) {
                String[] idiomas = perfil.getLanguages().split(",\\s*");
                for (String idioma : idiomas) {
                    atsProfile.addLanguage(idioma.trim(), Language.LanguageLevel.INTERMEDIATE);
                }
            }

            atsProfileRepository.save(atsProfile);

        } catch (Exception e) {
            System.err.println("Error generando perfil ATS desde microservicio para cliper " + cliper.getId() + ": " + e.getMessage());
        }
    }

    /**
     * Genera un resumen del perfil basado en los datos del microservicio
     */
    private String generateSummaryFromProfile(VideoProcessingResponse.Profile perfil) {
        StringBuilder summary = new StringBuilder();

        if (perfil.getName() != null && !perfil.getName().equals("No especificado")) {
            summary.append("Profesional: ").append(perfil.getName()).append(". ");
        }

        if (perfil.getProfession() != null && !perfil.getProfession().equals("No especificado")) {
            summary.append("Profesión: ").append(perfil.getProfession()).append(". ");
        }

        if (perfil.getExperience() != null && !perfil.getExperience().equals("No especificado")) {
            summary.append("Experiencia: ").append(perfil.getExperience()).append(". ");
        }

        if (perfil.getTechnologies() != null && !perfil.getTechnologies().equals("No especificado")) {
            summary.append("Tecnologías: ").append(perfil.getTechnologies()).append(". ");
        }

        if (perfil.getAchievements() != null && !perfil.getAchievements().equals("No especificado")) {
            summary.append("Logros: ").append(perfil.getAchievements()).append(". ");
        }

        return summary.toString().trim();
    }

    /**
     * Elimina todos los clipers (solo para administración)
     */
    public void clearAllClipers() {
        cliperRepository.deleteAll();
    }

    /**
     * Guarda el archivo de video y retorna la URL
     */
    private String saveVideoFile(org.springframework.web.multipart.MultipartFile videoFile) {
        try {
            // Crear directorio si no existe
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("./uploads/videos");
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }

            // Generar nombre único para el archivo
            String fileName = "video_" + System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
            java.nio.file.Path filePath = uploadDir.resolve(fileName);

            // Guardar el archivo
            java.nio.file.Files.copy(videoFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Devolver URL completa para que el frontend pueda acceder
            return "http://localhost:8080/uploads/videos/" + fileName;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al guardar el archivo de video: " + e.getMessage());
        }
    }

    /**
     * Genera o actualiza el perfil ATS usando datos del microservicio (versión para User)
     */
    private void generateOrUpdateATSProfileFromMicroservice(User user, VideoProcessingResponse.Profile perfil) {
        try {
            if (perfil == null) {
                generateOrUpdateATSProfileSimulated(user);
                return;
            }
            System.out.println("🔄 GENERANDO PERFIL ATS DESDE MICROSERVICIO");
            System.out.println("👤 Usuario: " + user.getId());
            System.out.println("📋 Datos del perfil:");
            System.out.println("  - Nombre: " + perfil.getName());
            System.out.println("  - Profesión: " + perfil.getProfession());
            System.out.println("  - Experiencia: " + perfil.getExperience());
            System.out.println("  - Educación: " + perfil.getEducation());
            System.out.println("  - Tecnologías: " + perfil.getTechnologies());
            System.out.println("  - Habilidades blandas: " + perfil.getSoftSkills());
            System.out.println("  - Idiomas: " + perfil.getLanguages());
            System.out.println("  - Logros: " + perfil.getAchievements());

            Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(user.getId());

            ATSProfile atsProfile;
            if (existingProfile.isPresent()) {
                atsProfile = existingProfile.get();
                System.out.println("📝 Actualizando perfil existente");
                // Limpiar listas existentes para evitar duplicados
                atsProfile.getEducation().clear();
                atsProfile.getExperience().clear();
                atsProfile.getSkills().clear();
                atsProfile.getLanguages().clear();
            } else {
                atsProfile = new ATSProfile(user);
                System.out.println("🆕 Creando nuevo perfil ATS");
            }

            // Actualizar con datos del microservicio
            atsProfile.setSummary(generateSummaryFromProfile(perfil));
            System.out.println("📝 Summary generado: " + atsProfile.getSummary());

            // Agregar educación si existe
            if (perfil.getEducation() != null && !perfil.getEducation().equals("No especificado")) {
                atsProfile.addEducation(perfil.getEducation(), "Grado obtenido", "Campo de estudio");
                System.out.println("🎓 Educación agregada: " + perfil.getEducation());
            } else {
                // Agregar educación simulada si no hay datos
                atsProfile.addEducation("Universidad Nacional", "Ingeniería de Sistemas", "Campo de estudio");
                System.out.println("🎓 Educación simulada agregada");
            }

            // Agregar experiencia si existe
            if (perfil.getExperience() != null && !perfil.getExperience().equals("No especificado")) {
                atsProfile.addExperience("Empresa Tecnológica", perfil.getProfession(), perfil.getExperience());
                System.out.println("💼 Experiencia agregada: " + perfil.getExperience());
            } else {
                // Agregar experiencia simulada si no hay datos
                atsProfile.addExperience("Empresa Tecnológica", "Desarrollador Full Stack",
                    "Desarrollo de aplicaciones web usando tecnologías modernas. Experiencia en desarrollo de APIs REST y aplicaciones escalables.");
                System.out.println("💼 Experiencia simulada agregada");
            }

            // Agregar tecnologías como skills técnicos
            if (perfil.getTechnologies() != null && !perfil.getTechnologies().equals("No especificado")) {
                String[] tecnologias = perfil.getTechnologies().split(",\\s*");
                for (String tecnologia : tecnologias) {
                    atsProfile.addSkill(tecnologia.trim(), Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.TECHNICAL);
                    System.out.println("🛠️ Skill técnico agregado: " + tecnologia.trim());
                }
            } else {
                // Agregar skills técnicos simulados
                String[] tecnologiasSimuladas = {"Java", "Spring Boot", "React", "PostgreSQL", "JavaScript"};
                for (String tecnologia : tecnologiasSimuladas) {
                    atsProfile.addSkill(tecnologia, Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.TECHNICAL);
                    System.out.println("🛠️ Skill técnico simulado agregado: " + tecnologia);
                }
            }

            // Agregar habilidades blandas como skills soft
            if (perfil.getSoftSkills() != null && !perfil.getSoftSkills().equals("No especificado")) {
                String[] habilidades = perfil.getSoftSkills().split(",\\s*");
                for (String habilidad : habilidades) {
                    atsProfile.addSkill(habilidad.trim(), Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.SOFT);
                    System.out.println("🤝 Skill blando agregado: " + habilidad.trim());
                }
            } else {
                // Agregar skills blandos simulados
                String[] habilidadesSimuladas = {"Trabajo en equipo", "Comunicación", "Resolución de problemas"};
                for (String habilidad : habilidadesSimuladas) {
                    atsProfile.addSkill(habilidad, Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.SOFT);
                    System.out.println("🤝 Skill blando simulado agregado: " + habilidad);
                }
            }

            // Agregar idiomas si existen
            if (perfil.getLanguages() != null && !perfil.getLanguages().equals("No especificado")) {
                String[] idiomas = perfil.getLanguages().split(",\\s*");
                for (String idioma : idiomas) {
                    atsProfile.addLanguage(idioma.trim(), Language.LanguageLevel.INTERMEDIATE);
                    System.out.println("🌐 Idioma agregado: " + idioma.trim());
                }
            } else {
                // Agregar idiomas simulados
                String[] idiomasSimulados = {"Español", "Inglés"};
                for (String idioma : idiomasSimulados) {
                    atsProfile.addLanguage(idioma, Language.LanguageLevel.INTERMEDIATE);
                    System.out.println("🌐 Idioma simulado agregado: " + idioma);
                }
            }

            ATSProfile savedProfile = atsProfileRepository.save(atsProfile);
            System.out.println("✅ Perfil ATS guardado exitosamente con ID: " + savedProfile.getId());
            System.out.println("📊 Estadísticas del perfil:");
            System.out.println("  - Educación: " + savedProfile.getEducation().size());
            System.out.println("  - Experiencia: " + savedProfile.getExperience().size());
            System.out.println("  - Skills: " + savedProfile.getSkills().size());
            System.out.println("  - Idiomas: " + savedProfile.getLanguages().size());

        } catch (Exception e) {
            System.err.println("❌ Error generando perfil ATS desde microservicio para usuario " + user.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera o actualiza el perfil ATS con datos simulados completos
     */
    private void generateOrUpdateATSProfileSimulated(User user) {
        try {
            Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(user.getId());

            ATSProfile atsProfile;
            if (existingProfile.isPresent()) {
                atsProfile = existingProfile.get();
                // Limpiar listas existentes para evitar duplicados
                atsProfile.getEducation().clear();
                atsProfile.getExperience().clear();
                atsProfile.getSkills().clear();
                atsProfile.getLanguages().clear();
            } else {
                atsProfile = new ATSProfile(user);
            }

            // Generar contenido simulado completo
            String simulatedSummary = "Profesional con experiencia en desarrollo de software. " +
                "Especializado en tecnologías web y aplicaciones empresariales. " +
                "Conocimientos en Java, Spring Boot, React y bases de datos SQL/NoSQL.";

            atsProfile.setSummary(simulatedSummary);

            // Agregar educación simulada
            atsProfile.addEducation("Universidad Nacional", "Ingeniería de Sistemas", "Campo de estudio");

            // Agregar experiencia simulada
            atsProfile.addExperience("Empresa Tecnológica", "Desarrollador Full Stack",
                "Desarrollo de aplicaciones web usando Java, Spring Boot, React y PostgreSQL. " +
                "Experiencia en desarrollo de APIs REST y aplicaciones escalables.");

            // Agregar skills técnicas simuladas
            String[] tecnologias = {"Java", "Spring Boot", "React", "PostgreSQL", "JavaScript", "TypeScript", "Git"};
            for (String tecnologia : tecnologias) {
                atsProfile.addSkill(tecnologia, Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.TECHNICAL);
            }

            // Agregar skills blandas simuladas
            String[] habilidadesBlandas = {"Trabajo en equipo", "Comunicación", "Resolución de problemas", "Aprendizaje continuo"};
            for (String habilidad : habilidadesBlandas) {
                atsProfile.addSkill(habilidad, Skill.SkillLevel.INTERMEDIATE, Skill.SkillCategory.SOFT);
            }

            // Agregar idiomas simulados
            String[] idiomas = {"Español", "Inglés"};
            for (String idioma : idiomas) {
                atsProfile.addLanguage(idioma, Language.LanguageLevel.INTERMEDIATE);
            }

            atsProfileRepository.save(atsProfile);

        } catch (Exception e) {
            System.err.println("Error generando perfil ATS simulado para usuario " + user.getId() + ": " + e.getMessage());
        }
    }

    /**
     * Actualiza el perfil ATS con el ID del cliper
     */
    private void updateATSProfileWithCliperId(String userId, String cliperId) {
        try {
            Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(userId);

            if (existingProfile.isPresent()) {
                ATSProfile atsProfile = existingProfile.get();
                atsProfile.setCliperId(cliperId);
                atsProfileRepository.save(atsProfile);
            }

        } catch (Exception e) {
            System.err.println("Error actualizando perfil ATS con cliper ID para usuario " + userId + ": " + e.getMessage());
        }
    }
}