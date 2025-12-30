// ===== LOGIN PAGE FUNCTIONALITY =====

/**
 * Handle login form submission
 */
async function handleLogin(event) {
  event.preventDefault();

  const form = document.getElementById("loginForm");
  if (!form) return;

  // Get form data
  const formData = new FormData(form);
  const loginData = {
    email: formData.get("email").trim(),
    password: formData.get("password"),
  };

  // Basic validation
  if (!loginData.email || !loginData.password) {
    showToast("Please enter both email and password", "error");
    return;
  }

  if (!isValidEmail(loginData.email)) {
    showFieldError(document.getElementById("email"), "Please enter a valid email address");
    return;
  }

  // Show loading state
  setFormLoading(form, true);
  showLoading("Signing you in...");

  try {
    // Call login API
    const response = await apiPost("/auth/login", loginData);

    if (response.success) {
      console.log("Login response:", response);
      console.log("Response data:", response.data);

      const rememberMe = document.getElementById("rememberMe")?.checked || false;

      // Check if OTP verification is required
      if (response.data && response.data.data && response.data.data.requiresOTP) {
        console.log("OTP required, storing email:", response.data.data.email);

        if (!response.data.data.email) {
          console.error("No email in response data:", response.data);
          throw new Error("Server error: Missing email in response");
        }

        // Store email for OTP verification
        sessionStorage.setItem("login_email", response.data.data.email);
        sessionStorage.setItem("temp_user_id", response.data.data.userId);

        // Store OTP in localStorage for TEST MODE display
        if (response.data.data.otpCode) {
          localStorage.setItem("test_otp_code", response.data.data.otpCode);
          console.log("✅ Test OTP stored in localStorage:", response.data.data.otpCode);
        }

        hideLoading();
        showModal("otpRequiredModal");
      } else {
        // Complete login
        handleAuthSuccess(response, rememberMe);
        hideLoading();

        // Redirect to voting page
        showToast("Login successful!", "success", 2000);
        setTimeout(() => {
          redirectTo("voting.html");
        }, 2000);
      }
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Login error:", error);
    hideLoading();
    setFormLoading(form, false);

    // Handle specific error types
    if (error.message.includes("Invalid credentials")) {
      showFieldError(document.getElementById("email"), "Invalid email or password");
      showFieldError(document.getElementById("password"), "Invalid email or password");
    } else if (error.message.includes("Account locked")) {
      showModal("lockedModal");
    } else if (error.message.includes("Account not verified")) {
      sessionStorage.setItem("signup_email", loginData.email);
      redirectTo("otp-verification.html");
    } else {
      const errorModal = document.getElementById("errorModal");
      const errorMessage = document.getElementById("errorMessage");

      if (errorModal && errorMessage) {
        errorMessage.textContent = error.message || "Login failed. Please try again.";
        showModal("errorModal");
      }
    }
  }
}

/**
 * Handle forgot password form submission
 */
async function handleForgotPassword(event) {
  event.preventDefault();

  const form = document.getElementById("forgotPasswordForm");
  if (!form) return;

  const email = document.getElementById("resetEmail").value.trim();

  // Validate email
  if (!email) {
    showFieldError(document.getElementById("resetEmail"), "Email is required");
    return;
  }

  if (!isValidEmail(email)) {
    showFieldError(document.getElementById("resetEmail"), "Please enter a valid email address");
    return;
  }

  // Show loading state
  setFormLoading(form, true);
  showLoading("Sending reset code...");

  try {
    // Call forgot password API
    const response = await apiPost("/auth/forgot-password", { email });

    if (response.success) {
      hideLoading();
      setFormLoading(form, false);
      showModal("resetSuccessModal");

      // Clear form
      form.reset();
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Forgot password error:", error);
    hideLoading();
    setFormLoading(form, false);

    if (error.message.includes("not found")) {
      showFieldError(document.getElementById("resetEmail"), "No account found with this email address");
    } else {
      const errorModal = document.getElementById("errorModal");
      const errorMessage = document.getElementById("errorMessage");

      if (errorModal && errorMessage) {
        errorMessage.textContent = error.message || "Failed to send reset code. Please try again.";
        showModal("errorModal");
      }
    }
  }
}

/**
 * Initialize login form validation
 */
function initializeLoginValidation() {
  const loginForm = document.getElementById("loginForm");
  if (!loginForm) return;

  // Email validation
  const email = loginForm.querySelector("#email");
  if (email) {
    email.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value && !isValidEmail(value)) {
        showFieldError(this, "Please enter a valid email address");
      } else {
        clearFieldError(this);
      }
    });

    email.addEventListener("input", function () {
      clearFieldError(this);
    });
  }

  // Password validation
  const password = loginForm.querySelector("#password");
  if (password) {
    password.addEventListener("input", function () {
      clearFieldError(this);
    });
  }
}

/**
 * Initialize forgot password validation
 */
function initializeForgotPasswordValidation() {
  const forgotForm = document.getElementById("forgotPasswordForm");
  if (!forgotForm) return;

  // Email validation
  const resetEmail = forgotForm.querySelector("#resetEmail");
  if (resetEmail) {
    resetEmail.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value && !isValidEmail(value)) {
        showFieldError(this, "Please enter a valid email address");
      } else {
        clearFieldError(this);
      }
    });

    resetEmail.addEventListener("input", function () {
      clearFieldError(this);
    });
  }
}

/**
 * Check if login form is ready for submission
 */
function isLoginFormReady() {
  const email = document.getElementById("email")?.value.trim();
  const password = document.getElementById("password")?.value;

  return email && password && isValidEmail(email);
}

/**
 * Update login button state
 */
function updateLoginButtonState() {
  const loginBtn = document.getElementById("loginBtn");
  if (loginBtn) {
    loginBtn.disabled = !isLoginFormReady();
  }
}

/**
 * Auto-fill email if coming from signup
 */
function autoFillEmail() {
  const signupEmail = sessionStorage.getItem("signup_email");
  if (signupEmail) {
    const emailInput = document.getElementById("email");
    if (emailInput) {
      emailInput.value = signupEmail;

      // Focus password field instead
      const passwordInput = document.getElementById("password");
      if (passwordInput) {
        passwordInput.focus();
      }
    }

    // Clear the stored email
    sessionStorage.removeItem("signup_email");
  }
}

/**
 * Show account creation success message
 */
function showAccountCreatedMessage() {
  const accountCreated = sessionStorage.getItem("account_created");
  if (accountCreated) {
    showToast("Account created successfully! Please sign in with your credentials.", "success", 5000);
    sessionStorage.removeItem("account_created");
  }
}

/**
 * Initialize login page
 */
function initializeLoginPage() {
  // Check authentication status
  checkAuthStatus();

  // Auto-fill email if coming from signup
  autoFillEmail();

  // Show account created message if applicable
  showAccountCreatedMessage();

  // Initialize form validation
  initializeLoginValidation();
  initializeForgotPasswordValidation();

  // Handle login form submission
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", handleLogin);

    // Monitor form changes for button state
    loginForm.addEventListener("input", updateLoginButtonState);

    // Initial button state
    updateLoginButtonState();
  }

  // Handle forgot password form submission
  const forgotPasswordForm = document.getElementById("forgotPasswordForm");
  if (forgotPasswordForm) {
    forgotPasswordForm.addEventListener("submit", handleForgotPassword);
  }

  // Handle OTP required modal redirect
  window.redirectToOTPVerification = function () {
    closeModal("otpRequiredModal");
    redirectTo("otp-verification.html");
  };

  // Handle enter key in password field
  const passwordField = document.getElementById("password");
  if (passwordField) {
    passwordField.addEventListener("keypress", function (event) {
      if (event.key === "Enter" && isLoginFormReady()) {
        loginForm.dispatchEvent(new Event("submit"));
      }
    });
  }

  console.log("Login page initialized");
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", initializeLoginPage);
