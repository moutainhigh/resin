package com.meitianhui.finance.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.TradeIDUtil;
import com.meitianhui.finance.dao.FinanceDao;
import com.meitianhui.finance.service.NotifyService;
import com.meitianhui.finance.service.OrderPayService;
import com.meitianhui.finance.service.TradeService;
import org.apache.log4j.Logger;

/**
 * 金融服务的服务层
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Service
public class OrderPayServiceImpl implements OrderPayService {
	
	private final Logger logger = Logger.getLogger(OrderPayServiceImpl.class);

	@Autowired
	public FinanceDao financeDao;
	@Autowired
	private TradeService tradeService;
	@Autowired
	public NotifyService notifyService;

	@Override
	public void wypOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "member_id", "order_no", "total_fee", "payment_way_key" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_WYP);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("amount", paramsMap.get("total_fee"));
			tempMap.put("out_trade_no", paramsMap.get("order_no"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_MTH);
			tempMap.put("detail", "我要批");
			paramsMap.put("flag", "new");
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void ldOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "payment_way_key", "activity_id",
					"activity_type", "award_name", "total_fee", "consumer_id", "stores_id" });
			String in_member_id = paramsMap.get("stores_id") + "";
			String activity_type = paramsMap.get("activity_type") + "";
			String detail = "一元购";
			if (activity_type.equals("DSK")) {
				in_member_id = Constant.MEMBER_ID_MTH;
			} else if (activity_type.equals("YYY")) {
				detail = "摇一摇";
			} else if (activity_type.equals("GGL")) {
				detail = "刮刮乐";
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_LUCK_DRAW);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("amount", paramsMap.get("total_fee"));
			tempMap.put("out_trade_no", paramsMap.get("activity_id"));
			tempMap.put("out_member_id", paramsMap.get("consumer_id"));
			tempMap.put("in_member_id", in_member_id);
			tempMap.put("detail", detail);
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void hyd2OrderWebPay(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "payment_id", "tid",
					"timeout", "price", "detail", "return_url", "req_source" });
			String req_source = paramsMap.get("req_source") + "";
			String payment_way_key = Constant.PAYMENT_WAY_15;
			if (req_source.equals("PC")) {
				payment_way_key = Constant.PAYMENT_WAY_16;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_HUIYIDING2);
			tempMap.put("payment_way_key", payment_way_key);
			tempMap.put("amount", paramsMap.get("price"));
			tempMap.put("out_trade_no", paramsMap.get("payment_id"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("return_url", paramsMap.get("return_url"));
			tempMap.put("timeout", paramsMap.get("timeout"));
			tempMap.put("extra_params", StringUtil.formatStr(paramsMap.get("extra_params")));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void hyd2OrderScanCodePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "member_id", "payment_id", "tid", "timeout", "price", "detail" });
			String payment_way_key = Constant.PAYMENT_WAY_20;
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_HUIYIDING2);
			tempMap.put("payment_way_key", payment_way_key);
			tempMap.put("amount", paramsMap.get("price"));
			tempMap.put("out_trade_no", paramsMap.get("payment_id"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("timeout", paramsMap.get("timeout"));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void hyd3OrderWebPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "payment_id", "timeout",
					"price", "detail", "return_url", "req_source" });
			String req_source = paramsMap.get("req_source") + "";
			String payment_way_key = Constant.PAYMENT_WAY_15;
			if (req_source.equals("PC")) {
				payment_way_key = Constant.PAYMENT_WAY_16;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_HUIYIDING3);
			tempMap.put("payment_way_key", payment_way_key);
			tempMap.put("amount", paramsMap.get("price"));
			tempMap.put("out_trade_no", paramsMap.get("payment_id"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("return_url", paramsMap.get("return_url"));
			tempMap.put("timeout", paramsMap.get("timeout"));
			tempMap.put("extra_params", StringUtil.formatStr(paramsMap.get("extra_params")));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void huidianWechatH5Pay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "payment_id",
					"time_expire", "price", "detail", "return_url", "openid" });
			String data_source = paramsMap.get("data_source") + "";
			// 惠易定2.0
			String order_type_key = Constant.ORDER_TYPE_HUIYIDING2;
			if (data_source.equals(Constant.DATA_SOURCE_SJLY_13)) {
				// 惠易定3.0
				order_type_key = Constant.ORDER_TYPE_HUIYIDING3;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", order_type_key);
			tempMap.put("payment_way_key", Constant.PAYMENT_WAY_17);
			tempMap.put("amount", paramsMap.get("price"));
			tempMap.put("out_trade_no", paramsMap.get("payment_id"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("return_url", paramsMap.get("return_url"));
			tempMap.put("time_expire", paramsMap.get("time_expire"));
			tempMap.put("openid", paramsMap.get("openid"));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void huidianWechatPcPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "payment_id",
					"time_expire", "price", "detail", "return_url" });
			String data_source = paramsMap.get("data_source") + "";
			// 惠易定2.0
			String order_type_key = Constant.ORDER_TYPE_HUIYIDING2;
			if (data_source.equals(Constant.DATA_SOURCE_SJLY_13)) {
				// 惠易定3.0
				order_type_key = Constant.ORDER_TYPE_HUIYIDING3;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", order_type_key);
			tempMap.put("payment_way_key", Constant.PAYMENT_WAY_18);
			tempMap.put("amount", paramsMap.get("price"));
			tempMap.put("out_trade_no", paramsMap.get("payment_id"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("return_url", paramsMap.get("return_url"));
			tempMap.put("time_expire", paramsMap.get("time_expire"));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void mainOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "order_type_key", "payment_way_key", "amount", 
							"member_id", "member_type_key", "goods_code", "order_id"  });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));//数据来源·
			tempMap.put("order_type_key", paramsMap.get("order_type_key"));
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));//支付方式
		    //根据order_id 查询 订单的 商品数量 qty
			String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
			Map<String, Object> bizParams = new HashMap<>();
			bizParams.put("order_id",  paramsMap.get("order_id"));
			Map<String, String> reqParams = new HashMap<>();
			reqParams.put("service", "order.own.fgOrderDetailFind");
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(order_service_url, reqParams);
			logger.info("传入order_id:"+paramsMap.get("order_id")+"查询订单结果:"+resultStr);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			String qty = "";
			if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
				qty = (String) dataMap.get("qty");
			} 
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			String amount = MoneyUtil.moneyMul(StringUtil.formatStr(paramsMap.get("amount")), qty);
			logger.info("调用 mainOrderPay支付,qty ->" + qty+"  传入金额："+StringUtil.formatStr(paramsMap.get("amount"))+" 计算出要付总金额："+amount);
			tempMap.put("amount", amount);//支付金额
			tempMap.put("out_member_id", paramsMap.get("member_id"));//会员ID
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			if(paramsMap.get("order_type_key").toString().equals("DDLX_07")){
				tempMap.put("detail", "自营商品支付");
			}else if (paramsMap.get("order_type_key").toString().equals("DDLX_21")){
				tempMap.put("detail", "会过商品支付");
			}	
			Map<String, String> outTradeBodyMap = new HashMap<String, String>();
			outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
			outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
			outTradeBodyMap.put("goods_code", paramsMap.get("goods_code") + "");
			outTradeBodyMap.put("order_id", paramsMap.get("order_id") + "");
			tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void huiguoCreatePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "payment_way_key", "amount", 
							"member_id", "member_type_key", "goods_code", "order_id"  });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));//数据来源·
			tempMap.put("order_type_key", Constant.ORDER_TYPE_HUIGUO);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));//支付方式
			tempMap.put("amount", paramsMap.get("amount"));//支付金额
			tempMap.put("out_member_id", paramsMap.get("member_id"));//会员ID
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", "会过");
			Map<String, String> outTradeBodyMap = new HashMap<String, String>();
			outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
			outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
			outTradeBodyMap.put("goods_code", paramsMap.get("goods_code") + "");
			outTradeBodyMap.put("order_id", paramsMap.get("order_id") + "");
//			outTradeBodyMap.put("min_num", paramsMap.get("min_num") + "");//参团人数
//			outTradeBodyMap.put("expiried_hour", paramsMap.get("expiried_hour") + "");//团购数量
//			outTradeBodyMap.put("stores_id", paramsMap.get("stores_id") + "");//门店ID
//			outTradeBodyMap.put("member_mobile", paramsMap.get("member_mobile") + "");//会员手机号码
//			outTradeBodyMap.put("received_mode", paramsMap.get("received_mode") + "");//收货方式
//			outTradeBodyMap.put("activity_type", paramsMap.get("activity_type") + "");//活动类型
			tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void tsActivityCreatePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "member_id", "member_type_key", "goods_id", "min_num",
							"expiried_hour", "stores_id", "member_mobile", "received_mode", "payment_way_key", "amount",
							"activity_type" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_TS);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("amount", paramsMap.get("amount"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", "拼团领");
			Map<String, String> outTradeBodyMap = new HashMap<String, String>();
			outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
			outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
			outTradeBodyMap.put("goods_id", paramsMap.get("goods_id") + "");
			outTradeBodyMap.put("min_num", paramsMap.get("min_num") + "");
			outTradeBodyMap.put("expiried_hour", paramsMap.get("expiried_hour") + "");
			outTradeBodyMap.put("stores_id", paramsMap.get("stores_id") + "");
			outTradeBodyMap.put("member_mobile", paramsMap.get("member_mobile") + "");
			outTradeBodyMap.put("received_mode", paramsMap.get("received_mode") + "");
			outTradeBodyMap.put("activity_type", paramsMap.get("activity_type") + "");
			tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void tsOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "member_type_key",
					"payment_way_key", "amount", "activity_id", "member_mobile", "activity_type" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_GROUP);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("amount", paramsMap.get("amount"));
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", "拼团领");
			Map<String, String> outTradeBodyMap = new HashMap<String, String>();
			outTradeBodyMap.put("member_id", paramsMap.get("member_id") + "");
			outTradeBodyMap.put("member_type_key", paramsMap.get("member_type_key") + "");
			outTradeBodyMap.put("activity_id", paramsMap.get("activity_id") + "");
			outTradeBodyMap.put("member_mobile", paramsMap.get("member_mobile") + "");
			outTradeBodyMap.put("activity_type", paramsMap.get("activity_type") + "");
			tempMap.put("out_trade_body", FastJsonUtil.toJson(outTradeBodyMap));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void mobileRechargeOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "member_id", "member_type_key",
					"member_mobile", "member_name", "mobile", "goods_id", "payment_way_key" });
			// 获取商品的售价
			String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("service", "recharger.app.mobileRechargeTypeDetailFind");
			paramsMap.put("goods_id", paramsMap.get("goods_id"));
			reqParams.put("params", FastJsonUtil.toJson(paramsMap));
			String resultStr = HttpClientUtil.post(goods_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException(RspCode.TRADE_FAIL, resultMap.get("error_msg") + "");
			}
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			Map<String, Object> typeDetailMap = (Map<String, Object>) dataMap.get("detail");
			String discount_price = typeDetailMap.get("discount_price") + "";
			String market_price = typeDetailMap.get("market_price") + "";

			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("order_type_key", Constant.ORDER_TYPE_PHONE_BILL);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("amount", discount_price);
			tempMap.put("out_member_id", paramsMap.get("member_id"));
			tempMap.put("in_member_id", Constant.MEMBER_ID_PLATFORM);
			tempMap.put("detail", market_price + "元话费充值");
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("member_id", paramsMap.get("member_id"));
			out_trade_body.put("member_type_key", paramsMap.get("member_type_key"));
			Map<String, Object> member_info = new HashMap<String, Object>();
			member_info.put("mobile", paramsMap.get("member_mobile"));
			member_info.put("name", paramsMap.get("member_name"));
			out_trade_body.put("member_info", FastJsonUtil.toJson(member_info));
			out_trade_body.put("goods_id", paramsMap.get("goods_id"));
			out_trade_body.put("payment_way_key", paramsMap.get("payment_way_key"));
			out_trade_body.put("contact_tel", paramsMap.get("mobile"));
			out_trade_body.put("sale_fee", market_price);
			out_trade_body.put("discount_fee", discount_price);
			out_trade_body.put("total_fee", discount_price);
			out_trade_body.put("goods_market_price", market_price);
			out_trade_body.put("goods_discount_price", discount_price);
			tempMap.put("out_trade_body", FastJsonUtil.toJson(out_trade_body));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}


	@Override
	public void miniAppWechatPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "data_source", "buyer_id", "seller_id", "amount","detail","out_trade_body"});
			// String data_source = paramsMap.get("data_source") + "";
			
			String order_type_key = null;
			if ("DDLX_07".equals(paramsMap.get("order_type_key") + "")) {
				// 领了么-自营
				order_type_key = Constant.ORDER_TYPE_CASHBACK;
			} else if ("DDLX_10".equals(paramsMap.get("order_type_key") + "")) {
				// 领了么-淘宝
				order_type_key = Constant.ORDER_TYPE_CASHBACK_TAOBAO;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("data_source", paramsMap.get("data_source"));
			tempMap.put("appid", paramsMap.get("appid"));
			tempMap.put("order_type_key", order_type_key);
			tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
			tempMap.put("order_id", paramsMap.get("order_id"));
			tempMap.put("amount", paramsMap.get("amount"));
			tempMap.put("out_trade_no", TradeIDUtil.getTradeNo());
			tempMap.put("out_member_id", paramsMap.get("buyer_id"));
			tempMap.put("in_member_id", paramsMap.get("seller_id"));
			tempMap.put("detail", paramsMap.get("detail"));
			tempMap.put("js_code", paramsMap.get("js_code"));
			tempMap.put("out_trade_body", FastJsonUtil.toJson(paramsMap.get("out_trade_body")));
			tradeService.orderPay(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
}
