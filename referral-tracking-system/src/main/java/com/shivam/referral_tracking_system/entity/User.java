package com.shivam.referral_tracking_system.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;


    private String password;

    // UNIQUE REFERRAL CODE FOR EACH USER
    @Column(unique = true)
    private String referralCode;

    // SELF REFERENCING
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    private User referrer;

    // PROFILE COMPLETION STATUS
    private boolean profileCompleted;

    public User() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getReferralCode() {
        return referralCode;
    }
    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
    public User getReferrer() {
        return referrer;
    }
    public void setReferrer(User referrer) {
        this.referrer = referrer;
    }
    public boolean isProfileCompleted() {
        return profileCompleted;
    }
    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    // MECHANISM FOR GENERATING REFERRAL CODE
    public static String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

