// ===== VOTING PAGE FUNCTIONALITY =====

let selectedConstituency = null;
let selectedCandidate = null;
let constituencies = [];
let candidates = [];

/**
 * Initialize voting page
 */
function initializeVotingPage() {
  // Check authentication status
  if (!isAuthenticated()) {
    redirectTo("login.html");
    return;
  }

  // Check if user has already voted
  checkVotingStatus();

  // Load constituencies
  loadConstituencies();

  // Update current timestamp
  updateCurrentTime();

  console.log("Voting page initialized");
}

/**
 * Check if user has already voted
 */
async function checkVotingStatus() {
  try {
    const response = await apiGet("/voting/status");

    // Debug logging to see what we're getting
    console.log("🔍 Voting status response:", response);
    console.log("🔍 Response success:", response.success);
    console.log("🔍 Response data:", response.data);
    console.log("🔍 VotingOpen value:", response.data?.data?.votingOpen);

    if (response.success && response.data.data.hasVoted) {
      showModal("alreadyVotedModal");
      return;
    }

    // Check if voting is open
    if (response.success && !response.data.data.votingOpen) {
      console.log("❌ TRIGGERING votingClosedModal because votingOpen is:", response.data.data.votingOpen);
      showModal("votingClosedModal");
      return;
    }

    console.log("✅ Voting status check passed - voting is open");
  } catch (error) {
    console.error("❌ Error checking voting status:", error);
    // Continue with voting process if check fails
  }
}

/**
 * Load constituencies from API
 */
async function loadConstituencies() {
  showLoading("Loading constituencies...");

  try {
    const response = await apiGet("/voting/constituencies");

    // Debug logging to see what we're getting
    console.log("🔍 Constituencies API response:", response);
    console.log("🔍 Response success:", response.success);
    console.log("🔍 Response data:", response.data);
    console.log("🔍 Response data.data:", response.data?.data);
    console.log("🔍 Data type:", typeof response.data?.data);
    console.log("🔍 Is Array:", Array.isArray(response.data?.data));

    if (response.success && response.data.success) {
      constituencies = response.data.data;
      console.log("🔍 Setting constituencies to:", constituencies);
      displayConstituencies(constituencies);
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("❌ Error loading constituencies:", error);
    handleApiError(error.message, "loading constituencies");
  } finally {
    hideLoading();
  }
}

/**
 * Display constituencies in the UI
 */
function displayConstituencies(constituenciesList) {
  const container = document.getElementById("constituencyList");
  if (!container) return;

  // Debug logging to see what we're trying to display
  console.log("🔍 displayConstituencies called with:", constituenciesList);
  console.log("🔍 Type:", typeof constituenciesList);
  console.log("🔍 Is Array:", Array.isArray(constituenciesList));

  // Ensure we have an array
  if (!Array.isArray(constituenciesList)) {
    console.log("❌ constituenciesList is not an array! Converting...");
    // Try to handle case where it might be wrapped in another object
    if (constituenciesList && typeof constituenciesList === "object" && constituenciesList.data) {
      constituenciesList = constituenciesList.data;
    } else {
      constituenciesList = [];
    }
  }

  if (constituenciesList.length === 0) {
    container.innerHTML = `
            <div class="loading-placeholder">
                <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--warning-color);"></i>
                <p>No constituencies available at this time.</p>
            </div>
        `;
    return;
  }

  container.innerHTML = constituenciesList
    .map(
      (constituency) => `
        <div class="constituency-item" onclick="selectConstituency(${constituency.id}, '${constituency.name}')">
            <div class="constituency-name">${constituency.name}</div>
            <div class="constituency-details">
                <div><strong>District:</strong> ${constituency.district || "N/A"}</div>
                <div><strong>Registered Voters:</strong> ${constituency.voterCount?.toLocaleString() || "N/A"}</div>
            </div>
        </div>
    `
    )
    .join("");
}

/**
 * Filter constituencies based on search
 */
function filterConstituencies() {
  const searchTerm = document.getElementById("constituencySearch").value.toLowerCase();
  const filtered = constituencies.filter(
    (constituency) =>
      constituency.name.toLowerCase().includes(searchTerm) || (constituency.district && constituency.district.toLowerCase().includes(searchTerm))
  );
  displayConstituencies(filtered);
}

/**
 * Select a constituency
 */
function selectConstituency(id, name) {
  selectedConstituency = { id, name };

  // Update UI
  document.querySelectorAll(".constituency-item").forEach((item) => {
    item.classList.remove("selected");
  });
  event.target.closest(".constituency-item").classList.add("selected");

  // Enable next button
  const nextBtn = document.getElementById("nextToPartiesBtn");
  if (nextBtn) {
    nextBtn.disabled = false;
  }

  console.log("Selected constituency:", selectedConstituency);
}

/**
 * Go to parties selection step
 */
function goToParties() {
  if (!selectedConstituency) {
    showToast("Please select a constituency first", "warning");
    return;
  }

  // Update step indicator
  updateStepIndicator(2);

  // Show parties step
  document.getElementById("constituencyStep").classList.add("hidden");
  document.getElementById("partiesStep").classList.remove("hidden");

  // Update UI with selected constituency
  document.getElementById("selectedConstituencyName").textContent = selectedConstituency.name;
  document.getElementById("constituencyDisplay").textContent = selectedConstituency.name;

  // Load candidates for selected constituency
  loadCandidates(selectedConstituency.id);
}

/**
 * Load candidates for selected constituency
 */
async function loadCandidates(constituencyId) {
  const container = document.getElementById("candidatesList");
  if (container) {
    container.innerHTML = `
            <div class="loading-placeholder">
                <div class="spinner"></div>
                <p>Loading candidates...</p>
            </div>
        `;
  }

  try {
    console.log("Loading candidates for constituency ID:", constituencyId);
    const response = await apiGet(`/voting/constituencies/${constituencyId}/parties`);
    console.log("Candidates API response:", response);

    if (response.success && response.data.success) {
      candidates = response.data.data;
      console.log("Setting candidates variable to:", candidates);
      console.log("candidates variable type:", typeof candidates, "IsArray:", Array.isArray(candidates));
      console.log("About to call displayCandidates with:", candidates);
      displayCandidates(candidates);
    } else {
      throw new Error(response.message || response.error);
    }
  } catch (error) {
    console.error("Error loading candidates:", error);
    const container = document.getElementById("candidatesList");
    if (container) {
      container.innerHTML = `
                <div class="loading-placeholder">
                    <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--error-color);"></i>
                    <p>Error loading candidates. Please try again.</p>
                    <button class="btn btn-primary" onclick="loadCandidates(${constituencyId})">
                        <i class="fas fa-redo"></i> Retry
                    </button>
                </div>
            `;
    }
  }
}

/**
 * Display candidates in the UI
 */
function displayCandidates(candidatesList) {
  const container = document.getElementById("candidatesList");
  if (!container) return;

  console.log("displayCandidates called with:", candidatesList, "Type:", typeof candidatesList, "IsArray:", Array.isArray(candidatesList));

  // Handle case where full API response is passed instead of just the data array
  if (candidatesList && candidatesList.success && candidatesList.data) {
    console.log("Detected full API response, extracting data array");
    candidatesList = candidatesList.data;
  }

  // Ensure candidatesList is an array
  if (!Array.isArray(candidatesList)) {
    console.error("candidatesList is not an array:", candidatesList);
    container.innerHTML = `
            <div class="loading-placeholder">
                <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--error-color);"></i>
                <p>Error: Invalid data format received from server.</p>
            </div>
        `;
    return;
  }

  if (candidatesList.length === 0) {
    container.innerHTML = `
            <div class="loading-placeholder">
                <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--warning-color);"></i>
                <p>No candidates available for this constituency.</p>
            </div>
        `;
    return;
  }

  container.innerHTML = candidatesList
    .map(
      (candidate) => `
        <div class="candidate-item" onclick="selectCandidate(${candidate.id}, '${candidate.name}', ${candidate.partyId}, '${candidate.partyName}', '${
        candidate.partySymbol
      }')">
            <div class="candidate-avatar">
                <i class="fas fa-user"></i>
            </div>
            <div class="candidate-info">
                <div class="candidate-name">${candidate.name}</div>
                <div class="candidate-party">${candidate.partyName} (${candidate.partySymbol})</div>
                <div class="candidate-details">
                    <div class="candidate-qualification">${candidate.qualification || "N/A"}</div>
                    <div class="candidate-bio">${candidate.bio || "No biography available"}</div>
                </div>
            </div>
            <div class="party-logo" style="color: ${candidate.partyColorCode || "#007bff"}">
                <i class="fas fa-flag"></i>
            </div>
        </div>
    `
    )
    .join("");
}

/**
 * Filter candidates based on search
 */
function filterCandidates() {
  const searchTerm = document.getElementById("candidatesSearch").value.toLowerCase();
  const filtered = candidates.filter(
    (candidate) =>
      candidate.name.toLowerCase().includes(searchTerm) ||
      candidate.partyName.toLowerCase().includes(searchTerm) ||
      candidate.qualification.toLowerCase().includes(searchTerm)
  );
  displayCandidates(filtered);
}

/**
 * Select a candidate
 */
function selectCandidate(candidateId, candidateName, partyId, partyName, partySymbol) {
  selectedCandidate = {
    candidateId,
    candidateName,
    partyId,
    partyName,
    partySymbol,
  };

  // Update UI
  document.querySelectorAll(".candidate-item").forEach((item) => {
    item.classList.remove("selected");
  });
  event.target.closest(".candidate-item").classList.add("selected");

  // Enable next button
  const nextBtn = document.getElementById("nextToConfirmBtn");
  if (nextBtn) {
    nextBtn.disabled = false;
  }

  console.log("Selected candidate:", selectedCandidate);
}

/**
 * Go back to constituencies
 */
function goBackToConstituencies() {
  // Update step indicator
  updateStepIndicator(1);

  // Show constituency step
  document.getElementById("partiesStep").classList.add("hidden");
  document.getElementById("constituencyStep").classList.remove("hidden");

  // Reset candidate selection
  selectedCandidate = null;
  const nextBtn = document.getElementById("nextToConfirmBtn");
  if (nextBtn) {
    nextBtn.disabled = true;
  }
}

/**
 * Change constituency (from parties step)
 */
function changeConstituency() {
  goBackToConstituencies();
}

/**
 * Go to confirmation step
 */
function goToConfirmation() {
  if (!selectedCandidate) {
    showToast("Please select a candidate first", "warning");
    return;
  }

  // Update step indicator
  updateStepIndicator(3);

  // Show confirmation step
  document.getElementById("partiesStep").classList.add("hidden");
  document.getElementById("confirmationStep").classList.remove("hidden");

  // Update confirmation details
  document.getElementById("confirmConstituency").textContent = selectedConstituency.name;
  document.getElementById("confirmCandidate").textContent = selectedCandidate.candidateName;
  document.getElementById("confirmParty").textContent = selectedCandidate.partyName + " (" + selectedCandidate.partySymbol + ")";
  document.getElementById("votingTime").textContent = new Date().toLocaleString();
}

/**
 * Go back to parties
 */
function goBackToParties() {
  // Update step indicator
  updateStepIndicator(2);

  // Show parties step
  document.getElementById("confirmationStep").classList.add("hidden");
  document.getElementById("partiesStep").classList.remove("hidden");

  // Reset confirmation checkbox
  const confirmCheckbox = document.getElementById("confirmVote");
  if (confirmCheckbox) {
    confirmCheckbox.checked = false;
  }
  toggleSubmitButton();
}

/**
 * Toggle submit button based on confirmation checkbox
 */
function toggleSubmitButton() {
  const checkbox = document.getElementById("confirmVote");
  const submitBtn = document.getElementById("submitVoteBtn");

  if (checkbox && submitBtn) {
    submitBtn.disabled = !checkbox.checked;
  }
}

/**
 * Submit vote
 */
async function submitVote() {
  if (!selectedConstituency || !selectedCandidate) {
    showToast("Please complete all selections", "error");
    return;
  }

  const confirmCheckbox = document.getElementById("confirmVote");
  if (!confirmCheckbox || !confirmCheckbox.checked) {
    showToast("Please confirm your vote selection", "warning");
    return;
  }

  showLoading("Submitting your vote...");

  try {
    const userId = sessionStorage.getItem("temp_user_id");
    if (!userId) {
      throw new Error("User session not found. Please log in again.");
    }

    const response = await apiPost("/voting/cast-vote", {
      userId: parseInt(userId),
      constituencyId: selectedConstituency.id,
      candidateId: selectedCandidate.candidateId,
      partyId: selectedCandidate.partyId,
    });

    if (response.success && response.data.success) {
      hideLoading();

      // Update success modal with vote details
      const transactionId = response.data.data.transactionId;
      const timestamp = new Date().toISOString();

      document.getElementById("transactionId").textContent = transactionId;
      document.getElementById("voteTimestamp").textContent = formatDateTime(timestamp);
      document.getElementById("receiptConstituency").textContent = selectedConstituency.name;
      document.getElementById("receiptCandidate").textContent = selectedCandidate.candidateName + " (" + selectedCandidate.partyName + ")";

      showModal("successModal");
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Error submitting vote:", error);
    hideLoading();

    if (error.message.includes("already voted")) {
      showModal("alreadyVotedModal");
    } else if (error.message.includes("voting closed")) {
      showModal("votingClosedModal");
    } else {
      const errorModal = document.getElementById("errorModal");
      const errorMessage = document.getElementById("errorMessage");

      if (errorModal && errorMessage) {
        errorMessage.textContent = error.message || "Failed to submit vote. Please try again.";
        showModal("errorModal");
      }
    }
  }
}

/**
 * Update step indicator
 */
function updateStepIndicator(activeStep) {
  for (let i = 1; i <= 3; i++) {
    const stepElement = document.getElementById(`step${i}`);
    if (stepElement) {
      stepElement.classList.remove("active", "completed");

      if (i < activeStep) {
        stepElement.classList.add("completed");
      } else if (i === activeStep) {
        stepElement.classList.add("active");
      }
    }
  }
}

/**
 * Update current time display
 */
function updateCurrentTime() {
  const timeElement = document.getElementById("votingTime");
  if (timeElement) {
    setInterval(() => {
      timeElement.textContent = new Date().toLocaleString();
    }, 1000);
  }
}

/**
 * Redirect to success page
 */
function redirectToSuccess() {
  closeModal("successModal");
  closeModal("alreadyVotedModal");
  redirectTo("success.html");
}

/**
 * Redirect to home page
 */
function redirectToHome() {
  closeModal("votingClosedModal");
  redirectTo("voting.html");
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", function () {
  initializeVotingPage();

  // Add confirmation checkbox event listener
  const confirmCheckbox = document.getElementById("confirmVote");
  if (confirmCheckbox) {
    confirmCheckbox.addEventListener("change", toggleSubmitButton);
  }
});
