// ===== SUCCESS PAGE FUNCTIONALITY =====

/**
 * Initialize success page
 */
function initializeSuccessPage() {
  // Check authentication status
  if (!isAuthenticated()) {
    redirectTo("login.html");
    return;
  }

  // Load vote receipt data
  loadVoteReceipt();

  // Load additional election information
  loadElectionInfo();

  console.log("Success page initialized");
}

/**
 * Load vote receipt data
 */
async function loadVoteReceipt() {
  try {
    // Get user data from storage
    const userData = getUserData();
    console.log("🔍 User Data from storage:", userData);

    // Use userId from userData (backend returns userId, not id)
    const userId = userData ? userData.userId : null;

    console.log("🆔 User ID to use (from userData.userId):", userId);

    if (!userId) {
      console.error("❌ No user ID found in storage");
      console.error("Available userData:", userData);
      showGenericReceipt();
      return;
    }

    // Call API with userId as query parameter
    const apiUrl = `/voting/receipt?userId=${userId}`;
    console.log("📡 Calling API:", apiUrl);
    const response = await apiGet(apiUrl);

    console.log("📨 API Response:", response);
    console.log("✅ Response success:", response.success);
    console.log("📦 Response data:", response.data);

    if (response.success) {
      const receipt = response.data.data;
      console.log("🎟️ Receipt data:", receipt);
      updateReceiptDisplay(receipt);
    } else {
      console.error("❌ Could not load receipt:", response.error);
      showGenericReceipt();
    }
  } catch (error) {
    console.error("💥 Error loading receipt:", error);
    showGenericReceipt();
  }
}

/**
 * Update receipt display with actual data
 */
function updateReceiptDisplay(receipt) {
  console.log("📥 Updating receipt display with:", receipt);

  // Update transaction ID
  const transactionIdElements = document.querySelectorAll("#transactionId, #transactionIdCopy");
  if (transactionIdElements.length > 0 && receipt.transactionId) {
    transactionIdElements.forEach((element) => {
      element.textContent = receipt.transactionId;
      console.log("✅ Updated transaction ID:", receipt.transactionId);
    });
  } else {
    console.warn("⚠️ Transaction ID not found");
  }

  // Update voter ID
  const voterIdElement = document.getElementById("voterId");
  if (voterIdElement && receipt.voterId) {
    voterIdElement.textContent = receipt.voterId;
    console.log("✅ Updated voter ID:", receipt.voterId);
  } else {
    console.warn("⚠️ Voter ID not found");
  }

  // Update constituency
  const constituencyElement = document.getElementById("constituency");
  if (constituencyElement && receipt.constituencyName) {
    constituencyElement.textContent = receipt.constituencyName;
    console.log("✅ Updated constituency:", receipt.constituencyName);
  } else {
    console.warn("⚠️ Constituency not found");
  }

  // Update selected party
  const partyElement = document.getElementById("selectedParty");
  if (partyElement && receipt.partyName) {
    partyElement.textContent = receipt.partyName;
    console.log("✅ Updated party:", receipt.partyName);
  } else {
    console.warn("⚠️ Party name not found");
  }

  // Update candidate name
  const candidateElement = document.getElementById("candidateName");
  if (candidateElement && receipt.candidateName) {
    candidateElement.textContent = receipt.candidateName;
    console.log("✅ Updated candidate name:", receipt.candidateName);
  } else {
    console.warn("⚠️ Candidate name not found");
  }

  // Update timestamp
  const timestampElement = document.getElementById("voteTimestamp");
  if (timestampElement && receipt.timestamp) {
    const formattedTime = formatDateTime(receipt.timestamp);
    timestampElement.textContent = formattedTime;
    console.log("✅ Updated timestamp from:", receipt.timestamp, "to:", formattedTime);
  } else {
    console.warn("⚠️ Timestamp not found");
  }
}

/**
 * Show generic receipt when actual data is not available
 */
function showGenericReceipt() {
  const transactionId = "TX" + Date.now().toString(36).toUpperCase();
  const timestamp = new Date();

  // Update with generic data
  const transactionIdElements = document.querySelectorAll("#transactionId, #transactionIdCopy");
  transactionIdElements.forEach((element) => {
    element.textContent = transactionId;
  });

  const timestampElement = document.getElementById("voteTimestamp");
  if (timestampElement) {
    timestampElement.textContent = formatDateTime(timestamp);
  }

  // Hide elements that don't have data
  const elementsToHide = ["#voterId", "#constituency", "#selectedParty"];
  elementsToHide.forEach((selector) => {
    const element = document.querySelector(selector);
    if (element && element.textContent === "Loading...") {
      element.textContent = "N/A";
    }
  });
}

/**
 * Load additional election information
 */
async function loadElectionInfo() {
  try {
    const response = await apiGet("/voting/election-info");

    if (response.success) {
      const info = response.data;
      updateElectionInfo(info);
    } else {
      console.warn("Could not load election info:", response.error);
      showDefaultElectionInfo();
    }
  } catch (error) {
    console.error("Error loading election info:", error);
    showDefaultElectionInfo();
  }
}

/**
 * Update election information display
 */
function updateElectionInfo(info) {
  // Update election period
  const electionPeriodElement = document.getElementById("electionPeriod");
  if (electionPeriodElement && info.startDate && info.endDate) {
    const startDate = new Date(info.startDate).toLocaleDateString();
    const endDate = new Date(info.endDate).toLocaleDateString();
    electionPeriodElement.textContent = `${startDate} - ${endDate}`;
  }

  // Update total voters
  const totalVotersElement = document.getElementById("totalVoters");
  if (totalVotersElement && info.totalRegisteredVoters) {
    totalVotersElement.textContent = info.totalRegisteredVoters.toLocaleString();
  }

  // Update turnout percentage
  const turnoutElement = document.getElementById("turnoutPercentage");
  if (turnoutElement && info.turnoutPercentage !== undefined) {
    turnoutElement.textContent = `${info.turnoutPercentage}%`;
  }
}

/**
 * Show default election information
 */
function showDefaultElectionInfo() {
  const electionPeriodElement = document.getElementById("electionPeriod");
  const totalVotersElement = document.getElementById("totalVoters");
  const turnoutElement = document.getElementById("turnoutPercentage");

  if (electionPeriodElement) electionPeriodElement.textContent = "Information not available";
  if (totalVotersElement) totalVotersElement.textContent = "Information not available";
  if (turnoutElement) turnoutElement.textContent = "Information not available";
}

/**
 * Copy transaction ID to clipboard
 */
async function copyTransactionId() {
  const transactionIdElement = document.getElementById("transactionId");
  if (!transactionIdElement) return;

  const transactionId = transactionIdElement.textContent;

  const success = await copyToClipboard(transactionId);

  if (success) {
    showToast("Transaction ID copied to clipboard!", "success");
  } else {
    showToast("Failed to copy to clipboard", "error");
  }
}

/**
 * Download receipt as PDF or text file
 */
function downloadReceipt() {
  const receiptData = gatherReceiptData();

  // Create text content for receipt
  const receiptText = `
VOTE RECEIPT - ONLINE VOTING SYSTEM
=========================================

Transaction ID: ${receiptData.transactionId}
Voter ID: ${receiptData.voterId}
Constituency: ${receiptData.constituency}
Selected Party: ${receiptData.selectedParty}
Vote Cast Time: ${receiptData.timestamp}
Status: Confirmed & Recorded

=========================================
This receipt serves as proof of your vote submission.
Keep this receipt for your records.

Online Voting System
${new Date().getFullYear()}
`;

  // Create and download file
  const blob = new Blob([receiptText], { type: "text/plain" });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = `vote-receipt-${receiptData.transactionId}.txt`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);

  showToast("Receipt downloaded successfully!", "success");
}

/**
 * Email receipt to user - REMOVED
 * This feature is not implemented in the backend
 */
// Commented out - endpoint /api/voting/email-receipt does not exist
/*
async function emailReceipt() {
  showLoading("Sending receipt to your email...");

  try {
    const response = await apiPost("/voting/email-receipt");

    if (response.success) {
      hideLoading();
      showModal("emailSuccessModal");
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Error emailing receipt:", error);
    hideLoading();
    showToast("Failed to email receipt. Please try again.", "error");
  }
}
*/

/**
 * Gather receipt data from DOM
 */
function gatherReceiptData() {
  return {
    transactionId: document.getElementById("transactionId")?.textContent || "N/A",
    voterId: document.getElementById("voterId")?.textContent || "N/A",
    constituency: document.getElementById("constituency")?.textContent || "N/A",
    selectedParty: document.getElementById("selectedParty")?.textContent || "N/A",
    timestamp: document.getElementById("voteTimestamp")?.textContent || new Date().toLocaleString(),
  };
}

/**
 * Share success on social media
 */
function shareSuccess() {
  showModal("shareModal");
}

/**
 * Share on Twitter
 */
function shareOnTwitter() {
  const text = "I just cast my vote in the online voting system! Every vote counts for democracy. #VoteOnline #Democracy #YourVoteCounts";
  const url = `https://twitter.com/intent/tweet?text=${encodeURIComponent(text)}`;
  window.open(url, "_blank", "width=600,height=400");
  closeModal("shareModal");
}

/**
 * Share on Facebook
 */
function shareOnFacebook() {
  const url = "https://www.facebook.com/sharer/sharer.php?u=" + encodeURIComponent(window.location.href);
  window.open(url, "_blank", "width=600,height=400");
  closeModal("shareModal");
}

/**
 * Share on WhatsApp
 */
function shareOnWhatsApp() {
  const text = "I just cast my vote in the online voting system! Every vote counts for democracy.";
  const url = `https://wa.me/?text=${encodeURIComponent(text)}`;
  window.open(url, "_blank");
  closeModal("shareModal");
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", initializeSuccessPage);
