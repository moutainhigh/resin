package com.meitianhui.finance.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.FileUtil;
import com.meitianhui.common.util.GZipUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.JsonUtils;
import com.meitianhui.common.util.MD5Util;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.controller.FinanceController;
import com.meitianhui.finance.dao.WechatBillLogDao;
import com.meitianhui.finance.entity.FDWechatBillLog;
import com.meitianhui.finance.service.TradeService;
import com.meitianhui.finance.service.WechatPayService;
import com.meitianhui.finance.util.WechatRequest;

@Service
public class WechatPayServiceImpl implements WechatPayService {

	private static final Logger logger = Logger.getLogger(WechatPayServiceImpl.class);

	@Autowired
	private TradeService tradeService;
	@Autowired
	private WechatBillLogDao fDWechatBillLogDao;

	/** 交易等待 **/
	public static String WECHAT_TRADE_WAIT = "WECHAT_TRADE_WAIT";
	/** 交易成功 **/
	public static String WECHAT_TRADE_SUCCESS = "TRADE_SUCCESS";
	/** 交易失败 **/
	public static String WECHAT_TRADE_FAIL = "TRADE_FAIL";

	private final String rootPath = System.getProperty("mth.ops.finance.root");

	private final String basePath = rootPath.substring(0, rootPath.indexOf("ops-finance")) + "bill" + File.separator
			+ "wechat" + File.separator;

	/**
	 * 微信扫码支付(收款)
	 */
	@Override
	public void barCodePay(String type, String app_key, String app_id, String mch_id, String cert_local_path,
			String cert_password, Map<String, Object> paramsMap, String scan_flag)
					throws BusinessException, SystemException, Exception {
		try {
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			// 组装参数
			Map<String, Object> reqParams = new HashMap<String, Object>();
			reqParams.put("appid", app_id);
			reqParams.put("mch_id", mch_id);
			reqParams.put("nonce_str", IDUtil.random(10));
			reqParams.put("body", paramsMap.get("body"));
			reqParams.put("out_trade_no", out_trade_no);
			reqParams.put("total_fee",
					MoneyUtil.moneyMulOfNotPoint(paramsMap.get("total_fee").toString(), String.valueOf(100)));
			reqParams.put("spbill_create_ip", "127.0.0.1");
			reqParams.put("auth_code", paramsMap.get("auth_code"));
			// 限制使用信用卡支付
			// reqParams.put("limit_pay", "no_credit");

			reqParams.put("sign",
					MD5Util.sign(StringUtil.createLinkString(reqParams), "&key=" + app_key, "UTF-8").toUpperCase());
			logger.info(type + "-微信条码支付->request" + reqParams.toString());
			WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
			String responseString = wchatHttpsRequest.sendPost(WechatRequest.MICROPAY_REQUST_URL, reqParams);
			Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
			logger.info(type + "-微信条码支付->response" + resultMap.toString());

			Map<String, Object> query_response = new HashMap<String, Object>();
			query_response.putAll(resultMap);
			// 特定的错误直接返回失败,不执行查询操作
			if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("FAIL")) {
				String err_code = StringUtil.formatStr(resultMap.get("err_code"));
				// AUTH_CODE_ERROR 每个二维码仅限使用一次，请刷新再试
				// AUTH_CODE_INVALID 收银员扫描的不是微信支付的条码
				if (err_code.equals("AUTH_CODE_INVALID") || err_code.equals("AUTH_CODE_ERROR")
						|| err_code.equals("AUTHCODEEXPIRE")) {
					throw new BusinessException(RspCode.WECHAT_ERROR,
							StringUtil.formatStr(resultMap.get("err_code_des")));
				}
			}
			// 无论成功与否,都进行交易查询(10次轮询，每次轮询间隔5秒)
			String status = null;
			if (StringUtil.isEmpty(scan_flag)) {
				// TODO 2.9.4后的版本废除
				status = barCodePayOrderQuery(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no,
						query_response, 10, 5000);
			} else {
				// (5次轮询，每次轮询间隔4秒)
				status = barCodePayOrderQuery(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no,
						query_response, 5, 4000);
			}
			if (status.equals(WECHAT_TRADE_SUCCESS)) {
				// 交易确定
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("transaction_no", (String) paramsMap.get("out_trade_no"));
				tempMap.put("transaction_status", Constant.TRANSACTION_STATUS_CONFIRMED);
				tempMap.put("transaction_body", FastJsonUtil.toJson(query_response));
				tradeService.transactionConfirmed(tempMap, new ResultData());
			} else {
				if (StringUtil.isEmpty(scan_flag)) {
					// TODO 2.9.4后的版本废除
					// 订单撤销
					orderReverse(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no);
					throw new BusinessException(RspCode.ALIPAY_ERROR, "条码支付失败");
				} else {
					asyncLoopQuery(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no,
							query_response);
				}
			}
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 条码支付交易查询
	 * 
	 * @param paramsMap
	 * @throws BusinessException
	 * @throws SystemException
	 */
	private String barCodePayOrderQuery(String app_key, String app_id, String mch_id, String cert_local_path,
			String cert_password, String out_trade_no, Map<String, Object> query_response, Integer qry_num,
			long sleep_time) {
		String status = null;
		try {
			for (int i = 1; i <= qry_num; i++) {
				logger.info("微信条码支付,第" + i + "次查询");
				status = orderQuery(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no,
						query_response);
				// 如果交易成功或者失败了,直接返回查询结果
				if (status.equals(WECHAT_TRADE_SUCCESS) || status.equals(WECHAT_TRADE_FAIL)) {
					break;
				}
				Thread.sleep(sleep_time);
			}
		} catch (Exception e) {
			logger.error("微信条码支付查询异常", e);
		}
		return status;
	}

	/**
	 * 异步轮询订单状态
	 * 
	 * @Title: asyncLoopQuery
	 * @param app_key
	 * @param app_id
	 * @param mch_id
	 * @param cert_local_path
	 * @param cert_password
	 * @param out_trade_no
	 * @param query_response
	 * @author tiny
	 */
	private void asyncLoopQuery(final String app_key, final String app_id, final String mch_id,
			final String cert_local_path, final String cert_password, final String out_trade_no,
			final Map<String, Object> query_response) {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, Object> query_response = new HashMap<String, Object>();
					// 5次轮询，每次轮询间隔6秒
					String status = barCodePayOrderQuery(app_key, app_id, mch_id, cert_local_path, cert_password,
							out_trade_no, query_response, 8, 5000);

					// 交易成功,进行交易确认,否则进行交易取消
					String transaction_status = Constant.TRANSACTION_STATUS_ERROR;
					if (status.equals(WECHAT_TRADE_SUCCESS)) {
						// 交易确定
						transaction_status = Constant.TRANSACTION_STATUS_CONFIRMED;
					} else {
						// 订单撤销
						orderReverse(app_key, app_id, mch_id, cert_local_path, cert_password, out_trade_no);
					}
					// 交易确定
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("transaction_no", out_trade_no);
					tempMap.put("transaction_status", transaction_status);
					tempMap.put("transaction_body", FastJsonUtil.toJson(query_response));
					tradeService.transactionConfirmed(tempMap, new ResultData());
				} catch (Exception e) {
					logger.error("微信条码支付异常", e);
				}
			}
		});
	}

	/**
	 * 交易查询
	 * 
	 * @param paramsMap
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public String orderQuery(String app_key, String app_id, String mch_id, String cert_local_path, String cert_password,
			String out_trade_no, Map<String, Object> query_response) {
		String status = WECHAT_TRADE_WAIT;
		try {
			Map<String, Object> reqParams = new HashMap<String, Object>();
			WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
			reqParams.put("appid", app_id);
			reqParams.put("mch_id", mch_id);
			reqParams.put("nonce_str", IDUtil.random(10));
			reqParams.put("out_trade_no", out_trade_no);
			reqParams.put("sign",
					MD5Util.sign(StringUtil.createLinkString(reqParams), "&key=" + app_key, "UTF-8").toUpperCase());
			logger.info("微信交易查询->request" + reqParams.toString());
			String responseString = wchatHttpsRequest.sendPost(WechatRequest.MICROPAY_ORDERQUERY_URL, reqParams);
			Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
			logger.info("微信交易查询->response" + resultMap.toString());
			query_response.putAll(resultMap);
			// trade_state 一定要是成功,交易才算是完成
			if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")) {
				// SUCCESS—支付成功
				// CLOSED—已关闭
				// REVOKED—已撤销（刷卡支付）
				// PAYERROR--支付失败(其他原因，如银行返回失败)
				// REFUND—转入退款
				// NOTPAY—未支付
				// USERPAYING--用户支付中
				String trade_state = resultMap.get("trade_state") + "";
				if (trade_state.equals("SUCCESS")) {
					status = WECHAT_TRADE_SUCCESS;
				} else if (trade_state.equals("PAYERROR")) {
					status = WECHAT_TRADE_FAIL;
				} else if (trade_state.equals("CLOSED")) {
					status = WECHAT_TRADE_FAIL;
				} else if (trade_state.equals("REVOKED")) {
					status = WECHAT_TRADE_FAIL;
				} else {
					// 下面三种状态继续轮询
					// REFUND—转入退款
					// NOTPAY—未支付
					// USERPAYING--用户支付中
					status = WECHAT_TRADE_WAIT;
				}
			}
		} catch (Exception e) {
			logger.error("微信交易查询异常", e);
		}
		return status;
	}

	/**
	 * 订单撤销
	 * 
	 * @param paramsMap
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void orderReverse(final String app_key, final String app_id, final String mch_id,
			final String cert_local_path, final String cert_password, final String out_trade_no)
					throws BusinessException, SystemException, Exception {
		FinanceController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				int i = 3;
				while (i > 0) {
					try {
						Thread.sleep(1000);
						i--;
						Map<String, Object> reqParams = new HashMap<String, Object>();
						reqParams.put("appid", app_id);
						reqParams.put("mch_id", mch_id);
						reqParams.put("nonce_str", IDUtil.random(10));
						reqParams.put("out_trade_no", out_trade_no);
						reqParams.put("sign",
								MD5Util.sign(StringUtil.createLinkString(reqParams), "&key=" + app_key, "UTF-8")
										.toUpperCase());
						logger.info("微信交易撤销->request" + reqParams.toString());
						WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
						String responseString = wchatHttpsRequest.sendPost(WechatRequest.MICROPAY_REVERSE_URL,
								reqParams);
						Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
						logger.info("微信交易撤销->response" + resultMap.toString());
						// 如果失败，需要人工处理
						if (resultMap.get("return_code").equals("SUCCESS")
								&& resultMap.get("result_code").equals("SUCCESS")) {
							i = 0;
						}
					} catch (Exception e) {
						logger.error("微信扫码退款失败,交易号为【" + out_trade_no + "】的交易异常");
					}
				}
			}
		});
	}

	/**
	 * 本地生活-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> consumerWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";

			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.consumer_app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.consumer_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.consumer_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.consumer_notify_url");
			// 证书路径
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.consumer_cert_local_path");
			// 证书密码
			String cert_password = PropertiesConfigUtil.getProperty("wechat.consumer_cert_password");
			return unifiedOrderCreate("本地生活", app_id, app_key, mch_id, notify_url, cert_local_path, cert_password,
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 店东助手-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> storeWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";

			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.store_app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.store_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.store_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.store_notify_url");
			// 证书路径
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.store_cert_local_path");
			// 证书密码
			String cert_password = PropertiesConfigUtil.getProperty("wechat.store_cert_password");
			return unifiedOrderCreate("店东助手", app_id, app_key, mch_id, notify_url, cert_local_path, cert_password,
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 熟么-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> shumeWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";

			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.shume_app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.shume_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.shume_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.shume_notify_url");
			// 证书路径
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.shume_cert_local_path");
			// 证书密码
			String cert_password = PropertiesConfigUtil.getProperty("wechat.shume_cert_password");
			return unifiedOrderCreate("熟么", app_id, app_key, mch_id, notify_url, cert_local_path, cert_password,
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 搞掂-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> salesassistantWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";

			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.salesassistant_app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.salesassistant_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.salesassistant_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.salesassistant_notify_url");
			// 证书路径
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.salesassistant_cert_local_path");
			// 证书密码
			String cert_password = PropertiesConfigUtil.getProperty("wechat.salesassistant_cert_password");
			return unifiedOrderCreate("搞掂", app_id, app_key, mch_id, notify_url, cert_local_path, cert_password,
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 惠点收银-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> cashierWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";
			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.cashier_app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.cashier_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.cashier_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.cashier_notify_url");
			// 证书路径
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.cashier_cert_local_path");
			// 证书密码
			String cert_password = PropertiesConfigUtil.getProperty("wechat.cashier_cert_password");
			return unifiedOrderCreate("惠点收银", app_id, app_key, mch_id, notify_url, cert_local_path, cert_password,
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 微信支付创建预订单
	 * 
	 * @Title: unifiedOrderCreate
	 * @param app_id
	 * @param app_key
	 * @param mch_id
	 * @param notify_url
	 * @param cert_local_path
	 * @param cert_password
	 * @param spbill_create_ip
	 * @param out_trade_no
	 * @param amount
	 * @param body
	 * @return
	 * @throws Exception
	 * @author tiny
	 */
	private Map<String, Object> unifiedOrderCreate(String appName, String app_id, String app_key, String mch_id,
			String notify_url, String cert_local_path, String cert_password, String spbill_create_ip,
			String out_trade_no, String amount, String body) throws Exception {
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();

		wechatPayInfoMap.put("appid", app_id);
		wechatPayInfoMap.put("mch_id", mch_id);
		wechatPayInfoMap.put("nonce_str", IDUtil.random(10));
		wechatPayInfoMap.put("body", body);
		wechatPayInfoMap.put("out_trade_no", out_trade_no);
		wechatPayInfoMap.put("total_fee", MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100)));
		wechatPayInfoMap.put("spbill_create_ip", spbill_create_ip);
		wechatPayInfoMap.put("notify_url", notify_url);
		wechatPayInfoMap.put("trade_type", "APP");
		wechatPayInfoMap.put("sign",
				MD5Util.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toUpperCase());

		WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
		logger.info(appName + "-微信支付预订单->request:" + wechatPayInfoMap.toString());
		String responseString = wchatHttpsRequest.sendPost(WechatRequest.UNIFIEDORDER_REQUST_URL, wechatPayInfoMap);
		logger.info(appName + "-微信支付预订单->response:" + responseString);
		Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
		if (!"SUCCESS".equals(resultMap.get("return_code"))) {
			throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("return_msg"));
		}
		// 组装返回参数
		wechatPayInfoMap.clear();
		// 商户号
		wechatPayInfoMap.put("appid", resultMap.get("appid"));
		// 商户号
		wechatPayInfoMap.put("partnerid", resultMap.get("mch_id"));
		// 随机字符串
		wechatPayInfoMap.put("noncestr", resultMap.get("nonce_str"));
		// 预支付交易会话标识
		wechatPayInfoMap.put("prepayid", resultMap.get("prepay_id"));
		// 扩展字段
		wechatPayInfoMap.put("package", "Sign=WXPay");
		// 时间戳
		wechatPayInfoMap.put("timestamp", System.currentTimeMillis() / 1000 + "");
		// 将数据签名
		wechatPayInfoMap.put("sign",
				MD5Util.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toLowerCase());
		return wechatPayInfoMap;
	}

	/**
	 * 惠易定-PC微信扫码支付
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> huidianWechatPcBarCodePay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "out_trade_no", "out_trade_no", "amount", "body",
					"spbill_create_ip", "time_expire" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";
			String time_expire = paramsMap.get("time_expire") + "";
			String app_key = PropertiesConfigUtil.getProperty("wechat.huidian_app_key");
			wechatPayInfoMap.put("appid", PropertiesConfigUtil.getProperty("wechat.huidian_app_id"));
			wechatPayInfoMap.put("mch_id", PropertiesConfigUtil.getProperty("wechat.huidian_mch_id"));
			wechatPayInfoMap.put("nonce_str", IDUtil.random(10));
			wechatPayInfoMap.put("body", body);
			wechatPayInfoMap.put("out_trade_no", out_trade_no);
			wechatPayInfoMap.put("total_fee", MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100)));
			wechatPayInfoMap.put("spbill_create_ip", spbill_create_ip);
			wechatPayInfoMap.put("notify_url", PropertiesConfigUtil.getProperty("wechat.huidian_notify_url"));
			wechatPayInfoMap.put("trade_type", "NATIVE");
			wechatPayInfoMap.put("time_expire", time_expire);
			wechatPayInfoMap.put("sign", MD5Util
					.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toUpperCase());
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.huidian_cert_local_path");
			String cert_password = PropertiesConfigUtil.getProperty("wechat.huidian_cert_password");

			WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
			logger.info("惠点公众号-微信支付预订单->request:" + wechatPayInfoMap.toString());
			String responseString = wchatHttpsRequest.sendPost(WechatRequest.UNIFIEDORDER_REQUST_URL, wechatPayInfoMap);
			logger.info("惠点公众号-微信支付预订单->response:" + responseString);
			Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
			if (!"SUCCESS".equals(resultMap.get("return_code")) || !"SUCCESS".equals(resultMap.get("result_code"))) {
				throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("return_msg"));
			}
			wechatPayInfoMap.clear();
			// 交易类型
			wechatPayInfoMap.put("trade_type", resultMap.get("trade_type"));
			// 预支付交易会话标识
			wechatPayInfoMap.put("prepay_id", resultMap.get("prepay_id"));
			// 二维码链接
			wechatPayInfoMap.put("code_url", resultMap.get("code_url"));
			return wechatPayInfoMap;
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 惠易定-H5微信支付
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> huidianWechatH5Pay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "openid", "out_trade_no", "amount", "body", "spbill_create_ip", "time_expire" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";
			String time_expire = paramsMap.get("time_expire") + "";
			String openid = paramsMap.get("openid") + "";

			String app_key = PropertiesConfigUtil.getProperty("wechat.huidian_app_key");
			wechatPayInfoMap.put("appid", PropertiesConfigUtil.getProperty("wechat.huidian_app_id"));
			wechatPayInfoMap.put("openid", openid);
			wechatPayInfoMap.put("mch_id", PropertiesConfigUtil.getProperty("wechat.huidian_mch_id"));
			wechatPayInfoMap.put("nonce_str", IDUtil.random(10));
			wechatPayInfoMap.put("body", body);
			wechatPayInfoMap.put("out_trade_no", out_trade_no);
			wechatPayInfoMap.put("total_fee", MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100)));
			wechatPayInfoMap.put("spbill_create_ip", spbill_create_ip);
			wechatPayInfoMap.put("notify_url", PropertiesConfigUtil.getProperty("wechat.huidian_notify_url"));
			wechatPayInfoMap.put("trade_type", "JSAPI");
			wechatPayInfoMap.put("time_expire", time_expire);
			wechatPayInfoMap.put("sign", MD5Util
					.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toUpperCase());
			String cert_local_path = PropertiesConfigUtil.getProperty("wechat.huidian_cert_local_path");
			String cert_password = PropertiesConfigUtil.getProperty("wechat.huidian_cert_password");

			WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
			logger.info("惠点科技公众号-微信支付预订单->request:" + wechatPayInfoMap.toString());
			String responseString = wchatHttpsRequest.sendPost(WechatRequest.UNIFIEDORDER_REQUST_URL, wechatPayInfoMap);
			logger.info("惠点科技公众号-微信支付预订单->response:" + responseString);
			Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
			if (!"SUCCESS".equals(resultMap.get("return_code")) || !"SUCCESS".equals(resultMap.get("result_code"))) {
				throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("return_msg"));
			}
			wechatPayInfoMap.clear();
			// 商户号
			wechatPayInfoMap.put("appId", resultMap.get("appid"));
			// 时间戳
			wechatPayInfoMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
			// 随机字符串
			wechatPayInfoMap.put("nonceStr", resultMap.get("nonce_str"));
			// 订单详情扩展字符串
			wechatPayInfoMap.put("package", "prepay_id=" + resultMap.get("prepay_id"));
			// 签名方式
			wechatPayInfoMap.put("signType", "MD5");
			// 签名
			wechatPayInfoMap.put("paySign", MD5Util
					.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toLowerCase());
			return wechatPayInfoMap;
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void wechatBillImport(String bill_date, String app_type, String app_key, String app_id, String mch_id,
			String cert_local_path, String cert_password) throws BusinessException, SystemException, Exception {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		String uploadFilePath = null;
		try {
			tempMap.put("bill_date", bill_date);
			tempMap.put("app_type", app_type);
			Integer count_num = fDWechatBillLogDao.selectWechatBillLogCount(tempMap);
			if (count_num > 0) {
				return;
			}
			// 1 微信对账文件路径
			uploadFilePath = downloadBill(app_id, app_key, mch_id, cert_local_path, cert_password, bill_date, app_type);
			if (uploadFilePath != null) {
				// 2 解析文件并导入数据
				importDate(new File(uploadFilePath), bill_date, app_type);
			}
		} catch (BusinessException e) {
			// 没有对账单数据
			logger.warn("下载对账单失败," + e.getMsg());
		} catch (Exception e) {
			/** 如果出现异常，删除数据并且删除下载的文件 **/
			// 删除数据
			fDWechatBillLogDao.deleteWechatBillLog(tempMap);
			// 删除文件
			new File(uploadFilePath + ".gzip").delete();
			throw e;
		} finally {
			if (uploadFilePath != null) {
				uploadFilePath = uploadFilePath.substring(0, uploadFilePath.lastIndexOf(File.separator));
				FileUtil.delListFile(uploadFilePath);
			}
		}
	}

	/**
	 * 下载微信对账单
	 * 
	 * @Title: downloadBill
	 * @param app_id
	 * @param app_key
	 * @param mch_id
	 * @param cert_local_path
	 * @param cert_password
	 * @param bill_date
	 * @param bill_source
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private String downloadBill(String app_id, String app_key, String mch_id, String cert_local_path,
			String cert_password, String bill_date, String app_type)
					throws BusinessException, SystemException, Exception {
		// 下载对账单文件
		String billPath = basePath + bill_date.replace("-", "") + File.separator;
		File billFileDir = new File(billPath);
		if (!billFileDir.exists()) {
			billFileDir.mkdirs();
		}
		String uploadFilePath = billPath + bill_date.replace("-", "") + "_" + app_type + "_bill";
		File uploadFile = new File(uploadFilePath);
		if (uploadFile.exists()) {
			FileUtil.delListFile(uploadFilePath);
		}
		uploadFile.mkdirs();
		// zip压缩文件名称
		String zipPath = uploadFilePath + ".gzip";

		// 组装参数
		Map<String, Object> reqParams = new HashMap<String, Object>();
		reqParams.put("appid", app_id);
		reqParams.put("mch_id", mch_id);
		reqParams.put("nonce_str", IDUtil.random(10));
		reqParams.put("bill_date", bill_date.replace("-", ""));
		reqParams.put("bill_type", "SUCCESS");
		reqParams.put("tar_type", "GZIP");
		reqParams.put("sign",
				MD5Util.sign(StringUtil.createLinkString(reqParams), "&key=" + app_key, "UTF-8").toUpperCase());
		logger.info("下载微信对账单,request" + reqParams.toString());
		WechatRequest wchatHttpsRequest = new WechatRequest(cert_local_path, cert_password);
		wchatHttpsRequest.downloadBillPost(reqParams, zipPath);
		// 解压zip文件
		String importFilePath = uploadFilePath + File.separator + mch_id + "Suc" + bill_date + ".csv";
		GZipUtil.unZipFile(zipPath, importFilePath);
		// 返回文件路径
		return importFilePath;
	}

	/**
	 * 3 导入数据入库
	 * 
	 * @Title: importDate
	 * @param importFile
	 * @param bill_date
	 * @throws Exception
	 * @author tiny
	 */
	private void importDate(File importFile, String bill_date, String app_type) throws Exception {
		CsvReader reader = null;
		Date date = new Date();
		try {
			reader = new CsvReader(importFile.getAbsolutePath(), ',', Charset.forName("UTF-8"));
			List<FDWechatBillLog> datalist = new ArrayList<FDWechatBillLog>();
			boolean isStart = false;
			while (reader.readRecord()) {
				String record = reader.getRawRecord().replace("`", "");
				if (StringUtils.isNotEmpty(record)) {
					String[] strs = record.split(",");
					if (strs[0].contains("总交易单数")) {
						break;
					}
					if (isStart) {
						FDWechatBillLog billLog = new FDWechatBillLog();
						billLog.setLog_id(IDUtil.getUUID());
						billLog.setTrade_date(DateUtil.str2Date(strs[0].trim(), DateUtil.fmt_yyyyMMddHHmmss));
						billLog.setTrade_no(strs[5].trim());
						billLog.setOut_trade_no(strs[6].trim());
						billLog.setTrade_account(strs[7].trim());
						billLog.setOrder_amount(new BigDecimal(strs[12].trim()));
						billLog.setDesc1(strs[14].trim());
						billLog.setApp_type(app_type);
						billLog.setBill_date(bill_date);
						billLog.setStatus(Constant.STATUS_NORMAL);
						billLog.setCreated_date(date);
						billLog.setModified_date(date);
						datalist.add(billLog);
						if (datalist.size() > 6000) {
							fDWechatBillLogDao.insertFDWechatBillLog(datalist);
							datalist.clear();
						}
					}
					if (isStart == false && "﻿交易时间".equals(strs[0])) {
						isStart = true;
					}
				}
			}
			if (datalist.size() > 0) {
				fDWechatBillLogDao.insertFDWechatBillLog(datalist);
				datalist.clear();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != reader) {
				reader.close();
			}
		}
	}

	@Override
	public void billCheck(String bill_date) throws BusinessException, SystemException, Exception {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("bill_date", bill_date);
		// 删除数据,重新生成对账单
		fDWechatBillLogDao.deleteWechatBillLog(tempMap);
		fDWechatBillLogDao.insertOpsBillCheckLog(tempMap);
		fDWechatBillLogDao.insertWechatBillCheckLog(tempMap);
	}


	/**
	 * 2017-12-2 丁龙 小程序微信支付创建预订单
	 * 微信小程序-微信信息
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Object> miniAppWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			//String openid = paramsMap.get("openid") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";
			//reqMap.put("order_id", paramsMap.get("order_id"));
			String order_id = paramsMap.get("order_id") + "";

			// 小程序appid
			String app_id = PropertiesConfigUtil.getProperty("wechat.mini_app_id");
			// 微信支付的商户id
			String mch_id = PropertiesConfigUtil.getProperty("wechat.mini_mch_id");
			// 支付成功后的服务器回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.mini_notify_url");
			// 微信支付的商户密钥
			String app_key = PropertiesConfigUtil.getProperty("wechat.mini_app_key");
			

			
			String js_code = paramsMap.get("js_code") + "";
			//根据js_code去调用微信接口 获得openid
			String openid = this.getOpenidBycode(js_code);
			

			return miniAppUnifiedOrderCreate("微信小程序", app_id, app_key, mch_id, notify_url, spbill_create_ip,
					out_trade_no, amount, body,openid,order_id);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	
	/**
	 * luyou 2018-01-06
	 * @param code
	 * @return
	 */
	private String getOpenidBycode(String code) throws Exception{
		
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();

		String xml = StringUtil.mapToXML(wechatPayInfoMap);

		// 小程序ID
		String app_id = PropertiesConfigUtil.getProperty("wechat.mini_app_id");
		// 小程序密钥
		String app_key = PropertiesConfigUtil.getProperty("wechat.mini_app_secret");
		
		String url = WechatRequest.JSCODE2SESSION_URL +"?appid="+app_id+"&secret="+app_key+"&js_code="+ code + "&grant_type=authorization_code";
		logger.info("获取openid接口:"+url);
		WechatRequest wchatHttpsRequest = new WechatRequest();
		
		String responseString = wchatHttpsRequest.httpRequest(url, "POST",xml);
		logger.info("微信小程序" + "-getOpenidBycode->response:" + responseString);
		
		Map<String, Object> resultMap = JsonUtils.jsonFormatMap(responseString);
		
		if (resultMap.get("errcode")!=null) {
			throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("errmsg"));
		}
		
		return resultMap.get("openid").toString();
	}	 	
	
	

	/**
	 * 2017-12-2 丁龙 小程序微信支付创建预订单
	 * 
	 * @Title: miniAppUnifiedOrderCreate
	 * @param app_id 
	 * @param app_key  
	 * @param mch_id
	 * @param notify_url
	 * @param spbill_create_ip
	 * @param out_trade_no
	 * @param amount
	 * @param body
	 * @return
	 * @throws Exception
	 * @author tiny
	 * @param openid 
	 */
	private Map<String, Object> miniAppUnifiedOrderCreate(String appName, String app_id, String app_key, String mch_id,
			String notify_url, String spbill_create_ip, String out_trade_no, String amount, String body, String openid,String order_id)
					throws Exception {
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();

		wechatPayInfoMap.put("appid", app_id);
		wechatPayInfoMap.put("openid", openid);
		wechatPayInfoMap.put("mch_id", mch_id);
		wechatPayInfoMap.put("nonce_str", StringUtil.getRandomStringByLength(32));
		//body = new String((body).getBytes("iso-8859-1"), "utf-8")
		wechatPayInfoMap.put("body", body);
		wechatPayInfoMap.put("out_trade_no", out_trade_no);
		wechatPayInfoMap.put("total_fee", MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100)));
		wechatPayInfoMap.put("spbill_create_ip", spbill_create_ip);
		wechatPayInfoMap.put("notify_url", notify_url);
		wechatPayInfoMap.put("trade_type", "JSAPI");
		wechatPayInfoMap.put("sign",
				MD5Util.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toUpperCase());
		String xml = StringUtil.mapToXML(wechatPayInfoMap);
		WechatRequest wchatHttpsRequest = new WechatRequest();
		logger.info(appName + "-微信支付预订单->request:" + wechatPayInfoMap.toString());
		String responseString = wchatHttpsRequest.httpRequest(WechatRequest.UNIFIEDORDER_REQUST_URL,"POST", xml);
		logger.info(appName + "-微信支付预订单->response:" + responseString);
		Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
		if (!"SUCCESS".equals(resultMap.get("return_code"))) {
			throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("return_msg"));
		}
		// 组装返回参数
		wechatPayInfoMap.clear();
		// 小程序ID
		wechatPayInfoMap.put("appId", resultMap.get("appid"));
		// 随机字符串
		wechatPayInfoMap.put("nonceStr", resultMap.get("nonce_str"));
		//预支付交易会话标识
		wechatPayInfoMap.put("package", "prepay_id=" + resultMap.get("prepay_id"));
		// 时间戳
		wechatPayInfoMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		//签名方式，固定值MD5
		wechatPayInfoMap.put("signType", "MD5");
		// 将数据签名
		wechatPayInfoMap.put("paySign",
				MD5Util.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toLowerCase());
		logger.info(appName + "-微信支付返回前台的数据->response:" + wechatPayInfoMap.toString());
		return wechatPayInfoMap;
	}

	@Override
	public Map<String, Object> consumerWechatH5Pay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "out_trade_no", "amount", "body", "spbill_create_ip" });
			String out_trade_no = paramsMap.get("out_trade_no") + "";
			String amount = paramsMap.get("amount") + "";
			String body = paramsMap.get("body") + "";
			String spbill_create_ip = paramsMap.get("spbill_create_ip") + "";

			// appId
			String app_id = PropertiesConfigUtil.getProperty("wechat.h5.app_id");
			// appKey
			String app_key = PropertiesConfigUtil.getProperty("wechat.consumer_app_key");
			// 商户号
			String mch_id = PropertiesConfigUtil.getProperty("wechat.consumer_mch_id");
			// 回调url
			String notify_url = PropertiesConfigUtil.getProperty("wechat.h5_notify_url");
			return unifiedOrderCreateH5("h5订单", app_id, app_key, mch_id, notify_url, 
					spbill_create_ip, out_trade_no, amount, body);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * 微信H5支付创建预订单
	 * 
	 * @Title: unifiedOrderCreateH5
	 * @param app_id
	 * @param app_key
	 * @param mch_id
	 * @param notify_url
	 * @param cert_local_path
	 * @param cert_password
	 * @param spbill_create_ip
	 * @param out_trade_no
	 * @param amount
	 * @param body
	 * @return
	 * @throws Exception
	 * @author tiny
	 */
	private Map<String, Object> unifiedOrderCreateH5(String appName, String app_id, String app_key, String mch_id,
			String notify_url,  String spbill_create_ip,String out_trade_no, String amount, String body) throws Exception {
		Map<String, Object> wechatPayInfoMap = new HashMap<String, Object>();
		wechatPayInfoMap.put("appid", app_id);
		wechatPayInfoMap.put("mch_id", mch_id);
		wechatPayInfoMap.put("nonce_str", IDUtil.random(10));
		wechatPayInfoMap.put("body", body);
		wechatPayInfoMap.put("out_trade_no", out_trade_no);
		//MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100))
		wechatPayInfoMap.put("total_fee",MoneyUtil.moneyMulOfNotPoint(amount, String.valueOf(100)));
		wechatPayInfoMap.put("spbill_create_ip", spbill_create_ip);
		wechatPayInfoMap.put("notify_url", notify_url);
		wechatPayInfoMap.put("trade_type", "MWEB");
		wechatPayInfoMap.put("scene_info", "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://www.wanzhonghulian.cn\",\"wap_name\": \"贝壳传奇\"}}");
		wechatPayInfoMap.put("sign",
				MD5Util.sign(StringUtil.createLinkString(wechatPayInfoMap), "&key=" + app_key, "UTF-8").toUpperCase());
		String wxUrl ="https://api.mch.weixin.qq.com/pay/unifiedorder";
		String contentXml=StringUtil.mapToXML(wechatPayInfoMap);
		//获取微信返回的结果
        String weixinPost = WechatRequest.httpsRequest(wxUrl, "POST", contentXml).toString();
        logger.info("h5支付，返回结果："+weixinPost);
        //解析返回的xml
        Map<String, Object> msg =  new HashMap<String, Object>();
        Map<String, Object>  msgMap =StringUtil.xml2Map(weixinPost);
        String finish_url =URLEncoder.encode(PropertiesConfigUtil.getProperty("wechat.h5_finish_url"),"GBK");
        logger.info("finish_url:"+finish_url);
        if("SUCCESS".equals(msgMap.get("result_code"))) {
        	msg.put("result", "ok");
        	String url =  (String) msgMap.get("mweb_url")+"&redirect_url="+finish_url;
        	logger.info("url："+url);
        	msg.put("url", url);
        }else {
        	String fail_url ="http://app.beeke.vip/openapi/h5/c/activity/invoic?payfaile=true";
        	logger.info("failurl："+fail_url);
        	String url =  (String) msgMap.get("mweb_url")+"&redirect_url="+fail_url;
        	msg.put("url", url);
        	msg.put("result", "false");
        }
		return msg;
	}
	
}
