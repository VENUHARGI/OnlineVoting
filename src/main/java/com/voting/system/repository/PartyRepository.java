package com.voting.system.repository;

import com.voting.system.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Party entity
 * 
 * Provides custom queries for party management and analytics
 */
@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

    /**
     * Find all active parties
     */
    List<Party> findByIsActiveTrueOrderByName();

    /**
     * Find parties by symbol
     */
    List<Party> findBySymbolOrderByName(String symbol);

    /**
     * Find parties by name pattern
     */
    @Query("SELECT p FROM Party p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND p.isActive = true ORDER BY p.name")
    List<Party> findByNameContaining(@Param("namePattern") String namePattern);

    /**
     * Find all unique party names across all constituencies
     */
    @Query("SELECT DISTINCT p.name FROM Party p WHERE p.isActive = true ORDER BY p.name")
    List<String> findAllUniquePartyNames();

    /**
     * Find all unique symbols
     */
    @Query("SELECT DISTINCT p.symbol FROM Party p WHERE p.isActive = true ORDER BY p.symbol")
    List<String> findAllUniqueSymbols();

    /**
     * Count parties by constituency - DISABLED
     */
    /*
     * @Query("SELECT p.constituency, COUNT(p) FROM Party p WHERE p.isActive = true GROUP BY p.constituency ORDER BY COUNT(p) DESC"
     * )
     * List<Object[]> countPartiesByConstituency();
     */

    /**
     * Find parties with vote count - DISABLED
     */
    /*
     * @Query("SELECT p, COUNT(v) as voteCount FROM Party p LEFT JOIN Vote v ON p = v.candidate.party WHERE p.constituency.id = :constituencyId AND p.isActive = true GROUP BY p ORDER BY COUNT(v) DESC, p.name"
     * )
     * List<Object[]> findPartiesWithVoteCount(@Param("constituencyId") Long
     * constituencyId);
     */

    /**
     * Find top parties by vote count globally
     */
    @Query("SELECT p, COUNT(v) as voteCount FROM Party p LEFT JOIN Vote v ON p = v.candidate.party WHERE p.isActive = true GROUP BY p ORDER BY COUNT(v) DESC, p.name")
    List<Object[]> findTopPartiesByVotes();

    /**
     * Find parties without any votes
     */
    @Query("SELECT p FROM Party p WHERE p.isActive = true AND NOT EXISTS (SELECT v FROM Vote v WHERE v.candidate.party = p)")
    List<Party> findPartiesWithoutVotes();

    /**
     * Get party statistics
     */
    @Query("SELECT COUNT(p) FROM Party p WHERE p.isActive = true")
    long countActiveParties();

    @Query("SELECT COUNT(p) FROM Party p WHERE p.isActive = false")
    long countInactiveParties();

    // Remove constituency-related count since parties are now independent
    // @Query("SELECT COUNT(DISTINCT p.constituency) FROM Party p WHERE p.isActive =
    // true")
    // long countConstituenciesWithParties();

    /**
     * Find parties created today
     */
    @Query("SELECT p FROM Party p WHERE FUNCTION('DATE', p.createdAt) = CURRENT_DATE ORDER BY p.createdAt DESC")
    List<Party> findPartiesCreatedToday();
}