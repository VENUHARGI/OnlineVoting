// ===== COMMON UTILITIES AND SHARED FUNCTIONS =====

// Application configuration
const APP_CONFIG = {
  API_BASE_URL: "/voting/api",
  TIMEOUT_DURATION: 30000,
  OTP_EXPIRY_MINUTES: 10,
  PASSWORD_MIN_LENGTH: 8,
  RESEND_COOLDOWN_SECONDS: 60,
};

// ===== API UTILITY FUNCTIONS =====

/**
 * Makes an HTTP request with proper error handling
 * @param {string} url - The API endpoint
 * @param {Object} options - Fetch options (method, headers, body, etc.)
 * @returns {Promise<Object>} - The response data or error
 */
async function apiRequest(url, options = {}) {
  const defaultOptions = {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    timeout: APP_CONFIG.TIMEOUT_DURATION,
  };

  const requestOptions = { ...defaultOptions, ...options };

  // Add authentication token if available
  const token = getAuthToken();
  if (token) {
    requestOptions.headers.Authorization = `Bearer ${token}`;
  }

  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), requestOptions.timeout);

    const response = await fetch(APP_CONFIG.API_BASE_URL + url, {
      ...requestOptions,
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    console.log("🚀 Raw Response Debug:");
    console.log("Response status:", response.status);
    console.log("Response statusText:", response.statusText);
    console.log("Response headers:", [...response.headers.entries()]);

    // Clone response to read body as text first for debugging
    const responseClone = response.clone();
    const responseText = await responseClone.text();
    console.log("Response body text:", responseText);
    console.log("Response body length:", responseText.length);

    // Try to parse JSON
    let data;
    try {
      data = await response.json();
    } catch (jsonError) {
      console.error("JSON parsing failed:", jsonError);
      console.error("Response text was:", responseText);
      throw new Error(`Invalid JSON response: ${responseText}`);
    }

    console.log("🔍 API Response Debug:");
    console.log("Raw response data:", data);
    console.log("Response ok:", response.ok);
    console.log("Response status:", response.status);

    if (!response.ok) {
      throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }

    return { success: true, data, status: response.status };
  } catch (error) {
    console.error("API Request Error:", error);

    if (error.name === "AbortError") {
      return { success: false, error: "Request timeout. Please try again." };
    }

    if (error.message.includes("Failed to fetch")) {
      return { success: false, error: "Network error. Please check your connection." };
    }

    return { success: false, error: error.message };
  }
}

/**
 * GET request helper
 */
async function apiGet(url, options = {}) {
  return apiRequest(url, { ...options, method: "GET" });
}

/**
 * POST request helper
 */
async function apiPost(url, data = null, options = {}) {
  const postOptions = {
    ...options,
    method: "POST",
    body: data ? JSON.stringify(data) : null,
  };
  return apiRequest(url, postOptions);
}

/**
 * PUT request helper
 */
async function apiPut(url, data = null, options = {}) {
  const putOptions = {
    ...options,
    method: "PUT",
    body: data ? JSON.stringify(data) : null,
  };
  return apiRequest(url, putOptions);
}

/**
 * DELETE request helper
 */
async function apiDelete(url, options = {}) {
  return apiRequest(url, { ...options, method: "DELETE" });
}

// ===== AUTHENTICATION UTILITIES =====

/**
 * Get authentication token from storage
 */
function getAuthToken() {
  return localStorage.getItem("auth_token") || sessionStorage.getItem("auth_token");
}

/**
 * Set authentication token in storage
 */
function setAuthToken(token, remember = false) {
  if (remember) {
    localStorage.setItem("auth_token", token);
    sessionStorage.removeItem("auth_token");
  } else {
    sessionStorage.setItem("auth_token", token);
    localStorage.removeItem("auth_token");
  }
}

/**
 * Remove authentication token from storage
 */
function removeAuthToken() {
  localStorage.removeItem("auth_token");
  sessionStorage.removeItem("auth_token");
  localStorage.removeItem("user_authenticated");
  sessionStorage.removeItem("user_authenticated");
}

/**
 * Check if user is authenticated
 */
function isAuthenticated() {
  // Simple session-based authentication without JWT
  return !!sessionStorage.getItem("user_authenticated") || !!localStorage.getItem("user_authenticated");
}

/**
 * Get user data from storage
 */
function getUserData() {
  const userData = localStorage.getItem("user_data") || sessionStorage.getItem("user_data");
  return userData ? JSON.parse(userData) : null;
}

/**
 * Set user data in storage
 */
function setUserData(userData, remember = false) {
  const dataString = JSON.stringify(userData);
  if (remember) {
    localStorage.setItem("user_data", dataString);
    sessionStorage.removeItem("user_data");
  } else {
    sessionStorage.setItem("user_data", dataString);
    localStorage.removeItem("user_data");
  }
}

/**
 * Remove user data from storage
 */
function removeUserData() {
  localStorage.removeItem("user_data");
  sessionStorage.removeItem("user_data");
}

/**
 * Logout user and redirect
 */
function logout() {
  removeAuthToken();
  removeUserData();
  redirectTo("login.html");
}

// ===== NAVIGATION UTILITIES =====

/**
 * Redirect to a specific page
 */
function redirectTo(url) {
  window.location.href = url;
}

/**
 * Go back to previous page
 */
function goBack() {
  window.history.back();
}

/**
 * Redirect to home page
 */
function redirectToHome() {
  redirectTo("voting.html");
}

/**
 * Redirect to dashboard
 */
function redirectToDashboard() {
  redirectTo("voting.html");
}

/**
 * Redirect to OTP verification
 */
function redirectToOTPVerification() {
  redirectTo("otp-verification.html");
}

/**
 * Redirect to success page
 */
function redirectToSuccess() {
  redirectTo("success.html");
}

// ===== VALIDATION UTILITIES =====

/**
 * Validate email format
 */
function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate phone number format
 */
function isValidPhoneNumber(phone) {
  const phoneRegex = /^[\+]?[(]?[\d\s\-\(\)]{10,}$/;
  return phoneRegex.test(phone);
}

/**
 * Check password strength
 */
function checkPasswordStrength(password) {
  let score = 0;
  let feedback = [];

  if (password.length >= APP_CONFIG.PASSWORD_MIN_LENGTH) score++;
  else feedback.push(`At least ${APP_CONFIG.PASSWORD_MIN_LENGTH} characters`);

  if (/[a-z]/.test(password)) score++;
  else feedback.push("Lowercase letter");

  if (/[A-Z]/.test(password)) score++;
  else feedback.push("Uppercase letter");

  if (/\d/.test(password)) score++;
  else feedback.push("Number");

  if (/[^\w\s]/.test(password)) score++;
  else feedback.push("Special character");

  const strength = {
    0: { level: "weak", label: "Very Weak" },
    1: { level: "weak", label: "Weak" },
    2: { level: "fair", label: "Fair" },
    3: { level: "good", label: "Good" },
    4: { level: "good", label: "Good" },
    5: { level: "strong", label: "Strong" },
  };

  return {
    score,
    ...strength[score],
    feedback: feedback.join(", "),
  };
}

/**
 * Validate required fields in a form
 */
function validateRequiredFields(form) {
  const requiredFields = form.querySelectorAll("[required]");
  let isValid = true;

  requiredFields.forEach((field) => {
    if (!field.value.trim()) {
      showFieldError(field, "This field is required");
      isValid = false;
    } else {
      clearFieldError(field);
    }
  });

  return isValid;
}

// ===== UI UTILITIES =====

/**
 * Show loading overlay
 */
function showLoading(message = "Loading...") {
  const overlay = document.getElementById("loadingOverlay");
  const messageElement = document.getElementById("loadingMessage");

  if (overlay) {
    if (messageElement) {
      messageElement.textContent = message;
    }
    overlay.classList.remove("hidden");
  }
}

/**
 * Hide loading overlay
 */
function hideLoading() {
  const overlay = document.getElementById("loadingOverlay");
  if (overlay) {
    overlay.classList.add("hidden");
  }
}

/**
 * Show modal
 */
function showModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove("hidden");
    document.body.style.overflow = "hidden";

    // Focus trap for accessibility
    const focusableElements = modal.querySelectorAll("button, input, textarea, select, a[href]");
    if (focusableElements.length > 0) {
      focusableElements[0].focus();
    }
  }
}

/**
 * Close modal
 */
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add("hidden");
    document.body.style.overflow = "";
  }
}

/**
 * Show toast notification
 */
function showToast(message, type = "success", duration = 3000) {
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.innerHTML = `
        <i class="fas ${type === "success" ? "fa-check" : "fa-exclamation-triangle"}"></i>
        ${message}
    `;

  document.body.appendChild(toast);

  // Show toast
  setTimeout(() => toast.classList.remove("hidden"), 100);

  // Hide toast
  setTimeout(() => {
    toast.classList.add("hidden");
    setTimeout(() => document.body.removeChild(toast), 300);
  }, duration);
}

/**
 * Show field error
 */
function showFieldError(field, message) {
  const formGroup = field.closest(".form-group");
  if (formGroup) {
    formGroup.classList.add("error");

    const errorElement = formGroup.querySelector(".error-message");
    if (errorElement) {
      errorElement.textContent = message;
    }
  }
}

/**
 * Clear field error
 */
function clearFieldError(field) {
  const formGroup = field.closest(".form-group");
  if (formGroup) {
    formGroup.classList.remove("error");

    const errorElement = formGroup.querySelector(".error-message");
    if (errorElement) {
      errorElement.textContent = "";
    }
  }
}

/**
 * Clear all form errors
 */
function clearFormErrors(form) {
  const errorElements = form.querySelectorAll(".error-message");
  const formGroups = form.querySelectorAll(".form-group.error");

  errorElements.forEach((element) => (element.textContent = ""));
  formGroups.forEach((group) => group.classList.remove("error"));
}

/**
 * Set form loading state
 */
function setFormLoading(form, loading = true) {
  const submitBtn = form.querySelector('button[type="submit"]');
  const inputs = form.querySelectorAll("input, textarea, select, button");

  if (loading) {
    form.classList.add("form-loading");
    inputs.forEach((input) => (input.disabled = true));
    if (submitBtn) {
      submitBtn.setAttribute("data-original-text", submitBtn.textContent);
    }
  } else {
    form.classList.remove("form-loading");
    inputs.forEach((input) => (input.disabled = false));
    if (submitBtn) {
      const originalText = submitBtn.getAttribute("data-original-text");
      if (originalText) {
        submitBtn.textContent = originalText;
        submitBtn.removeAttribute("data-original-text");
      }
    }
  }
}

// ===== UTILITY FUNCTIONS =====

/**
 * Debounce function calls
 */
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * Format date and time
 */
function formatDateTime(date) {
  return new Date(date).toLocaleString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/**
 * Format time duration
 */
function formatDuration(seconds) {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds.toString().padStart(2, "0")}`;
}

/**
 * Generate random ID
 */
function generateId() {
  return Math.random().toString(36).substring(2) + Date.now().toString(36);
}

/**
 * Copy text to clipboard
 */
async function copyToClipboard(text) {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch (err) {
    // Fallback for older browsers
    const textArea = document.createElement("textarea");
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    try {
      document.execCommand("copy");
      document.body.removeChild(textArea);
      return true;
    } catch (err) {
      document.body.removeChild(textArea);
      return false;
    }
  }
}

/**
 * Handle API errors globally
 */
function handleApiError(error, context = "") {
  console.error(`API Error in ${context}:`, error);

  // Handle specific error types
  if (error.includes("Unauthorized") || error.includes("401")) {
    logout();
    return;
  }

  if (error.includes("Forbidden") || error.includes("403")) {
    showModal("permissionModal");
    return;
  }

  if (error.includes("Network")) {
    showModal("networkErrorModal");
    return;
  }

  // Show generic error
  const errorModal = document.getElementById("errorModal");
  if (errorModal) {
    const errorMessage = errorModal.querySelector("#errorMessage");
    if (errorMessage) {
      errorMessage.textContent = error;
    }
    showModal("errorModal");
  } else {
    showToast(error, "error");
  }
}

// ===== INITIALIZATION =====

/**
 * Initialize common functionality when DOM loads
 */
document.addEventListener("DOMContentLoaded", function () {
  // Initialize modal close handlers
  document.addEventListener("click", function (event) {
    // Close modal when clicking outside
    if (event.target.classList.contains("modal")) {
      const modalId = event.target.id;
      if (modalId) {
        closeModal(modalId);
      }
    }

    // Close modal when clicking close button
    if (event.target.classList.contains("modal-close") || event.target.closest(".modal-close")) {
      const modal = event.target.closest(".modal");
      if (modal) {
        closeModal(modal.id);
      }
    }
  });

  // Handle escape key for modals
  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      const visibleModal = document.querySelector(".modal:not(.hidden)");
      if (visibleModal) {
        closeModal(visibleModal.id);
      }
    }
  });

  // Initialize user info if authenticated
  if (isAuthenticated()) {
    const userData = getUserData();
    const userNameElements = document.querySelectorAll("#userName");
    if (userData && userNameElements.length > 0) {
      userNameElements.forEach((element) => {
        element.textContent = userData.firstName + " " + userData.lastName;
      });
    }
  }

  console.log("Common utilities initialized");
});
