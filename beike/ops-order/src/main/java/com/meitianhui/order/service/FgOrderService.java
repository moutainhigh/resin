package com.meitianhui.order.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 领了么
 * 
 * @author Tiny
 *
 */
public interface FgOrderService {

	/**
	 * 领了么订单结算
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderSettlement(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自营商贸领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCreateNotity(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自营订单支付通知
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleOwnOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会过订单支付通知
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHuiguoOrderPayNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单验证(验证会员是领取过此商品)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者领了么订单创建(订单类型必填)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void consumerOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者会过商品订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void huiguoOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者自营商品订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void OwnOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东领了么订单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesFreeGetRecordCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 消费者现场领取
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleLocalFreeGet(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单导入
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFreeGetOrderImport(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单取消
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCancelled(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单取消(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会过订单取消(运营)
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHgOrderCancelledForOp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单编辑(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderForOpEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单编辑(运营设置物流信息)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderForLogisticEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单确定
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderConfirm(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询是否成功下单
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void selectFgOrderByStatus(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批订单列表查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 退款订单列表查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListForOpByRefundFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批订单列表金额查询（运营）
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderListForMoneyFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单详情查询(运营)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderDetailForOperationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单详情查询(领有惠)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderDetailForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单详情查询(内部调用)
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void fgOrderDetailForOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批结算订单统计查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderSettlementCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 供应商下领了么订单统计查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderSupplyCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单列表查询 自营
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListByOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单列表查询 新自营
	 */
	public void fgOrderListByNewOwnFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单列表查询 会过
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListByHuiguoFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单列表查询 淘淘领
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单列表查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单留言
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderMessage(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么违规记录统计
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderViolationCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 异常订单
	 * 
	 * @Title: fgOrderExceptionCount
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void fgOrderExceptionCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么订单返款撤销
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleFgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会过订单返款撤销
	 *
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handleHgOrderRevoke(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东推荐的参加领了么的消费者列表
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesRecomConsumerFreeGetListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东推荐的参加领了么的消费者列表(会员列表)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesRecomConsumerFreeGetListFindForMemberList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么会员黑名单创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么会员黑名单查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么会员黑名单删除
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgBlacklistDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么违规会员验证
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgViolationCheck(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 返回金额列表订单
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgOrderFormMoneyTabulationFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException;

	/**
	 * 会过自营订单退款
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void handlefgOrderRefund(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会过自营订单原路返还退款
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void handlefgOrderRefundBack(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询操作日志
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void queryOperateLogList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 佣金表日志分页
	 * 
	 * @param paramsMap
	 * @param result
	 */
	void fgOrderCommissionList(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询总佣金,总会员数,当周待转佣金
	 */
	void fgOrderCommissionToAmountMemberTotal(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 本周待转佣金日志
	 */
	void fgOrderCommissionWeekAmountLog(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 新自营商品订单创建
	 */
	void newOwnOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 充值订单创建
	 */
	void rcOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;


	/**
	 * 修改充值订单表的状态
	 */
	public void updateRcOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询支付订单日志(分页)
	 */
	public void rcOrderPageListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 贝壳商城订单接口创建
	 */
	public void beikeMallOrderCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 贝壳商城订单接口列表查询
	 */
	void beikeMallOrderListPageFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询订单接口_通过Order_id查询,提供给支付模块调用
	 */
	void beikeMallOrderDetailFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 修改订单状态
	 */
	void beikeMallOrderUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 取消贝壳商城订单
	 */
	void handleBeikeMallOrderCancelled(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳商城订单评论接口
	 */
	void beikeMallOrderForComment(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 红包兑取消订单接口
	 */
	void handlehongBaoOrderCancelled(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳商城确认收货
	 */
	void beikeMallOrderForConfirmReceipt(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳街市订单详情
	 */
	void beikeStreetOrderDetailFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳街市订单修改状态
	 */
	void beikeStreetOrderUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳街市订单接口列表查询
	 */
	void beikeStreetOrdeListByPageFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳街市取消订单
	 */
	void handleBeikeStreetOrderCancelled(Map<String, Object> paramsMap, ResultData result) throws Exception;
/**
	 * 贝壳商城_查询用户花费的贝壳数
	 */
	void beikeMallOrderShell(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳商城订单详情接口
	 */
	void beikeMallOrderDetail(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 	通过订单号查询贝壳商城订单
	 */
	void  findOrderInfoByNo(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 	话费充值修改状态
	 */
	void updateTelephoneChargeOrder(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 	查询电话充值订单状态
	 */
	void  selectTelephoneOrderStatus(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 	查询已完成订单的使用礼券
	 */
	void selectMemberCouponsId(Map<String, Object> paramsMap, ResultData result) throws Exception;

}
