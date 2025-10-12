package com.clipers.clipers.repository;

import com.clipers.clipers.entity.JobMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, String> {
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.user.id = :userId")
    List<JobMatch> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.job.id = :jobId")
    List<JobMatch> findByJobId(@Param("jobId") String jobId);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.user.id = :userId AND jm.job.id = :jobId")
    Optional<JobMatch> findByUserIdAndJobId(@Param("userId") String userId, @Param("jobId") String jobId);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.job.id = :jobId AND jm.user.id = :userId")
    JobMatch findByJobIdAndUserId(@Param("jobId") String jobId, @Param("userId") String userId);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.user.id = :userId ORDER BY jm.score DESC")
    Page<JobMatch> findByUserIdOrderByScoreDesc(@Param("userId") String userId, Pageable pageable);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.job.id = :jobId ORDER BY jm.score DESC")
    Page<JobMatch> findByJobIdOrderByScoreDesc(@Param("jobId") String jobId, Pageable pageable);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.user.id = :userId AND jm.score >= :minScore ORDER BY jm.score DESC")
    List<JobMatch> findHighScoringMatchesForUser(@Param("userId") String userId, @Param("minScore") Double minScore);
    
    @Query("SELECT jm FROM JobMatch jm WHERE jm.job.id = :jobId AND jm.score >= :minScore ORDER BY jm.score DESC")
    List<JobMatch> findHighScoringMatchesForJob(@Param("jobId") String jobId, @Param("minScore") Double minScore);
    
    @Query("SELECT AVG(jm.score) FROM JobMatch jm WHERE jm.user.id = :userId")
    Double getAverageScoreForUser(@Param("userId") String userId);
}
