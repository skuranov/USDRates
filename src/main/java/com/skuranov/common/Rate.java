package com.skuranov.common;


/**
 * Model class for Rate
 */
public class Rate {

    private String rateTime;
    private String usdRate;

    public String getUsdRate() {
        return usdRate;
    }

    public void setUsdRate(String usdRate) {
        this.usdRate = usdRate;
    }


    public Rate() {
    }

    public Rate(String rateTime, String usdRate) {
        this.rateTime = rateTime;
        this.usdRate = usdRate;
    }

    public String getRateTime() {
        return this.rateTime;
    }

    public void setRateTime(String rateTime) {
        this.rateTime = rateTime;
    }


    public String getUSDRate() {
        return this.usdRate;
    }

    public void setUSDRate(String usdRate) {
        this.usdRate = usdRate;
    }


}