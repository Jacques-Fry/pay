package com.alibaba.pay;

import com.alibaba.aliPay.AliPayApplication;
import com.alibaba.aliPay.util.PayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AliPayApplication.class)
public class AliPayApplicationTests {

	@Autowired
	PayUtil payUtil;

	@Test
	public void contextLoads() {
		payUtil.createQrCode("2016101000653079","7820.88");
	}



	@Test
	public void tr() {
		payUtil.start("287250016251025390","2016101000653079","8888.88","牛皮鞋");
	}
}
