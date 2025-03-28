package com.shivam.referral_tracking_system.repository;

import com.shivam.referral_tracking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByReferralCode(String referralCode);

}
