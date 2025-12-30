package com.voting.system.repository;

import com.voting.system.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Candidate entity operations
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /**
     * Find all active candidates
     */
    List<Candidate> findByIsActiveTrue();

    /**
     * Find candidates by constituency ID
     */
    @Query("SELECT c FROM Candidate c WHERE c.constituency.id = :constituencyId AND c.isActive = true")
    List<Candidate> findByConstituencyId(@Param("constituencyId") Long constituencyId);

    /**
     * Find candidates by party ID
     */
    @Query("SELECT c FROM Candidate c WHERE c.party.id = :partyId AND c.isActive = true")
    List<Candidate> findByPartyId(@Param("partyId") Long partyId);

    /**
     * Find candidate by party ID and constituency ID
     */
    @Query("SELECT c FROM Candidate c WHERE c.party.id = :partyId AND c.constituency.id = :constituencyId AND c.isActive = true")
    Optional<Candidate> findByPartyIdAndConstituencyId(@Param("partyId") Long partyId,
            @Param("constituencyId") Long constituencyId);

    /**
     * Find candidates by name (case insensitive)
     */
    @Query("SELECT c FROM Candidate c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true")
    List<Candidate> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Check if candidate exists for a party in a constituency
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Candidate c WHERE c.party.id = :partyId AND c.constituency.id = :constituencyId")
    boolean existsByPartyIdAndConstituencyId(@Param("partyId") Long partyId,
            @Param("constituencyId") Long constituencyId);

    /**
     * Get next ID for manual ID assignment (for schemas without auto-increment)
     */
    @Query(value = "SELECT SEQ_VOTING_CANDIDATES.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextId();
}