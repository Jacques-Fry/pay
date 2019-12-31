package com.wx.wxPay.utils;

import com.github.wxpay.sdk.WXPay;
import com.wx.wxPay.config.PayConfig;
import com.wx.wxPay.utils2.PayCommonUtil;
import com.wx.wxPay.utils2.StringUtilPay;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: 98050
 * @create: 2018-10-27 15:54
 **/
@Component
public class PayHelper {

    private WXPay wxPay;

    private static final Logger logger = LoggerFactory.getLogger(PayHelper.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public PayHelper(PayConfig payConfig) {
        // 真实开发时
        wxPay = new WXPay(payConfig);
        // 测试时
        // wxPay = new WXPay(payConfig, WXPayConstants.SignType.MD5, true);
    }

    public String createPayUrl(Long orderId) {
        String key = "leyou.pay.url." + orderId;
        try {
            String url = this.redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(url)) {
                return url;
            }
        } catch (Exception e) {
            logger.error("查询缓存付款链接异常,订单编号：{}", orderId, e);
        }

        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", "支付测试,请尽量不要付款!");
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //货币
            data.put("fee_type", "CNY");
            //金额，单位是分
            String money=(int)((Math.random() * 9 + 1) * 1000000)+"";

            data.put("total_fee", "1");

            //调用微信支付的终端IP（estore商城的IP）
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", "http://6s.net579.com:22708/back");
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");
            //商品id,使用假数据
            data.put("product_id", "1234567");

            //生成签名
            String sign=PayCommonUtil.createSign("UTF-8", data);
            //签名
            data.put("sign", sign);

            Map<String, String> result = this.wxPay.unifiedOrder(data);

            if ("SUCCESS".equals(result.get("return_code"))) {
                String url = result.get("code_url");
                // 将付款地址缓存，时间为10分钟
                try {
                    this.redisTemplate.opsForValue().set(key, url, 10, TimeUnit.MINUTES);
                } catch (Exception e) {
                    logger.error("缓存付款链接异常,订单编号：{}", orderId, e);
                }
                return url;
            } else {
                logger.error("创建预交易订单失败，错误信息：{}", result.get("return_msg"));
                return null;
            }
        } catch (Exception e) {
            logger.error("创建预交易订单异常", e);
            return null;
        }
    }

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    public PayState queryOrder(Long orderId) {
        Map<String, String> data = new HashMap<>();
        // 订单号
        data.put("out_trade_no", orderId.toString());
        try {
            Map<String, String> result = this.wxPay.orderQuery(data);

            if (result == null) {
                // 未查询到结果，认为是未付款
                System.out.println("===============查询未付款==============");
                return PayState.NOT_PAY;
            }

            String state = result.get("trade_state");
            if ("SUCCESS".equals(state)) {
                System.out.println("===============查询付款成功==============");
                // success，则认为付款成功
                // 修改订单状态
                //this.orderService.updateOrderStatus(orderId, 2);



                return PayState.SUCCESS;
            } else if (StringUtils.equals("USERPAYING", state) || StringUtils.equals("NOTPAY", state)) {
                // 未付款或正在付款，都认为是未付款
                return PayState.NOT_PAY;
            } else {
                // 其它状态认为是付款失败
                return PayState.FAIL;
            }
        } catch (Exception e) {
            logger.error("查询订单状态异常", e);
            return PayState.NOT_PAY;
        }
    }

    /**
     * 回调微信支付状态
     */
    public String notifyWeiXinPay(HttpServletRequest request){
        InputStream inStream = null;
        Map<String, String> return_data = new HashMap<>();
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultXml = new String(outSteam.toByteArray(), "utf-8");

            Map<String, String> params = PayCommonUtil.doXMLParse(resultXml);
            //判断是否支付成功

            if(StringUtils.equals("SUCCESS",params.get("result_code"))){
                System.out.println("===============付款成功==============");

                // ------------------------------
                // 处理业务开始
                // ------------------------------
                // 此处处理订单状态，结合自己的订单数据完成订单状态的更新
                // ------------------------------

                //支付成功
                return_data.put("return_code", "SUCCESS");
                return_data.put("return_msg", "OK");

            }else{
                System.out.println("===============付款失败==============");
                System.out.println(params.toString());
                System.out.println("===============付款失败==============");
                return_data.put("return_code", "FAIL");
                return_data.put("return_msg", "result_code不为SUCCESS");
            }

            outSteam.close();
            inStream.close();

            return StringUtilPay.GetMapToXML(return_data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        //程序异常
        return_data.put("return_code", "FAIL");
        return_data.put("return_msg", "程序处理异常");
        return StringUtilPay.GetMapToXML(return_data);

    }
}
