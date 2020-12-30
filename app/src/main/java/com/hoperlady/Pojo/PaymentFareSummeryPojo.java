package com.hoperlady.Pojo;

/**
 * Created by user88 on 12/30/2015.
 */
public class PaymentFareSummeryPojo {
    private String payment_title = "";
    private String currencycode = "";
    private String payment_amount = "";
    private String dt = "";

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_title() {
        return payment_title;
    }

    public void setPayment_title(String payment_title) {
        this.payment_title = payment_title;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;

    }

}
