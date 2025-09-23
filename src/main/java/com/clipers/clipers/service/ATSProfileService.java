package com.clipers.clipers.service;

import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.ATSProfileRepository;
import com.clipers.clipers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio para gestión de perfiles ATS
 */
@Service
@Transactional
public class ATSProfileService {

    private final ATSProfileRepository atsProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ATSProfileService(ATSProfileRepository atsProfileRepository, UserRepository userRepository) {
        this.atsProfileRepository = atsProfileRepository;
        this.userRepository = userRepository;
    }

    public Optional<ATSProfile> findByUserId(String userId) {
        return atsProfileRepository.findByUserId(userId);
    }

    public ATSProfile createProfile(String userId, String summary, String cliperId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getRole() != User.Role.CANDIDATE) {
            throw new IllegalArgumentException("Solo los candidatos pueden crear perfiles ATS");
        }

        // Verificar si ya existe un perfil
        Optional<ATSProfile> existingProfile = atsProfileRepository.findByUserId(userId);
        if (existingProfile.isPresent()) {
            throw new IllegalStateException("El usuario ya tiene un perfil ATS");
        }

        ATSProfile profile = new ATSProfile(user)
                .withSummary(summary)
                .withCliper(cliperId);

        return atsProfileRepository.save(profile);
    }

    public ATSProfile updateProfile(String userId, String summary) {
        ATSProfile profile = atsProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil ATS no encontrado"));

        profile.withSummary(summary);
        return atsProfileRepository.save(profile);
    }

    public void deleteProfile(String userId) {
        ATSProfile profile = atsProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil ATS no encontrado"));
        
        atsProfileRepository.delete(profile);
    }
}
