package com.shivam.referral_tracking_system.dto;

import com.shivam.referral_tracking_system.entity.ReferralStatus;

public class ReferralResponse {

    private Long referralId;
    private Long referrerId;
    private String referrerName;
    private Long referredId;
    private String referredName;
    private ReferralStatus status;

    public ReferralResponse() {
    }

    public ReferralResponse(Long referralId, Long referrerId, String referrerName,
                            Long referredId, String referredName, ReferralStatus status) {
        this.referralId = referralId;
        this.referrerId = referrerId;
        this.referrerName = referrerName;
        this.referredId = referredId;
        this.referredName = referredName;
        this.status = status;
    }

    public Long getReferralId() {
        return referralId;
    }

    public void setReferralId(Long referralId) {
        this.referralId = referralId;
    }

    public Long getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Long referrerId) {
        this.referrerId = referrerId;
    }

    public String getReferrerName() {
        return referrerName;
    }

    public void setReferrerName(String referrerName) {
        this.referrerName = referrerName;
    }

    public Long getReferredId() {
        return referredId;
    }

    public void setReferredId(Long referredId) {
        this.referredId = referredId;
    }

    public String getReferredName() {
        return referredName;
    }

    public void setReferredName(String referredName) {
        this.referredName = referredName;
    }

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }
}
