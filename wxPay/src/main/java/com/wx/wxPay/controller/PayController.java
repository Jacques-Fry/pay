package com.wx.wxPay.controller;

import com.wx.wxPay.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jack_YD
 * @create 2019/10/15 10:33
 */
@RestController
@CrossOrigin
public class PayController {
    @Autowired
    private PayHelper payHelper;

    @GetMapping("create")
    public Result create() {

        long orderId = new IdWorker(1, 1).nextId();

        String payUrl = payHelper.createPayUrl(orderId);

        PayResult payResult = new PayResult(payUrl, orderId + "");

        return new Result(true, 20000, "生成成功!", payResult);
    }

    @GetMapping("selectCode")
    public Result selectCode(long orderId) {

        return new Result(true, 20000, "查询成功!", payHelper.queryOrder(orderId));
    }

    /**
     * 此函数会被执行多次，如果支付状态已经修改为已支付，则下次再调的时候判断是否已经支付，如果已经支付了，则什么也执行
     *
     */
    @RequestMapping(value = "/back", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String notifyWeiXinPay(HttpServletRequest request){
        System.out.println("微信支付回调");
        return payHelper.notifyWeiXinPay(request);


    }


}
