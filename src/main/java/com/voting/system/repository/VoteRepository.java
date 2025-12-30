package com.voting.system.repository;

import com.voting.system.model.Candidate;
import com.voting.system.model.Constituency;
import com.voting.system.model.User;
import com.voting.system.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Vote entity
 * 
 * Provides custom queries for vote management and comprehensive analytics
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

        /**
         * Get next available ID using Oracle sequence (thread-safe)
         */
        @Query(value = "SELECT SEQ_VOTING_VOTES.NEXTVAL FROM DUAL", nativeQuery = true)
        Long getNextId();

        /**
         * Find vote by user and constituency (to prevent duplicate voting)
         */
        Optional<Vote> findByUserAndConstituency(User user, Constituency constituency);

        /**
         * Check if user has already voted in constituency
         */
        boolean existsByUserAndConstituency(User user, Constituency constituency);

        /**
         * Check if user has voted in any constituency
         */
        boolean existsByUser(User user);

        /**
         * Find votes by user
         */
        List<Vote> findByUserOrderByVotedAtDesc(User user);

        /**
         * Find votes by user ID
         */
        @Query("SELECT v FROM Vote v WHERE v.user.id = :userId ORDER BY v.votedAt DESC")
        List<Vote> findByUserIdOrderByVotedAtDesc(@Param("userId") Long userId);

        /**
         * Find votes by constituency
         */
        List<Vote> findByConstituencyOrderByVotedAtDesc(Constituency constituency);

        /**
         * Find votes by candidate
         */
        List<Vote> findByCandidateOrderByVotedAtDesc(Candidate candidate);

        /**
         * Find votes by session ID
         */
        List<Vote> findBySessionIdOrderByVotedAtDesc(String sessionId);

        /**
         * Find votes by status
         */
        List<Vote> findByStatusOrderByVotedAtDesc(Vote.VoteStatus status);

        /**
         * Find votes in time range
         */
        @Query("SELECT v FROM Vote v WHERE v.votedAt BETWEEN :startTime AND :endTime ORDER BY v.votedAt DESC")
        List<Vote> findVotesBetween(@Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        /**
         * Find votes today
         */
        @Query("SELECT v FROM Vote v WHERE FUNCTION('DATE', v.votedAt) = CURRENT_DATE ORDER BY v.votedAt DESC")
        List<Vote> findVotesToday();

        /**
         * Count votes by candidate in constituency
         */
        @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidate = :candidate AND v.constituency = :constituency AND v.status = 'CAST'")
        long countVotesByCandidateInConstituency(@Param("candidate") Candidate candidate,
                        @Param("constituency") Constituency constituency);

        /**
         * Count total votes in constituency
         */
        @Query("SELECT COUNT(v) FROM Vote v WHERE v.constituency = :constituency AND v.status = 'CAST'")
        long countVotesInConstituency(@Param("constituency") Constituency constituency);

        /**
         * Get vote results for constituency (by candidate)
         */
        @Query("SELECT v.candidate, COUNT(v) as voteCount FROM Vote v WHERE v.constituency = :constituency AND v.status = 'CAST' GROUP BY v.candidate ORDER BY COUNT(v) DESC")
        List<Object[]> getVoteResultsByConstituency(@Param("constituency") Constituency constituency);

        /**
         * Get vote results by constituency ID (by candidate)
         */
        @Query("SELECT v.candidate, COUNT(v) as voteCount FROM Vote v WHERE v.constituency.id = :constituencyId AND v.status = 'CAST' GROUP BY v.candidate ORDER BY COUNT(v) DESC")
        List<Object[]> getVoteResultsByConstituencyId(@Param("constituencyId") Long constituencyId);

        /**
         * Get overall vote results (all constituencies by party)
         */
        @Query("SELECT v.candidate.party.name, COUNT(v) as voteCount FROM Vote v WHERE v.status = 'CAST' GROUP BY v.candidate.party.name ORDER BY COUNT(v) DESC")
        List<Object[]> getOverallVoteResults();

        /**
         * Get constituency-wise vote summary
         */
        @Query("SELECT v.constituency.name, COUNT(v) as totalVotes FROM Vote v WHERE v.status = 'CAST' GROUP BY v.constituency.name ORDER BY COUNT(v) DESC")
        List<Object[]> getConstituencyWiseVoteSummary();

        /**
         * Get state-wise vote summary
         */
        @Query("SELECT v.constituency.state, COUNT(v) as totalVotes FROM Vote v WHERE v.status = 'CAST' GROUP BY v.constituency.state ORDER BY COUNT(v) DESC")
        List<Object[]> getStateWiseVoteSummary();

        /**
         * Find votes by IP address (security analysis)
         */
        List<Vote> findByIpAddressOrderByVotedAtDesc(String ipAddress);

        /**
         * Count votes by IP address
         */
        @Query("SELECT v.ipAddress, COUNT(v) FROM Vote v WHERE v.ipAddress IS NOT NULL GROUP BY v.ipAddress ORDER BY COUNT(v) DESC")
        List<Object[]> countVotesByIpAddress();

        /**
         * Find suspicious voting patterns (multiple votes from same IP)
         */
        @Query("SELECT v.ipAddress, COUNT(v) as voteCount FROM Vote v WHERE v.ipAddress IS NOT NULL GROUP BY v.ipAddress HAVING COUNT(v) > :threshold ORDER BY COUNT(v) DESC")
        List<Object[]> findSuspiciousVotingPatterns(@Param("threshold") int threshold);

        /**
         * Get hourly vote distribution
         */
        @Query("SELECT FUNCTION('HOUR', v.votedAt) as hour, COUNT(v) as voteCount FROM Vote v WHERE FUNCTION('DATE', v.votedAt) = CURRENT_DATE GROUP BY FUNCTION('HOUR', v.votedAt) ORDER BY FUNCTION('HOUR', v.votedAt)")
        List<Object[]> getHourlyVoteDistribution();

        /**
         * Get voting statistics
         */
        @Query("SELECT COUNT(v) FROM Vote v WHERE v.status = 'CAST'")
        long countTotalValidVotes();

        @Query("SELECT COUNT(DISTINCT v.user) FROM Vote v WHERE v.status = 'CAST'")
        long countUniqueVoters();

        @Query("SELECT COUNT(DISTINCT v.constituency) FROM Vote v WHERE v.status = 'CAST'")
        long countConstituenciesWithVotes();

        @Query("SELECT COUNT(DISTINCT v.candidate.party) FROM Vote v WHERE v.status = 'CAST'")
        long countPartiesWithVotes();

        /**
         * Find peak voting times
         */
        @Query("SELECT FUNCTION('DATE', v.votedAt) as voteDate, COUNT(v) as voteCount FROM Vote v GROUP BY FUNCTION('DATE', v.votedAt) ORDER BY COUNT(v) DESC")
        List<Object[]> findPeakVotingDays();

        /**
         * Get voter turnout by constituency
         */
        @Query("SELECT c.name as constituencyName, COUNT(v) as voteCount, (SELECT COUNT(u) FROM User u WHERE u.isVerified = true AND u.isActive = true) as totalEligibleVoters FROM Vote v RIGHT JOIN v.constituency c WHERE v.status = 'CAST' OR v IS NULL GROUP BY c.name ORDER BY COUNT(v) DESC")
        List<Object[]> getVoterTurnoutByConstituency();

        /**
         * Find winning party by constituency
         */
        @Query(value = "SELECT c.name as constituency_name, p.name as party_name, p.symbol, COUNT(v.id) as vote_count FROM VOTING_VOTES v JOIN VOTING_CANDIDATES cand ON v.candidate_id = cand.id JOIN VOTING_PARTIES p ON cand.party_id = p.id JOIN VOTING_CONSTITUENCIES c ON v.constituency_id = c.id WHERE v.status = 'CAST' GROUP BY c.id, c.name, p.id, p.name, p.symbol HAVING COUNT(v.id) = (SELECT MAX(vote_count) FROM (SELECT COUNT(v2.id) as vote_count FROM VOTING_VOTES v2 JOIN VOTING_CANDIDATES cand2 ON v2.candidate_id = cand2.id WHERE v2.constituency_id = c.id AND v2.status = 'CAST' GROUP BY cand2.party_id) subquery) ORDER BY c.name", nativeQuery = true)
        List<Object[]> findWinningPartiesByConstituency();
}