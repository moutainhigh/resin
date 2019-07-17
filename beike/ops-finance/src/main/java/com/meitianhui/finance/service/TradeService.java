package com.meitianhui.finance.service;

import java.util.Map;


import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.entity.FDTransactionsResult;

public interface TradeService {

	/**
	 * 交易注册(付款码)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void tradeCodeRegister(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易码校验(付款码)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void tradeCodeVerify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 付款码创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void barCodeCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 付款码支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void barCodePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 搞掂APP付款码支付
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void barCodePayForSalesassistant(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易确认接口
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionConfirmed(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额充值接口
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balanceRecharge(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额转正
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balancePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 支付（搞掂APP）（类似余额转正）
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void balancePayForSalesassistant(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单支付
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单赠送 买家是消费者,卖家是商户
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderReward(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额提现
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balanceWithdraw(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额提现退款
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balanceWithdrawRefund(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单结算
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderSettlement(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单退款
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderRefund(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 订单原路返还退款
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void orderRefundBack(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易冲正
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionReverse(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会员资产清零
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void assetClear(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额冻结
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balanceFreeze(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 余额解冻
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void balanceUnFreeze(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易状态查询
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionStatusFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 分账-余额充值
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void subAccounts(FDTransactionsResult fDTransactionsResult)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fDTransactionLogCreate(String transaction_no, String event)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易异常日志
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fDTransactionErrorLogCreate(String transaction_no, String event)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易流程
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionFlowCreate(String transaction_no, String flow_key, String remark)
			throws BusinessException, SystemException, Exception;

	/**
	 * 交易状态确认
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void transactionStatusConfirmed(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * pos支付
	 * 
	 * @param paramsMap
	 * @param result
	 */
	public void posPayForSalesassistant(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 贝壳支付分账
	 */
	public void shellSubAccounts(FDTransactionsResult fDTransactionsResult)
			throws BusinessException, SystemException, Exception;

}
