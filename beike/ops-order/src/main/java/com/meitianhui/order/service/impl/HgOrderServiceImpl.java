package com.meitianhui.order.service.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.order.constant.Constant;
import com.meitianhui.order.constant.OrderIDUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.controller.OrderController;
import com.meitianhui.order.dao.FgBlacklistDao;
import com.meitianhui.order.dao.FgOrderDao;
import com.meitianhui.order.dao.HgOrderDao;
import com.meitianhui.order.dao.OrderDao;
import com.meitianhui.order.entity.*;
import com.meitianhui.order.service.HgOrderService;
import com.meitianhui.order.service.OrderService;
import com.meitianhui.order.service.PsOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会过
 * 
 * @author luyou
 *
 */
@SuppressWarnings("unchecked")
@Service
public class HgOrderServiceImpl implements HgOrderService {

	private static final Logger logger = Logger.getLogger(HgOrderServiceImpl.class);

	public static String GOODS_SERVICE_URL = PropertiesConfigUtil.getProperty("goods_service_url");
	
	@Autowired
	public RedisUtil redisUtil;
	@Autowired
	private DocUtil docUtil;

	@Autowired
	private OrderDao orderDao;
	@Autowired
	private FgOrderDao fgOrderDao;
	@Autowired
	private HgOrderDao hgOrderDao;

	@Autowired
	private FgBlacklistDao fgBlacklistDao;

	@Autowired
	private OrderService orderService;
	@Autowired
	private PsOrderService psOrderService;

	
	
	@Override
	public void handleOwnOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","transaction_no"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("external_buyer_name", order.get("member_mobile"));//自营的淘宝关联名字用会员手机号码来做标识查询
		
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}
	
	@Override
	public void handleHuiguoOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id","transaction_no"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("external_buyer_name", order.get("member_mobile"));//自营的淘宝关联名字用会员手机号码来做标识查询
		
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}
	
	@Override
	public void handleFgOrderCreateNotity(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "payment_way_key",
				"delivery_address", "contact_person", "contact_tel" ,"transaction_no" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("order_id", paramsMap.get("order_id"));
		//tempMap.put("order_type", Constant.ORDER_TYPE_MEITIANHUI);
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_ERROE, "订单不存在");
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单状态异常");
		}
		tempMap.clear();
		tempMap.put("order_id", order.get("order_id"));
		tempMap.put("status", Constant.ORDER_CONFIRMED);
		tempMap.put("external_order_no", order.get("order_no"));
		tempMap.put("transaction_no", paramsMap.get("transaction_no"));
		tempMap.put("payment_way_key", paramsMap.get("payment_way_key"));
		tempMap.put("remark", StringUtil.formatStr(paramsMap.get("remark")));
		tempMap.put("delivery_address", StringUtil.formatStr(paramsMap.get("delivery_address")));
		tempMap.put("contact_person", StringUtil.formatStr(paramsMap.get("contact_person")));
		tempMap.put("contact_tel", StringUtil.formatStr(paramsMap.get("contact_tel")));
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_no", order.get("order_no"));
		result.setResultData(paramMap);
	}

	@Override
	public void freeGetValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		String already_got = "false";
		String in_blacklist = "false";
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "mobile" });
		String goods_code = paramsMap.get("goods_code") + "";
		String mobile = paramsMap.get("mobile") + "";
		// 查询会员是否已经领过商品了(一个月只能领取一次)
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_code", goods_code);
		tempMap.put("member_mobile", mobile);
		tempMap.put("status_neq", Constant.ORDER_CANCELLED);
		String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
		tempMap.put("order_date_start", order_date_start);
		tempMap.put("order_date_end", new_date);
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
		if (orderList.size() > 0) {
			already_got = "true";
		}

		// 检测手机号是不是已经列入黑名单了
		tempMap.put("account", paramsMap.get("mobile"));
		tempMap.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(tempMap);
		if (orderItemList.size() > 0) {
			in_blacklist = "true";
		}
		resultDataMap.put("already_got", already_got);
		resultDataMap.put("in_blacklist", in_blacklist);
		result.setResultData(resultDataMap);
	}

	
	@Override
	public void consumerOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_code", goods_code);
			tempMap.put("member_mobile", mobile);
			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			tempMap.put("order_date_start", order_date_start);
			tempMap.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");
			
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "psGoods.consumer.freeGetGoodsTaobaoSalesEdit");
			bizParams.put("goods_id", goods_id);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}

			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

				// 消费者扣除的金币(支付金额-优惠金额)
				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
				if (consumer_pay_gold_amount > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("member_id", consumer_id);
					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
					out_trade_body.put("title", goodsDataMap.get("title"));
					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
		
	}
	
	@Override
	public void huiguoOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("goods_code", goods_code);
//			tempMap.put("member_mobile", mobile);
//			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
//			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
//			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
//			tempMap.put("order_date_start", order_date_start);
//			tempMap.put("order_date_end", new_date);
//			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
//			if (orderList.size() > 0) {
//				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
//						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
//			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");

			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "psGoods.consumer.freeGetGoodsTaobaoSalesEdit");
			bizParams.put("goods_id", goods_id);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}

			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

//会过商品订单  不扣除金币了
				// 消费者扣除的金币(支付金额-优惠金额)
//				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
//				if (consumer_pay_gold_amount > 0) {
//					// 交易的订单类型
//					String order_type_key = "DDLX_07";
//					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
//						order_type_key = "DDLX_10";
//					}
//					Map<String, Object> out_trade_body = new HashMap<String, Object>();
//					out_trade_body.put("member_id", consumer_id);
//					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
//					out_trade_body.put("title", goodsDataMap.get("title"));
//					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
//					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
//							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
//				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
		
	}
	
	
	@Override
	public void OwnOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile",
							"order_type", "data_source", "delivery_address", 
							"contact_person", "contact_tel" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String orderType = StringUtil.formatStr(paramsMap.get("order_type"));
			String dataSource = StringUtil.formatStr(paramsMap.get("data_source"));
			String delivery_address = StringUtil.formatStr(paramsMap.get("delivery_address"));
			String remark = StringUtil.formatStr(paramsMap.get("remark"));
			String contact_person = StringUtil.formatStr(paramsMap.get("contact_person"));
			String contact_tel = StringUtil.formatStr(paramsMap.get("contact_tel"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			String payment_way_key = StringUtil.formatStr(paramsMap.get("payment_way_key"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			if(payment_way_key == null || payment_way_key == "" || payment_way_key == "null" ){
				payment_way_key = "ZFFS_01";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("goods_code", goods_code);
//			tempMap.put("member_mobile", mobile);
//			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
//			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
//			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
//			tempMap.put("order_date_start", order_date_start);
//			tempMap.put("order_date_end", new_date);
//			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
//			if (orderList.size() > 0) {
//				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
//						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
//			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type="";
			if(orderType.equals("taobao")){
				order_type = Constant.ORDER_TYPE_TAOBAO;
			}else if(orderType.equals("meitianhui")){
				order_type = Constant.ORDER_TYPE_MEITIANHUI;
			}else if(orderType.equals("huiguo")){
				order_type = Constant.ORDER_TYPE_HUIGUO;
			}
			
			

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");

			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "psGoods.consumer.freeGetGoodsTaobaoSalesEdit");
			bizParams.put("goods_id", goods_id);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			
			try {
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key(payment_way_key);
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(contact_tel);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source(dataSource);
				order.setDelivery_address(delivery_address);
				order.setRemark(remark);
				order.setQty(qty);
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

// 会过商品订单不扣除 金币了
//				// 消费者扣除的金币(支付金额-优惠金额)
//				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
//				if (consumer_pay_gold_amount > 0) {
//					// 交易的订单类型
//					String order_type_key = "DDLX_07";
//					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
//						order_type_key = "DDLX_10";
//					}
//					Map<String, Object> out_trade_body = new HashMap<String, Object>();
//					out_trade_body.put("member_id", consumer_id);
//					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
//					out_trade_body.put("title", goodsDataMap.get("title"));
//					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
//					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
//							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
//				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
		
	}


	
	@Override
	public void consumerFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "consumer_id", "recommend_stores_id", "mobile" });
			String goods_code = paramsMap.get("goods_code") + "";
			String consumer_id = paramsMap.get("consumer_id") + "";
			String recommend_stores_id = paramsMap.get("recommend_stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
			String qty = StringUtil.formatStr(paramsMap.get("qty"));
			
			if(qty == null || qty == "" || qty == "null" ){
				qty = "1";
			}
			
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.ORDER_ERROE, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 查询会员是否已经领过商品了(一个月只能领取一次)
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_code", goods_code);
			tempMap.put("member_mobile", mobile);
			tempMap.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			tempMap.put("order_date_start", order_date_start);
			tempMap.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}
			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);

			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}
			String valid_thru = StringUtil.formatStr(goodsDataMap.get("valid_thru"));
			if (StringUtil.isNotEmpty(valid_thru)) {
				if (DateUtil.str2Date(valid_thru, DateUtil.fmt_yyyyMMddHHmmss).getTime() > new Date().getTime()) {
					throw new BusinessException(RspCode.ORDER_ERROE, "商品尚未开始售卖,请耐心等待");
				}
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type = Constant.ORDER_TYPE_TAOBAO;

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				tempMap.clear();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 订单编号
			String order_no = OrderIDUtil.getOrderNo();
			// 店东佣金
			String sub_amount = MoneyUtil.moneySub(market_price, discount_price);
			Integer commission = new BigDecimal(discount_price).intValue();

			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");

			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "psGoods.consumer.freeGetGoodsTaobaoSalesEdit");
			bizParams.put("goods_id", goods_id);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			
			try {
				if ("meitianhui".equals(paramsMap.get("order_type") + "")) {
					order_type = paramsMap.get("order_type") + "";
				}
				Date date = new Date();
				// 进行订单创建
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(order_no);
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(consumer_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_CONSUMER);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", nick_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				if ("meitianhui".equals(paramsMap.get("order_type") + "")) {
					order.setPayment_way_key("ZFFS_02");
					order.setData_source("SJLY_17");
				} else {
					order.setPayment_way_key("ZFFS_01");
					order.setData_source("SJLY_01");
				}
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_amount));
				order.setContact_tel(mobile);
				order.setContact_person(nick_name);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setQty(qty);
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setTotal_fee(new BigDecimal(sub_amount));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				orderItem.setTaobao_link(StringUtil.formatStr(goodsDataMap.get("taobao_link")));
				fgOrderDao.insertFgOrderItem(orderItem);
				// 创建消费者领样记录
				FgOrderExtra fgOrderExtra = new FgOrderExtra();
				fgOrderExtra.setOrder_id(order.getOrder_id());
				fgOrderExtra.setConsumer_id(consumer_id);
				fgOrderExtra.setStores_id(recommend_stores_id);
				fgOrderExtra.setCreated_date(date);
				Map<String, Object> remarkMap = new HashMap<String, Object>();
				remarkMap.put("market_price", market_price);
				remarkMap.put("discount_price", discount_price);
				remarkMap.put("stores_commission", commission + "");
				fgOrderExtra.setRemark(FastJsonUtil.toJson(remarkMap));
				fgOrderDao.insertFgOrderExtra(fgOrderExtra);

				// 消费者扣除的金币(支付金额-优惠金额)
				Integer consumer_pay_gold_amount = new BigDecimal(sub_amount).intValue();
				if (consumer_pay_gold_amount > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (data_source.equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("member_id", consumer_id);
					out_trade_body.put("goods_id", goodsDataMap.get("goods_id"));
					out_trade_body.put("title", goodsDataMap.get("title"));
					out_trade_body.put("consumer_pay_gold_amount", consumer_pay_gold_amount);
					orderService.balancePay("SJLY_01", consumer_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(consumer_pay_gold_amount), order_type_key, order_no, "淘淘领", out_trade_body);
				}

				// 把生成的订单号发送给前端
				tempMap.clear();
				tempMap.put("order_id", order.getOrder_id());
				result.setResultData(tempMap);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
			// 建议门店与会员的关联关系
			orderService.storesMemberRelCreate(recommend_stores_id, consumer_id);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	@Override
	public void storesFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_code", "stores_id", "mobile" });
			String goods_code = paramsMap.get("goods_code") + "";
			String member_id = paramsMap.get("stores_id") + "";
			String mobile = paramsMap.get("mobile") + "";
			// 手机号商品加锁
			lockKey = "[freeGetRecordCreate]_" + goods_code + mobile;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 查询会员是否已经领过商品了
			bizParams.clear();
			bizParams.put("goods_code", goods_code);
			bizParams.put("member_mobile", mobile);
			bizParams.put("status_neq", Constant.ORDER_CANCELLED);
			String new_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
			String order_date_start = DateUtil.addDate(new_date, DateUtil.fmt_yyyyMMdd, 2, -1);
			bizParams.put("order_date_start", order_date_start);
			bizParams.put("order_date_end", new_date);
			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrderListCheck(bizParams);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.FREE_GET_TIMES_ERROR,
						RspCode.MSG.get(RspCode.FREE_GET_TIMES_ERROR));
			}

			// 查询门店联系信息(redis缓存)
			Map<String, Object> storesDataMap = null;
			String storeForOrderFind_key = "[storesFreeGetRecordCreate_storeForOrderFind]_" + member_id;
			Object obj_storesData = redisUtil.getObj(storeForOrderFind_key);
			if (obj_storesData != null) {
				storesDataMap = (Map<String, Object>) obj_storesData;
			} else {
				// 查询门店信息
				reqParams.put("service", "member.storeForOrderFind");
				bizParams.put("stores_id", member_id);
				reqParams.put("params", FastJsonUtil.toJson(bizParams));
				String resultStr = HttpClientUtil.postShort(OrderServiceImpl.MEMBER_SERVICE_URL, reqParams);
				resultMap = FastJsonUtil.jsonToMap(resultStr);
				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
					throw new BusinessException((String) resultMap.get("error_code"),
							(String) resultMap.get("error_msg"));
				}
				storesDataMap = (Map<String, Object>) resultMap.get("data");
				redisUtil.setObj(storeForOrderFind_key, storesDataMap, 1800);
			}

			String stores_name = StringUtil.formatStr(storesDataMap.get("stores_name"));
			String contact_person = StringUtil.formatStr(storesDataMap.get("contact_person"));

			// 获取商品信息
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			String status = StringUtil.formatStr(goodsDataMap.get("status"));
			if (!status.equals("on_shelf")) {
				throw new BusinessException(RspCode.ORDER_ERROE, "商品已下架");
			}

			// 商品id
			String goods_id = goodsDataMap.get("goods_id") + "";
			// 市场价
			String market_price = goodsDataMap.get("market_price") + "";
			// 优惠价
			String discount_price = goodsDataMap.get("discount_price") + "";
			// 商品来源
			String data_source = StringUtil.formatStr(goodsDataMap.get("data_source"));
			// 订单类型
			String order_type = "taobao";
			// 返现金额
			String sub_price = MoneyUtil.moneySub(market_price, discount_price);

			/*** 如果是新人专享商品，会员必须是第一次领用才能下单 modify by dingshuo 2016-10-11 **/
			// 商品标签
			String label_promotion = StringUtil.formatStr(goodsDataMap.get("label_promotion"));
			if (label_promotion.contains(Constant.ORDER_LABEL_FRESHMAN)) {
				// 查询会员是否已经领过商品了
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("member_mobile", mobile);
				tempMap.put("status_neq", Constant.ORDER_CANCELLED);
				List<Map<String, Object>> freshmanOrderList = fgOrderDao.selectFgOrder(tempMap);
				if (freshmanOrderList.size() > 0) {
					throw new BusinessException(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE,
							RspCode.MSG.get(RspCode.FREE_GET_ONLY_FRESHMAN_RECEIVE));
				}
			}
			// 扣库存
			orderService.psGoodsSaleQtyDeduction(goods_id, "1");
			try {
				// 订单赠送交易成功后，进行订单创建
				Date date = new Date();
				FgOrder order = new FgOrder();
				order.setOrder_id(IDUtil.getUUID());
				order.setOrder_no(OrderIDUtil.getOrderNo());
				order.setDesc1(goodsDataMap.get("title") + "");
				order.setMember_id(member_id);
				order.setMember_type_key(Constant.MEMBER_TYPE_STORES);
				Map<String, Object> memberInfo = new HashMap<String, Object>();
				memberInfo.put("name", stores_name);
				memberInfo.put("mobile", mobile);
				order.setMember_info(FastJsonUtil.toJson(memberInfo));
				order.setMember_mobile(mobile);
				order.setItem_num(1);
				order.setPayment_way_key("ZFFS_01");
				order.setSale_fee(new BigDecimal(market_price));
				order.setDiscount_fee(new BigDecimal(discount_price));
				order.setTotal_fee(new BigDecimal(sub_price));
				order.setContact_tel(mobile);
				order.setContact_person(contact_person);
				order.setCreated_date(date);
				order.setModified_date(date);
				order.setStatus(Constant.ORDER_PROCESSING);
				order.setSettle_status(Constant.ORDER_SETTLE_PENDING);
				order.setOrder_date(date);
				order.setOrder_type(order_type);
				order.setData_source("SJLY_02");
				fgOrderDao.insertFgOrder(order);
				// 创建领了么订单
				FgOrderItem orderItem = new FgOrderItem();
				orderItem.setGoods_id(goodsDataMap.get("goods_id") + "");
				orderItem.setGoods_code(goodsDataMap.get("goods_code") + "");
				orderItem.setGoods_title(goodsDataMap.get("title") + "");
				orderItem.setGoods_pic_info(StringUtil.formatStr(goodsDataMap.get("pic_info")));
				orderItem.setSpecification(StringUtil.formatStr(goodsDataMap.get("specification")));
				orderItem.setQty(1);
				orderItem.setGoods_unit(StringUtil.formatStr(goodsDataMap.get("goods_unit")));
				orderItem.setSale_price(new BigDecimal(market_price));
				orderItem.setDiscount_fee(new BigDecimal(discount_price));
				orderItem.setTotal_fee(new BigDecimal(sub_price));
				orderItem.setSettled_price(new BigDecimal(goodsDataMap.get("settled_price") + ""));
				orderItem.setService_fee(new BigDecimal(goodsDataMap.get("service_fee") + ""));
				orderItem.setOrder_item_id(IDUtil.getUUID());
				orderItem.setOrder_id(order.getOrder_id());
				orderItem.setSupplier_id(StringUtil.formatStr(goodsDataMap.get("supplier_id")));
				orderItem.setSupplier(StringUtil.formatStr(goodsDataMap.get("supplier")));
				orderItem.setManufacturer(StringUtil.formatStr(goodsDataMap.get("manufacturer")));
				orderItem.setCreated_date(date);
				orderItem.setModified_date(date);
				Map<String, Object> orderItemMap = new HashMap<String, Object>();
				orderItemMap.put("data_source", data_source);
				orderItemMap.put("label_promotion", label_promotion);
				orderItem.setRemark(FastJsonUtil.toJson(orderItemMap));
				fgOrderDao.insertFgOrderItem(orderItem);
			} catch (Exception e) {
				// 还原库存
				orderService.psGoodsSaleQtyRestore(goods_id, "1");
				throw e;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	@Override
	public void handleFreeGetOrderImport(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		RedisLock pointLock = null;
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "goods_code", "qty", "contact_person", "contact_tel", "delivery_address",
							"logistics", "data_source", "external_order_no", "external_buyer_name", "amount" });
			String goods_code = paramsMap.get("goods_code") + "";
			String external_buyer_name = StringUtil.formatStr(paramsMap.get("external_buyer_name"));
			// redis加锁,同一个商品一个买家只能有一个进程处理
			String lockKey = "freeGetOrderImport_" + goods_code + external_buyer_name;
			lock = new RedisLock(redisUtil, lockKey, 30 * 1000); // 30秒超时时间
			lock.lock();
			
			Map<String, Object> tempMap = new HashMap<String, Object>();
			List<Map<String, Object>> orderDetailList = new ArrayList<Map<String, Object>>();
			
			String temp_order_type = StringUtil.formatStr(paramsMap.get("order_type"));	
			if(temp_order_type == null || temp_order_type == "" || temp_order_type == "null"){
				throw new BusinessException(RspCode.ORDER_ERROE, "订单类型为空");
			}
			if(temp_order_type.equals("自营") || temp_order_type.equals("会过")){	
				String transaction_no  =  StringUtil.formatStr(paramsMap.get("transaction_no"));
				if(transaction_no == null || transaction_no == "" || transaction_no == "null"){
					throw new BusinessException(RspCode.ORDER_ERROE, "商户订单号为空");
				}
				tempMap.put("transaction_no", transaction_no);
				tempMap.put("status", Constant.ORDER_CLOSED);
				String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
				tempMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 2, -1));
				tempMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -1));
				orderDetailList = hgOrderDao.selectHgOrderDetailForImport(tempMap);
				logger.info("订单导入"+temp_order_type+"商品返款" +"transaction_no="+paramsMap.get("transaction_no")+"goods_code="+goods_code);
			}else{
				// 如果是淘系的商品 则判断买家淘宝账号是否已经领过此商品
				tempMap.put("goods_code", goods_code);
				tempMap.put("external_buyer_name", external_buyer_name);
				tempMap.put("status", Constant.ORDER_CLOSED);
				String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
				tempMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 2, -1));
				tempMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -1));
				orderDetailList = fgOrderDao.selectFgOrderDetailForImport(tempMap);
				logger.info("订单导入"+temp_order_type+"商品返款    淘淘领" +"goods_code="+goods_code);
			}
			
			if (orderDetailList.size() > 0) {
				throw new BusinessException(RspCode.ORDER_ERROE, "用户重复领取");
			}
			
			// 通过淘宝订单号和商品编号查询对应的预订单是否存在(状态要为确认)
			tempMap.clear();
			tempMap.put("goods_code", goods_code);
			tempMap.put("external_order_no", paramsMap.get("external_order_no"));
			tempMap.put("status", Constant.ORDER_CONFIRMED);
			orderDetailList = fgOrderDao.selectFgOrderDetailForImport(tempMap);
			if (orderDetailList.size() == 0) {
				throw new BusinessException(RspCode.ORDER_NOT_EXIST, "订单未确定或不存在");
			}
			Map<String, Object> orderDetail = orderDetailList.get(0);
			String member_mobile = orderDetail.get("member_mobile") + "";
			// 检测淘宝用户是否在黑名单中,如果是则对应的手机号也加入黑名单
			// freeGetOrderImportCheck(external_buyer_name, member_mobile);
			// 2017-8-9 丁忍修改：取消自动把每天惠账号加入黑名单

			// 返现金额
			String amount = "0.00";
			String order_type = StringUtil.formatStr(orderDetail.get("order_type"));
			String order_no = orderDetail.get("order_no") + "";
			amount = orderDetail.get("total_fee") + "";
			String member_id = orderDetail.get("member_id") + "";

			String temp_member_type = orderDetail.get("member_type_key") + "";
			if (temp_member_type.equals(Constant.MEMBER_TYPE_CONSUMER)) {
				// redis加锁,同一个买家只能有一个进程处理
				String pointLockKey = "freeGetOrderImport_" + external_buyer_name;
				pointLock = new RedisLock(redisUtil, pointLockKey, 30 * 1000); // 30秒超时时间
				pointLock.lock();
				//
				tempMap.clear();
				tempMap.put("member_id", member_id);
				tempMap.put("status", Constant.ORDER_CLOSED);
				List<Map<String, Object>> tempList1 = new ArrayList<Map<String, Object>>();
				tempList1 = fgOrderDao.selectFgOrder(tempMap);
				// 当订单数量小于1时，证明该会员已完成的订单为0，所以可以给对应的推荐人加积分
				if (tempList1.size() < 1) {
					// ***********************************
					// 1.调用查询推荐人接口，获取推荐人信息
					// ***********************************
					String user_service_url = PropertiesConfigUtil.getProperty("member_service_url");
					Map<String, String> reqParams = new HashMap<String, String>();
					Map<String, String> bizParams = new HashMap<String, String>();
					reqParams.put("service", "consumer.consumer.userRecommendFind");
					bizParams.put("member_id", member_id);
					reqParams.put("params", FastJsonUtil.toJson(bizParams));
					String resultStr = HttpClientUtil.postShort(user_service_url, reqParams);
					Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
					if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
						// ********************************************************************
						// 2. 进行交易冲正 会员加积分 公司减积分
						// ********************************************************************
						Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
						String reference_id = (String) dateMap.get("reference_id");

						if (reference_id != null) {
							Map<String, Object> out_trade_body = new HashMap<String, Object>();
							Integer point = new BigDecimal(10).intValue(); // 默认加5积分

							out_trade_body.clear();
							out_trade_body.put("reference_id", reference_id);
							out_trade_body.put("order_no", order_no);
							out_trade_body.put("amount", point);
							out_trade_body.put("desc1", orderDetail.get("desc1"));
							orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, reference_id, "ZFFS_09",
									new BigDecimal(point), "DDLX_20", order_no, "推荐人赠送积分", out_trade_body);
						}

						// String finance_service_url =
						// PropertiesConfigUtil.getProperty("finance_service_url");
						// reqParams.clear();
						// bizParams.clear();
						// reqParams.put("service",
						// "finance.consumer.memberPointEdit");
						// bizParams.put("member_id", reference_id);
						// bizParams.put("booking_mark", "income");
						// bizParams.put("point_values", "5");
						// bizParams.put("member_type_key", "consumer");
						//
						// Map<String, Object> remarkMap = new HashMap<String,
						// Object>();
						// remarkMap.put("transaction_no", "0");
						// remarkMap.put("business_type_key", "0");
						// remarkMap.put("payment_way_key", "0");
						// remarkMap.put("detail", "推荐用户赠送积分");
						// remarkMap.put("transaction_member_id", "0");
						// remarkMap.put("transaction_member_type_key", "0");
						// remarkMap.put("transaction_member_name", "0");
						// remarkMap.put("transaction_member_contact", "0");
						//
						//
						// bizParams.put("remark",
						// FastJsonUtil.toJson(remarkMap));
						// reqParams.put("params",
						// FastJsonUtil.toJson(bizParams));
						// String tempResultStr =
						// HttpClientUtil.postShort(finance_service_url,
						// reqParams);
						// Map<String, Object> TempResultMap =
						// FastJsonUtil.jsonToMap(tempResultStr);
						// if (((String)
						// TempResultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC))
						// {
						// System.out.println("添加积分成功");
						// }
					}
				}
			}

			// 更新领了么订单信息
			tempMap.clear();
			tempMap.put("data_source", StringUtil.formatStr(paramsMap.get("data_source")));
			tempMap.put("external_buyer_name", external_buyer_name);
			tempMap.put("external_buyer_pay_no", StringUtil.formatStr(paramsMap.get("external_buyer_pay_no")));
			tempMap.put("status", Constant.ORDER_CLOSED);
			tempMap.put("logistics", paramsMap.get("logistics") + "");
			tempMap.put("modified_date",
					DateUtil.date2Str((Date) orderDetail.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("order_id", orderDetail.get("order_id") + "");
			// 非每天惠的才更新收货信息
			if (!order_type.equals(Constant.ORDER_TYPE_MEITIANHUI)) {
				tempMap.put("contact_tel", paramsMap.get("contact_tel") + "");
				tempMap.put("contact_person", paramsMap.get("contact_person") + "");
				tempMap.put("delivery_address", paramsMap.get("delivery_address") + "");
			}
			int updateFlag = fgOrderDao.updateFgOrder(tempMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
			}
			// 交易的订单类型
			String order_type_key = "DDLX_07";
			if (order_type.equals(Constant.ORDER_TYPE_TAOBAO)) {
				order_type_key = "DDLX_10";
			}
			// 领了么返款(订单赠送)
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("member_id", member_id);
			out_trade_body.put("order_no", order_no);
			amount = MoneyUtil.moneyMul(amount, StringUtil.formatStr(paramsMap.get("qty"))); 
			out_trade_body.put("amount", amount);
			out_trade_body.put("desc1", orderDetail.get("desc1"));
			orderService.orderReward("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", new BigDecimal(amount),
					order_type_key, order_no, "淘淘领返款", out_trade_body);
			// 如果是消费者,则给对应的门店进行返佣
			String member_type_key = orderDetail.get("member_type_key") + "";
			String discount_fee = orderDetail.get("discount_fee") + "";
			if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
				// 查询消费者对应的推荐门店
				tempMap.clear();
				tempMap.put("order_id", orderDetail.get("order_id"));
				tempMap.put("consumer_id", member_id);
				Map<String, Object> fgOrderExtraMap = fgOrderDao.selectFgOrderExtra(tempMap);
				if (null != fgOrderExtraMap) {
					String stores_id = fgOrderExtraMap.get("stores_id") + "";
					// 店东获取消费者支付金额对应的金币值
					Integer commission = new BigDecimal(discount_fee).intValue();
					if (commission > 0) {
						out_trade_body.clear();
						out_trade_body.put("stores_id", stores_id);
						out_trade_body.put("order_no", order_no);
						out_trade_body.put("amount", commission);
						out_trade_body.put("desc1", orderDetail.get("desc1"));
						orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, stores_id, "ZFFS_08",
								new BigDecimal(commission), order_type_key, order_no, "平台返利", out_trade_body);
					}
				}
				// 消费者返款 解封见面礼
				// String order_date = DateUtil.date2Str((Date)
				// orderDetail.get("order_date"), DateUtil.fmt_yyyyMMdd);
				// gcActivityFaceGiftPay(member_id, member_type_key, order_no,
				// order_date);

			} else if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
				// 如果是门店的商品,则导入商品到门店的商品库中
				psOrderService.psGoodsImport(member_id, goods_code, 1, "淘淘领商品导入", "lingleme");
			}
			// 违规用户冻结返款金额
			balanceFreeze(member_id, member_type_key, amount, member_mobile);

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (pointLock != null) {
				pointLock.unlock();
			}
		}
	}


	/**
	 * 黑名单中的用户自动冻结余额
	 * 
	 * @param member_mobile
	 * @param taobao_account
	 * @throws Exception
	 */
	private void balanceFreeze(final String member_id, final String member_type_key, final String amount,
			final String member_mobile) throws Exception {
		OrderController.threadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 休眠一秒,等待数据库数据刷新
					Thread.sleep(1000);
					// 检测手机号是否是黑名单
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("account", member_mobile);
					tempMap.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
					List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(tempMap);
					if (fgBlacklist.size() > 0) {
						Map<String, String> reqParams = new HashMap<String, String>();
						Map<String, Object> bizParams = new HashMap<String, Object>();
						reqParams.put("service", "finance.balanceFreeze");
						bizParams.put("data_source", "SJLY_03");
						bizParams.put("member_id", member_id);
						bizParams.put("member_type_key", member_type_key);
						bizParams.put("amount", amount);
						bizParams.put("detail", "返款冻结");
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
					}
				} catch (Exception e) {
					logger.error("返款冻结异常", e);
				}
			}
		});
	}

	@Override
	public void handleLocalFreeGet(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "data_source" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		Map<String, Object> memberInfoMap = FastJsonUtil.jsonToMap(order.get("member_info") + "");
		String mobile = StringUtil.formatStr(memberInfoMap.get("mobile"));
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String amount = order.get("total_fee") + "";

		// 更新领了么订单信息
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("data_source", paramsMap.get("data_source"));
		tempMap.put("external_buyer_name", mobile);
		tempMap.put("external_buyer_pay_no", member_id);
		tempMap.put("status", Constant.ORDER_CLOSED);
		tempMap.put("modified_date", DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		tempMap.put("order_id", order.get("order_id") + "");
		// 设置物流信息为现场领
		Map<String, Object> logisticsMap = new HashMap<String, Object>();
		logisticsMap.put("company", "现场领");
		logisticsMap.put("number", "");
		tempMap.put("logistics", FastJsonUtil.toJson(logisticsMap));
		int updateFlag = fgOrderDao.updateFgOrder(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) > 0) {
			// 领了么返款(订单赠送)
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("order_no", order_no);
			out_trade_body.put("member_id", member_id);
			out_trade_body.put("member_type_key", member_type_key);
			orderService.orderReward("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", new BigDecimal(amount),
					"DDLX_07", order_no, "淘淘领返款", out_trade_body);
		}
		// 如果是消费者,则给对应的门店进行返佣
		String discount_fee = order.get("discount_fee") + "";
		if (member_type_key.equals(Constant.MEMBER_TYPE_CONSUMER)) {
			// 查询消费者对应的推荐门店
			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("order_id", order.get("order_id"));
			out_trade_body.put("consumer_id", member_id);
			Map<String, Object> fgOrderExtraMap = fgOrderDao.selectFgOrderExtra(out_trade_body);
			if (null != fgOrderExtraMap) {
				String stores_id = fgOrderExtraMap.get("stores_id") + "";
				// 店东获取消费者支付金额对应的金币值
				Integer commission = new BigDecimal(discount_fee).intValue();
				if (commission > 0) {
					tempMap.clear();
					tempMap.put("stores_id", stores_id);
					tempMap.put("order_no", order_no);
					tempMap.put("amount", commission);
					tempMap.put("desc1", "现场领");
					orderService.balancePay("SJLY_03", Constant.MEMBER_ID_MTH, stores_id, "ZFFS_08",
							new BigDecimal(commission), "DDLX_07", order_no, "平台返利", tempMap);
				}
			}
		}
	}

	@Override
	public void handleFgOrderCancelled(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "remark" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		// 获取订单商品列表信息
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderItem(paramsMap);
		Map<String, Object> orderItem = orderItemList.get(0);
		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}
		// 还原库存
		orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");

		
		
		// 如果是消费者取消,退回扣除的金币
		if (((String) order.get("member_type_key")).equals(Constant.MEMBER_TYPE_CONSUMER)) {

			//如果订单是会过或者是自营的 则不退回扣除的金币
			if((order.get("order_type")).equals("taobao")){
			
				Integer total_fee = new BigDecimal(order.get("total_fee") + "").intValue();
				if (total_fee > 0) {
					// 交易的订单类型
					String order_type_key = "DDLX_07";
					if (StringUtil.formatStr(order.get("order_type")).equals(Constant.ORDER_TYPE_TAOBAO)) {
						order_type_key = "DDLX_10";
					}
					// 领了么取消订单退金币
					String member_id = order.get("member_id") + "";
					String order_no = order.get("order_no") + "";
					Map<String, Object> out_trade_body = new HashMap<String, Object>();
					out_trade_body.put("order_no", order_no);
					orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_08",
							new BigDecimal(total_fee), order_type_key, order_no, "淘淘领退款", out_trade_body);
				}
			}
		}
	}

	@Override
	public void handleFgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		// 自营的订单才会容许取消订单
		if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)
				&& !((String) order.get("order_type")).equals(Constant.ORDER_TYPE_MEITIANHUI)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}

		// 获取订单商品列表信息
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderItem(paramsMap);
		Map<String, Object> orderItem = orderItemList.get(0);
		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark"));
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}

		// 如果是自营商品，进行订单退款操作
		if (((String) order.get("order_type")).equals(Constant.ORDER_TYPE_MEITIANHUI)) {
			// 退还支付金额
			BigDecimal sale_fee = new BigDecimal(order.get("sale_fee") + "");
			if (sale_fee.compareTo(BigDecimal.ZERO) > 0) {
				String member_id = order.get("member_id") + "";
				String order_no = order.get("order_no") + "";
				Map<String, Object> out_trade_body = new HashMap<String, Object>();
				out_trade_body.put("order_no", order_no);
				orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", sale_fee, "DDLX_07",
						order_no, "淘淘领退款", out_trade_body);
			}
		}
		// 还原库存
		orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
	}





    @Override
    public void handleHgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
        List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
        if (orderList.size() == 0) {
            throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
        }
        Map<String, Object> order = orderList.get(0);
        // 会过的订单才会容许取消订单
        if (!((String) order.get("status")).equals(Constant.ORDER_CONFIRMED)
                && !((String) order.get("order_type")).equals(Constant.ORDER_TYPE_HUIGUO)) {
            throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
        }

        // 获取订单商品列表信息
        List<Map<String, Object>> orderItemList = hgOrderDao.selectHgOrderItem(paramsMap);
        Map<String, Object> orderItem = orderItemList.get(0);
        paramsMap.put("modified_date",
                DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
        paramsMap.put("status", Constant.ORDER_CANCELLED);
        paramsMap.put("remark", paramsMap.get("biz_remark"));
        int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
        if (updateFlag == 0) {
            throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
        }

        // 如果是会过商品，进行订单退款操作
        if (((String) order.get("order_type")).equals(Constant.ORDER_TYPE_HUIGUO)) {
            // 退还支付金额
            BigDecimal sale_fee = new BigDecimal(order.get("sale_fee") + "");
            if (sale_fee.compareTo(BigDecimal.ZERO) > 0) {
                String member_id = order.get("member_id") + "";
                String order_no = order.get("order_no") + "";
                Map<String, Object> out_trade_body = new HashMap<String, Object>();
                out_trade_body.put("order_no", order_no);
                orderService.orderRefund("SJLY_03", member_id, Constant.MEMBER_ID_MTH, "ZFFS_05", sale_fee, "DDLX_21",
                        order_no, "会过退款", out_trade_body);
            }
        }
        // 还原库存
        orderService.psGoodsSaleQtyRestore(orderItem.get("goods_id") + "", orderItem.get("qty") + "");
    }



	@Override
	public void hgOrderForOpEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("external_order_no", external_order_no);
			List<Map<String, Object>> tempOrderList = hgOrderDao.selectHgOrder(tempMap);
			if (tempOrderList.size() > 0) {
				Map<String, Object> order = tempOrderList.get(0);
				String temp_order_id = StringUtil.formatStr(order.get("order_id"));
				if (!temp_order_id.equals(paramsMap.get("order_id"))) {
					throw new BusinessException(RspCode.ORDER_ERROE, "订单号已被使用");
				}
			}
		}
		hgOrderDao.updateHgOrder(paramsMap);
	}

	@Override
	public void handleFgOrderConfirm(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		RedisLock lock = null;
		String lockKey = null;
		try {

			ValidateUtil.validateParams(paramsMap, new String[] { "data_source", "order_id", "external_order_no" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("external_order_no", paramsMap.get("external_order_no"));

			String order_id = paramsMap.get("order_id") + "";
			lockKey = "[handleFgOrderConfirm]_" + order_id;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(tempMap);
			if (orderList.size() > 0) {
				throw new BusinessException(RspCode.ORDER_ERROE, "订单号已存在");
			}
			tempMap.clear();
			tempMap.put("order_id", paramsMap.get("order_id"));
			orderList = fgOrderDao.selectFgOrder(tempMap);
			if (orderList.size() == 0) {
				throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
			}
			Map<String, Object> order = orderList.get(0);
			if (!((String) order.get("status")).equals(Constant.ORDER_PROCESSING)) {
				throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
			}
			paramsMap.put("modified_date",
					DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			paramsMap.put("status", Constant.ORDER_CONFIRMED);
			int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	@Override
	public void selectFgOrderByStatus(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		List<Map<String, Object>> list = fgOrderDao.selectFgOrderByStatus(paramsMap);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map<String, Object> maps : list) {
				tempMap.put("count_num", maps.get("count_num"));
			}
			result.setResultData(tempMap);
		}
	}

	@Override
	public void hgOrderListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			List<String> list = StringUtil.str2List(external_order_no, ",");
			if (list.size() > 1) {
				paramsMap.remove("external_order_no");
				paramsMap.put("external_order_no_in", list);
			}
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		// 2017/11/20 (丁龙添加) (数据分表,2017-09-30号后的数据重新创建了新的数据表)
		/*
		 * if (StringUtils.isNotBlank((String)
		 * paramsMap.get("order_date_start")) && StringUtils.isNotBlank((String)
		 * paramsMap.get("order_date_end"))) { String[] startTimes = ((String)
		 * paramsMap.get("order_date_start")).split("-"); String[] endTimes =
		 * ((String) paramsMap.get("order_date_end")).split("-"); long
		 * year_month_startTime = Long.parseLong(startTimes[0] + startTimes[1]);
		 * long year_month_endTime = Long.parseLong(endTimes[0] + endTimes[1]);
		 * //查询数据 List<Map<String, Object>> orderItemList1 = null;
		 * List<Map<String, Object>> orderItemList2 = null; if
		 * (year_month_startTime <= 201709) { orderItemList1 =
		 * fgOrderDao.selectFgOrderDetail2017(paramsMap); } if
		 * (year_month_endTime > 201709) { orderItemList2 =
		 * fgOrderDao.selectFgOrderDetail(paramsMap); } if (orderItemList1 !=
		 * null) { orderItemList.addAll(orderItemList1); } if (orderItemList2 !=
		 * null) { orderItemList.addAll(orderItemList2); } }
		 */
		List<Map<String, Object>> orderItemList = hgOrderDao.selectHgOrderDetail(paramsMap);

		for (Map<String, Object> map : orderItemList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("desc1", map.get("desc1"));
//			tempMap.put("item_num", map.get("item_num") + "");
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("delivery_address", map.get("delivery_address") + "");
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
//			tempMap.put("external_buyer_name", StringUtil.formatStr(map.get("external_buyer_name")));
//			tempMap.put("external_buyer_pay_no", StringUtil.formatStr(map.get("external_buyer_pay_no")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("member_id", map.get("member_id") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			Map<String, Object> memberInfoMap = FastJsonUtil.jsonToMap(map.get("member_info") + "");
			tempMap.put("member_mobile", memberInfoMap.get("mobile"));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("data_source", StringUtil.formatStr(map.get("data_source")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("settle_status", map.get("settle_status"));
//			tempMap.put("item_qty", map.get("item_qty") + "");
			tempMap.put("item_goods_id", map.get("item_goods_id"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_goods_code", StringUtil.formatStr(map.get("item_goods_code")));
			tempMap.put("item_supplier", StringUtil.formatStr(map.get("item_supplier")));
			tempMap.put("item_manufacturer", StringUtil.formatStr(map.get("item_manufacturer")));
			tempMap.put("item_goods_unit", map.get("item_goods_unit") + "");
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_total_fee", map.get("item_total_fee") + "");
			tempMap.put("item_discount_fee", map.get("item_discount_fee") + "");
			tempMap.put("item_service_fee", map.get("item_service_fee") + "");
			tempMap.put("item_settled_price", map.get("item_settled_price") + "");
			tempMap.put("transaction_no", StringUtil.formatStr(map.get("transaction_no")));
			tempMap.put("qty", StringUtil.formatStr(map.get("qty")));
			if(StringUtil.formatStr(map.get("qty")).equals("")){
				tempMap.put("qty","1");
			}
			String data_source = "huiguo";
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(map.get("item_remark") + "");
				data_source = StringUtil.formatStr(itemRemarkMap.get("data_source"));
			}
			tempMap.put("item_data_source", data_source);

//			if(StringUtil.formatStr(map.get("external_order_no")) != ""){
//				Map<String, String> reqParams = new HashMap<String, String>();
//				Map<String, Object> bizParams = new HashMap<String, Object>();
//				reqParams.put("service", "finance.consumer.transactionsnoByOuttradnoFind");
//				bizParams.put("order_id", StringUtil.formatStr(map.get("order_id")));
//				reqParams.put("params", FastJsonUtil.toJson(bizParams));
//				String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
//				Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
//				if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
//					throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
//				}
//				Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
//				tempMap.put("transaction_no", StringUtil.formatStr(dateMap.get("transaction_no")));
//				tempMap.put("payment_way_key", StringUtil.formatStr(dateMap.get("payment_way_key")));
//			}else{
//				tempMap.put("transaction_no", "");
//				tempMap.put("payment_way_key", "");
//			}
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}







	@Override
	public void hgOrderListForMoneyFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		String external_order_no = StringUtil.formatStr(paramsMap.get("external_order_no"));
		if (!StringUtils.isEmpty(external_order_no)) {
			List<String> list = StringUtil.str2List(external_order_no, ",");
			if (list.size() > 1) {
				paramsMap.remove("external_order_no");
				paramsMap.put("external_order_no_in", list);
			}
		}

		Map<String, Object> orderItemList = hgOrderDao.selectHgOrderDetailMoney(paramsMap);
		Map<String, Object> map = new HashMap<String, Object>();


		if(orderItemList != null){
            map.put("total_fee", orderItemList.get("total_fee").toString());//返款总额
            map.put("discount_fee", orderItemList.get("settled_price").toString());//结算总额
            map.put("sale_fee", orderItemList.get("sale_fee").toString());//预付总额
		}else{
			map.put("total_fee", "00.00");// 返款总额
			map.put("discount_fee", "00.00");// 结算总额
			map.put("sale_fee", "00.00");// 预付总额
		}
		result.setResultData(map);

	}



	@Override
	public void hgOrderFormMoneyTabulationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException {

		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code" });
		List<Map<String, Object>> orderItemList = hgOrderDao.selectHgOrderDetailMoneyTabulationFind(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		for (Map<String, Object> map : orderItemList) {
			tempMap.put("sale", map.get("sale").toString());// 预付价
			tempMap.put("total_fee", map.get("total_fee").toString());// 返款总额
			tempMap.put("discount_fee", map.get("settled_price").toString());// 结算总额
			tempMap.put("sale_fee", map.get("sale_fee").toString());// 预付总额
			tempMap.put("goods_code", map.get("goods_code").toString());// 商品码
			tempMap.put("goods_title", map.get("goods_title").toString());// 商品标题
//			tempMap.put("item_num", map.get("item_num").toString());// 订单总数
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void hgOrderDetailForOperationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "external_order_no" });
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("external_order_no", paramsMap.get("external_order_no"));
		map.put("status", "confirmed");
		List<Map<String, Object>> orderItemList = hgOrderDao.selectHgOrderDetail(paramsMap);
		map.clear();
		map.put("order_no", orderItemList.get(0).get("order_no"));
		map.put("member_mobile", orderItemList.get(0).get("member_mobile"));
		result.setResultData(map);
	}

	@Override
	public void fgOrderDetailForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_no" });
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		map.put("order_no", orderItemList.get(0).get("order_no"));
		map.put("status", orderItemList.get(0).get("status"));
		result.setResultData(map);
	}

	@Override
	public void fgOrderDetailForOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id" });
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> orderItemList = fgOrderDao.selectFgOrderDetail(paramsMap);
		if (orderItemList.size() > 0) {
			map.put("sale_fee", StringUtil.formatStr(orderItemList.get(0).get("sale_fee")));
		}
		result.setResultData(map);
	}

	
	@Override
	public void fgOrderListByOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListByOwn(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	@Override
	public void fgOrderListByHuiguoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListByHuiguo(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_code", map.get("item_goods_code") + "");
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	
	@Override
	public void fgOrderList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderList(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}

	@Override
	public void fgOrderListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> fgOrderDetailList = fgOrderDao.selectFgOrderListForSmallProgram(paramsMap);
		for (Map<String, Object> map : fgOrderDetailList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("order_id", map.get("order_id"));
			tempMap.put("order_no", map.get("order_no"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("order_date", DateUtil.date2Str((Date) map.get("order_date"), DateUtil.fmt_yyyyMMddHHmmss));
			// 倒计时
			tempMap.put("diff_time", (((Date) map.get("order_date")).getTime() + 3600000 - new Date().getTime()) + "");
			tempMap.put("payment_way_key", StringUtil.formatStr(map.get("payment_way_key")));
			tempMap.put("order_type", StringUtil.formatStr(map.get("order_type")));
			tempMap.put("total_fee", map.get("total_fee") + "");
			tempMap.put("discount_fee", map.get("discount_fee") + "");
			tempMap.put("sale_fee", map.get("sale_fee") + "");
			tempMap.put("delivery_address", StringUtil.formatStr(map.get("delivery_address")));
			tempMap.put("contact_person", StringUtil.formatStr(map.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(map.get("contact_tel")));
			tempMap.put("logistics", StringUtil.formatStr(map.get("logistics")));
			tempMap.put("external_order_no", StringUtil.formatStr(map.get("external_order_no")));
			tempMap.put("biz_remark", StringUtil.formatStr(map.get("biz_remark")));
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("status", map.get("status"));
			tempMap.put("item_goods_title", map.get("item_goods_title") + "");
			tempMap.put("item_qty", StringUtil.formatStr(map.get("item_qty")));
			tempMap.put("item_taobao_link", StringUtil.formatStr(map.get("item_taobao_link")));
			tempMap.put("item_sale_price", map.get("item_sale_price") + "");
			tempMap.put("item_goods_pic_info", map.get("item_goods_pic_info") + "");
			tempMap.put("qty", map.get("qty") + "");
			String goods_pic_info = (String) map.get("item_goods_pic_info");
			if (!"".equals(goods_pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(goods_pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析商品标签
			String item_remark = StringUtil.formatStr(map.get("item_remark"));
			if (StringUtil.isNotEmpty(item_remark)) {
				Map<String, Object> itemRemarkMap = FastJsonUtil.jsonToMap(item_remark);
				String label_promotion = StringUtil.formatStr(itemRemarkMap.get("label_promotion"));
				tempMap.put("label_promotion", label_promotion);
			}
			String goods_code = map.get("item_goods_code") + "";
			Map<String, Object> goodsDataMap = orderService.psGoodsFindForOrder(null, goods_code);
			// 淘口令
			tempMap.put("product_source", StringUtil.formatStr(goodsDataMap.get("product_source")));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		result.setResultData(map);
	}
	
	
	
	@Override
	public void handleFgOrderMessage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "remark" });
		fgOrderDao.updateFgOrder(paramsMap);
	}

	@Override
	public void fgOrderExceptionCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		paramsMap.put("status", Constant.ORDER_CONFIRMED);
		String date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
		paramsMap.put("order_date_start", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -7));
		paramsMap.put("order_date_end", DateUtil.addDate(date, DateUtil.fmt_yyyyMMdd, 3, -3));
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("count_num", "0");
		Map<String, Object> fgOrderCountMap = fgOrderDao.selectFgOrderCount(paramsMap);
		if (null != fgOrderCountMap) {
			countMap.put("count_num", fgOrderCountMap.get("count_num") + "");
		}
		result.setResultData(countMap);
	}

	@Override
	public void fgOrderViolationCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("violation_num", "0");
		countMap.put("total_num", "0");
		Map<String, Object> fgOrderCountMap = fgOrderDao.selectFgOrderViolationCount(paramsMap);
		if (null != fgOrderCountMap) {
			String violation_num = StringUtil.formatStr(fgOrderCountMap.get("violation_num"));
			if (StringUtils.isNotEmpty(violation_num)) {
				countMap.put("violation_num", violation_num);
			}
			countMap.put("total_num", fgOrderCountMap.get("total_num") + "");
		}
		result.setResultData(countMap);
	}

	@Override
	public void handleFgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		String biz_remark = paramsMap.get("biz_remark") + "";
		List<Map<String, Object>> orderList = fgOrderDao.selectFgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CLOSED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		if (!((String) order.get("settle_status")).equals(Constant.ORDER_SETTLE_PENDING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单已结算,不能进行撤销操作");
		}
		// 将返给消费者的钱扣回来
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String total_fee = order.get("total_fee") + "";

		// 如果账户余额为负数，则加入黑名单
		String member_mobile = order.get("member_mobile") + "";
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberAssetFind");
		bizParams.put("member_id", member_id);
		bizParams.put("member_type_key", member_type_key);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
		String cash_balance = dateMap.get("cash_balance") + "";

		// 如果会员余额为0，则自动加入黑名单
		if (MoneyUtil.moneySub(new BigDecimal(cash_balance), new BigDecimal(total_fee))
				.compareTo(BigDecimal.ZERO) < 0) {
			bizParams.clear();
			bizParams.put("account", member_mobile);
			bizParams.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
			List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(bizParams);
			if (fgBlacklist.size() == 0) {
				FgBlacklist blacklist = new FgBlacklist();
				blacklist.setBlacklist_id(IDUtil.getUUID());
				// blacklist.setMobile(member_mobile);2017-8-9修改
				blacklist.setAccount(member_mobile);
				blacklist.setAccount_type("meitianhui");
				blacklist.setCreated_date(new Date());
				blacklist.setRemark("返款时自动添加");
				fgBlacklistDao.insertFgBlacklist(blacklist);
				biz_remark += "|会员余额不足，已加入黑名单";
			}
		}

		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark") + "");
		paramsMap.put("biz_remark", biz_remark);
		int updateFlag = fgOrderDao.updateFgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}

		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		out_trade_body.put("order_no", order_no);
		out_trade_body.put("member_id", member_id);
		out_trade_body.put("member_type_key", member_type_key);
		orderService.orderRefund("SJLY_03", Constant.MEMBER_ID_MTH, member_id, "ZFFS_05", new BigDecimal(total_fee),
				"DDLX_07", order_no, "淘淘领撤销返款", out_trade_body);

	}



	@Override
	public void handleHgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "order_id", "biz_remark" });
		String biz_remark = paramsMap.get("biz_remark") + "";
		List<Map<String, Object>> orderList = hgOrderDao.selectHgOrder(paramsMap);
		if (orderList.size() == 0) {
			throw new BusinessException(RspCode.ORDER_NOT_EXIST, RspCode.MSG.get(RspCode.ORDER_NOT_EXIST));
		}
		Map<String, Object> order = orderList.get(0);
		if (!((String) order.get("status")).equals(Constant.ORDER_CLOSED)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "操作失败,请刷新后重试");
		}
		if (!((String) order.get("settle_status")).equals(Constant.ORDER_SETTLE_PENDING)) {
			throw new BusinessException(RspCode.ORDER_STATUS_ERROR, "订单已结算,不能进行撤销操作");
		}
		// 将返给消费者的钱扣回来
		String member_id = order.get("member_id") + "";
		String member_type_key = order.get("member_type_key") + "";
		String order_no = order.get("order_no") + "";
		String total_fee = order.get("total_fee") + "";

		// 如果账户余额为负数，则加入黑名单
		String member_mobile = order.get("member_mobile") + "";
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberAssetFind");
		bizParams.put("member_id", member_id);
		bizParams.put("member_type_key", member_type_key);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(OrderServiceImpl.FINANCE_SERVICE_URL, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dateMap = (Map<String, Object>) resultMap.get("data");
		String cash_balance = dateMap.get("cash_balance") + "";

		// 如果会员余额为0，则自动加入黑名单
		if (MoneyUtil.moneySub(new BigDecimal(cash_balance), new BigDecimal(total_fee))
				.compareTo(BigDecimal.ZERO) < 0) {
			bizParams.clear();
			bizParams.put("account", member_mobile);
			bizParams.put("account_type", Constant.ACCOUNT_TYPE_MEITIANHUI);
			List<Map<String, Object>> fgBlacklist = fgBlacklistDao.selectFgBlacklist(bizParams);
			if (fgBlacklist.size() == 0) {
				FgBlacklist blacklist = new FgBlacklist();
				blacklist.setBlacklist_id(IDUtil.getUUID());
				// blacklist.setMobile(member_mobile);2017-8-9修改
				blacklist.setAccount(member_mobile);
				blacklist.setAccount_type("meitianhui");
				blacklist.setCreated_date(new Date());
				blacklist.setRemark("返款时自动添加");
				fgBlacklistDao.insertFgBlacklist(blacklist);
				biz_remark += "|会员余额不足，已加入黑名单";
			}
		}

		paramsMap.put("modified_date",
				DateUtil.date2Str((Date) order.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", Constant.ORDER_CANCELLED);
		paramsMap.put("remark", paramsMap.get("biz_remark") + "");
		paramsMap.put("biz_remark", biz_remark);
		int updateFlag = hgOrderDao.updateHgOrder(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.ORDER_PROCESSING, "操作失败,请刷新后重试");
		}

		Map<String, Object> out_trade_body = new HashMap<String, Object>();
		out_trade_body.put("order_no", order_no);
		out_trade_body.put("member_id", member_id);
		out_trade_body.put("member_type_key", member_type_key);



		orderService.orderRefund("SJLY_03", Constant.MEMBER_ID_MTH, member_id, "ZFFS_05", new BigDecimal(total_fee),
				"DDLX_21", order_no, "会过撤销返款", out_trade_body);
		/**
		 * 数据字典
		 * SJLY_03 运营管理系统
		 * ZFFS_05 零钱
		 * DDLX_21 会过订单
		 */
	}




	@Override
	public void storesRecomConsumerFreeGetListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		paramsMap.put("order_date_start", DateUtil.getFormatDate(DateUtil.fmt_yyyyMM) + "-01");
		paramsMap.put("order_date_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
		List<Map<String, Object>> recomList = fgOrderDao.selectStoresRecomConsumerFreeGetCount(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> recomMap : recomList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", recomMap.get("consumer_id"));
			map.put("name", StringUtil.formatStr(recomMap.get("name")));
			map.put("contact", StringUtil.formatStr(recomMap.get("contact")));
			resultList.add(map);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", resultList);
		result.setResultData(resultMap);
	}

	@Override
	public void storesRecomConsumerFreeGetListFindForMemberList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		List<Map<String, Object>> recomList = fgOrderDao.selectStoresRecomConsumerFreeGetCount(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> recomMap : recomList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("member_id", recomMap.get("consumer_id"));
			map.put("name", StringUtil.formatStr(recomMap.get("name")));
			map.put("contact", StringUtil.formatStr(recomMap.get("contact")));
			resultList.add(map);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", resultList);
		result.setResultData(resultMap);
	}

	@Override
	public void fgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		// 统计金额和数量
		Map<String, Object> settlementCountMap = fgOrderDao.selectFgOrderSettlementCount(paramsMap);
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("total_fee", "0.00");
		countMap.put("count_num", "0");
		if (null != settlementCountMap) {
			if (null != settlementCountMap.get("total_fee")) {
				countMap.put("total_fee", settlementCountMap.get("total_fee"));
			}
			countMap.put("count_num", settlementCountMap.get("count_num") + "");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", countMap);
		result.setResultData(map);
	}

   /*muyi*/
	@Override
	public void hgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
			List<String> list = StringUtil.str2List(status, ",");
			if (list.size() > 1) {
				paramsMap.remove("status");
				paramsMap.put("status_in", list);
			}
		}
		// 统计金额和数量
		Map<String, Object> settlementCountMap = hgOrderDao.selectHgOrderSettlementCount(paramsMap);
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("total_fee", "0.00");
		countMap.put("count_num", "0");
		if (null != settlementCountMap) {
			if (null != settlementCountMap.get("total_fee")) {
				countMap.put("total_fee", settlementCountMap.get("total_fee"));
			}
			countMap.put("count_num", settlementCountMap.get("count_num") + "");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("count", countMap);
		result.setResultData(map);
	}
	@Override
	public void fgOrderSupplyCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id" });
		List<String> tempList = fgOrderDao.selectFgOrderSupplyCount(paramsMap);
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		int a = 0, b = 0, c = 0, d = 0, e = 0;
		for (String str : tempList) {
			if (str.equals("confirmed")) {
				a++;
			} else if (str.equals("closed")) {
				b++;
			} else if (str.equals("processing")) {
				c++;
			} else if (str.equals("cancelled")) {
				d++;
			} else if (str.equals("received")) {
				e++;
			}
		}
		countMap.put("confirmed", a);
		countMap.put("closed", b);
		countMap.put("processing", c);
		countMap.put("cancelled", d);
		countMap.put("received", e);
		result.setResultData(countMap);
	}

	@Override
	public void handleFgOrderSettlement(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "operator", "order_date" });
		String operator = paramsMap.get("operator") + "";
		Map<String, List<Map<String, Object>>> settlementMap = new HashMap<String, List<Map<String, Object>>>();
		// 查询需要结算的订单列表
		paramsMap.put("settle_status", Constant.ORDER_SETTLE_PENDING);
		paramsMap.put("status", Constant.ORDER_CLOSED);
		List<Map<String, Object>> settlementList = fgOrderDao.selectFgOrderSettlement(paramsMap);

		// 商品出结算清单
		for (Map<String, Object> map : settlementList) {
			String supplier_id = map.get("item_supplier_id") + "";
			if (null != settlementMap.get(supplier_id)) {
				List<Map<String, Object>> orderList = settlementMap.get(supplier_id);
				orderList.add(map);
			} else {
				List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
				orderList.add(map);
				settlementMap.put(supplier_id, orderList);
			}
		}
		int succ_count = 0;
		// 按供应商进行订单结算
		Map<String, Object> bizParams = new HashMap<String, Object>();
		for (String supplier_id : settlementMap.keySet()) {
			// 结算订单
			OdSettlement settlement = new OdSettlement();
			// 结算订单列表
			List<OdSettlementDetail> detailList = new ArrayList<OdSettlementDetail>();
			settlement.setSettlement_id(IDUtil.getTimestamp(7));
			Date date = new Date();
			String settlementAmount = "0"; // 结算金额
			// String orderAmount = "0"; //订单金额
			String supplier = "";
			Integer order_qty = 0;
			List<Map<String, Object>> tempList = settlementMap.get(supplier_id);
			// 生成结算清单
			for (Map<String, Object> tempMap : tempList) {
				// 以结算的金额为主
				String qty = tempMap.get("item_qty") + "";
				String total_fee = MoneyUtil.moneyMul(tempMap.get("item_settled_price") + "", qty);
				settlementAmount = MoneyUtil.moneyAdd(settlementAmount, total_fee); // 结算金额
				supplier = StringUtil.formatStr(tempMap.get("item_supplier"));
				OdSettlementDetail detail = new OdSettlementDetail();
				detail.setData_source(Constant.ORDER_TYPE_CASHBACK);
				detail.setDetail_id(IDUtil.getUUID());
				detail.setSettlement_id(settlement.getSettlement_id());
				detail.setOrder_id(tempMap.get("order_id") + "");
				detail.setOrder_no(tempMap.get("order_no") + "");
				detail.setGoods_id(tempMap.get("item_goods_id") + "");
				detail.setGoods_code(StringUtil.formatStr(tempMap.get("item_goods_code")));
				detail.setGoods_title(StringUtil.formatStr(tempMap.get("item_goods_title")));
				detail.setSupplier_id(supplier_id);
				detail.setSupplier(supplier);
				detail.setQty(Integer.parseInt(tempMap.get("item_qty") + ""));
				detail.setTotal_fee(new BigDecimal(total_fee));
				detail.setCreated_date(date);
				detail.setModified_date(date);
				detail.setStatus(Constant.ORDER_SETTLE_SETTLED);
				detailList.add(detail);
				bizParams.clear();
				bizParams.put("order_id", detail.getOrder_id());
				bizParams.put("settle_status", Constant.ORDER_SETTLE_SETTLED);
				bizParams.put("modified_date",
						DateUtil.date2Str((Date) tempMap.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
				int updateFlag = fgOrderDao.updateFgOrder(bizParams);
				if (updateFlag == 0) {
					throw new BusinessException(RspCode.ORDER_PROCESSING, "请刷新订单后再进行结算操作");
				}
				order_qty += 1;
			}
			settlement.setData_source(Constant.ORDER_TYPE_CASHBACK);
			// 订单金额
			settlement.setOrder_amount(new BigDecimal(settlementAmount));
			// 平台服务费
			settlement.setCommission_fee(new BigDecimal("0.00"));
			// 结算金额
			settlement.setAmount(new BigDecimal(settlementAmount));
			settlement.setOperator(operator);
			settlement.setSettled_date(DateUtil.str2Date(paramsMap.get("order_date") + "", DateUtil.fmt_yyyyMMdd));
			settlement.setSupplier_id(supplier_id);
			settlement.setSupplier(supplier);
			settlement.setCreated_date(date);
			settlement.setModified_date(date);
			settlement.setStatus(Constant.ORDER_SETTLE_SETTLED);
			// 计算成功数
			succ_count = succ_count + detailList.size();
			orderDao.insertOdSettlementDetailList(detailList);
			settlement.setOrder_qty(order_qty);
			orderDao.insertOdSettlement(settlement);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fail_count", settlementList.size() - succ_count);
		resultMap.put("succ_count", succ_count);
		result.setResultData(resultMap);
	}


	/**
	 * 木易
	 */
	@Override
	public void handleHgOrderSettlement(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "operator", "order_date" });
		String operator = paramsMap.get("operator") + "";
		Map<String, List<Map<String, Object>>> settlementMap = new HashMap<String, List<Map<String, Object>>>();
		// 查询需要结算的订单列表
		paramsMap.put("settle_status", Constant.ORDER_SETTLE_PENDING);
		paramsMap.put("status", Constant.ORDER_CLOSED);
		List<Map<String, Object>> settlementList = hgOrderDao.selectHgOrderSettlement(paramsMap);

		// 商品出结算清单
		for (Map<String, Object> map : settlementList) {

			String supplier_id = map.get("item_supplier_id") + "";
			if (null != settlementMap.get(supplier_id)) {
				List<Map<String, Object>> orderList = settlementMap.get(supplier_id);
				orderList.add(map);
			} else {
				List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
				orderList.add(map);
				settlementMap.put(supplier_id, orderList);
			}
		}
		int succ_count = 0;
		// 按供应商进行订单结算
		Map<String, Object> bizParams = new HashMap<String, Object>();
		for (String supplier_id : settlementMap.keySet()) {
			// 结算订单
			OdSettlement settlement = new OdSettlement();
			// 结算订单列表
			List<OdSettlementDetail> detailList = new ArrayList<OdSettlementDetail>();
			settlement.setSettlement_id(IDUtil.getTimestamp(7));
			Date date = new Date();
			String settlementAmount = "0"; // 结算金额
			// String orderAmount = "0"; //订单金额
			String supplier = "";
			Integer order_qty = 0;
			List<Map<String, Object>> tempList = settlementMap.get(supplier_id);
			// 生成结算清单
			for (Map<String, Object> tempMap : tempList) {
				// 以结算的金额为主
				String qty = tempMap.get("item_qty") + "";
				String total_fee = MoneyUtil.moneyMul(tempMap.get("item_settled_price") + "", qty);
				settlementAmount = MoneyUtil.moneyAdd(settlementAmount, total_fee); // 结算金额
				supplier = StringUtil.formatStr(tempMap.get("item_supplier_name"));
				OdSettlementDetail detail = new OdSettlementDetail();
				detail.setData_source(Constant.ORDER_TYPE_HUIGUO1);
				detail.setDetail_id(IDUtil.getUUID());
				detail.setSettlement_id(settlement.getSettlement_id());
				detail.setOrder_id(tempMap.get("order_id") + "");
				detail.setOrder_no(tempMap.get("order_no") + "");
				detail.setGoods_id(tempMap.get("item_goods_id") + "");
				detail.setGoods_code(StringUtil.formatStr(tempMap.get("item_goods_code")));
				detail.setGoods_title(StringUtil.formatStr(tempMap.get("item_goods_title")));
				detail.setSupplier_id(supplier_id);
				detail.setSupplier(supplier);
				detail.setQty(Integer.parseInt(tempMap.get("item_qty") + ""));
				detail.setTotal_fee(new BigDecimal(total_fee));
				detail.setCreated_date(date);
				detail.setModified_date(date);
				detail.setStatus(Constant.ORDER_SETTLE_SETTLED);
				detailList.add(detail);
				bizParams.clear();
				bizParams.put("order_id", detail.getOrder_id());
				bizParams.put("settle_status", Constant.ORDER_SETTLE_SETTLED);
				bizParams.put("modified_date",
						DateUtil.date2Str((Date) tempMap.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
				int updateFlag = hgOrderDao.updateHgOrder(bizParams);
				if (updateFlag == 0) {
					throw new BusinessException(RspCode.ORDER_PROCESSING, "请刷新订单后再进行结算操作");
				}
				order_qty += 1;
			}
			settlement.setData_source(Constant.ORDER_TYPE_HUIGUO1);
			// 订单金额
			settlement.setOrder_amount(new BigDecimal(settlementAmount));
			// 平台服务费
			settlement.setCommission_fee(new BigDecimal("0.00"));
			// 结算金额
			settlement.setAmount(new BigDecimal(settlementAmount));
			settlement.setOperator(operator);
			settlement.setSettled_date(DateUtil.str2Date(paramsMap.get("order_date") + "", DateUtil.fmt_yyyyMMdd));
			settlement.setSupplier_id(supplier_id);
			settlement.setSupplier(supplier);
			settlement.setCreated_date(date);
			settlement.setModified_date(date);
			settlement.setStatus(Constant.ORDER_SETTLE_SETTLED);
			// 计算成功数
			succ_count = succ_count + detailList.size();
			orderDao.insertOdSettlementDetailList(detailList);
			settlement.setOrder_qty(order_qty);
			orderDao.insertOdSettlement(settlement);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fail_count", settlementList.size() - succ_count);
		resultMap.put("succ_count", succ_count);
		result.setResultData(resultMap);
	}



	@Override
	public void fgBlacklistCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "account", "account_type" });
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(paramsMap);
		if (orderItemList.size() > 0) {
			throw new BusinessException(RspCode.BLACKLIST_REQ_ERROR, "会员已经加入了黑名单");
		}
		FgBlacklist blacklist = new FgBlacklist();
		BeanConvertUtil.mapToBean(blacklist, paramsMap);
		blacklist.setBlacklist_id(IDUtil.getUUID());
		blacklist.setCreated_date(new Date());
		fgBlacklistDao.insertFgBlacklist(blacklist);
	}

	@Override
	public void fgBlacklistFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<Map<String, Object>> orderItemList = fgBlacklistDao.selectFgBlacklist(paramsMap);
		for (Map<String, Object> map : orderItemList) {
			map.put("remark", StringUtil.formatStr(map.get("remark")));
			map.put("account", StringUtil.formatStr(map.get("account")));
			map.put("account_type", StringUtil.formatStr(map.get("account_type")));
			map.put("created_date", DateUtil.date2Str((Date) map.get("created_date"), DateUtil.fmt_yyyyMMddHHmmss));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", orderItemList);
		result.setResultData(map);
	}

	@Override
	public void fgBlacklistDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "blacklist_id" });
		String blacklist_id = paramsMap.get("blacklist_id") + "";
		fgBlacklistDao.deleteFgBlacklist(blacklist_id);
	}

	@Override
	public void fgViolationCheck(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });

	}

	/**
	 * 解封见面礼
	 * 
	 * @param member_id
	 * @param member_type_key
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	private void gcActivityFaceGiftPay(final String member_id, final String member_type_key, final String order_no,
			final String order_date) throws BusinessException, SystemException, Exception {
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "gcActivity.platform.gcActivityFaceGiftPay");
			bizParams.put("member_id", member_id);
			bizParams.put("member_type_key", member_type_key);
			bizParams.put("order_no", order_no);
			bizParams.put("order_date", order_date);
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.postShort(OrderServiceImpl.GOODS_SERVICE_URL, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
		} catch (Exception e) {
			logger.error("解封见面礼异常", e);
		}
	}
}
