package com.meitianhui.finance.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface OrderPayService {

	/**
	 * 我要批订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void wypOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 一元抽奖订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ldOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠易定2.0订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hyd2OrderWebPay(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠易定2.0订单扫码支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hyd2OrderScanCodePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠易定3.0订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void hyd3OrderWebPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠点微信公众号H5支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void huidianWechatH5Pay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠点微信公众号PC支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void huidianWechatPcPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 伙拼团活动创建并进行首笔订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tsActivityCreatePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	
	/**
	 * 会过订单支付
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void huiguoCreatePay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 订单支付
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void mainOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 伙拼团订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tsOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 手机话费充值订单支付
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void mobileRechargeOrderPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 2017-12-2  丁龙添加
	 * @param paramsMap 参数
	 * @param result 返回的参数
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void miniAppWechatPay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

}
