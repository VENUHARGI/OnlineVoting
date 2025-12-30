package com.voting.system.repository;

import com.voting.system.model.Constituency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Constituency entity
 * 
 * Provides custom queries for constituency management and analytics
 */
@Repository
public interface ConstituencyRepository extends JpaRepository<Constituency, Long> {

    /**
     * Find constituency by name
     */
    Optional<Constituency> findByName(String name);

    /**
     * Find constituency by name (case insensitive)
     */
    @Query("SELECT c FROM Constituency c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Constituency> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Check if constituency name exists
     */
    boolean existsByName(String name);

    /**
     * Check if constituency name exists (case insensitive)
     */
    @Query("SELECT COUNT(c) > 0 FROM Constituency c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    /**
     * Find all active constituencies
     */
    List<Constituency> findByIsActiveTrueOrderByName();

    /**
     * Find constituencies by state
     */
    List<Constituency> findByStateOrderByName(String state);

    /**
     * Find active constituencies by state
     */
    List<Constituency> findByStateAndIsActiveTrueOrderByName(String state);

    /**
     * Find all unique states
     */
    @Query("SELECT DISTINCT c.state FROM Constituency c WHERE c.isActive = true ORDER BY c.state")
    List<String> findAllActiveStates();

    /**
     * Find constituencies by name pattern
     */
    @Query("SELECT c FROM Constituency c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND c.isActive = true ORDER BY c.name")
    List<Constituency> findByNameContaining(@Param("namePattern") String namePattern);

    /**
     * Find constituencies with candidates
     */
    @Query("SELECT DISTINCT c FROM Constituency c JOIN Candidate cand ON cand.constituency = c WHERE c.isActive = true AND cand.isActive = true ORDER BY c.name")
    List<Constituency> findConstituenciesWithActiveCandidates();

    /**
     * Count constituencies by state
     */
    @Query("SELECT c.state, COUNT(c) FROM Constituency c WHERE c.isActive = true GROUP BY c.state ORDER BY c.state")
    List<Object[]> countConstituenciesByState();

    /**
     * Find constituencies with candidate count
     */
    @Query("SELECT c, COUNT(cand) as candidateCount FROM Constituency c LEFT JOIN Candidate cand ON cand.constituency = c WHERE c.isActive = true AND (cand IS NULL OR cand.isActive = true) GROUP BY c ORDER BY c.name")
    List<Object[]> findConstituenciesWithCandidateCount();

    /**
     * Find constituencies without any candidates
     */
    @Query("SELECT c FROM Constituency c WHERE c.isActive = true AND NOT EXISTS (SELECT cand FROM Candidate cand WHERE cand.constituency = c AND cand.isActive = true)")
    List<Constituency> findConstituenciesWithoutCandidates();

    /**
     * Get constituency statistics
     */
    @Query("SELECT COUNT(c) FROM Constituency c WHERE c.isActive = true")
    long countActiveConstituencies();

    @Query("SELECT COUNT(c) FROM Constituency c WHERE c.isActive = false")
    long countInactiveConstituencies();

    /**
     * Find constituencies created today
     */
    @Query("SELECT c FROM Constituency c WHERE FUNCTION('DATE', c.createdAt) = CURRENT_DATE ORDER BY c.createdAt DESC")
    List<Constituency> findConstituenciesCreatedToday();
}