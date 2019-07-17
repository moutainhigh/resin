package com.meitianhui.order.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.order.constant.RspCode;
/*import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.internal.toplink.LinkException;*/

@Service
public class StartGateServiceDataImpl implements ApplicationListener<ContextRefreshedEvent> {

	private static final Log LOGGER = LogFactory.getLog(StartGateServiceDataImpl.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			// 在web项目中（spring mvc），系统会存在两个容器，一个是root application context
			// ,另一个就是我们自己的 projectName-servlet context（作为root application
			// context的子容器）。
			// 这种情况下，就会造成onApplicationEvent方法被执行两次。为了避免这个问题，我们可以只在root
			// application context初始化完成后调用逻辑代码，其他的容器的初始化完成，则不做任何处理。

			// if (event.getApplicationContext().getParent() == null) {
			// 需要实现的功能
			//MyThread thread = new MyThread();
			// 使用另一个线程来执行该方法，会避免占用Tomcat的启动时间
			//new Thread(thread).start();
			// }
			
		} catch (Exception e) {
			LOGGER.error("StartGateServiceDataImpl", e);
		}
	}

	// class MyThread implements Runnable {
		// // Tomcat启动结束后执行
		// @Override
		// public void run() {
			// // 子线程需要做的事情
			// // iOS
			// TmcClient clientIos = new TmcClient("24586682", "f229a77e8778dc83859a36e5e25c3631", "default");
			// clientIos.setMessageHandler(new MessageHandler() {
				// public void onMessage(Message message, MessageStatus status) {
					// try {	
						// //System.out.println("======TMC长链接======");
						// // 默认不抛出异常则认为消息处理成功
						// //taobao_tae_BaichuanTradePaidDone
						// //taobao_tae_BaichuanTradeCreated
						// LOGGER.info("IOS淘宝长链接发送详细信息："+message.getContent());
						// LOGGER.info("IOS淘宝长链接发送状态信息："+message.getTopic());
						// if (message.getTopic().toString().equals("taobao_tae_BaichuanTradePaidDone")) {
							// Map<String, Object> tempData = FastJsonUtil.jsonToMap(message.getContent());
							// tempMessage(tempData);
						// }
					// } catch (Exception e) {
						// e.printStackTrace();
						// status.fail();// 消息处理失败回滚，服务端需要重发
					// }
				// }
			// });
			// // android
			// TmcClient clientAndroid = new TmcClient("24580977", "cd78291f6d463f964ce7973204ae1ff3", "default");
			// clientAndroid.setMessageHandler(new MessageHandler() {
				// public void onMessage(Message message, MessageStatus status) {
					// try {
						// //System.out.println("======TMC长链接======");
						// // 默认不抛出异常则认为消息处理成功
						// //taobao_tae_BaichuanTradePaidDone
						// //taobao_tae_BaichuanTradeCreated
						// LOGGER.info("ANDROID淘宝长链接发送详细信息："+message.getContent());
						// LOGGER.info("ANDROID淘宝长链接发送状态信息："+message.getTopic());
						// if (message.getTopic().toString().equals("taobao_tae_BaichuanTradePaidDone")) {
							// Map<String, Object> tempData = FastJsonUtil.jsonToMap(message.getContent());
							// tempMessage(tempData);
						// }
					// } catch (Exception e) {
						// e.printStackTrace();
						// status.fail();// 消息处理失败回滚，服务端需要重发
					// }
				// }
			// });

			// try {
				// clientAndroid.connect();
				// clientIos.connect();
			// } catch (LinkException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
			// }
		// }
	// }

	public void tempMessage(Map<String, Object> tempData) {
		try {
			LOGGER.info("淘宝长链接发送消息，淘宝订单号为："+tempData.get("order_id").toString());
			String tempStringData = StringUtils.substringBefore(tempData.get("extre").toString(), ";");
			String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, String> bizParams = new HashMap<String, String>();
			
			reqParams.put("service", "order.own.fgOrderDetailFind");
			bizParams.put("order_id", StringUtils.substringAfter(tempStringData, ":"));
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultTempStr = HttpClientUtil.postShort(order_service_url, reqParams);
			Map<String, Object> resultTempMap = FastJsonUtil.jsonToMap(resultTempStr);
			if (!((String) resultTempMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultTempMap.get("error_code"), (String) resultTempMap.get("error_msg"));
			}
			
			Map<String, Object> resultTempMap2 = FastJsonUtil.jsonToMap(resultTempMap.get("data").toString());
			if(resultTempMap2.get("sale_fee").toString().equals(tempData.get("paid_fee").toString())){
				LOGGER.info("价格对比成功！");
				reqParams.clear();
				bizParams.clear();
				reqParams.put("service", "order.fgOrderConfirm");
				bizParams.put("data_source", "SJLY_01");// 领有惠淘淘领
				bizParams.put("order_id", StringUtils.substringAfter(tempStringData, ":"));
				bizParams.put("external_order_no", tempData.get("order_id").toString());
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(order_service_url, reqParams);
				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
