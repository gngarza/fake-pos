package com.ultrafake.pos.util;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {


    public static String toMoneyString(BigDecimal num) {
        return num.setScale(2, RoundingMode.HALF_UP).toString();
    }

}
