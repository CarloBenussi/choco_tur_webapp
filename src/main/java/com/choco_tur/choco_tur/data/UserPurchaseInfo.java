package com.choco_tur.choco_tur.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class UserPurchaseInfo {
    @Id
    private String offerId;

    private boolean redeemed;

    private String purchaseTime;

    private String expiryTime;

    // 0-tokens 1-tour
    private Integer purchaseMethod;

    private String purchaseValidationNumber;

    // Short-lived
    private long purchaseValidationNumberGenerationTime = -1;
}
