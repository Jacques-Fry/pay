package com.wx.wxPay.utils;

/**
 * @author Jack_YD
 * @create 2019/10/15 11:05
 */
public class PayResult {
    private String url;
    private String orderId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PayResult() {
    }

    public PayResult(String url, String orderId) {
        this.url = url;
        this.orderId = orderId;
    }
}
