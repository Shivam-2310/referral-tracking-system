package com.shivam.referral_tracking_system.service;

import com.shivam.referral_tracking_system.dto.ProfileCompletionRequest;
import com.shivam.referral_tracking_system.dto.ReferralResponse;
import com.shivam.referral_tracking_system.dto.SignupRequest;
import com.shivam.referral_tracking_system.entity.Referral;
import com.shivam.referral_tracking_system.entity.ReferralStatus;
import com.shivam.referral_tracking_system.entity.User;
import com.shivam.referral_tracking_system.repository.ReferralRepository;
import com.shivam.referral_tracking_system.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReferralRepository referralRepository;

    public User signup(SignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setReferralCode(User.generateReferralCode());
        user.setProfileCompleted(false);
        user = userRepository.save(user);

        if (request.getReferralCode() != null && !request.getReferralCode().trim().isEmpty()) {
            Optional<User> referrerOpt = userRepository.findByReferralCode(request.getReferralCode());
            if (referrerOpt.isPresent()) {
                user.setReferrer(referrerOpt.get());
                user = userRepository.save(user);
                Referral referral = new Referral();
                referral.setReferrer(referrerOpt.get());
                referral.setReferred(user);
                referral.setStatus(ReferralStatus.PENDING);
                referralRepository.save(referral);
            } else {
                throw new IllegalArgumentException("Invalid referral code provided");
            }
        }
        return user;
    }

    public User completeProfile(ProfileCompletionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setMobileNumber(request.getMobileNumber());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setProfileCompleted(true);
        user = userRepository.save(user);

        Optional<Referral> referralOpt = referralRepository.findByReferred(user);
        if (referralOpt.isPresent()) {
            Referral referral = referralOpt.get();
            referral.setStatus(ReferralStatus.SUCCESSFUL);
            referralRepository.save(referral);
        }
        return user;
    }

    public List<ReferralResponse> getReferralsDto(Long userId) {
        User referrer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Referral> referrals = referralRepository.findByReferrer(referrer);

        return referrals.stream()
                .map(r -> new ReferralResponse(
                        r.getId(),
                        r.getReferrer().getId(),
                        r.getReferrer().getName(),
                        r.getReferred().getId(),
                        r.getReferred().getName(),
                        r.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public ByteArrayInputStream generateReferralReport() throws IOException {
        List<User> users = userRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out),
                CSVFormat.DEFAULT.withHeader("User ID", "Name", "Email", "Referral Code", "Referred Users", "Referred Users Count"))) {
            for (User user : users) {
                List<Referral> referrals = referralRepository.findByReferrer(user);
                String referredUsers = referrals.stream()
                        .map(ref -> ref.getReferred().getName())
                        .collect(Collectors.joining(", "));
                csvPrinter.printRecord(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getReferralCode(),
                        referredUsers,
                        referrals.size()
                );
            }
            csvPrinter.flush();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}


