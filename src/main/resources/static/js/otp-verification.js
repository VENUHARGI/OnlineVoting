// ===== OTP VERIFICATION PAGE FUNCTIONALITY =====

let otpTimer = null;
let resendTimer = null;
let otpExpiryTime = null;

/**
 * Handle OTP form submission
 */
async function handleOTPVerification(event) {
  event.preventDefault();

  const form = document.getElementById("otpForm");
  if (!form) return;

  // Get OTP code from inputs
  const otpCode = getOTPCode();

  // Validate OTP
  if (!otpCode || otpCode.length !== 6) {
    showOTPError("Please enter the complete 6-digit verification code");
    return;
  }

  // Get email from session
  const email = getStoredEmail();
  if (!email) {
    showToast("Session expired. Please start over.", "error");
    redirectTo("login.html");
    return;
  }

  // Show loading state
  setFormLoading(form, true);
  showLoading("Verifying your code...");

  try {
    // Get verification context
    const context = getVerificationContext();
    console.log("DEBUG: Verification context:", context);

    // Determine the correct endpoint based on purpose
    let endpoint = "/auth/verify-otp"; // Default for SIGNUP
    if (context.purpose === "LOGIN") {
      endpoint = "/auth/verify-login-otp";
    } else if (context.purpose === "PASSWORD_RESET") {
      endpoint = "/auth/verify-password-reset-otp"; // If exists
    }

    console.log("DEBUG: Using endpoint:", endpoint);
    console.log("DEBUG: Request payload:", {
      email,
      otpCode: otpCode,
      purpose: context.purpose,
    });

    // Call verify OTP API
    const response = await apiPost(endpoint, {
      email,
      otpCode: otpCode,
      purpose: context.purpose,
    });

    if (response.success) {
      // Handle successful verification based on context
      hideLoading();

      if (context.purpose === "SIGNUP") {
        // Account verified, redirect to login
        sessionStorage.setItem("account_created", "true");
        sessionStorage.setItem("signup_email", email);
        clearOTPSession();
        showSuccessModal("Your account has been verified successfully! You can now sign in.", "login.html");
      } else if (context.purpose === "LOGIN") {
        // Login verification completed - set simple session flag
        sessionStorage.setItem("user_authenticated", "true");
        if (response.data && response.data.data) {
          setUserData(response.data.data, false);
        }
        clearOTPSession();
        showSuccessModal("Login verified successfully!", "voting.html");
      } else {
        // Default success
        clearOTPSession();
        showSuccessModal("Verification completed successfully!", "voting.html");
      }
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("OTP verification error:", error);
    hideLoading();
    setFormLoading(form, false);

    // Handle specific error types
    if (error.message.includes("Invalid OTP")) {
      showOTPError("Invalid verification code. Please check and try again.");
      clearOTPInputs();
    } else if (error.message.includes("expired")) {
      showModal("expiredModal");
    } else if (error.message.includes("too many attempts")) {
      showToast("Too many failed attempts. Please request a new code.", "error");
      disableOTPForm(300); // 5 minutes
    } else {
      const errorModal = document.getElementById("errorModal");
      const errorMessage = document.getElementById("errorMessage");

      if (errorModal && errorMessage) {
        errorMessage.textContent = error.message || "Verification failed. Please try again.";
        showModal("errorModal");
      }
    }
  }
}

/**
 * Get OTP code from input fields
 */
function getOTPCode() {
  const inputs = document.querySelectorAll(".otp-input");
  let code = "";
  inputs.forEach((input) => {
    code += input.value;
  });
  return code;
}

/**
 * Get stored email for verification
 */
function getStoredEmail() {
  // Check sessionStorage first
  const storedEmail = sessionStorage.getItem("signup_email") || sessionStorage.getItem("login_email") || sessionStorage.getItem("reset_email");

  if (storedEmail) {
    return storedEmail;
  }

  // Fallback: Check if email is in URL query parameter (for direct testing)
  const urlParams = new URLSearchParams(window.location.search);
  const emailParam = urlParams.get("email");

  if (emailParam) {
    console.log("ℹ️  Using email from URL parameter:", emailParam);
    return emailParam;
  }

  return null;
}

/**
 * Get verification context
 */
function getVerificationContext() {
  if (sessionStorage.getItem("signup_pending")) {
    return { purpose: "SIGNUP" };
  } else if (sessionStorage.getItem("login_email")) {
    return { purpose: "LOGIN" };
  } else if (sessionStorage.getItem("reset_email")) {
    return { purpose: "PASSWORD_RESET" };
  }
  return { purpose: "SIGNUP" }; // Default
}

/**
 * Clear OTP session data
 */
function clearOTPSession() {
  sessionStorage.removeItem("signup_email");
  sessionStorage.removeItem("signup_pending");
  sessionStorage.removeItem("login_email");
  sessionStorage.removeItem("reset_email");
  sessionStorage.removeItem("temp_session");
}

/**
 * Show OTP error
 */
function showOTPError(message) {
  const errorElement = document.getElementById("otpError");
  if (errorElement) {
    errorElement.textContent = message;
  }

  // Add error styling to OTP inputs
  const inputs = document.querySelectorAll(".otp-input");
  inputs.forEach((input) => {
    input.classList.add("error");
  });
}

/**
 * Clear OTP error
 */
function clearOTPError() {
  const errorElement = document.getElementById("otpError");
  if (errorElement) {
    errorElement.textContent = "";
  }

  // Remove error styling from OTP inputs
  const inputs = document.querySelectorAll(".otp-input");
  inputs.forEach((input) => {
    input.classList.remove("error");
  });
}

/**
 * Clear OTP inputs
 */
function clearOTPInputs() {
  const inputs = document.querySelectorAll(".otp-input");
  inputs.forEach((input) => {
    input.value = "";
    input.classList.remove("filled");
  });

  // Focus first input
  if (inputs.length > 0) {
    inputs[0].focus();
  }
}

/**
 * Show success modal and redirect
 */
function showSuccessModal(message, redirectUrl) {
  const successModal = document.getElementById("successModal");
  const successMessage = document.getElementById("successMessage");

  if (successModal && successMessage) {
    successMessage.textContent = message;
    showModal("successModal");

    // Auto redirect after 3 seconds
    setTimeout(() => {
      redirectTo(redirectUrl);
    }, 3000);
  } else {
    // Fallback
    showToast(message, "success", 3000);
    setTimeout(() => {
      redirectTo(redirectUrl);
    }, 3000);
  }
}

/**
 * Resend OTP
 */
async function resendOTP() {
  const email = getStoredEmail();
  if (!email) {
    showToast("Session expired. Please start over.", "error");
    redirectTo("login.html");
    return;
  }

  const context = getVerificationContext();

  showLoading("Sending new code...");

  try {
    const response = await apiPost("/auth/resend-otp", {
      email,
      purpose: context.purpose,
    });

    if (response.success) {
      hideLoading();
      showModal("resendSuccessModal");

      // Clear current inputs
      clearOTPInputs();
      clearOTPError();

      // Start new timer
      startOTPTimer(APP_CONFIG.OTP_EXPIRY_MINUTES * 60);
      startResendCooldown(APP_CONFIG.RESEND_COOLDOWN_SECONDS);
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Resend OTP error:", error);
    hideLoading();

    const errorModal = document.getElementById("errorModal");
    const errorMessage = document.getElementById("errorMessage");

    if (errorModal && errorMessage) {
      errorMessage.textContent = error.message || "Failed to send new code. Please try again.";
      showModal("errorModal");
    }
  }
}

/**
 * Resend OTP and close expired modal
 */
function resendOTPAndCloseModal() {
  closeModal("expiredModal");
  resendOTP();
}

/**
 * Start OTP expiry timer
 */
function startOTPTimer(seconds) {
  const countdownElement = document.getElementById("countdown");
  const timerElement = document.getElementById("otpTimer");

  if (!countdownElement) return;

  otpExpiryTime = Date.now() + seconds * 1000;

  if (otpTimer) {
    clearInterval(otpTimer);
  }

  otpTimer = setInterval(() => {
    const remaining = Math.max(0, Math.ceil((otpExpiryTime - Date.now()) / 1000));

    if (remaining <= 0) {
      clearInterval(otpTimer);
      countdownElement.textContent = "0:00";
      if (timerElement) {
        timerElement.innerHTML = '<p style="color: var(--error-color);">Code has expired. Please request a new one.</p>';
      }
      showModal("expiredModal");
      return;
    }

    countdownElement.textContent = formatDuration(remaining);

    // Warning when less than 1 minute
    if (remaining <= 60) {
      countdownElement.style.color = "var(--error-color)";
    } else if (remaining <= 180) {
      countdownElement.style.color = "var(--warning-color)";
    }
  }, 1000);
}

/**
 * Start resend cooldown timer
 */
function startResendCooldown(seconds) {
  const resendBtn = document.getElementById("resendBtn");
  const resendCooldown = document.getElementById("resendCooldown");
  const resendTimerElement = document.getElementById("resendTimer");

  if (!resendBtn || !resendCooldown || !resendTimerElement) return;

  resendBtn.style.display = "none";
  resendCooldown.classList.remove("hidden");

  let remaining = seconds;
  resendTimerElement.textContent = remaining;

  if (resendTimer) {
    clearInterval(resendTimer);
  }

  resendTimer = setInterval(() => {
    remaining--;
    resendTimerElement.textContent = remaining;

    if (remaining <= 0) {
      clearInterval(resendTimer);
      resendBtn.style.display = "flex";
      resendCooldown.classList.add("hidden");
    }
  }, 1000);
}

/**
 * Disable OTP form temporarily
 */
function disableOTPForm(seconds) {
  const form = document.getElementById("otpForm");
  const inputs = document.querySelectorAll(".otp-input");
  const verifyBtn = document.getElementById("verifyBtn");
  const resendBtn = document.getElementById("resendBtn");

  inputs.forEach((input) => (input.disabled = true));
  if (verifyBtn) verifyBtn.disabled = true;
  if (resendBtn) resendBtn.disabled = true;

  setTimeout(() => {
    inputs.forEach((input) => (input.disabled = false));
    if (verifyBtn) verifyBtn.disabled = false;
    if (resendBtn) resendBtn.disabled = false;
  }, seconds * 1000);
}

/**
 * Initialize OTP input handling
 */
function initializeOTPInputs() {
  const inputs = document.querySelectorAll(".otp-input");

  inputs.forEach((input, index) => {
    input.addEventListener("input", function (event) {
      const value = event.target.value;

      // Only allow digits
      if (!/^\d$/.test(value)) {
        event.target.value = "";
        return;
      }

      // Clear error when user types
      clearOTPError();

      // Mark as filled
      event.target.classList.add("filled");

      // Move to next input
      if (value && index < inputs.length - 1) {
        inputs[index + 1].focus();
      }

      // Auto-submit when all fields are filled
      if (index === inputs.length - 1 && getOTPCode().length === 6) {
        setTimeout(() => {
          const form = document.getElementById("otpForm");
          if (form) {
            form.dispatchEvent(new Event("submit"));
          }
        }, 200);
      }
    });

    input.addEventListener("keydown", function (event) {
      // Handle backspace
      if (event.key === "Backspace" && !event.target.value && index > 0) {
        inputs[index - 1].focus();
        inputs[index - 1].value = "";
        inputs[index - 1].classList.remove("filled");
      }

      // Handle paste
      if (event.key === "v" && (event.ctrlKey || event.metaKey)) {
        event.preventDefault();
        navigator.clipboard.readText().then((text) => {
          const cleanText = text.replace(/\D/g, "").slice(0, 6);
          if (cleanText.length === 6) {
            inputs.forEach((inp, idx) => {
              inp.value = cleanText[idx] || "";
              if (cleanText[idx]) {
                inp.classList.add("filled");
              }
            });
            clearOTPError();
          }
        });
      }
    });

    input.addEventListener("blur", function () {
      if (!this.value) {
        this.classList.remove("filled");
      }
    });
  });
}

/**
 * Initialize page with email display
 */
function initializeEmailDisplay() {
  const email = getStoredEmail();
  const emailDisplay = document.getElementById("emailDisplay");
  const instructions = document.getElementById("otpInstructions");

  if (email && emailDisplay) {
    emailDisplay.textContent = email;
  }

  if (!email) {
    showToast("Session expired. Please start over.", "error");
    setTimeout(() => {
      redirectTo("login.html");
    }, 2000);
    return false;
  }

  return true;
}

/**
 * Initialize OTP verification page
 */
function initializeOTPPage() {
  // Check authentication status
  if (isAuthenticated()) {
    redirectTo("voting.html");
    return;
  }

  // Initialize email display
  if (!initializeEmailDisplay()) {
    return;
  }

  // Initialize OTP inputs
  initializeOTPInputs();

  // Handle form submission
  const otpForm = document.getElementById("otpForm");
  if (otpForm) {
    otpForm.addEventListener("submit", handleOTPVerification);
  }

  // Start timers
  startOTPTimer(APP_CONFIG.OTP_EXPIRY_MINUTES * 60);

  // Focus first input
  const firstInput = document.querySelector(".otp-input");
  if (firstInput) {
    setTimeout(() => firstInput.focus(), 100);
  }

  // Auto-fetch test OTP on page load - with delay to ensure DOM is ready
  setTimeout(() => {
    console.log("🔄 Calling loadTestOTP() after DOM ready");
    loadTestOTP();
  }, 500);

  console.log("OTP verification page initialized");
}

/**
 * Load test OTP automatically on page load
 */
async function loadTestOTP() {
  try {
    console.log("🔍 loadTestOTP() called");

    // Always fetch OTP from database - database is the single source of truth
    // This avoids stale data issues with localStorage
    const email = getStoredEmail();
    console.log("📧 Retrieved email from storage:", email);

    if (!email) {
      console.warn("⚠️ No email found for test OTP");
      return;
    }

    console.log("🔍 Fetching latest OTP from database for email:", email);
    const response = await apiGet(`/auth/test-otp?email=${encodeURIComponent(email)}`);

    console.log("📨 Test OTP Response:", response);
    console.log("📨 Response.success:", response.success);
    console.log("📨 Response.data:", response.data);

    // Check for OTP - it may be nested at response.data.data.otpCode
    let otpCode = response.data?.otpCode || response.data?.data?.otpCode;

    if (response.success && otpCode) {
      console.log("✅ OTP Code retrieved from database:", otpCode);
      localStorage.setItem("test_otp_code", otpCode); // Cache in localStorage for reference only
      const element = document.getElementById("testOTPCode");
      if (element) {
        element.textContent = otpCode;
        console.log("✅ OTP displayed in UI:", otpCode);
        console.log("✅ Element text content updated to:", element.textContent);
      } else {
        console.error("❌ Element with id 'testOTPCode' not found!");
      }
    } else {
      console.warn("⚠️ Failed to fetch OTP from database");
      console.warn("⚠️ Response object:", JSON.stringify(response, null, 2));
      console.warn("⚠️ Error:", response.error);
    }
  } catch (error) {
    console.warn("⚠️ Error auto-loading test OTP:", error);
    console.error("Error details:", error);
  }
}

/**
 * Copy test OTP to clipboard
 */
function copyTestOTP() {
  const otpCode = document.getElementById("testOTPCode").textContent;

  navigator.clipboard
    .writeText(otpCode)
    .then(() => {
      showToast("OTP copied to clipboard!", "success");
      console.log("✅ OTP copied:", otpCode);
    })
    .catch(() => {
      showToast("Failed to copy OTP", "error");
    });
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", initializeOTPPage);
