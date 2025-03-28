package com.shivam.referral_tracking_system.repository;

import com.shivam.referral_tracking_system.entity.Referral;
import com.shivam.referral_tracking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrer(User referrer);

    Optional<Referral> findByReferred(User referred);
}
