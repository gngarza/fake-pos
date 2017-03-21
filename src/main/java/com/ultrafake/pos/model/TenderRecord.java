package com.ultrafake.pos.model;

import com.ultrafake.pos.util.MathUtils;

import java.math.BigDecimal;
import java.time.Instant;

public class TenderRecord {

    private BigDecimal amountTendered;
    private BigDecimal changeGiven;
    private Instant timestamp;

    public String getDisplayAmountTendered() {
        return MathUtils.toMoneyString(getAmountTendered());
    }

    public String getDisplayChangeGiven() {
        return MathUtils.toMoneyString(getChangeGiven());
    }

    public String getDisplayTimestamp() {
        return getTimestamp().toString();
    }

    public BigDecimal getAmountTendered() {
        return amountTendered;
    }

    public void setAmountTendered(BigDecimal amountTendered) {
        this.amountTendered = amountTendered;
    }

    public BigDecimal getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(BigDecimal changeGiven) {
        this.changeGiven = changeGiven;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
