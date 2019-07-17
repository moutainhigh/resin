package com.meitianhui.finance.service;

import java.util.Map;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

public interface WechatPayService {

	/**
	 * 扫码支付
	 * 
	 * @param type
	 * @param app_key
	 * @param app_id
	 * @param mch_id
	 * @param cert_local_path
	 * @param cert_password
	 * @param paramsMap
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void barCodePay(String type, String app_key, String app_id, String mch_id, String cert_local_path,
			String cert_password, Map<String, Object> paramsMap, String scan_flag)
					throws BusinessException, SystemException, Exception;

	/**
	 * 订单查询
	 * 
	 * @param app_key
	 * @param app_id
	 * @param mch_id
	 * @param cert_local_path
	 * @param cert_password
	 * @param out_trade_no
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public String orderQuery(String app_key, String app_id, String mch_id, String cert_local_path, String cert_password,
			String out_trade_no, Map<String, Object> query_response);

	/**
	 * 订单退款
	 * 
	 * @param app_key
	 * @param app_id
	 * @param mch_id
	 * @param cert_local_path
	 * @param cert_password
	 * @param out_trade_no
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void orderReverse(String app_key, String app_id, String mch_id, String cert_local_path, String cert_password,
			String out_trade_no) throws BusinessException, SystemException, Exception;

	/**
	 * 本地生活-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> consumerWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	/**
	 * 店东助手-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> storeWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	/**
	 * 熟么-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> shumeWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	/**
	 * 搞掂-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> salesassistantWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 惠点收银-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> cashierWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠点公众号-PC端扫码支付
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> huidianWechatPcBarCodePay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	/**
	 * 惠点公众号-H5微信支付
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> huidianWechatH5Pay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

	
	/**
	 * 交易对账
	 * 
	 * @Title: billCheck
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void billCheck(String bill_date)
			throws BusinessException, SystemException, Exception;

	/**
	 * 微信对账单下载
	 * 
	 * @Title: wechatBillImport
	 * @param paramsMap
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void wechatBillImport(String bill_date, String app_type, String app_key, String app_id, String mch_id,
			String cert_local_path, String cert_password) throws BusinessException, SystemException, Exception;

	/**
	 * 微信小程序-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	Map<String, Object> miniAppWechatInfoGet(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 本地生活-微信信息
	 * 
	 * @param paramMap
	 * @param result
	 * @throws BusinessException
	 */
	public Map<String, Object> consumerWechatH5Pay(Map<String, Object> paramsMap)
			throws BusinessException, SystemException, Exception;

}
