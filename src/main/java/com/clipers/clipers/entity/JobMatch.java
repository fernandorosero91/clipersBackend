package com.clipers.clipers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_matches")
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    private Double score; // 0.0 to 1.0

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ElementCollection
    @CollectionTable(name = "job_match_skills", joinColumns = @JoinColumn(name = "job_match_id"))
    @Column(name = "matched_skill")
    private List<String> matchedSkills;

    // Campos adicionales para integración con IA (MicroSelectIA)
    private Integer aiRank;  // Ranking de la IA (1 = mejor candidato)
    
    @Column(columnDefinition = "TEXT")
    private String aiExplanation;  // Explicación detallada de la IA
    
    private String matchQuality;  // EXCELLENT, GOOD, FAIR, POOR
    
    @ElementCollection
    @CollectionTable(name = "job_match_missing_skills", joinColumns = @JoinColumn(name = "job_match_id"))
    @Column(name = "missing_skill")
    private List<String> missingSkills;  // Skills que faltan al candidato
    
    @Column(columnDefinition = "TEXT")
    private String aiRecommendations;  // Recomendaciones de la IA
    
    private LocalDateTime aiProcessedAt;  // Timestamp de procesamiento por IA
    
    private Double matchPercentage;  // 0-100 (score * 100 para mejor visualización)

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public JobMatch() {}

    public JobMatch(Job job, User user, Double score, String explanation) {
        this.job = job;
        this.user = user;
        this.score = score;
        this.explanation = explanation;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public Integer getAiRank() { return aiRank; }
    public void setAiRank(Integer aiRank) { this.aiRank = aiRank; }

    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String aiExplanation) { this.aiExplanation = aiExplanation; }

    public String getMatchQuality() { return matchQuality; }
    public void setMatchQuality(String matchQuality) { this.matchQuality = matchQuality; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public String getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(String aiRecommendations) { this.aiRecommendations = aiRecommendations; }

    public LocalDateTime getAiProcessedAt() { return aiProcessedAt; }
    public void setAiProcessedAt(LocalDateTime aiProcessedAt) { this.aiProcessedAt = aiProcessedAt; }

    public Double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Double matchPercentage) { this.matchPercentage = matchPercentage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
