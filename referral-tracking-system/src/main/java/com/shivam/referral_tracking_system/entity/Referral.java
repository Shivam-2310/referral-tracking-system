package com.shivam.referral_tracking_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USER WHO REFERRED
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    private User referrer;

    // USER WHO WAS REFERRED
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_id", unique = true)
    private User referred;

    // REFERRAL STATUS
    @Enumerated(EnumType.STRING)
    private ReferralStatus status;

    public Referral() {}

    public Referral(User referrer, User referred, ReferralStatus status) {
        this.referrer = referrer;
        this.referred = referred;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getReferrer() {
        return referrer;
    }
    public void setReferrer(User referrer) {
        this.referrer = referrer;
    }
    public User getReferred() {
        return referred;
    }
    public void setReferred(User referred) {
        this.referred = referred;
    }
    public ReferralStatus getStatus() {
        return status;
    }
    public void setStatus(ReferralStatus status) {
        this.status = status;
    }
}
