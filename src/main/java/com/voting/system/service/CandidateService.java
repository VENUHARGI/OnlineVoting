package com.voting.system.service;

import com.voting.system.model.Candidate;
import com.voting.system.model.Constituency;
import com.voting.system.model.Party;
import com.voting.system.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing candidates
 */
@Service
@Transactional
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    /**
     * Get all active candidates
     */
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findByIsActiveTrue();
    }

    /**
     * Get candidate by ID
     */
    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById((Long) id);
    }

    /**
     * Get candidates by constituency ID
     */
    public List<Candidate> getCandidatesByConstituencyId(Long constituencyId) {
        return candidateRepository.findByConstituencyId(constituencyId);
    }

    /**
     * Get candidates by party ID
     */
    public List<Candidate> getCandidatesByPartyId(Long partyId) {
        return candidateRepository.findByPartyId(partyId);
    }

    /**
     * Get candidate by party and constituency
     */
    public Optional<Candidate> getCandidateByPartyAndConstituency(Long partyId, Long constituencyId) {
        return candidateRepository.findByPartyIdAndConstituencyId(partyId, constituencyId);
    }

    /**
     * Search candidates by name
     */
    public List<Candidate> searchCandidatesByName(String name) {
        return candidateRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Save a candidate
     */
    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save((Candidate) candidate);
    }

    /**
     * Create a new candidate
     */
    public Candidate createCandidate(String name, Integer age, String qualification, String bio, Party party,
            Constituency constituency) {
        Candidate candidate = new Candidate(name, age, qualification, party, constituency);
        candidate.setBio(bio);
        return candidateRepository.save(candidate);
    }

    /**
     * Update candidate information
     */
    public Candidate updateCandidate(Long id, String name, Integer age, String qualification, String bio) {
        Optional<Candidate> candidateOpt = candidateRepository.findById((Long) id);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            candidate.setName(name);
            candidate.setAge(age);
            candidate.setQualification(qualification);
            candidate.setBio(bio);
            return candidateRepository.save(candidate);
        }
        throw new RuntimeException("Candidate not found with ID: " + id);
    }

    /**
     * Deactivate a candidate
     */
    public void deactivateCandidate(Long id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById((Long) id);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            candidate.setIsActive(false);
            candidateRepository.save(candidate);
        } else {
            throw new RuntimeException("Candidate not found with ID: " + id);
        }
    }

    /**
     * Activate a candidate
     */
    public void activateCandidate(Long id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById((Long) id);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            candidate.setIsActive(true);
            candidateRepository.save(candidate);
        } else {
            throw new RuntimeException("Candidate not found with ID: " + id);
        }
    }

    /**
     * Check if a candidate exists for a party in a constituency
     */
    public boolean candidateExistsForPartyInConstituency(Long partyId, Long constituencyId) {
        return candidateRepository.existsByPartyIdAndConstituencyId(partyId, constituencyId);
    }

    /**
     * Delete a candidate (hard delete)
     */
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById((Long) id);
    }
}