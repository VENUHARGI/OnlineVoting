// ===== SIGNUP PAGE FUNCTIONALITY =====

/**
 * Handle signup form submission
 */
async function handleSignup(event) {
  event.preventDefault();

  const form = document.getElementById("signupForm");
  if (!form) return;

  // Validate form
  if (!validateAuthForm("signupForm")) {
    return;
  }

  // Get form data
  const formData = new FormData(form);
  const signupData = {
    firstName: formData.get("firstName").trim(),
    lastName: formData.get("lastName").trim(),
    email: formData.get("email").trim(),
    phoneNumber: formData.get("phoneNumber")?.trim() || null,
    password: formData.get("password"),
  };

  // Show loading state
  setFormLoading(form, true);
  showLoading("Creating your account...");

  try {
    // Call signup API
    const response = await apiPost("/auth/signup", signupData);

    if (response.success) {
      // Store email for OTP verification
      sessionStorage.setItem("signup_email", signupData.email);
      sessionStorage.setItem("signup_pending", "true");

      // Store OTP code from response if available (for development/testing)
      if (response.data && response.data.otpCode) {
        // Clear old OTP first
        localStorage.removeItem("test_otp_code");
        sessionStorage.removeItem("test_otp_code");

        // Store new OTP in both storage locations
        sessionStorage.setItem("test_otp_code", response.data.otpCode);
        localStorage.setItem("test_otp_code", response.data.otpCode);
        console.log("✅ OTP Code stored from signup response:", response.data.otpCode);
        console.log("📱 Stored in both sessionStorage and localStorage");
      }

      // Show success modal
      hideLoading();
      showModal("successModal");

      // Auto-redirect to OTP verification after 2 seconds
      setTimeout(() => {
        redirectToOTPVerification();
      }, 2000);
    } else {
      throw new Error(response.error);
    }
  } catch (error) {
    console.error("Signup error:", error);
    hideLoading();
    setFormLoading(form, false);

    // Show error modal with specific message
    const errorModal = document.getElementById("errorModal");
    const errorMessage = document.getElementById("errorMessage");

    if (errorModal && errorMessage) {
      // Use the exact error message from backend instead of hardcoded messages
      if (error.message.includes("already exists")) {
        // Show the actual backend error message (could be email or phone)
        errorMessage.textContent = error.message;
      } else if (error.message.includes("validation")) {
        errorMessage.textContent = "Please check your information and try again.";
      } else {
        errorMessage.textContent = error.message || "Failed to create account. Please try again.";
      }
      showModal("errorModal");
    }
  }
}

/**
 * Validate signup form with real-time feedback
 */
function validateSignupForm() {
  const form = document.getElementById("signupForm");
  if (!form) return false;

  let isValid = true;
  clearFormErrors(form);

  // Validate first name
  const firstName = form.querySelector("#firstName");
  if (firstName) {
    const value = firstName.value.trim();
    if (!value) {
      showFieldError(firstName, "First name is required");
      isValid = false;
    } else if (value.length < 2) {
      showFieldError(firstName, "First name must be at least 2 characters");
      isValid = false;
    } else if (!/^[a-zA-Z\s'-]+$/.test(value)) {
      showFieldError(firstName, "First name contains invalid characters");
      isValid = false;
    }
  }

  // Validate last name
  const lastName = form.querySelector("#lastName");
  if (lastName) {
    const value = lastName.value.trim();
    if (!value) {
      showFieldError(lastName, "Last name is required");
      isValid = false;
    } else if (value.length < 2) {
      showFieldError(lastName, "Last name must be at least 2 characters");
      isValid = false;
    } else if (!/^[a-zA-Z\s'-]+$/.test(value)) {
      showFieldError(lastName, "Last name contains invalid characters");
      isValid = false;
    }
  }

  // Validate email
  const email = form.querySelector("#email");
  if (email) {
    const value = email.value.trim();
    if (!value) {
      showFieldError(email, "Email is required");
      isValid = false;
    } else if (!isValidEmail(value)) {
      showFieldError(email, "Please enter a valid email address");
      isValid = false;
    }
  }

  // Validate phone number (optional but must be valid if provided)
  const phoneNumber = form.querySelector("#phoneNumber");
  if (phoneNumber && phoneNumber.value.trim()) {
    const value = phoneNumber.value.trim();

    // Extract only digits from phone number
    const digitsOnly = value.replace(/\D/g, "");

    // Check if phone number has more than 10 digits
    if (digitsOnly.length > 10) {
      showFieldError(phoneNumber, "Phone number cannot have more than 10 digits");
      isValid = false;
    } else if (!isValidPhoneNumber(value)) {
      showFieldError(phoneNumber, "Please enter a valid phone number");
      isValid = false;
    } else {
      clearFieldError(phoneNumber);
    }
  }

  // Validate password
  const password = form.querySelector("#password");
  if (password) {
    const value = password.value;
    if (!value) {
      showFieldError(password, "Password is required");
      isValid = false;
    } else {
      const strength = checkPasswordStrength(value);
      if (strength.score < 3) {
        showFieldError(password, `Password is too weak. Missing: ${strength.feedback}`);
        isValid = false;
      }
    }
  }

  // Validate password confirmation
  const confirmPassword = form.querySelector("#confirmPassword");
  if (confirmPassword) {
    const value = confirmPassword.value;
    const passwordValue = password ? password.value : "";
    if (!value) {
      showFieldError(confirmPassword, "Please confirm your password");
      isValid = false;
    } else if (value !== passwordValue) {
      showFieldError(confirmPassword, "Passwords do not match");
      isValid = false;
    }
  }

  // Validate terms acceptance
  const termsAccept = form.querySelector("#termsAccept");
  if (termsAccept && !termsAccept.checked) {
    showFieldError(termsAccept, "You must accept the terms and conditions");
    isValid = false;
  }

  return isValid;
}

/**
 * Initialize real-time form validation
 */
function initializeSignupValidation() {
  const form = document.getElementById("signupForm");
  if (!form) return;

  // First name validation
  const firstName = form.querySelector("#firstName");
  if (firstName) {
    firstName.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value) {
        if (value.length < 2) {
          showFieldError(this, "First name must be at least 2 characters");
        } else if (!/^[a-zA-Z\s'-]+$/.test(value)) {
          showFieldError(this, "First name contains invalid characters");
        } else {
          clearFieldError(this);
        }
      }
    });
  }

  // Last name validation
  const lastName = form.querySelector("#lastName");
  if (lastName) {
    lastName.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value) {
        if (value.length < 2) {
          showFieldError(this, "Last name must be at least 2 characters");
        } else if (!/^[a-zA-Z\s'-]+$/.test(value)) {
          showFieldError(this, "Last name contains invalid characters");
        } else {
          clearFieldError(this);
        }
      }
    });
  }

  // Email validation
  const email = form.querySelector("#email");
  if (email) {
    const debouncedEmailCheck = debounce(async function (emailValue) {
      if (isValidEmail(emailValue)) {
        try {
          const response = await apiPost("/auth/check-email", { email: emailValue });
          if (response.success && response.data.exists) {
            showFieldError(email, "An account with this email already exists");
          } else {
            clearFieldError(email);
          }
        } catch (error) {
          console.log("Email check failed:", error.message);
        }
      }
    }, 500);

    email.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value && isValidEmail(value)) {
        debouncedEmailCheck(value);
      }
    });
  }

  // Phone number validation
  const phoneNumber = form.querySelector("#phoneNumber");
  if (phoneNumber) {
    phoneNumber.addEventListener("blur", function () {
      const value = this.value.trim();
      if (value) {
        const digitsOnly = value.replace(/\D/g, "");
        if (digitsOnly.length > 10) {
          showFieldError(this, "Phone number cannot have more than 10 digits");
        } else if (!isValidPhoneNumber(value)) {
          showFieldError(this, "Please enter a valid phone number");
        } else {
          clearFieldError(this);
        }
      } else {
        clearFieldError(this); // Clear error if field is empty (it's optional)
      }
    });
  }

  // Password strength real-time update
  const password = form.querySelector("#password");
  if (password) {
    password.addEventListener("input", function () {
      updatePasswordStrength(this.value);

      // Revalidate confirmation if it has value
      const confirmPassword = form.querySelector("#confirmPassword");
      if (confirmPassword && confirmPassword.value) {
        validatePasswordConfirmation();
      }
    });
  }

  // Password confirmation validation
  const confirmPassword = form.querySelector("#confirmPassword");
  if (confirmPassword) {
    confirmPassword.addEventListener("input", function () {
      validatePasswordConfirmation();
    });
  }

  // Terms checkbox validation
  const termsAccept = form.querySelector("#termsAccept");
  if (termsAccept) {
    termsAccept.addEventListener("change", function () {
      if (this.checked) {
        clearFieldError(this);
      }
    });
  }
}

/**
 * Check if form is valid for submission
 */
function isSignupFormReady() {
  const form = document.getElementById("signupForm");
  if (!form) return false;

  const requiredFields = form.querySelectorAll("[required]");
  let allFilled = true;

  requiredFields.forEach((field) => {
    if (!field.value.trim() || (field.type === "checkbox" && !field.checked)) {
      allFilled = false;
    }
  });

  return allFilled;
}

/**
 * Update submit button state
 */
function updateSubmitButtonState() {
  const submitBtn = document.getElementById("signupBtn");
  if (submitBtn) {
    submitBtn.disabled = !isSignupFormReady();
  }
}

/**
 * Initialize signup page
 */
function initializeSignupPage() {
  // Check authentication status
  checkAuthStatus();

  // Initialize form validation
  initializeSignupValidation();

  // Handle form submission
  const signupForm = document.getElementById("signupForm");
  if (signupForm) {
    signupForm.addEventListener("submit", handleSignup);

    // Monitor form changes for submit button state
    signupForm.addEventListener("input", updateSubmitButtonState);
    signupForm.addEventListener("change", updateSubmitButtonState);

    // Initial button state
    updateSubmitButtonState();
  }

  // Handle success modal redirect
  window.redirectToOTPVerification = function () {
    closeModal("successModal");
    redirectTo("otp-verification.html");
  };

  console.log("Signup page initialized");
}

/**
 * Initialize when DOM loads
 */
document.addEventListener("DOMContentLoaded", initializeSignupPage);
