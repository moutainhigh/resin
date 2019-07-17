package com.meitianhui.finance.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.finance.constant.Constant;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.service.FdMemberBankService;
import com.meitianhui.finance.service.FinanceService;
import com.meitianhui.finance.service.GoldService;
import com.meitianhui.finance.service.MemberCapitalAccountService;
import com.meitianhui.finance.service.OrderPayService;
import com.meitianhui.finance.service.PrepayCardService;
import com.meitianhui.finance.service.TradeService;
import com.meitianhui.finance.service.VoucherService;

/**
 * 金融服务控制层
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/finance")
public class FinanceController extends BaseController {

	@Autowired
	private FinanceService financeService;
	@Autowired
	private MemberCapitalAccountService memberCapitalAccountService;
	@Autowired
	private OrderPayService orderPayService;
	@Autowired
	private TradeService tradeService;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private GoldService goldService;
	@Autowired
	private PrepayCardService prepayCardService;
	@Autowired
	private FdMemberBankService fdMemberBankService;

	
	private static final Logger logger = Logger.getLogger(FinanceController.class);
	
	/**
	 * 可缓存线程池
	 */
	public static ExecutorService threadExecutor = Executors.newCachedThreadPool();

	@Override
	public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {

		try {
			String operateName = request.getParameter("service");
			String type = operateName.split("\\.")[0];
			if (type.equals("trade")) {
				tradeServer(request, response, paramsMap, result);
			} else if (type.equals("voucher")) {
				voucherServer(request, response, paramsMap, result);
			} else if (type.equals("mobileRecharge")) {
				mobileRechargeServer(request, response, paramsMap, result);
			} else if(type.equals("syncLog")){
//				syncLogServer(request, response, paramsMap, result);
			} else {
				financeServer(request, response, paramsMap, result);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 礼券
	 * 
	 * @Title: voucherServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void voucherServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");
			if ("voucher.app.voucherExchangeLogFind".equals(operateName)) {
				voucherService.handleVoucherExchangeLogFind(paramsMap, result);
			} else if ("voucher.app.voucherExchangeGold".equals(operateName)) {
				voucherService.handleVoucherExchangeGold(paramsMap, result);
			} else if ("voucher.app.voucherBalanceFind".equals(operateName)) {
				voucherService.voucherBalanceFind(paramsMap, result);
			} else if ("voucher.app.voucherExchange".equals(operateName)) {
				voucherService.handleVoucherExchange(paramsMap, result);
			} else if ("voucher.stores.voucherExchangeLogPageFind".equals(operateName)) {
				voucherExchangeLogForStoresPageFind(request, paramsMap, result);
			} else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询礼券交易流水
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void voucherExchangeLogForStoresPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
			paramsMap.put("member_type_key", Constant.MEMBER_TYPE_STORES);
			paramsMap.put("member_id", paramsMap.get("stores_id"));
			paramsMap.remove("stores_id");
			voucherService.voucherExchangeLogForStoresFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @Title: mobileRechargeServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void mobileRechargeServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");
			if ("mobileRecharge.app.orderPay".equals(operateName)) {
				orderPayService.mobileRechargeOrderPay(paramsMap, result);
			} else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 资产服务
	 * 
	 * @Title: financeServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void financeServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");
			if ("finance.memberCapitalAccountCreate".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountCreate(paramsMap, result);
			} else if ("finance.memberCapitalAccountFind".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountFind(paramsMap, result);
			} else if ("finance.memberCapitalAccountEdit".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountEdit(paramsMap, result);
			} else if ("finance.memberCapitalAccountDelete".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountDelete(paramsMap, result);
			} else if ("finance.memberCapitalAccountApplicationCreate".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountApplicationCreate(paramsMap, result);
			} else if ("finance.memberCapitalAccountApplicationApplyListPageFind".equals(operateName)) {
				memberCapitalAccountApplicationApplyListPageFind(request, paramsMap, result);
			} else if ("finance.memberCapitalAccountApplicationListPageFind".equals(operateName)) {
				memberCapitalAccountApplicationListPageFind(request, paramsMap, result);
			} else if ("finance.memberCapitalAccountApplicationEdit".equals(operateName)) {
				memberCapitalAccountService.memberCapitalAccountApplicationEdit(paramsMap, result);
			} else if ("finance.bankListPageFind".equals(operateName)) {
				bankListPageFind(request, paramsMap, result);
			} else if ("finance.prepayCardStatusFind".equals(operateName)) {
				prepayCardService.prepayCardStatusFind(paramsMap, result);
			} else if ("finance.consumerTempPrepayCardCardNoGet".equals(operateName)) {
				prepayCardService.consumerTempPrepayCardCardNoGet(paramsMap, result);
			} else if ("finance.consumerTempPrepayCardCreate".equals(operateName)) {
				prepayCardService.consumerTempPrepayCardCreate(paramsMap, result);
			} else if ("finance.prepayCardActivate".equals(operateName)) {
				prepayCardService.prepayCardActivate(paramsMap, result);
			} else if ("finance.consumerPrepayCardBind".equals(operateName)) {
				prepayCardService.consumerPrepayCardBind(paramsMap, result);
			} else if ("finance.consumerPrepayCardUnBind".equals(operateName)) {
				prepayCardService.consumerPrepayCardUnBind(paramsMap, result);
			} else if ("finance.storesPrepayCardScan".equals(operateName)) {
				prepayCardService.storesPrepayCardScan(paramsMap, result);
			} else if ("finance.prepayCardScan".equals(operateName)) {
				// TODO 2.9.3生效后可以废弃
				prepayCardService.consumerPrepayCardScan(paramsMap, result);
			} else if ("finance.consumerPrepayCardScan".equals(operateName)) {
				// TODO 2.9.3生效后可以废弃
				prepayCardService.consumerPrepayCardScan(paramsMap, result);
			} else if ("finance.consumerPrepayCardFind".equals(operateName)) {
				prepayCardService.consumerPrepayCardFind(paramsMap, result);
			} else if ("finance.consumerPrepayCardEdit".equals(operateName)) {
				prepayCardService.consumerPrepayCardEdit(paramsMap, result);
			} else if ("finance.consumerPrepayCardCount".equals(operateName)) {
				prepayCardService.consumerPrepayCardCount(paramsMap, result);
			} else if ("finance.prepayCardActivateCount".equals(operateName)) {
				prepayCardService.prepayCardActivateCount(paramsMap, result);
			} else if ("finance.prepayCardActivatePageFind".equals(operateName)) {
				prepayCardActivatePageFind(request, paramsMap, result);
			} else if ("finance.transPrepayCardPageFind".equals(operateName)) {
				transPrepayCardPageFind(request, paramsMap, result);
			} else if ("finance.storesActivatePrepayCardFind".equals(operateName)) {
				prepayCardService.storesActivatePrepayCardFind(paramsMap, result);
			} else if ("finance.goldExchangeCash".equals(operateName)) {
				goldService.handleGoldExchangeCash(paramsMap, result);
			}else if ("finance.consumer.memberPointFind".equals(operateName)) { 
				financeService.memberPointFind(paramsMap, result);
			}else if ("finance.consumer.memberPointEdit".equals(operateName)) {
				//修改会员积分
				financeService.memberPointEdit(paramsMap, result);
			} else if ("finance.storeCashierPromotion".equals(operateName)) {
				financeService.storeCashierPromotion(paramsMap, result);
			} else if ("finance.memberAssetQuery".equals(operateName)) {
				financeService.memberAssetFind(paramsMap, result);
			} else if ("finance.memberAssetFind".equals(operateName)) {
				financeService.memberAssetFind(paramsMap, result);
			} else if ("finance.memberAssetListFind".equals(operateName)) {
				financeService.memberAssetListFind(paramsMap, result);
			} else if ("finance.usableCashBalanceFind".equals(operateName)) {
				financeService.memberUsableCashBalanceFind(paramsMap, result);
			} else if ("finance.storeCashCount".equals(operateName)) {
				financeService.storeCashCount(paramsMap, result);
			} else if ("finance.storeVoucherBillCount".equals(operateName)) {
				financeService.storeVoucherBillCount(paramsMap, result);
			} else if ("finance.storeVoucherRewardAccountCount".equals(operateName)) {
				financeService.storeVoucherRewardAccountCount(paramsMap, result);
			} else if ("finance.storesCashierCreate".equals(operateName)) {
				financeService.storesCashierCreate(paramsMap, result);
			} else if ("finance.storesCashierFind".equals(operateName)) {
				financeService.storesCashierFind(paramsMap, result);
			} else if ("finance.storeCashierPage".equals(operateName)) {
				storeCashierPage(request, paramsMap, result);
			} else if ("finance.memberCouponCreate".equals(operateName)) {
				financeService.memberCouponCreate(paramsMap, result);
			} else if ("finance.memberCouponFind".equals(operateName)) {
				financeService.memberCouponFind(paramsMap, result);
			} else if ("finance.memberCouponStatusEdit".equals(operateName)) {
				financeService.memberCouponStatusEdit(paramsMap, result);
			} else if ("finance.memberIdFindBySkuCode".equals(operateName)) {
				financeService.memberIdBySkuCodeFind(paramsMap, result);
			} else if ("finance.memberCouponCount".equals(operateName)) {
				financeService.memberCouponCount(paramsMap, result);
			} else if ("finance.disabledCouponStatusUpdate".equals(operateName)) {
				financeService.disabledCouponStatusUpdate(paramsMap, result);
			} else if ("finance.tradeConsumerListForStores".equals(operateName)) {
				tradeConsumerPageFindForStores(request, paramsMap, result);
			} else if ("finance.tradeConsumerListForMemberList".equals(operateName)) {
				financeService.tradeConsumerListForMemberList(paramsMap, result);
			} else if ("finance.memberCashLogPageFind".equals(operateName)) {
				memberCashLogPageFind(request, paramsMap, result);
			} else if ("finance.memberCashLogPageFindNew".equals(operateName)) {
				memberCashLogPageFindNew(request, paramsMap, result);
			} else if ("finance.memberGoldLogPageFind".equals(operateName)) {
				memberGoldLogPageFind(request, paramsMap, result);
			} else if ("finance.consumer.memberPointLogPageFind".equals(operateName)) {
				//分页查询会员积分日志
				memberPointLogPageFind(request, paramsMap, result);
			} else if ("finance.memberVoucherLogPageFind".equals(operateName)) {
				memberVoucherLogPageFind(request, paramsMap, result);
			} else if ("finance.storeVoucherBillPage".equals(operateName)) {
				// 店东礼券赠送列表
				storeVoucherBillPage(request, paramsMap, result);
			} else if ("finance.storeVoucherBill".equals(operateName)) {
				financeService.storeVoucherBill(paramsMap, result);
			} else if ("finance.consumerVoucherBill".equals(operateName)) {
				// 运营系统用
				financeService.consumerVoucherBill(paramsMap, result);
			} else if ("finance.memberAssetInit".equals(operateName)) {
				financeService.handleInitMemberAsset(paramsMap, result);
			} else if ("finance.balanceRecharge".equals(operateName)) {
				String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
				if (StringUtils.isEmpty(member_id)) {
					String buyer_id = paramsMap.get("buyer_id") + "";
					paramsMap.put("member_id", buyer_id);
					paramsMap.remove("buyer_id");
					paramsMap.remove("seller_id");
				}
				throw new BusinessException(RspCode.PAYMENT_WAY_ERROR, "充值暂未开通");	
				//tradeService.balanceRecharge(paramsMap, result);
			} else if ("finance.balancePay".equals(operateName)) {
				paramsMap.put("out_member_id", paramsMap.get("buyer_id"));
				paramsMap.put("in_member_id", paramsMap.get("seller_id"));
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.balancePay(paramsMap, result);
			} else if ("finance.orderPay".equals(operateName)) {
				paramsMap.put("out_member_id", paramsMap.get("buyer_id"));
				paramsMap.put("in_member_id", paramsMap.get("seller_id"));
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.orderPay(paramsMap, result);
			} else if ("finance.orderReward".equals(operateName)) {
				// 买方双方互换位置
				String buyer_id = paramsMap.get("buyer_id") + "";
				String seller_id = paramsMap.get("seller_id") + "";
				paramsMap.put("out_member_id", seller_id);
				paramsMap.put("in_member_id", buyer_id);
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.orderReward(paramsMap, result);
			} else if ("finance.balanceWithdraw".equals(operateName)) {
				String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
				if (StringUtils.isEmpty(member_id)) {
					String buyer_id = paramsMap.get("buyer_id") + "";
					paramsMap.put("member_id", buyer_id);
					paramsMap.remove("buyer_id");
					paramsMap.remove("seller_id");
				}
				tradeService.balanceWithdraw(paramsMap, result);
			} else if ("finance.balanceWithdrawRefund".equals(operateName)) {
				tradeService.balanceWithdrawRefund(paramsMap, result);
			} else if ("finance.orderRefund".equals(operateName)) {
				// 买方双方互换位置
				String buyer_id = paramsMap.get("buyer_id") + "";
				String seller_id = paramsMap.get("seller_id") + "";
				paramsMap.put("out_member_id", seller_id);
				paramsMap.put("in_member_id", buyer_id);
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.orderRefund(paramsMap, result);
			}else if ("finance.orderRefundBack".equals(operateName)) {
				// 买方双方互换位置
				String buyer_id = paramsMap.get("buyer_id") + "";
				String seller_id = paramsMap.get("seller_id") + "";
				paramsMap.put("out_member_id", seller_id);
				paramsMap.put("in_member_id", buyer_id);
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.orderRefundBack(paramsMap, result);
			} else if ("finance.orderSettlement".equals(operateName)) {
				paramsMap.put("out_member_id", paramsMap.get("buyer_id"));
				paramsMap.put("in_member_id", paramsMap.get("seller_id"));
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.orderSettlement(paramsMap, result);
			} else if ("finance.assetClear".equals(operateName)) {
				paramsMap.put("member_id", paramsMap.get("seller_id"));
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				tradeService.assetClear(paramsMap, result);
			} else if ("finance.transactionReverse".equals(operateName)) {
				tradeService.transactionReverse(paramsMap, result);
			} else if ("finance.transactionConfirmed".equals(operateName)) {
				tradeService.transactionConfirmed(paramsMap, result);
			} else if ("finance.balanceFreeze".equals(operateName)) {
				tradeService.balanceFreeze(paramsMap, result);
			} else if ("finance.balanceUnFreeze".equals(operateName)) {
				tradeService.balanceUnFreeze(paramsMap, result);
			} else if ("finance.transactionStatusFind".equals(operateName)) {
				tradeService.transactionStatusFind(paramsMap, result);
			} else if ("finance.storesUnfreezeBalanceForConsumer".equals(operateName)) {
				financeService.handleStoresUnfreezeBalanceForConsumer(paramsMap, result);
			} else if ("finance.wypOrderPay".equals(operateName)) {
				orderPayService.wypOrderPay(paramsMap, result);
			} else if ("finance.ldOrderPay".equals(operateName)) {
				orderPayService.ldOrderPay(paramsMap, result);
			} else if ("finance.hyd2OrderWebPay".equals(operateName)) {
				orderPayService.hyd2OrderWebPay(request, paramsMap, result);
			} else if ("finance.hyd2OrderScanCodePay".equals(operateName)) {
				orderPayService.hyd2OrderScanCodePay(paramsMap, result);
			} else if ("finance.hyd3OrderWebPay".equals(operateName)) {
				orderPayService.hyd3OrderWebPay(paramsMap, result);
			} else if ("finance.huidianWechatH5Pay".equals(operateName)) {
				orderPayService.huidianWechatH5Pay(paramsMap, result);
			} else if ("finance.huidianWechatPcPay".equals(operateName)) {
				orderPayService.huidianWechatPcPay(paramsMap, result);
			} else if ("finance.miniAppWechatPay".equals(operateName)) {
				//2017-12-2 微信小程序 丁龙
				orderPayService.miniAppWechatPay(paramsMap, result);
			}else if ("finance.tsActivityCreatePay".equals(operateName)) {
				orderPayService.tsActivityCreatePay(paramsMap, result);
			} else if ("finance.consumer.huiguoCreatePay".equals(operateName)) {
				orderPayService.huiguoCreatePay(paramsMap, result);
			} else if ("finance.consumer.mainOrderPay".equals(operateName)) {
				orderPayService.mainOrderPay(paramsMap, result);
			} else if ("finance.tsOrderPay".equals(operateName)) {
				orderPayService.tsOrderPay(paramsMap, result);
			} else if ("finance.billCheckLogListPageFind".equals(operateName)) {
				billCheckLogListPageFind(request, paramsMap, result);
			} else if ("finance.salesassistant.memberAssetFind".equals(operateName)) {
				financeService.memberAssetFind(paramsMap, result);
			} else if ("finance.menberBonusListPageFind".equals(operateName)){
				menberBonusListPageFind(request, paramsMap, result);
			} else if ("finance.stores.storesCashCommissionLogListPageFind".equals(operateName)) {
				storesCashCommissionLogListPageFind(request, paramsMap, result);
			} else if ("finance.stores.storesCashCommissionCreate".equals(operateName)) {
				financeService.storesCashCommissionCreate(paramsMap, result);
			} else if ("finance.stores.storesCashCommissionEdit".equals(operateName)) {
				financeService.storesCashCommissionEdit(paramsMap, result);
			} else if ("finance.consumer.transactionsnoByOuttradnoFind".equals(operateName)) {
				financeService.transactionsnoByOuttradnoFind(paramsMap, result);
			} else if ("finance.stores.transmatic".equals(operateName)) {
				financeService.transmatic(paramsMap, result);
			} else if ("finance.memberAssetsInformation".equals(operateName)) {
				financeService.memberAssetsInformation(paramsMap, result);
			} else if ("finance.consumer.hebaoGetCashCoupon".equals(operateName)) {
				//和包用户领取现金券
				financeService.handleHebaoGetCashCoupon(paramsMap, result);
			} else if ("consumer.hebaoUseCashCoupon".equals(operateName)){ 
				//和包用户核销现金券
				financeService.hebaoUseCashCoupon(paramsMap, result);
			} else if ("finance.consumer.hebaoCashCouponListFind".equals(operateName)) {
				//获取和包用户已领现金券列表
				financeService.hebaoCashCouponListFind(paramsMap, result);
			} else if ("finance.consumer.monthlyGiftCoupon".equals(operateName)) {
				//每月给相对应等级的会员送礼券
				financeService.monthlyGiftCoupon(paramsMap, result);
			} else if ("finance.consumer.birthdayGiftCoupon".equals(operateName)) {
				//会员生日赠送礼券  
				financeService.birthdayGiftCoupon(paramsMap, result);
			} else if ("finance.consumer.findGiftCoupon".equals(operateName)) {
				//查询礼券
				financeService.findGiftCoupon(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponList_v1".equals(operateName)) {
				//查询礼券列表
				findGiftCouponList(request, paramsMap, result);
			} else if ("finance.consumer.editMemberCoupon".equals(operateName)) {
				//插入礼券列表
				financeService.editMemberCoupon(paramsMap, result);
			} else if ("finance.memberReceiveMoney".equals(operateName)) {
				financeService.handleMemberReceiveMoney(paramsMap, result);
			} else if ("finance.consumer.updateGiftCoupon".equals(operateName)) {
				//修改用户礼券
				financeService.updateGiftCoupon(paramsMap, result);
			} else if ("finance.consumer.updateMemberGift".equals(operateName)) {
				//退回免邮卷
				financeService.updateMemberGift(paramsMap, result);
			} else if ("finance.consumer.findMemberBankBinding".equals(operateName)) {
				//查询会员的银行卡绑定信息
				fdMemberBankService.findMemberBankBindingInfo(paramsMap, result);
			} else if ("finance.consumer.addFdMemberBankInfo".equals(operateName)) {
				//添加会员的银行卡绑定信息
				fdMemberBankService.addFdMemberBankInfo(paramsMap, result);
			} else if ("finance.consumer.insertWithdrawal".equals(operateName)) {
				//申请提现
				fdMemberBankService.insertWithdrawal(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponList".equals(operateName)) {
				//查询礼券列表
				financeService.findGiftCouponList(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponList_v2".equals(operateName)) {
				//查询礼券列表
				financeService.findGiftCouponList_v2(paramsMap, result);
			} else if ("finance.consumer.activationGiftCoupon".equals(operateName)) {
				//激活礼券
				financeService.activationGiftCoupon(paramsMap, result);
			}else if ("finance.consumer.repairGiftCoupon".equals(operateName)) {
				//补签
				financeService.repairGiftCoupon(paramsMap, result);
			}else if ("finance.consumer.shellSwitchToCount".equals(operateName)) {
				//贝壳换补签次数
				financeService.shellSwitchToCount(paramsMap, result);
			} else if ("finance.consumer.buyByShell".equals(operateName)) {
				//贝壳交易
				financeService.buyByShell(paramsMap, result);
			}else if ("finance.consumer.findGiftCouponSignInfo".equals(operateName)) {
				//查找礼券签到的信息
				financeService.findGiftCouponSignInfo(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponTransform".equals(operateName)) {
				//礼券转换(399未激活的礼券,转换成尖货礼券)
				financeService.findGiftCouponTransform(paramsMap, result);
			} else if ("finance.consumer.updateGiftCouponMun".equals(operateName)) {
				//修改礼券的数量
				financeService.updateGiftCouponMun(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponType".equals(operateName)) {
				//查找礼券签到的信息
				financeService.findGiftCouponType(paramsMap, result);
			} else if ("finance.consumer.manageRateToBalance".equals(operateName)) {
				//掌柜次邀的返佣转移到红包余额
				financeService.manageRateToBalance(paramsMap, result);
			} else if ("finance.consumer.testManageRateToBalance".equals(operateName)) {
				//测试掌柜次邀的返佣转移到红包余额
				financeService.testManageRateToBalance(paramsMap, result);
			} else if ("finance.consumer.activationFormerOneGiftCoupon".equals(operateName)) {
				//激活时间最前的一张礼券
				financeService.activationFormerOneGiftCoupon(paramsMap, result);
			} else if ("finance.consumer.findGiftCouponSignInfoForLastMonth".equals(operateName)) {
				//查找礼券签到的信息
				financeService.findGiftCouponSignInfoForLastMonth(paramsMap, result);
			}else if ("finance.consumer.findMemberAssetByMobile".equals(operateName)) {
				//根据手机号查询会员资产
				financeService.findMemberAssetByMobile(paramsMap, result);
			}else if ("finance.consumer.getRebateSynLog".equals(operateName)) {
				//根据手机号查询会员资产
				financeService.findMemberAssetByMobile(paramsMap, result);
			}
			else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 交易服务
	 * 
	 * @Title: tradeServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void tradeServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");

			if ("trade.tradeCodeRegister".equals(operateName)) {
				tradeService.tradeCodeRegister(paramsMap, result);
			} else if ("trade.tradeCodeVerify".equals(operateName)) {
				tradeService.tradeCodeVerify(paramsMap, result);
			} else if ("trade.barCodeCreate".equals(operateName)) {
				tradeService.barCodeCreate(paramsMap, result);
			} else if ("trade.barCodePay".equals(operateName)) {
				tradeService.barCodePay(paramsMap, result);
			} else if ("trade.transactionStatusFind".equals(operateName)) {
				tradeService.transactionStatusFind(paramsMap, result);
			} else if ("trade.transactionStatusConfirmed".equals(operateName)) {
				tradeService.transactionStatusConfirmed(paramsMap, result);
			} else if ("trade.salesassistant.barCodePay".equals(operateName)) {
				tradeService.barCodePayForSalesassistant(paramsMap, result);
			} else if ("trade.salesassistant.posPay".equals(operateName)) {
				//pos机支付入口
				tradeService.posPayForSalesassistant(paramsMap, result);
			} else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询会员现金日志
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCashLogPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.memberCashLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 分页查询会员现金日志(新接口)
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCashLogPageFindNew(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.memberCashLogListFindNew(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * 分页查询会员积分日志
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberPointLogPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.memberPointLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * 分页查询会员金币日志
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberGoldLogPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.memberGoldLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询会员礼券日志
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberVoucherLogPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.memberVoucherLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询礼券交易流水
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeVoucherBillPage(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.storeVoucherBill(paramsMap, result);
			Map<String, Object> pageData = new HashMap<String, Object>();
			pageData.put("list", result.getResultData());
			pageData.put("page", pageParam);
			result.setResultData(pageData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询 收银记录
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storeCashierPage(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.storesCashierFind(paramsMap, result);
			Map<String, Object> pageData = new HashMap<String, Object>();
			pageData.put("list", result.getResultData());
			pageData.put("page", pageParam);
			result.setResultData(pageData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询亲情卡交易信息
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transPrepayCardPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "card_no" });
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			prepayCardService.transPrepayCardFind(paramsMap, result);
			Map<String, Object> pageData = new HashMap<String, Object>();
			pageData.put("list", result.getResultData());
			pageData.put("page", pageParam);
			result.setResultData(pageData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询店东激活的亲情卡信息
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void prepayCardActivatePageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			prepayCardService.prepayCardActivateFind(paramsMap, result);
			Map<String, Object> pageData = new HashMap<String, Object>();
			pageData.put("list", result.getResultData());
			pageData.put("page", pageParam);
			result.setResultData(pageData);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/***
	 * 和店东发生交易的消费者分页列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @author 丁硕
	 * @date 2016年10月12日
	 */
	public void tradeConsumerPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.tradeConsumerListForStores(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询礼券交易流水
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void bankListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			memberCapitalAccountService.bankListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询会员绑定银行卡申请待审核记录查询
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCapitalAccountApplicationApplyListPageFind(HttpServletRequest request,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			memberCapitalAccountService.memberCapitalAccountApplicationApplyListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询会员银行绑卡申请信息
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void memberCapitalAccountApplicationListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			memberCapitalAccountService.memberCapitalAccountApplicationListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 分页查询交易异常日志
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void billCheckLogListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.billCheckLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * 门店的店东的佣金明细列表分页查询
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException 
	 */
	public void storesCashCommissionLogListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.storesCashCommissionLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 分页查询用户积分
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException 
	 */
	public void menberBonusListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.menberBonusListPageFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * 分页查询用户积分
	 */
	public void findGiftCouponList(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			financeService.findGiftCouponList_v1(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}


}
