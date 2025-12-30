package com.voting.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Web Controller for serving static HTML pages
 * 
 * Handles routing for the frontend UI pages
 */
@Controller
public class WebController {

    /**
     * Root route - redirect to login page
     */
    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("redirect:/login.html");
    }

    /**
     * Login page route
     */
    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("redirect:/login.html");
    }

    /**
     * Signup page route
     */
    @GetMapping("/signup")
    public ModelAndView signup() {
        return new ModelAndView("redirect:/signup.html");
    }

    /**
     * Voting page route
     */
    @GetMapping("/voting")
    public ModelAndView voting() {
        return new ModelAndView("redirect:/voting.html");
    }

    /**
     * OTP verification page route
     */
    @GetMapping("/otp-verification")
    public ModelAndView otpVerification() {
        return new ModelAndView("redirect:/otp-verification.html");
    }

    /**
     * Success page route
     */
    @GetMapping("/success")
    public ModelAndView success() {
        return new ModelAndView("redirect:/success.html");
    }

    /**
     * Error page route
     */
    @GetMapping("/error")
    public ModelAndView error() {
        return new ModelAndView("redirect:/error.html");
    }
}