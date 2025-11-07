package com.clipers.clipers.repository;

import com.clipers.clipers.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {

    List<JobApplication> findByJobId(String jobId);

    List<JobApplication> findByCandidateId(String candidateId);

    Optional<JobApplication> findByJobIdAndCandidateId(String jobId, String candidateId);

    long countByJobId(String jobId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.company.user.id = :companyUserId AND ja.job.id = :jobId")
    List<JobApplication> findByJobIdAndCompanyOwner(@Param("jobId") String jobId, @Param("companyUserId") String companyUserId);
}