// ===== ERROR PAGE FUNCTIONALITY =====

/**
 * Initialize error page
 */
function initializeErrorPage() {
  // Get error details from URL parameters or session storage
  const errorDetails = getErrorDetails();

  // Display error details
  displayError(errorDetails);

  // Set current timestamp
  updateTimestamp();

  // Initialize system status check
  checkSystemStatus();

  console.log("Error page initialized with:", errorDetails);
}

/**
 * Get error details from various sources
 */
function getErrorDetails() {
  const urlParams = new URLSearchParams(window.location.search);
  const sessionError = sessionStorage.getItem("last_error");

  let errorDetails = {
    code: urlParams.get("code") || sessionStorage.getItem("error_code") || null,
    message: urlParams.get("message") || sessionStorage.getItem("error_message") || null,
    type: urlParams.get("type") || sessionStorage.getItem("error_type") || "general",
  };

  // Try to get from session storage if URL params are empty
  if (sessionError) {
    try {
      const parsed = JSON.parse(sessionError);
      errorDetails = { ...errorDetails, ...parsed };
    } catch (e) {
      errorDetails.message = sessionError;
    }
  }

  // Clear session error to prevent showing same error repeatedly
  sessionStorage.removeItem("last_error");
  sessionStorage.removeItem("error_code");
  sessionStorage.removeItem("error_message");
  sessionStorage.removeItem("error_type");

  return errorDetails;
}

/**
 * Display error details on the page
 */
function displayError(errorDetails) {
  const { code, message, type } = errorDetails;

  // Update error title and message
  const titleElement = document.getElementById("errorTitle");
  const messageElement = document.getElementById("errorMessage");
  const iconElement = document.getElementById("errorIcon");

  // Update error icon based on type
  if (iconElement) {
    const iconClass = getErrorIcon(type);
    iconElement.innerHTML = `<i class="fas ${iconClass}"></i>`;
  }

  // Update title based on error type
  if (titleElement) {
    titleElement.textContent = getErrorTitle(type);
  }

  // Update error message
  if (messageElement) {
    messageElement.textContent = message || getDefaultErrorMessage(type);
  }

  // Show error code if available
  if (code) {
    const codeSection = document.getElementById("errorCodeSection");
    const codeElement = document.getElementById("errorCode");

    if (codeSection && codeElement) {
      codeElement.textContent = code;
      codeSection.style.display = "block";
    }
  }

  // Show specific error type information
  showErrorTypeInfo(type);
}

/**
 * Get appropriate icon for error type
 */
function getErrorIcon(type) {
  const icons = {
    network: "fa-wifi",
    server: "fa-server",
    auth: "fa-user-lock",
    permission: "fa-ban",
    notFound: "fa-search",
    validation: "fa-exclamation-circle",
    session: "fa-clock",
    general: "fa-exclamation-triangle",
  };

  return icons[type] || icons.general;
}

/**
 * Get appropriate title for error type
 */
function getErrorTitle(type) {
  const titles = {
    network: "Connection Problem",
    server: "Server Error",
    auth: "Authentication Required",
    permission: "Access Denied",
    notFound: "Page Not Found",
    validation: "Invalid Input",
    session: "Session Expired",
    general: "Something Went Wrong",
  };

  return titles[type] || titles.general;
}

/**
 * Get default error message for type
 */
function getDefaultErrorMessage(type) {
  const messages = {
    network: "Unable to connect to the server. Please check your internet connection.",
    server: "The server is currently unavailable. Please try again later.",
    auth: "You need to sign in to access this resource.",
    permission: "You do not have permission to access this resource.",
    notFound: "The page you are looking for could not be found.",
    validation: "The information provided is not valid.",
    session: "Your session has expired. Please sign in again.",
    general: "An unexpected error occurred. Please try again.",
  };

  return messages[type] || messages.general;
}

/**
 * Show specific error type information
 */
function showErrorTypeInfo(type) {
  // Hide all error type sections
  const errorTypes = document.querySelectorAll(".error-type");
  errorTypes.forEach((section) => {
    section.style.display = "none";
  });

  // Show relevant error type section
  const relevantSection = document.getElementById(type + "Error");
  if (relevantSection) {
    relevantSection.style.display = "flex";
  }

  // Update action buttons based on error type
  updateActionButtons(type);
}

/**
 * Update action buttons based on error type
 */
function updateActionButtons(type) {
  const retryBtn = document.getElementById("retryBtn");
  const backBtn = document.getElementById("backBtn");
  const homeBtn = document.getElementById("homeBtn");
  const supportBtn = document.getElementById("supportBtn");

  // Show/hide buttons based on error type
  switch (type) {
    case "network":
    case "server":
      if (retryBtn) retryBtn.style.display = "inline-flex";
      break;
    case "auth":
      if (retryBtn) {
        retryBtn.textContent = "Sign In";
        retryBtn.onclick = () => redirectTo("login.html");
      }
      break;
    case "session":
      if (retryBtn) {
        retryBtn.textContent = "Sign In Again";
        retryBtn.onclick = () => {
          removeAuthToken();
          removeUserData();
          redirectTo("login.html");
        };
      }
      break;
  }
}

/**
 * Update timestamp display
 */
function updateTimestamp() {
  const timestampElement = document.getElementById("timestamp");
  if (timestampElement) {
    timestampElement.textContent = new Date().toLocaleString();
  }
}

/**
 * Check system status
 */
async function checkSystemStatus() {
  try {
    // Check various system components
    const checks = [checkService("auth", "/auth/health"), checkService("voting", "/voting/health"), checkService("database", "/system/health/database")];

    const results = await Promise.allSettled(checks);
    updateSystemStatus(results);
  } catch (error) {
    console.log("System status check failed:", error);
    // Don't show error for status check failure
  }
}

/**
 * Check individual service status
 */
async function checkService(name, endpoint) {
  try {
    const response = await fetch(APP_CONFIG.API_BASE_URL + endpoint, {
      method: "GET",
      timeout: 5000,
    });
    return { name, status: response.ok ? "good" : "error" };
  } catch (error) {
    return { name, status: "error" };
  }
}

/**
 * Update system status indicators
 */
function updateSystemStatus(results) {
  const statusMapping = {
    auth: "Authentication Service",
    voting: "Voting Service",
    database: "Database Service",
  };

  results.forEach((result, index) => {
    if (result.status === "fulfilled") {
      const { name, status } = result.value;
      const indicatorElement = document.querySelector(`[data-service="${name}"] .status-indicator`);

      if (indicatorElement) {
        indicatorElement.className = `status-indicator status-${status}`;
      }
    }
  });
}

/**
 * Try again (reload page or retry action)
 */
function tryAgain() {
  window.location.reload();
}

/**
 * Go to home page
 */
function goHome() {
  if (isAuthenticated()) {
    redirectTo("voting.html");
  } else {
    redirectTo("login.html");
  }
}

/**
 * Show FAQ modal or redirect
 */
function showFAQ() {
  showToast("FAQ section will be available soon", "info", 3000);
}

/**
 * Report issue
 */
function reportIssue() {
  showModal("reportModal");
}

/**
 * Live chat
 */
function liveChat() {
  showToast("Live chat will be available during business hours", "info", 3000);
}

/**
 * Handle report form submission
 */
function handleReportSubmission(event) {
  event.preventDefault();

  const form = document.getElementById("reportForm");
  if (!form) return;

  const formData = new FormData(form);
  const reportData = {
    type: formData.get("issueType"),
    description: formData.get("issueDescription"),
    url: window.location.href,
    userAgent: navigator.userAgent,
    timestamp: new Date().toISOString(),
  };

  // Simulate report submission
  showLoading("Submitting report...");

  setTimeout(() => {
    hideLoading();
    closeModal("reportModal");
    showToast("Thank you for your report. Our team will investigate the issue.", "success", 5000);
    form.reset();
  }, 1500);
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", function () {
  initializeErrorPage();

  // Handle report form submission
  const reportForm = document.getElementById("reportForm");
  if (reportForm) {
    reportForm.addEventListener("submit", handleReportSubmission);
  }
});
