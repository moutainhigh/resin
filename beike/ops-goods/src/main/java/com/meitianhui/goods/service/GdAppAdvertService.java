package com.meitianhui.goods.service;

import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

/**
 * 广告
 * 
 * @ClassName: GdAdvertService
 * @author tiny
 * @date 2017年4月5日 下午4:49:35
 *
 */
public interface GdAppAdvertService {

	/**
	 * 创建广告
	 * 
	 * @Title: gdAppAdvertCreate
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void gdAppAdvertCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 广告编辑
	 * 
	 * @Title: gdAppAdvertEdit
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void gdAppAdvertEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 广告删除
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void gdAppAdvertDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询广告(运营系统)
	 * 
	 * @Title: gdAppAdvertListForOpFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void gdAppAdvertListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询广告(APP)
	 * 
	 * @Title: gdAppAdvertListForOpFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void gdAppAdvertListForAppFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询广告(APP) 新接口
	 */
	void gdAppAdvertListForAppFind_V1(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/***
	 *获取APP欢迎页广告
	 */
	void gdAppAdSplash(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 *获取APP会员页推荐商品
	 */
	void gdAppVipPageAd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;


}
