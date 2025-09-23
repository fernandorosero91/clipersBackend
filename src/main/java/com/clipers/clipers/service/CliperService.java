package com.clipers.clipers.service;

import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.Cliper;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.ATSProfileRepository;
import com.clipers.clipers.repository.CliperRepository;
import com.clipers.clipers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que maneja Clipers implementando Chain of Responsibility implícitamente
 * El procesamiento se delega a la entidad Cliper que maneja la cadena internamente
 */
@Service
@Transactional
public class CliperService {

    private final CliperRepository cliperRepository;
    private final UserRepository userRepository;
    private final ATSProfileRepository atsProfileRepository;
    private final NotificationService notificationService;

    @Autowired
    public CliperService(CliperRepository cliperRepository,
                        UserRepository userRepository,
                        ATSProfileRepository atsProfileRepository,
                        NotificationService notificationService) {
        this.cliperRepository = cliperRepository;
        this.userRepository = userRepository;
        this.atsProfileRepository = atsProfileRepository;
        this.notificationService = notificationService;
    }

    /**
     * Template Method implementado implícitamente
     * Define el flujo de creación y procesamiento de Clipers
     */
    public Cliper createCliper(String userId, String title, String description, String videoUrl, Integer duration) {
        // Step 1: Validate user
        User user = validateAndGetUser(userId);
        
        // Step 2: Create cliper
        Cliper cliper = new Cliper(title, description, videoUrl, duration, user);
        cliper = cliperRepository.save(cliper);
        
        // Step 3: Start processing asynchronously
        processCliperAsync(cliper);
        
        return cliper;
    }

    private User validateAndGetUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        if (user.getRole() != User.Role.CANDIDATE) {
            throw new IllegalArgumentException("Solo los candidatos pueden crear Clipers");
        }
        
        return user;
    }

    /**
     * Procesa el Cliper de forma asíncrona
     * Utiliza Chain of Responsibility implementado en la entidad Cliper
     */
    private void processCliperAsync(Cliper cliper) {
        new Thread(() -> {
            try {
                // La entidad Cliper maneja internamente la cadena de procesamiento
                cliper.processVideo();
                
                // Guardar cambios
                cliperRepository.save(cliper);
                
                // Si el procesamiento fue exitoso, generar/actualizar perfil ATS
                if (cliper.isProcessingComplete()) {
                    generateOrUpdateATSProfile(cliper);
                    notificationService.notifyCliperProcessed(cliper.getUser().getId(), cliper.getId());
                }
                
            } catch (Exception e) {
                System.err.println("Error procesando cliper " + cliper.getId() + ": " + e.getMessage());
                cliper.setStatus(Cliper.Status.FAILED);
                cliperRepository.save(cliper);
            }
        }).start();
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

        // Resetear estado y reiniciar procesamiento
        cliper.setStatus(Cliper.Status.UPLOADED);
        cliper = cliperRepository.save(cliper);
        
        processCliperAsync(cliper);
    }
}