package com.wx.pay;

import com.wx.wxPay.WxPayApplication;
import com.wx.wxPay.utils.PayHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WxPayApplication.class)
public class PayApplicationTests {


	@Autowired
	PayHelper payHelper;

	@Test
	public void contextLoads() {

		String payUrl = payHelper.createPayUrl(123132L);
		System.out.println(payUrl);
	}

}
