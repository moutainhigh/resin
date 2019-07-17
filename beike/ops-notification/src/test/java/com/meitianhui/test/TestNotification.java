package com.meitianhui.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.JedisPoolUtil;
import com.meitianhui.common.util.RedisUtil;

import redis.clients.jedis.Jedis;

public class TestNotification {

	@Autowired
	public RedisUtil redisUtil;
	
//	@Test
	public void pushNotificationByTag() {
		try {
			String url = "http://127.0.0.1:8080/ops-notification/notification";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "notification.pushNotificationByTag");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("tag", "consumer");
			params.put("title", "通知");
			params.put("alert", "本地生活界面大升级，只为遇见更好的你，快来看看吧！");
			Map<String, Object> extrasparam = new HashMap<String, Object>();
			params.put("extrasparam", FastJsonUtil.toJson(extrasparam));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void pushConsumerNotification() {
		try {
			String url = "http://127.0.0.1:8080/ops-notification/notification";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "notification.pushConsumerNotification");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("title", "活动通知");
			params.put("alert", "数据线免费包邮领取，再送剪卡器1个！6.8返6.8元！快快来本地生活领取吧");
			Map<String, Object> extrasparam = new HashMap<String, Object>();
			params.put("extrasparam", FastJsonUtil.toJson(extrasparam));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void pushNotificationByAlias() {
		try {
			String url = "http://127.0.0.1:8080/ops-notification/notification";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "notification.pushNotificationByAlias");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("title", "活动通知");
			params.put("member_id", "9654");
			params.put("alert", "数据线免费包邮领取，再送剪卡器1个！6.8返6.8元！快快来店东助手领取吧");
			Map<String, Object> extrasparam = new HashMap<String, Object>();
			params.put("extrasparam", FastJsonUtil.toJson(extrasparam));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public  void testNotificationSMS() {
		try {
			String url = "http://127.0.0.1:8080/ops-notification/notification";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "notification.sendCheckCode");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", " ");
			params.put("sms_source", "9654");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public  void  testRedis() {
		//初始化连接池
		String mobile ="15012650695";
		
		
		
		
	}
	
	
}
