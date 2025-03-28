package com.shivam.referral_tracking_system.controller;

import com.shivam.referral_tracking_system.dto.ProfileCompletionRequest;
import com.shivam.referral_tracking_system.dto.ReferralResponse;
import com.shivam.referral_tracking_system.dto.SignupRequest;
import com.shivam.referral_tracking_system.entity.Referral;
import com.shivam.referral_tracking_system.entity.User;
import com.shivam.referral_tracking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.signup(request);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/complete-profile")
    public ResponseEntity<?> completeProfile(
            @PathVariable Long userId,
            @RequestBody ProfileCompletionRequest request) {
        try {
            request.setUserId(userId);
            User user = userService.completeProfile(request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/referrals/{userId}")
    public ResponseEntity<?> getReferrals(@PathVariable Long userId) {
        try {
            List<ReferralResponse> referralResponses = userService.getReferralsDto(userId);
            return ResponseEntity.ok(referralResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/referral-report")
    public ResponseEntity<?> referralReport() {
        try {
            ByteArrayInputStream csvData = userService.generateReferralReport();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=referral_report.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(csvData));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error generating report");
        }
    }
}
