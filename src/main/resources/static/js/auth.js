// ===== AUTHENTICATION FUNCTIONS =====

/**
 * Toggle password visibility
 */
function togglePassword(inputId) {
  const passwordInput = document.getElementById(inputId);
  const toggleIcon = document.getElementById(inputId + "ToggleIcon");

  if (passwordInput && toggleIcon) {
    if (passwordInput.type === "password") {
      passwordInput.type = "text";
      toggleIcon.className = "fas fa-eye-slash";
    } else {
      passwordInput.type = "password";
      toggleIcon.className = "fas fa-eye";
    }
  }
}

/**
 * Update password strength indicator
 */
function updatePasswordStrength(password, containerId = "passwordStrength") {
  const container = document.getElementById(containerId);
  if (!container) return;

  const strength = checkPasswordStrength(password);

  // Create or update strength bars
  let strengthBars = container.querySelectorAll(".strength-bar");
  if (strengthBars.length === 0) {
    container.innerHTML = `
            <div class="strength-bars" style="display: flex; gap: 0.25rem; margin-bottom: 0.5rem;">
                <div class="strength-bar"></div>
                <div class="strength-bar"></div>
                <div class="strength-bar"></div>
                <div class="strength-bar"></div>
                <div class="strength-bar"></div>
            </div>
            <div class="password-strength-text"></div>
        `;
    strengthBars = container.querySelectorAll(".strength-bar");
  }

  const strengthText = container.querySelector(".password-strength-text");

  // Update strength bars
  strengthBars.forEach((bar, index) => {
    bar.className = "strength-bar";
    if (index < strength.score) {
      bar.classList.add(strength.level);
    }
  });

  // Update strength text
  if (strengthText) {
    strengthText.textContent = password ? `${strength.label}${strength.feedback ? ": " + strength.feedback : ""}` : "";
    strengthText.className = `password-strength-text ${strength.level}`;
  }
}

/**
 * Initialize password strength monitoring
 */
function initializePasswordStrength(passwordId = "password", containerId = "passwordStrength") {
  const passwordInput = document.getElementById(passwordId);
  if (passwordInput) {
    passwordInput.addEventListener("input", function () {
      updatePasswordStrength(this.value, containerId);
    });
  }
}

/**
 * Validate password confirmation
 */
function validatePasswordConfirmation(passwordId = "password", confirmId = "confirmPassword") {
  const password = document.getElementById(passwordId);
  const confirmPassword = document.getElementById(confirmId);
  const confirmError = document.getElementById(confirmId + "Error");

  if (password && confirmPassword) {
    if (confirmPassword.value && password.value !== confirmPassword.value) {
      if (confirmError) {
        confirmError.textContent = "Passwords do not match";
      }
      showFieldError(confirmPassword, "Passwords do not match");
      return false;
    } else {
      if (confirmError) {
        confirmError.textContent = "";
      }
      clearFieldError(confirmPassword);
      return true;
    }
  }
  return true;
}

/**
 * Initialize password confirmation validation
 */
function initializePasswordConfirmation(passwordId = "password", confirmId = "confirmPassword") {
  const confirmPassword = document.getElementById(confirmId);
  if (confirmPassword) {
    confirmPassword.addEventListener("input", function () {
      validatePasswordConfirmation(passwordId, confirmId);
    });
  }
}

/**
 * Validate form fields with specific rules
 */
function validateAuthForm(formId) {
  const form = document.getElementById(formId);
  if (!form) return false;

  let isValid = true;

  // Clear previous errors
  clearFormErrors(form);

  // Validate email
  const email = form.querySelector("#email");
  if (email && email.value) {
    if (!isValidEmail(email.value)) {
      showFieldError(email, "Please enter a valid email address");
      isValid = false;
    }
  }

  // Validate phone number if present
  const phone = form.querySelector("#phoneNumber");
  if (phone && phone.value) {
    if (!isValidPhoneNumber(phone.value)) {
      showFieldError(phone, "Please enter a valid phone number");
      isValid = false;
    }
  }

  // Validate password strength
  const password = form.querySelector("#password");
  if (password && password.value) {
    const strength = checkPasswordStrength(password.value);
    if (strength.score < 3) {
      showFieldError(password, "Password is too weak. " + strength.feedback);
      isValid = false;
    }
  }

  // Validate password confirmation
  const confirmPassword = form.querySelector("#confirmPassword");
  if (confirmPassword && confirmPassword.value) {
    if (!validatePasswordConfirmation()) {
      isValid = false;
    }
  }

  // Validate terms acceptance
  const termsAccept = form.querySelector("#termsAccept");
  if (termsAccept && !termsAccept.checked) {
    showFieldError(termsAccept, "You must accept the terms and conditions");
    isValid = false;
  }

  // Validate required fields
  if (!validateRequiredFields(form)) {
    isValid = false;
  }

  return isValid;
}

/**
 * Check authentication status and redirect if needed
 */
function checkAuthStatus() {
  const currentPage = window.location.pathname.split("/").pop();
  const publicPages = ["login.html", "signup.html", "otp-verification.html", "error.html"];

  if (!isAuthenticated() && !publicPages.includes(currentPage)) {
    redirectTo("login.html");
    return false;
  }

  if (isAuthenticated() && (currentPage === "login.html" || currentPage === "signup.html")) {
    redirectTo("voting.html");
    return false;
  }

  return true;
}

/**
 * Handle successful authentication
 */
function handleAuthSuccess(response, rememberMe = false) {
  // Extract user data from the nested response structure
  const user = response.data && response.data.data ? response.data.data : response.data;

  // Store authentication data
  setUserData(user, rememberMe);

  // Update UI if on the same page
  const userNameElements = document.querySelectorAll("#userName");
  if (userNameElements.length > 0) {
    userNameElements.forEach((element) => {
      element.textContent = user.firstName + " " + user.lastName;
    });
  }

  console.log("Authentication successful for user:", user.email);
}

/**
 * Show forgot password form
 */
function showForgotPassword() {
  const loginForm = document.getElementById("loginForm");
  const forgotForm = document.getElementById("forgotPasswordForm");

  if (loginForm && forgotForm) {
    loginForm.classList.add("hidden");
    forgotForm.classList.remove("hidden");

    // Focus on reset email input
    const resetEmail = document.getElementById("resetEmail");
    if (resetEmail) {
      resetEmail.focus();
    }
  }
}

/**
 * Show login form
 */
function showLoginForm() {
  const loginForm = document.getElementById("loginForm");
  const forgotForm = document.getElementById("forgotPasswordForm");

  if (loginForm && forgotForm) {
    loginForm.classList.remove("hidden");
    forgotForm.classList.add("hidden");

    // Focus on email input
    const email = document.getElementById("email");
    if (email) {
      email.focus();
    }
  }
}

/**
 * Initialize authentication page
 */
function initializeAuthPage() {
  // Check if user is already authenticated
  checkAuthStatus();

  // Initialize password strength monitoring
  initializePasswordStrength();

  // Initialize password confirmation validation
  initializePasswordConfirmation();

  // Add real-time validation
  const inputs = document.querySelectorAll('input[type="email"], input[type="tel"]');
  inputs.forEach((input) => {
    input.addEventListener("blur", function () {
      if (this.value) {
        if (this.type === "email" && !isValidEmail(this.value)) {
          showFieldError(this, "Please enter a valid email address");
        } else if (this.type === "tel" && !isValidPhoneNumber(this.value)) {
          showFieldError(this, "Please enter a valid phone number");
        } else {
          clearFieldError(this);
        }
      }
    });
  });

  console.log("Authentication page initialized");
}

/**
 * Contact support
 */
function contactSupport() {
  // You can implement actual support contact logic here
  showToast("Please contact support at support@votingsystem.com or call 1-800-VOTE-HELP", "info", 5000);
}

/**
 * Show FAQ
 */
function showFAQ() {
  // You can implement FAQ modal or redirect to FAQ page
  showToast("FAQ section will be available soon", "info", 3000);
}

/**
 * Report issue
 */
function reportIssue() {
  const reportModal = document.getElementById("reportModal");
  if (reportModal) {
    showModal("reportModal");
  } else {
    showToast("Issue reporting will be available soon", "info", 3000);
  }
}

/**
 * Live chat
 */
function liveChat() {
  // You can implement live chat functionality here
  showToast("Live chat will be available during business hours", "info", 3000);
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", function () {
  // Only initialize auth page functionality on auth pages
  const currentPage = window.location.pathname.split("/").pop();
  const authPages = ["login.html", "signup.html", "otp-verification.html"];

  if (authPages.includes(currentPage)) {
    initializeAuthPage();
  }

  console.log("Authentication utilities initialized");
});
