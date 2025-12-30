package com.voting.system.service;

import com.voting.system.model.Constituency;
import com.voting.system.model.Party;
import com.voting.system.repository.ConstituencyRepository;
import com.voting.system.repository.PartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Constituency and Party management
 * 
 * Handles constituency operations, party management, and related analytics
 */
@Service
@Transactional
public class ConstituencyService {

    @Autowired
    private ConstituencyRepository constituencyRepository;

    @Autowired
    private PartyRepository partyRepository;

    // Constituency Management

    /**
     * Get all active constituencies
     */
    public List<Constituency> getAllActiveConstituencies() {
        return constituencyRepository.findByIsActiveTrueOrderByName();
    }

    /**
     * Get constituency by ID
     */
    public Optional<Constituency> getConstituencyById(Long id) {
        return constituencyRepository.findById((Long) id);
    }

    /**
     * Get constituency by name
     */
    public Optional<Constituency> getConstituencyByName(String name) {
        return constituencyRepository.findByNameIgnoreCase(name);
    }

    /**
     * Get constituencies by state
     */
    public List<Constituency> getConstituenciesByState(String state) {
        return constituencyRepository.findByStateAndIsActiveTrueOrderByName(state);
    }

    /**
     * Get all unique states
     */
    public List<String> getAllStates() {
        return constituencyRepository.findAllActiveStates();
    }

    /**
     * Search constituencies by name pattern
     */
    public List<Constituency> searchConstituencies(String namePattern) {
        return constituencyRepository.findByNameContaining(namePattern);
    }

    /**
     * Create new constituency
     */
    public Constituency createConstituency(String name, String state, String description) {
        // Check if constituency already exists
        if (constituencyRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Constituency with this name already exists");
        }

        Constituency constituency = new Constituency(name, state, description);
        return constituencyRepository.save(constituency);
    }

    /**
     * Update constituency
     */
    public Constituency updateConstituency(Long id, String name, String state, String description) {
        Constituency constituency = constituencyRepository.findById((Long) id)
                .orElseThrow(() -> new RuntimeException("Constituency not found"));

        // Check if new name conflicts with existing constituency (excluding current)
        Optional<Constituency> existingConstituency = constituencyRepository.findByNameIgnoreCase(name);
        if (existingConstituency.isPresent() && !existingConstituency.get().getId().equals(id)) {
            throw new RuntimeException("Another constituency with this name already exists");
        }

        constituency.setName(name);
        constituency.setState(state);
        constituency.setDescription(description);

        return constituencyRepository.save(constituency);
    }

    /**
     * Activate/Deactivate constituency
     */
    public void updateConstituencyStatus(Long id, Boolean isActive) {
        Constituency constituency = constituencyRepository.findById((Long) id)
                .orElseThrow(() -> new RuntimeException("Constituency not found"));

        constituency.setIsActive(isActive);
        constituencyRepository.save(constituency);

        // If deactivating constituency, also deactivate all its parties
        // Note: In the new candidate-based structure, parties are independent
        // and constituency deactivation doesn't affect parties directly
        /*
         * if (!isActive) {
         * List<Party> parties =
         * partyRepository.findByConstituencyOrderByName(constituency);
         * for (Party party : parties) {
         * party.setIsActive(false);
         * }
         * partyRepository.saveAll(parties);
         * }
         */
    }

    /**
     * Get constituencies with candidate count
     */
    public List<ConstituencyWithCandidateCount> getConstituenciesWithCandidateCount() {
        List<Object[]> results = constituencyRepository.findConstituenciesWithCandidateCount();

        return results.stream()
                .map(row -> new ConstituencyWithCandidateCount(
                        (Constituency) row[0],
                        (Long) row[1]))
                .toList();
    }

    /**
     * Get constituencies without candidates
     */
    public List<Constituency> getConstituenciesWithoutCandidates() {
        return constituencyRepository.findConstituenciesWithoutCandidates();
    }

    // Party Management

    /**
     * Get active parties by constituency
     */
    /**
     * Get candidates by constituency (replacement for parties by constituency)
     * Note: This method should use CandidateService instead
     */
    /*
     * public List<Party> getPartiesByConstituency(Long constituencyId) {
     * return partyRepository.findByConstituencyId(constituencyId);
     * }
     */

    /**
     * Get party by ID
     */
    public Optional<Party> getPartyById(Long id) {
        return partyRepository.findById((Long) id);
    }

    /**
     * Get party by name (constituency-independent)
     * Note: In the new structure, parties are independent of constituencies
     */
    /*
     * public Optional<Party> getPartyByNameAndConstituency(String name, Long
     * constituencyId) {
     * return partyRepository.findByNameAndConstituencyId(name, constituencyId);
     * }
     */

    /**
     * Search parties by name pattern
     */
    public List<Party> searchParties(String namePattern) {
        return partyRepository.findByNameContaining(namePattern);
    }

    /**
     * Get all unique party names
     */
    public List<String> getAllUniquePartyNames() {
        return partyRepository.findAllUniquePartyNames();
    }

    /**
     * Get all unique symbols
     */
    public List<String> getAllUniqueSymbols() {
        return partyRepository.findAllUniqueSymbols();
    }

    /**
     * Create new party - DISABLED: parties are now independent of constituencies
     */
    /*
     * public Party createParty(String name, String symbol, String description, Long
     * constituencyId, String logoUrl) {
     * // This method is disabled in the new candidate-based structure
     * throw new
     * RuntimeException("Party creation through constituency service is no longer supported. Use CandidateService instead."
     * );
     * }
     */

    /**
     * Update party
     */
    public Party updateParty(Long id, String name, String symbol, String description, String logoUrl) {
        Party party = partyRepository.findById((Long) id)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        // Check if new name conflicts with existing party (excluding current)
        List<Party> existingParties = partyRepository.findByNameContaining(name);
        Optional<Party> exactMatch = existingParties.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
        if (exactMatch.isPresent() && !exactMatch.get().getId().equals(id)) {
            throw new RuntimeException("Another party with this name already exists");
        }

        // Check if new symbol conflicts with existing party (excluding current)
        List<Party> existingSymbols = partyRepository.findBySymbolOrderByName(symbol);
        Optional<Party> exactSymbolMatch = existingSymbols.stream()
                .filter(p -> p.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
        if (exactSymbolMatch.isPresent() && !exactSymbolMatch.get().getId().equals(id)) {
            throw new RuntimeException("Another party with this symbol already exists");
        }

        party.setName(name);
        party.setSymbol(symbol);
        party.setDescription(description);
        party.setLogoUrl(logoUrl);

        return partyRepository.save(party);
    }

    /**
     * Activate/Deactivate party
     */
    public void updatePartyStatus(Long id, Boolean isActive) {
        Party party = partyRepository.findById((Long) id)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        party.setIsActive(isActive);
        partyRepository.save(party);
    }

    /**
     * Get parties with vote count for constituency - DISABLED
     * Note: Use CandidateService for candidate-based vote counts
     */
    /*
     * public List<PartyWithVoteCount> getPartiesWithVoteCount(Long constituencyId)
     * {
     * // This method is disabled in the new candidate-based structure
     * throw new
     * RuntimeException("Party-based vote counting is no longer supported. Use CandidateService instead."
     * );
     * }
     */

    /**
     * Get top parties by votes globally
     */
    public List<PartyWithVoteCount> getTopPartiesByVotes() {
        List<Object[]> results = partyRepository.findTopPartiesByVotes();

        return results.stream()
                .map(row -> new PartyWithVoteCount(
                        (Party) row[0],
                        (Long) row[1]))
                .toList();
    }

    /**
     * Get parties without votes
     */
    public List<Party> getPartiesWithoutVotes() {
        return partyRepository.findPartiesWithoutVotes();
    }

    /**
     * Get constituency and party statistics
     */
    public ConstituencyPartyStats getStatistics() {
        ConstituencyPartyStats stats = new ConstituencyPartyStats();

        stats.setTotalConstituencies(constituencyRepository.count());
        stats.setActiveConstituencies(constituencyRepository.countActiveConstituencies());
        stats.setInactiveConstituencies(constituencyRepository.countInactiveConstituencies());

        stats.setTotalParties(partyRepository.count());
        stats.setActiveParties(partyRepository.countActiveParties());
        stats.setInactiveParties(partyRepository.countInactiveParties());
        // stats.setConstituenciesWithParties(partyRepository.countConstituenciesWithParties());
        // // Disabled in candidate-based structure

        stats.setConstituenciesCreatedToday(constituencyRepository.findConstituenciesCreatedToday().size());
        stats.setPartiesCreatedToday(partyRepository.findPartiesCreatedToday().size());

        return stats;
    }

    // DTOs for service responses

    public static class ConstituencyWithCandidateCount {
        private Constituency constituency;
        private Long candidateCount;

        public ConstituencyWithCandidateCount(Constituency constituency, Long candidateCount) {
            this.constituency = constituency;
            this.candidateCount = candidateCount;
        }

        // Getters
        public Constituency getConstituency() {
            return constituency;
        }

        public Long getCandidateCount() {
            return candidateCount;
        }
    }

    public static class PartyWithVoteCount {
        private Party party;
        private Long voteCount;

        public PartyWithVoteCount(Party party, Long voteCount) {
            this.party = party;
            this.voteCount = voteCount;
        }

        // Getters
        public Party getParty() {
            return party;
        }

        public Long getVoteCount() {
            return voteCount;
        }
    }

    public static class ConstituencyPartyStats {
        private long totalConstituencies;
        private long activeConstituencies;
        private long inactiveConstituencies;
        private long totalParties;
        private long activeParties;
        private long inactiveParties;
        private long constituenciesWithParties;
        private long constituenciesCreatedToday;
        private long partiesCreatedToday;

        // Getters and setters
        public long getTotalConstituencies() {
            return totalConstituencies;
        }

        public void setTotalConstituencies(long totalConstituencies) {
            this.totalConstituencies = totalConstituencies;
        }

        public long getActiveConstituencies() {
            return activeConstituencies;
        }

        public void setActiveConstituencies(long activeConstituencies) {
            this.activeConstituencies = activeConstituencies;
        }

        public long getInactiveConstituencies() {
            return inactiveConstituencies;
        }

        public void setInactiveConstituencies(long inactiveConstituencies) {
            this.inactiveConstituencies = inactiveConstituencies;
        }

        public long getTotalParties() {
            return totalParties;
        }

        public void setTotalParties(long totalParties) {
            this.totalParties = totalParties;
        }

        public long getActiveParties() {
            return activeParties;
        }

        public void setActiveParties(long activeParties) {
            this.activeParties = activeParties;
        }

        public long getInactiveParties() {
            return inactiveParties;
        }

        public void setInactiveParties(long inactiveParties) {
            this.inactiveParties = inactiveParties;
        }

        public long getConstituenciesWithParties() {
            return constituenciesWithParties;
        }

        public void setConstituenciesWithParties(long constituenciesWithParties) {
            this.constituenciesWithParties = constituenciesWithParties;
        }

        public long getConstituenciesCreatedToday() {
            return constituenciesCreatedToday;
        }

        public void setConstituenciesCreatedToday(long constituenciesCreatedToday) {
            this.constituenciesCreatedToday = constituenciesCreatedToday;
        }

        public long getPartiesCreatedToday() {
            return partiesCreatedToday;
        }

        public void setPartiesCreatedToday(long partiesCreatedToday) {
            this.partiesCreatedToday = partiesCreatedToday;
        }
    }
}