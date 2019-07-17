package com.meitianhui.finance.constant;

import java.util.HashMap;
import java.util.Map;

import com.meitianhui.common.constant.CommonConstant;

public class Constant extends CommonConstant {

	/** 交易状态 **/
	public static final Map<String, String> TRANSACTION_STATUS_MAP = new HashMap<String, String>();

	/** 交易确认 **/
	public static final String TRANSACTION_STATUS_CONFIRMED = "confirmed";
	/** 交易取消 **/
	public static final String TRANSACTION_STATUS_CANCELLED = "cancelled";
	/** 交易暂停 **/
	public static final String TRANSACTION_STATUS_PENDING = "pending";
	/** 异常 **/
	public static final String TRANSACTION_STATUS_ERROR = "error";
	/** 交易完成 **/
	public static final String TRANSACTION_STATUS_COMPLETED = "completed";
	/** 交易关闭 **/
	public static final String TRANSACTION_STATUS_CLOSED = "closed";
	
	
	/** 业务类型 **/
	public static final Map<String, String> BUSINESS_TYPE_MAP = new HashMap<String, String>();
	/** 余额充值 **/
	public static final String BUSINESS_TYPE_BALANCERECHARGE = "JYLX_01";
	/** 余额提现 **/
	public static final String BUSINESS_TYPE_BALANCEWITHDRAW = "JYLX_02";
	/** 余额支付 **/
	public static final String BUSINESS_TYPE_BALANCEPAY = "JYLX_03";
	/** 订单支付 **/
	public static final String BUSINESS_TYPE_ORDERPAY = "JYLX_07";
	/** 订单退款 **/
	public static final String BUSINESS_TYPE_ORDERREFUND = "JYLX_08";
	/** 订单赠送 **/
	public static final String BUSINESS_TYPE_ORDERREWARD = "JYLX_09";
	/** 现金消费 **/
	public static final String BUSINESS_TYPE_CASHCONSUME = "JYLX_10";
	/** 积分赠送 **/
	public static final String BUSINESS_TYPE_BONUSREWARD = "JYLX_11";
	/** 交易冲正 **/
	public static final String BUSINESS_TYPE_TRANSACTIONREVERSE = "JYLX_12";
	/** 资产清零 **/
	public static final String BUSINESS_TYPE_ASSETCLEAR = "JYLX_13";
	/** 订单结算 **/
	public static final String BUSINESS_TYPE_ORDERSETTLEMENT = "JYLX_15";
	/** 余额提现退款 **/
	public static final String BUSINESS_TYPE_BALANCEWITHDRAWREFUND = "JYLX_16";
	/** pos机结算 **/
	public static final String BUSINESS_TYPE_POSPAY = "JYLX_17";
	
	/** 现金券结算 **/
	public static final String BUSINESS_TYPE_CASH_COUPON = "JYLX_50";
	
	/** 支付方式 **/
	public static final Map<String, String> PAYMENT_WAY_MAP = new HashMap<String, String>();
	/** 支付宝 **/
	public static final String PAYMENT_WAY_01 = "ZFFS_01";
	/** 微信 **/
	public static final String PAYMENT_WAY_02 = "ZFFS_02";
	/** 银联 **/
	public static final String PAYMENT_WAY_03 = "ZFFS_03";
	/** 现金 **/
	public static final String PAYMENT_WAY_04 = "ZFFS_04";
	/** 零钱 **/
	public static final String PAYMENT_WAY_05 = "ZFFS_05";
	/** 礼券 **/
	public static final String PAYMENT_WAY_06 = "ZFFS_06";
	/** 亲情卡 **/
	public static final String PAYMENT_WAY_07 = "ZFFS_07";
	/** 金币(红包支付) **/
	public static final String PAYMENT_WAY_08 = "ZFFS_08";
	/** 积分（礼券支付） **/
	public static final String PAYMENT_WAY_09 = "ZFFS_09";
	/** H5支付 **/
	public static final String PAYMENT_WAY_10 = "ZFFS_10";
	/** 微信条码支付(付款码) **/
	public static final String PAYMENT_WAY_11 = "ZFFS_11";
	/** 翼支付 **/
	public static final String PAYMENT_WAY_12 = "ZFFS_12";
	/** 翼支付扫码支付 **/
	public static final String PAYMENT_WAY_14 = "ZFFS_14";
	/** 支付宝Wap支付 **/
	public static final String PAYMENT_WAY_15 = "ZFFS_15";
	/** 支付宝PC支付 **/
	public static final String PAYMENT_WAY_16 = "ZFFS_16";
	/** 微信H5网页支付 **/
	public static final String PAYMENT_WAY_17 = "ZFFS_17";
	/** 微信PC扫码支付 **/
	public static final String PAYMENT_WAY_18 = "ZFFS_18";
	/** 权益券支付 **/
	public static final String PAYMENT_WAY_19 = "ZFFS_19";
	/** 支付宝扫码支付(收款码) **/
	public static final String PAYMENT_WAY_20 = "ZFFS_20";
	/** 现金的支付 **/
	public static final String PAYMENT_WAY_21 = "HYD_ZFFS_21";
	/**POS银联支付   2017/11/14 新增*/
	public static final String PAYMENT_WAY_22 = "ZFFS_22";
	/**POS支付宝支付 2017/11/14 新增*/
	public static final String PAYMENT_WAY_23 = "ZFFS_23";
	/**POS微信支付   2017/11/14 新增*/
	public static final String PAYMENT_WAY_24 = "ZFFS_24";
	/**和包扫码支付   2017/12/23  新增*/
	public static final String PAYMENT_WAY_25 = "ZFFS_25";
	/**微信小程序支付   2018/01/08 新增*/
	public static final String PAYMENT_WAY_26 = "ZFFS_26";
	
	/**现金券支付   2018/05/08 新增*/
	public static final String PAYMENT_WAY_50 = "ZFFS_50";
	
	/** 入账**/
	public static final String CATEGORY_INCOME = "income";
	/** 出账 **/
	public static final String CATEGORY_EXPENDITURE = "expenditure";
	/** 冻结 **/
	public static final String CATEGORY_FROZEN = "frozen";
	/** 解冻 **/
	public static final String CATEGORY_ACTIVATED = "activated";
	
	
	/** 我要批**/
	public static final String ORDER_TYPE_WYP = "DDLX_01";
	/** 优惠券 **/
	public static final String ORDER_TYPE_COUPON = "DDLX_02";
	/** 一元抽奖 **/
	public static final String ORDER_TYPE_LUCK_DRAW = "DDLX_03";
	/** 精选特卖 **/
	public static final String ORDER_TYPE_LOCAL_SALE = "DDLX_04";
	/** 名品汇交易**/
	public static final String ORDER_TYPE_GOLD_EXCHANGE = "DDLX_05";
	/** 惠易定2.0交易 **/
	public static final String ORDER_TYPE_HUIYIDING2 = "DDLX_06";
	/** 领了么-自营 **/
	public static final String ORDER_TYPE_CASHBACK = "DDLX_07";
	/** 团购预售 **/
	public static final String ORDER_TYPE_PRESELL = "DDLX_09";
	/** 领了么-淘宝 **/
	public static final String ORDER_TYPE_CASHBACK_TAOBAO = "DDLX_10";
	/** 惠易定3.0交易  **/
	public static final String ORDER_TYPE_HUIYIDING3 = "DDLX_11";
	/** 充话费 **/
	public static final String ORDER_TYPE_PHONE_BILL = "DDLX_12";
	/** 火车票  **/
	public static final String ORDER_TYPE_TICKET = "DDLX_13";
	/** 伙拼团活动创建 **/
	public static final String ORDER_TYPE_TS = "DDLX_14";
	/** 伙拼团 **/
	public static final String ORDER_TYPE_GROUP = "DDLX_15";
	/** 任务管理 **/
	public static final String ORDER_TYPE_TASK = "DDLX_16";
	/** POS收银 **/
	public static final String ORDER_TYPE_POS_CASHIER = "DDLX_17";
	/** 充值 **/
	public static final String ORDER_TYPE_RECHARGE = "DDLX_18";
	/** 提现 **/
	public static final String ORDER_TYPE_BALANCEWITHDRAW = "DDLX_19";
	/** 会员权益 **/
	public static final String ORDER_TYPE_POINT = "DDLX_20";
	/** 会过 **/
	public static final String ORDER_TYPE_HUIGUO = "DDLX_21";
	
	/** 现金券 **/
	public static final String ORDER_TYPE_CASH_COUPON = "DDLX_50";

	/** 注册赠送68贝壳 **/
	public static final String ORDER_TYPE_REGISTER = "DDLX_23";


	

	
	/** 收款码 **/
	public static final String SECURITY_TYPE_RECEIVED = "R";
	/** 付款码 **/
	public static final String SECURITY_TYPE_PAYMENT = "P";
	
	/** 假期 **/
	public static final Map<String, String> HOLIDAY_MAP = new HashMap<String, String>();

	//**consumer（消费者/用户）**/
	public static final String MEMBER_TYPE_KEY_CONSUMER = "consumer";
	/**stores（门店/商家）**/
	public static final String MEMBER_TYPE_KEY_STORES = "stores";
	/**supplier（供应商/企业）**/
	public static final String MEMBER_TYPE_KEY_SUPPLIER = "supplier";
	/**company（公司)**/
	public static final String MEMBER_TYPE_KEY_COMPANY = "company";

	static {

		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_CONFIRMED, "确认");
		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_CANCELLED, "取消");
		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_PENDING, "暂停");
		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_ERROR, "异常");
		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_COMPLETED, "完成");
		TRANSACTION_STATUS_MAP.put(TRANSACTION_STATUS_CLOSED, "关闭");

		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_BALANCERECHARGE, "余额充值");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_BALANCEWITHDRAW, "余额提现");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_BALANCEPAY, "余额支付");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_ORDERPAY, "订单支付");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_ORDERREFUND, "订单退款");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_ORDERREWARD, "订单赠送");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_CASHCONSUME, "现金消费");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_BONUSREWARD, "积分赠送");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_TRANSACTIONREVERSE, "交易冲正");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_ASSETCLEAR, "资产清零");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_ORDERSETTLEMENT, "订单结算");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_POSPAY, "POS支付");
		BUSINESS_TYPE_MAP.put(BUSINESS_TYPE_CASH_COUPON, "现金券消费");
		
		

		PAYMENT_WAY_MAP.put(PAYMENT_WAY_01, "支付宝");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_02, "微信");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_03, "银联");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_04, "现金");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_05, "零钱");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_06, "礼券");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_07, "亲情卡");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_08, "金币");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_09, "积分");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_10, "支付宝条码");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_11, "微信条码");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_12, "翼支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_14, "翼支付条码");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_15, "支付宝Wap支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_16, "支付宝PC支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_17, "微信H5网页支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_18, "微信PC扫码支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_19, "权益券支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_20, "支付宝扫码支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_21, "现金支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_22, "POS银联支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_23, "POS支付宝支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_24, "POS微信支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_25, "和包扫码支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_26, "微信小程序支付");
		PAYMENT_WAY_MAP.put(PAYMENT_WAY_50, "现金券支付");
		
		HOLIDAY_MAP.put("2018-06-16", "端午节");
		HOLIDAY_MAP.put("2018-06-17", "端午节");
		HOLIDAY_MAP.put("2018-06-18", "端午节");
		HOLIDAY_MAP.put("2018-10-01", "国庆节");
		HOLIDAY_MAP.put("2018-10-02", "国庆节");
		HOLIDAY_MAP.put("2018-10-03", "国庆节");
		HOLIDAY_MAP.put("2018-09-22", "中秋节");
		HOLIDAY_MAP.put("2018-09-23", "中秋节");
		HOLIDAY_MAP.put("2018-09-24", "中秋节");
		
	}

}
