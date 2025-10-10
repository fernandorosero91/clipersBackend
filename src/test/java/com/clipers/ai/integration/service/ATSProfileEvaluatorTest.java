package com.clipers.ai.integration.service;

import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.entity.Education;
import com.clipers.clipers.entity.Experience;
import com.clipers.clipers.entity.Skill;
import com.clipers.clipers.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ATSProfileEvaluator
 */
@ExtendWith(MockitoExtension.class)
class ATSProfileEvaluatorTest {

    private ATSProfileEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new ATSProfileEvaluator();
    }

    @Test
    void testEvaluateATSProfileWithNullUser() {
        double score = evaluator.evaluateATSProfile(null);
        assertEquals(0.0, score, "Score should be 0.0 for null user");
    }

    @Test
    void testEvaluateATSProfileWithNullProfile() {
        User user = new User();
        user.setAtsProfile(null);
        
        double score = evaluator.evaluateATSProfile(user);
        assertEquals(0.0, score, "Score should be 0.0 for null profile");
    }

    @Test
    void testEvaluateATSProfileWithEmptyProfile() {
        User user = new User();
        user.setAtsProfile(new ATSProfile());
        
        double score = evaluator.evaluateATSProfile(user);
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    @Test
    void testEvaluateATSProfileWithStrictStrategy() {
        User user = createUserWithCompleteProfile();
        
        double score = evaluator.evaluateATSProfile(user, new ATSProfileEvaluator.StrictEvaluator());
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    @Test
    void testEvaluateATSProfileWithBalancedStrategy() {
        User user = createUserWithCompleteProfile();
        
        double score = evaluator.evaluateATSProfile(user, new ATSProfileEvaluator.BalancedEvaluator());
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    @Test
    void testEvaluateATSProfileWithLenientStrategy() {
        User user = createUserWithCompleteProfile();
        
        double score = evaluator.evaluateATSProfile(user, new ATSProfileEvaluator.LenientEvaluator());
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    @Test
    void testEvaluateSkillsMatchWithNullInputs() {
        ATSProfile profile = new ATSProfile();
        
        double score = evaluator.evaluateSkillsMatch(profile, null);
        assertEquals(0.0, score, "Score should be 0.0 for null inputs");
    }

    @Test
    void testEvaluateSkillsMatchWithEmptyLists() {
        ATSProfile profile = new ATSProfile();
        
        double score = evaluator.evaluateSkillsMatch(profile, Arrays.asList());
        assertEquals(0.0, score, "Score should be 0.0 for empty lists");
    }

    @Test
    void testEvaluateSkillsMatchWithPartialMatch() {
        ATSProfile profile = new ATSProfile();
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill.setLevel(Skill.SkillLevel.ADVANCED);
        
        Skill pythonSkill = new Skill();
        pythonSkill.setName("Python");
        pythonSkill.setLevel(Skill.SkillLevel.INTERMEDIATE);
        
        profile.setSkills(Arrays.asList(javaSkill, pythonSkill));
        
        List<String> requiredSkills = Arrays.asList("Java", "JavaScript", "SQL");
        
        double score = evaluator.evaluateSkillsMatch(profile, requiredSkills);
        assertEquals(1.0 / 3.0, score, "Score should match ratio of required skills found");
    }

    @Test
    void testEvaluateSkillsMatchWithFullMatch() {
        ATSProfile profile = new ATSProfile();
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill.setLevel(Skill.SkillLevel.ADVANCED);
        
        Skill sqlSkill = new Skill();
        sqlSkill.setName("SQL");
        sqlSkill.setLevel(Skill.SkillLevel.EXPERT);
        
        profile.setSkills(Arrays.asList(javaSkill, sqlSkill));
        
        List<String> requiredSkills = Arrays.asList("Java", "SQL");
        
        double score = evaluator.evaluateSkillsMatch(profile, requiredSkills);
        assertEquals(1.0, score, "Score should be 1.0 for full match");
    }

    @Test
    void testSetAndGetEvaluationStrategy() {
        ATSProfileEvaluator.EvaluationStrategy strategy = new ATSProfileEvaluator.StrictEvaluator();
        
        evaluator.setEvaluationStrategy(strategy);
        
        ATSProfileEvaluator.EvaluationStrategy retrievedStrategy = evaluator.getEvaluationStrategy();
        assertEquals(strategy.getClass(), retrievedStrategy.getClass(), "Retrieved strategy should match set strategy");
    }

    @Test
    void testStrictEvaluatorWithNoSkills() {
        ATSProfile profile = new ATSProfile();
        profile.setSkills(Arrays.asList());
        
        double score = new ATSProfileEvaluator.StrictEvaluator().evaluate(profile, null);
        assertEquals(0.0, score, "Score should be 0.0 for no skills");
    }

    @Test
    void testStrictEvaluatorWithExpertSkills() {
        ATSProfile profile = new ATSProfile();
        Skill expertSkill = new Skill();
        expertSkill.setName("Java");
        expertSkill.setLevel(Skill.SkillLevel.EXPERT);
        
        profile.setSkills(Arrays.asList(expertSkill));
        
        double score = new ATSProfileEvaluator.StrictEvaluator().evaluate(profile, null);
        assertEquals(1.0, score, "Score should be 1.0 for expert skills");
    }

    @Test
    void testStrictEvaluatorWithExperience() {
        ATSProfile profile = new ATSProfile();
        Experience experience = new Experience();
        experience.setPosition("Senior Developer");
        experience.setDescription("Senior Java developer with 8 years of experience");
        experience.setStartDate(LocalDate.now().minusYears(8));
        experience.setEndDate(LocalDate.now());
        
        profile.setExperience(Arrays.asList(experience));
        
        double score = new ATSProfileEvaluator.StrictEvaluator().evaluate(profile, null);
        assertEquals(0.8, score, "Score should be 0.8 for 8 years of experience");
    }

    @Test
    void testStrictEvaluatorWithEducation() {
        ATSProfile profile = new ATSProfile();
        Education education = new Education();
        education.setDegree("Master");
        education.setField("Computer Science");
        education.setInstitution("University");
        education.setStartDate(LocalDate.now().minusYears(2));
        education.setEndDate(LocalDate.now().minusYears(1));
        
        profile.setEducation(Arrays.asList(education));
        
        double score = new ATSProfileEvaluator.StrictEvaluator().evaluate(profile, null);
        assertEquals(0.8, score, "Score should be 0.8 for Master's degree");
    }

    @Test
    void testBalancedEvaluatorWithCompleteness() {
        ATSProfile profile = new ATSProfile();
        profile.setSummary("Experienced developer with 5 years of experience");
        
        Skill skill = new Skill();
        skill.setName("Java");
        skill.setLevel(Skill.SkillLevel.ADVANCED);
        profile.setSkills(Arrays.asList(skill));
        
        Experience experience = new Experience();
        experience.setPosition("Developer");
        experience.setDescription("Java developer");
        experience.setStartDate(LocalDate.now().minusYears(3));
        experience.setEndDate(LocalDate.now());
        profile.setExperience(Arrays.asList(experience));
        
        double score = new ATSProfileEvaluator.BalancedEvaluator().evaluate(profile, null);
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    @Test
    void testLenientEvaluatorWithMinimalProfile() {
        ATSProfile profile = new ATSProfile();
        profile.setSummary("Developer");
        
        double score = new ATSProfileEvaluator.LenientEvaluator().evaluate(profile, null);
        assertTrue(score >= 0.0 && score <= 1.0, "Score should be between 0.0 and 1.0");
    }

    /**
     * Helper method to create a user with complete profile for testing
     */
    private User createUserWithCompleteProfile() {
        User user = new User();
        ATSProfile profile = new ATSProfile();
        
        // Add skills
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill.setLevel(Skill.SkillLevel.EXPERT);
        
        Skill sqlSkill = new Skill();
        sqlSkill.setName("SQL");
        sqlSkill.setLevel(Skill.SkillLevel.ADVANCED);
        
        profile.setSkills(Arrays.asList(javaSkill, sqlSkill));
        
        // Add experience
        Experience experience = new Experience();
        experience.setPosition("Senior Developer");
        experience.setDescription("Senior Java developer with 5 years of experience");
        experience.setStartDate(LocalDate.now().minusYears(5));
        experience.setEndDate(LocalDate.now());
        
        profile.setExperience(Arrays.asList(experience));
        
        // Add education
        Education education = new Education();
        education.setDegree("Bachelor");
        education.setField("Computer Science");
        education.setInstitution("University");
        education.setStartDate(LocalDate.now().minusYears(4));
        education.setEndDate(LocalDate.now().minusYears(2));
        
        profile.setEducation(Arrays.asList(education));
        
        // Add summary
        profile.setSummary("Experienced Java developer with strong problem-solving skills");
        
        user.setAtsProfile(profile);
        return user;
    }
}