package com.alibaba.aliPay.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 花落泪知雨
 * @create 2019/9/9
 */
@Component
public class PayUtil {


    @Value("${ali.pay.severUrl}")
    private String severUrl;
    @Value("${ali.pay.privateKey}")
    private String privateKey;
    @Value("${ali.pay.alipayPublicKey}")
    private String alipayPublicKey;


    /**
     * 当面付
     * @param authCode
     * @param appId
     * @param totalAmount
     * @param subject
     */
    public void start(String authCode,String appId,String totalAmount,String subject){
        AlipayClient alipayClient = new
                DefaultAlipayClient(severUrl,appId,privateKey,"json","utf-8",
                alipayPublicKey,"RSA2" );
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        AlipayTradePayModel model = new AlipayTradePayModel();
        request.setBizModel(model);

        model.setOutTradeNo(System.currentTimeMillis()+"");
        model.setSubject(subject);
        model.setTotalAmount(totalAmount);
        model.setAuthCode(authCode);//沙箱钱包中的付款码
        model.setScene("bar_code");

        AlipayTradePayResponse response = null;
        try {
            response = alipayClient.execute(request);
            System.out.println("body----------"+response.getBody());
            System.out.println(response.getTradeNo());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫码付
     * @param appId
     * @param totalAmount
     * @return
     */
    public String createQrCode(String appId,String totalAmount) {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "utf-8", alipayPublicKey, "RSA2"); //获得初始化的AlipayClient
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        request.setBizContent("{" +
                "    \"out_trade_no\":\"20150320010101002\"," +//商户订单号
                "    \"total_amount\":"+totalAmount+"," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"store_id\":\"NJ_001\"," +
                "    \"timeout_express\":\"90m\"}");//订单允许的最晚付款时间

        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            System.out.println("订单生成异常!");
        }
        System.out.println(response.getBody());
        return response.getBody();
    }
}
