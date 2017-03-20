package com.ultrafake.pos.model;

import com.ultrafake.pos.util.MathUtils;

import java.math.BigDecimal;
import java.time.Instant;

public class TenderRecord {
    public BigDecimal amountTendered;
    public BigDecimal changeGiven;
    public Instant timestamp;

    public String getDisplayAmountTendered() {
        return MathUtils.toMoneyString(amountTendered);
    }

    public String getDisplayChangeGiven() {
        return MathUtils.toMoneyString(changeGiven);
    }

    public String getDisplayTimestamp() {
        return timestamp.toString();
    }
}
