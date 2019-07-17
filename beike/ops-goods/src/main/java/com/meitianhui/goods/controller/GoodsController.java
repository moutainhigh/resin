package com.meitianhui.goods.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.constant.Constant;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.service.BeikeMallGoodsService;
import com.meitianhui.goods.service.GcActivityService;
import com.meitianhui.goods.service.GdActivityDeliveryService;
import com.meitianhui.goods.service.GdActivityService;
import com.meitianhui.goods.service.GdAppAdvertService;
import com.meitianhui.goods.service.GdBenefitService;
import com.meitianhui.goods.service.GdCategoryCatService;
import com.meitianhui.goods.service.GdGoodsLabelService;
import com.meitianhui.goods.service.GoodsService;
import com.meitianhui.goods.service.HongbaoGoodsService;
import com.meitianhui.goods.service.ItemStoreService;
import com.meitianhui.goods.service.LdActivityService;
import com.meitianhui.goods.service.MobileRechargeService;
import com.meitianhui.goods.service.PPActivityService;
import com.meitianhui.goods.service.PlPartcipatorService;
import com.meitianhui.goods.service.PsGoodsFavoritesService;
import com.meitianhui.goods.service.PsGoodsService;
import com.meitianhui.goods.service.StoresGoodsService;

/**
 * 商品管理
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {

	private static final Logger logger = Logger.getLogger(GoodsController.class);

	@Autowired
	public RedisUtil redisUtil;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private MobileRechargeService mobileRechargeService;

	@Autowired
	private ItemStoreService itemStroeService;

	@Autowired
	private StoresGoodsService storesGoodsService;

	@Autowired
	private PlPartcipatorService plPartcipatorService;

	@Autowired
	private LdActivityService ldActivityService;

	@Autowired
	private PPActivityService ppActivityService;

	@Autowired
	private PsGoodsService psGoodsService;

	@Autowired
	private GdActivityService gdActivityService;

	@Autowired
	private GdBenefitService gdBenefitService;

	@Autowired
	private GdActivityDeliveryService gdActivityDeliveryService;

	@Autowired
	private GcActivityService gcActivityService;

	@Autowired
	private GdAppAdvertService gdAppAdvertService;

	@Autowired
	private PsGoodsFavoritesService psGoodsFavoritesService;
	
	@Autowired
	private GdCategoryCatService gdCategoryCatService;
	
	@Autowired
	private BeikeMallGoodsService beikeMallGoodsService;
	
	@Autowired
	private HongbaoGoodsService hongbaoGoodsService;
	
	@Autowired
	private GdGoodsLabelService gdGoodsLabelService;

	@Override
	public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		String type = operateName.split("\\.")[0];
		if (type.equals("gdActivity")) {
			// 权益活动
			gdActivityServer(request, response, paramsMap, result);
		} else if (type.equals("gdBenefit")) {
			// 会员权益
			gdBenefitServer(request, response, paramsMap, result);
		} else if (type.equals("gcActivity")) {
			// 红包见面礼
			gcActivityServer(request, response, paramsMap, result);
		} else if (type.equals("gdAppAdvert")) {
			// App广告
			gdAppAdvertServer(request, response, paramsMap, result);
		} else if (type.equals("psGoodsFavorites")) {
			// 商品收藏
			psGoodsFavoritesServer(request, response, paramsMap, result);
		} else if (type.equals("storesGoods")) {
			// 门店商品
			storesGoodsServer(request, response, paramsMap, result);
		} else if (type.equals("psGoods")) {
			// 商品列表
			psGoodsServer(request, response, paramsMap, result);
		}
//		else if (type.equals("goods")) {
//			goodsServer(request, response, paramsMap, result);
//		}
 		else if (type.equals("goodsAd")) {
			goodsAd(request, response, paramsMap, result);
		}
		else {
			appServer(request, response, paramsMap, result);
		}

	}

	private void goodsAd(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("goodsAd.app.member.ad".equals(operateName)) {
			goodsService.findAppMemberAd(paramsMap, result);
//		} else if ("goods.stores.Goods.detail".equals(operateName)) {
//			goodsService.findDetailGoods(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR, RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}


	private void goodsServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("goods.stores.allGoods.and.activity".equals(operateName)) {
			//某个商家的所有商品和活动商品
			goodsService.findAllGoodsAndActivity(paramsMap, result);
		} else if ("goods.stores.Goods.detail".equals(operateName)) {
			//查询商品详情
			goodsService.findDetailGoods(paramsMap, result);
		} else if ("goods.stores.Goods.byLabel".equals(operateName)) {
			//查询贝壳专区和立享五折
			String page = request.getParameter("page");
			Map<String, Object> pMap = FastJsonUtil.jsonToMap(page);
			paramsMap.put("page_no", pMap.get("page_no"));
			paramsMap.put("page_size",  pMap.get("page_size"));
			goodsService.findGoodsByLabelAndAreaName(paramsMap, result);
		} else if ("goods.stores.search.byAreaName.AndNickname".equals(operateName)) {
			//搜索
			String page = request.getParameter("page");
			Map<String, Object> pMap = FastJsonUtil.jsonToMap(page);
			paramsMap.put("page_no", pMap.get("page_no"));
			paramsMap.put("page_size",  pMap.get("page_size"));
			goodsService.findGoodsByAreaNameAndNickname(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR, RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}


	/****
	 * 红包见面礼
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author 丁硕
	 * @date 2017年3月1日
	 */
	private void gcActivityServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("gcActivity.consumer.gcActivityScanQRCode".equals(operateName)) { // 消费者扫描二维码，见面礼
			gcActivityService.handleGcActivityScanQRCode(paramsMap, result);
		} else if ("gcActivity.platform.gcActivityFaceGiftPay".equals(operateName)) { // 见面礼红包到帐
			gcActivityService.handleGcActivityFaceGiftPay(paramsMap, result);
		} else if ("gcActivity.platform.gcActivityCreate".equals(operateName)) { // 见面礼红包到帐
			gcActivityService.gcActivityCreate(paramsMap, result);
		} else if ("gcActivity.consumer.gcActivityDetailCreate".equals(operateName)) { // 红包创建
			gcActivityService.gcActivityDetailCreate(paramsMap, result);
		} else if ("gcActivity.consumer.gcActivityDetailOpen".equals(operateName)) { //红包打开
			gcActivityService.handleGcActivityOpen(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}

	/**
	 * 权益活动
	 * 
	 * @Title: gdActivityServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void gdActivityServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("gdActivity.operate.gdActivityCreate".equals(operateName)) {
			// 创建会员权益活动（运营）
			gdActivityService.gdActivityCreate(paramsMap, result);
		} else if ("gdActivity.operate.gdActivityListPageFind".equals(operateName)) {
			// 查询会员权益活动列表（运营）
			gdActivityListForOpPageFind(request, paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityListPageFind".equals(operateName)) {
			// 查询会员权益活动列表（APP）
			gdActivityListForConsumerPageFind(request, paramsMap, result);
		}else if ("gdActivity.consumer.jifenOrderListPageFind".equals(operateName)) {
			// 查询积分兑换商品列表（APP）
			jifenOrderListPageFind(request, paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityDetailFind".equals(operateName)) {
			// 查询会员权益活动详情（APP）
			gdActivityService.gdActivityDetailFind(paramsMap, result);
		} else if ("gdActivity.operate.gdActivityCancel".equals(operateName)) {
			// 删除会员权益活动（运营）
			gdActivityService.gdActivityCancel(paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityGet".equals(operateName)) {
			// 领取会员权益活动（APP）
			gdActivityService.handleGdActivityGet(paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityCountFind".equals(operateName)) {
			// 查询会员权益商品数量（APP）
			gdActivityService.gdActivityCountFind(paramsMap, result);
		} else if ("gdActivity.operate.gdActivityDeliveryListPageFind".equals(operateName)) {
			// 查询会员权益商品订单列表（运营）
			gdActivityDeliveryListForOpPageFind(request, paramsMap, result);
		} else if ("gdActivity.operate.gdActivityDeliveryDeliver".equals(operateName)) {
			// 新增（配送发货）会员权益商品订单（运营）
			gdActivityDeliveryService.handleGdActivityDeliver(paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityDeliveryCountFind".equals(operateName)) {
			// 查询当前用户订单的订单数（APP）
			gdActivityDeliveryService.gdActivityDeliveryCountFind(paramsMap, result);
		} else if ("gdActivity.consumer.gdActivityDeliveryCreate".equals(operateName)) {
			// 新增会员权益活动商品订单（APP）
			gdActivityDeliveryService.handleGdActivityDeliveryCreate(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}

	/**
	 * 查询权益活动列表
	 * 
	 * @Title: gdActivityListPageFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void gdActivityListForOpPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdActivityService.gdActivityListForOpFind(paramsMap, result);
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
	 * 查询权益活动列表
	 * 
	 * @Title: gdActivityListPageFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void gdActivityListForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdActivityService.gdActivityListForConsumerFind(paramsMap, result);
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
	 * 查询积分兑换商品列表
	 * 
	 */
	public void jifenOrderListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ValidateUtil.validateParams(paramsMap, new String[] { "consumer_id" });
			gdActivityService.jifenOrderListForConsumerFind(paramsMap, result);
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
	 * 查询权益活动配送列表
	 * 
	 * @Title: gdActivityListPageFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void gdActivityDeliveryListForOpPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdActivityDeliveryService.gdActivityDeliveryListForOpFind(paramsMap, result);
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
	 * 会员权益
	 * 
	 * @Title: gdActivityServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void gdBenefitServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("gdBenefit.operate.gdBenefitCreate".equals(operateName)) {
			gdBenefitService.gdBenefitCreate(paramsMap, result);
		} else if ("gdBenefit.consumer.gdBenefitListPageFind".equals(operateName)) {
			gdBenefitListForConsumerPageFind(request, paramsMap, result);
		} else if ("gdBenefit.consumer.gdBenefitLogListPageFind".equals(operateName)) {
			gdBenefitLogListPageFind(request, paramsMap, result);
		} else if ("gdBenefit.consumer.usableGdBenefitCount".equals(operateName)) {
			gdBenefitService.usableGdBenefitCount(paramsMap, result);
		} else if ("gdBenefit.consumer.gdBenefitTransfer".equals(operateName)) {
			gdBenefitService.handleGdBenefitTransfer(paramsMap, result);
		} else if ("gdBenefit.consumer.freeCouponPay".equals(operateName)) {
			gdBenefitService.handleFreeCouponPay(paramsMap, result);
		} else if ("gdBenefit.consumer.MemberPointPay".equals(operateName)) {
			// 会员积分兑换商品
			gdBenefitService.handleMemberPointPay(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}

	/**
	 * 查询会员权益
	 * 
	 * @Title: gdActivityListPageFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void gdBenefitListForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdBenefitService.usableGdBenefitListForConsumerFind(paramsMap, result);
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
	 * 查询会员权益日志
	 * 
	 * @Title: gdActivityListPageFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void gdBenefitLogListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdBenefitService.gdBenefitLogListForConsumerFind(paramsMap, result);
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
	 * APP广告
	 * 
	 * @Title: gdAppAdvertServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void gdAppAdvertServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("gdAppAdvert.operate.gdAppAdvertCreate".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertCreate(paramsMap, result);
		} else if ("gdAppAdvert.operate.gdAppAdvertEdit".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertEdit(paramsMap, result);
		} else if ("gdAppAdvert.operate.gdAppAdvertDelete".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertDelete(paramsMap, result);
		} else if ("gdAppAdvert.operate.gdAppAdvertListFind".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertListForOpFind(paramsMap, result);
		} else if ("gdAppAdvert.app.gdAppAdvertFind".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertListForAppFind(paramsMap, result);
		} else if ("gdAppAdvert.app.gdAppAdvertFind_V1".equals(operateName)) {
			gdAppAdvertService.gdAppAdvertListForAppFind_V1(paramsMap, result);
		}else if ("gdAppAdvert.app.gdAppAdSplash".equals(operateName)) {
			gdAppAdvertService.gdAppAdSplash(paramsMap, result);
		}else if ("gdAppAdvert.app.gdAppVipPageAd".equals(operateName)) {
			gdAppAdvertService.gdAppVipPageAd(paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}

	/**
	 * 门店商品收藏
	 * 
	 * @Title: gdAppAdvertServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void psGoodsFavoritesServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("psGoodsFavorites.app.psGoodsFavoritesCreate".equals(operateName)) {
			psGoodsFavoritesService.psGoodsFavoritesCreate(paramsMap, result);
		} else if ("psGoodsFavorites.app.fgGoodsFavoritesListPageFind".equals(operateName)) {
			fgGoodsFavoritesListForAppPageFind(request, paramsMap, result);
		} else if ("psGoodsFavorites.app.tsGoodsFavoritesListPageFind".equals(operateName)) {
			tsGoodsFavoritesListForAppPageFind(request, paramsMap, result);
		} else if ("psGoodsFavorites.app.psGoodsFavoritesCancel".equals(operateName)) {
			psGoodsFavoritesService.psGoodsFavoritesCancel(paramsMap, result);
		} else if ("psGoodsFavorites.stores.fgGoodsFavoritesListPageFind".equals(operateName)) {
			fgGoodsFavoritesListPageFindForStores(request, paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}
	
	
	public void fgGoodsFavoritesListPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsFavoritesService.fgGoodsFavoritesListFindForStores(paramsMap, result);
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

	public void fgGoodsFavoritesListForAppPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsFavoritesService.fgGoodsFavoritesListForAppFind(paramsMap, result);
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

	public void tsGoodsFavoritesListForAppPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsFavoritesService.tsGoodsFavoritesListForAppFind(paramsMap, result);
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
	 * 门店商品服务
	 * 
	 * @Title: storeItemServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void storesGoodsServer(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("storesGoods.stores.specificationUnitFind".equals(operateName)) {
			specificationUnitFind(paramsMap, result);
		} else if ("storesGoods.stores.goodsCodeFind".equals(operateName)) {
			storesGoodsService.goodsCodeFind(paramsMap, result);
		} else if ("storesGoods.stores.goodsCreate".equals(operateName)) {
			storesGoodsService.storesGoodsCreate(paramsMap, result);
		} else if ("storesGoods.stores.goodsDelete".equals(operateName)) {
			storesGoodsService.storesGoodsDelete(paramsMap, result);
		} else if ("storesGoods.stores.goodsEdit".equals(operateName)) {
			storesGoodsService.storesGoodsEdit(paramsMap, result);
		} else if ("storesGoods.stores.goodsSaleStatusEdit".equals(operateName)) {
			storesGoodsService.storesGoodsSaleStatusEdit(paramsMap, result);
		} else if ("storesGoods.stores.stockReplenish".equals(operateName)) {
			storesGoodsService.stockReplenish(paramsMap, result);
		} else if ("storesGoods.stores.goodsListPageFind".equals(operateName)) {
			storesGoodsListPageFind(request, paramsMap, result);
		} else if ("storesGoods.stores.goodsDetailFind".equals(operateName)) {
			storesGoodsService.storesGoodsDetailFind(paramsMap, result);
		} else if ("storesGoods.stores.isSellGoodsCountForStoresSale".equals(operateName)) {
			storesGoodsService.isSellGoodsCountForStoresSaleFind(paramsMap, result);
		} else if ("storesGoods.stores.cashierGoodsListPageFind".equals(operateName)) {
			cashierGoodsListPageFind(request, paramsMap, result);
		} else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}

	/**
	 * 门店商品查询
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			storesGoodsService.storesGoodsListForStoresFind(paramsMap, result);
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
	 * 门店商品查询
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void cashierGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			storesGoodsService.cashierGoodsListFind(paramsMap, result);
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
	 * 规格类型查询
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void specificationUnitFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (String key : Constant.SPECIFICATION_UNIT_TYPE.keySet()) {
				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put("index", key);
				tempMap.put("content", Constant.SPECIFICATION_UNIT_TYPE.get(key));
				list.add(tempMap);
			}
			result.setResultData(list);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 商品服务
	 * 
	 * @Title: psGoodsServer
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void psGoodsServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		if ("psGoods.consumer.freeGetGoodsListPageFind".equals(operateName)) {
			freeGetGoodsListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.gdFreegetGoodsListPageFind".equals(operateName)) {
			gdFreegetGoodsListPageFind(request, paramsMap, result);
		}  else if ("psGoods.consumer.less9PsGoodsListPageFind".equals(operateName)) {
			less9PsGoodsListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.less9PsGoodsListPageFindNewOwn".equals(operateName)) {
			less9PsGoodsListPageFindNewOwn(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsForHuiguoListPageFind".equals(operateName)) {
			freeGetGoodsListPageFindForHuiguo(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsForOwnListPageFind".equals(operateName)) {
			freeGetGoodsListPageFindForOwn(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsForNewOwnListPageFind".equals(operateName)) {
			freeGetGoodsListPageFindForNewOwn(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsByLabelListPageFind".equals(operateName)) {
			freeGetGoodsByLabelListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.gdFreeGetGoodsByLabelListPageFind".equals(operateName)) {
			gdFreeGetGoodsByLabelListPageFind(request, paramsMap, result);//0元购
		} else if ("psGoods.consumer.freeGetGoodsForHuiguoByLabelListPageFind".equals(operateName)) {
			freeGetGoodsByLabelListPageFindForHuiguo(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsForOwnByLabelListPageFind".equals(operateName)) {
			freeGetGoodsByLabelListPageFindForOwn(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsForNewOwnByLabelListPageFind".equals(operateName)) {
			freeGetGoodsByLabelListPageFindForNewOwn(request, paramsMap, result);//查询按标签查询新自营商品列表 APP端 
		} else if ("psGoods.consumer.freeGetGoodsNewestListPageFind".equals(operateName)) {
			//淘淘领商品查询 上新查询
			freeGetGoodsNewestListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsPreSaleListPageFind".equals(operateName)) {
			//淘淘领商品查询 预售查询
			freeGetGoodsPreSaleListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.tsGoodsListPageFind".equals(operateName)) {
			tsGoodsListPageFind(request, paramsMap, result);
		} else if ("psGoods.supply.psGoodsSupplyCount".equals(operateName)) {
			psGoodsService.psGoodsSupplyCount(paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsSearchListPageFind".equals(operateName)) {
			freeGetGoodsListBySearchPageFind(request, paramsMap, result);
		} else if ("psGoods.operate.freeGetGoodsForOperateListFind".equals(operateName)) {		
			psGoodsService.freeGetGoodsForOperateListFind(paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsListPageFind".equals(operateName)) {
			//小程序-查询商品列表
			freeGetGoodsListPageFindForSmallProgram(request, paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsForHuiguoListPageFind".equals(operateName)) {
			//小程序-查询会过商品列表
			freeGetGoodsListPageFindByHuiguoForSmallProgram(request, paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsForOwnListPageFind".equals(operateName)) {
			//小程序-查询自营商品列表
			freeGetGoodsListPageFindByOwnForSmallProgram(request, paramsMap, result);
		}  else if ("psGoods.smallprogram.freeGetGoodsPreSaleListPageFind".equals(operateName)) {
			//小程序-查询预售商品列表
			freeGetGoodsPreSaleListPageFindForSmallProgram(request, paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsByLabelListPageFind".equals(operateName)) {
			//小程序-查询按标签查询商品列表
			freeGetGoodsByLabelListPageFindForSmallProgram(request, paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsActivityHomeListPageFind".equals(operateName)) {
			//小程序-查询活动商品列表
			freeGetGoodsActivityHomeListPageFindForSmallProgram(paramsMap, result);
		} else if ("psGoods.smallprogram.freeGetGoodsSearchListPageFind".equals(operateName)) {
			//小程序-查询按搜索条件查询商品列表
			freeGetGoodsSearchListPageFindForSmallProgram(request, paramsMap, result);
		} else if ("psGoods.consumer.freeGetGoodsTaobaoSalesEdit".equals(operateName)) {
			//销售数量增加
			psGoodsService.freeGetGoodsTaobaoSalesEdit(paramsMap, result);
		} else if ("psGoods.consumer.gdFreeGetGoodsTaobaoSalesEdit".equals(operateName)) {
			//销售数量增加
			psGoodsService.gdFreeGetGoodsTaobaoSalesEdit(paramsMap, result);
		} else if ("psGoods.stores.freeGetGoodsListPageFind".equals(operateName)) {
			//店东助手-查询商品列表
			freeGetGoodsListPageFindForStores(request, paramsMap, result);
		} else if ("psGoods.stores.freeGetGoodsNewestListPageFind".equals(operateName)) {
			//店东助手-查询上新商品列表
			freeGetGoodsNewestListPageFindForStores(request, paramsMap, result);
		} else if ("psGoods.stores.freeGetGoodsByLabelListPageFind".equals(operateName)) {
			//店东助手-查询按标签查询商品列表
			freeGetGoodsByLabelListPageFindForStores(request, paramsMap, result);
		} else if ("psGoods.stores.freeGetGoodsPreSaleListPageFind".equals(operateName)) {
			//店东助手-查询预售商品列表
			freeGetGoodsPreSaleListPageFindForStores(request, paramsMap, result);
		} else if ("psGoods.consumer.selectPsGoodsforGoodsCodeSet".equals(operateName)) {
			//分享模块-根据商品code查询商品信息
			psGoodsService.selectPsGoodsforGoodsCodeSet(paramsMap, result);
		} else if ("psGoods.consumer.beiKeGoodsForListPageFind".equals(operateName)) {
			//贝壳传奇-查询贝壳商城商品
			beiKeGoodsForListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.beiKeGoodsForListByLabelListPageFind".equals(operateName)) {
			beiKeGoodsForListByLabelListPageFind(request, paramsMap, result);//查询按标签查询贝壳商品列表 APP端 
		} else if ("psGoods.consumer.hongbaoGoodsForListPageFind".equals(operateName)) {
			//贝壳传奇-查询礼包专区商品
			hongbaoGoodsForListPageFind(request, paramsMap, result);
		} else if ("psGoods.consumer.vipGoodsFind".equals(operateName)){
			//贝壳传奇-查询vip推荐商品
			beikeMallGoodsService.vipGoodsFind(paramsMap, result);
		} else if ("psGoods.homeGoodsFind_V1".equals(operateName)) {
			//查找首页橱窗商品
			beikeMallGoodsService.homeGoodsFind_V1(paramsMap, result);
		} else if ("psGoods.findCommendGoods".equals(operateName)) {
			//查找首页推荐商品
			findCommendGoods(request, paramsMap, result);
		} else if ("psGoods.consumer.newYearGoodsFind".equals(operateName)){
			//查找年货节商品
			beikeMallGoodsService.newYearGoodsFind(paramsMap, result);
		}  else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR) + ";service:" + operateName);
		}
	}
	
	
	
	/**
	 * 查询自营商品列表 小程序端 
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindByOwnForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindByOwnForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询会过商品列表 小程序端 
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindByHuiguoForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindByHuiguoForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * APP服务
	 * 
	 * @Title: appService
	 * @param request
	 * @param response
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	private void appServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");
			if ("goods.stores.allGoods.and.activity".equals(operateName)) {
				//某个商家的所有商品和活动商品
				goodsService.findAllGoodsAndActivity(paramsMap, result);
			} else if ("goods.stores.and.activity".equals(operateName)) {
				//某个商家的活动商品
				String page = request.getParameter("page");
				Map<String, Object> pMap = FastJsonUtil.jsonToMap(page);
				paramsMap.put("page_no", pMap.get("page_no"));
				paramsMap.put("page_size",  pMap.get("page_size"));
				goodsService.findGoodsOfActivity(paramsMap, result);
			} else if ("goods.stores.Goods.detail".equals(operateName)) {
				//查询商品详情
				goodsService.findDetailGoods(paramsMap, result);
			} else if ("goods.stores.Goods.byLabel".equals(operateName)) {
				//查询贝壳专区和立享五折
				String page = request.getParameter("page");
				Map<String, Object> pMap = FastJsonUtil.jsonToMap(page);
				paramsMap.put("page_no", pMap.get("page_no"));
				paramsMap.put("page_size",  pMap.get("page_size"));
				goodsService.findGoodsByLabelAndAreaName(paramsMap, result);
			} else if ("goods.stores.search.byAreaName.AndNickname".equals(operateName)) {
				//搜索
				String page = request.getParameter("page");
				Map<String, Object> pMap = FastJsonUtil.jsonToMap(page);
				paramsMap.put("page_no", pMap.get("page_no"));
				paramsMap.put("page_size",  pMap.get("page_size"));
				goodsService.findGoodsByAreaNameAndNickname(paramsMap, result);
			}


			else if ("goods.itemStoreAdd".equals(operateName)) {
				itemStroeService.itemStoreCreate(paramsMap, result);
			} else if ("goods.itemQuickCreate".equals(operateName)) {
				itemStroeService.itemStoreQuickCreate(paramsMap, result);
			} else if ("goods.itemStoreEdit".equals(operateName)) {
				itemStroeService.itemStoreEdit(paramsMap, result);
			} else if ("goods.itemStoreSaleQtyDeduction".equals(operateName)) {
				itemStroeService.itemStoreSaleQtyDeduction(paramsMap, result);
			} else if ("goods.itemStoreSaleQtyRestore".equals(operateName)) {
				itemStroeService.itemStoreSaleQtyRestore(paramsMap, result);
			} else if ("goods.itemStoreGroupTypeFind".equals(operateName)) {
				itemStroeService.itemStoreGroupTypeFind(paramsMap, result);
			} else if ("goods.itemStoreGroupTypeForConsumerFind".equals(operateName)) {
				itemStroeService.itemStoreGroupTypeForConsumerFind(paramsMap, result);
			} else if ("goods.itemStorePageFindForConsumer".equals(operateName)) {
				itemStorePageFindForConsumer(request, paramsMap, result);
			} else if ("goods.recommendItemStoreForConsumerListPageFind".equals(operateName)) {
				recommendItemStoreForConsumerListPageFind(request, paramsMap, result);
			} else if ("goods.itemStorePageFindForStores".equals(operateName)) {
				itemStorePageFindForStores(request, paramsMap, result);
			} else if ("goods.itemStoreForOrderFind".equals(operateName)) {
				itemStroeService.itemStoreForOrderFind(paramsMap, result);
			} else if ("goods.itemStoreDetailFind".equals(operateName)) {
				itemStroeService.itemStoreDetailFind(paramsMap, result);
			} else if ("goods.itemFind".equals(operateName)) {
				itemStroeService.itemFind(paramsMap, result);
			} else if ("goods.itemStoreDeleted".equals(operateName)) {
				itemStroeService.itemStoreDeleted(paramsMap, result);
			} else if ("goods.psGoodsImport".equals(operateName)) {
				itemStroeService.handlePsGoodsImport(paramsMap, result);
			} else if ("goods.storesExchangeFind".equals(operateName)) {
				itemStroeService.storesExchangeFind(paramsMap, result);
			} else if ("goods.psGoodsSync".equals(operateName)) {
				psGoodsService.psGoodsSync(paramsMap, result);
			} else if ("goods.psOwnGoodsSync".equals(operateName)) {
				psGoodsService.handlePsOwnGoodsSync(paramsMap, result);
			} else if ("goods.selectGdGoodsSkuidList".equals(operateName)) {
				psGoodsService.selectGdGoodsSkuidList(paramsMap, result);
			} else if ("goods.operate.gdGoodsItemSync".equals(operateName)) {
				psGoodsService.gdGoodsItemSync(paramsMap, result);
			} else if ("goods.wpyGoodsSkuSync".equals(operateName)) {
				psGoodsService.wpyGoodsSkuSync(paramsMap, result);
			} else if ("goods.psGoodsEdit".equals(operateName)) {
				psGoodsService.psGoodsEdit(paramsMap, result);
			} else if ("goods.psGoodsNewEdit".equals(operateName)) {
				//新商品库编辑
				psGoodsService.psGoodsNewEdit(paramsMap, result);
			} else if ("goods.psGoodsStatusEdit".equals(operateName)) {
				psGoodsService.psGoodsStatusEdit(paramsMap, result);
			} else if ("goods.psGoodsOrderBottom".equals(operateName)) {
				psGoodsService.psGoodsOrderBottom(paramsMap, result);
			} else if ("goods.psGoodsSaleQtyRestore".equals(operateName)) {
				psGoodsService.psGoodsSaleQtyRestore(paramsMap, result);
			} else if ("goods.gdFreeGetGoodsSaleQtyRestore".equals(operateName)) {
				psGoodsService.gdFreeGetGoodsSaleQtyRestore(paramsMap, result);
			} else if ("goods.psGoodsSaleQtyForOwnRestore".equals(operateName)) {
				psGoodsService.handlePsGoodsSaleQtyForOwnRestore(paramsMap, result);
			} else if ("goods.newPsGoodsSaleQtyRestore".equals(operateName)) {
				psGoodsService.newPsGoodsSaleQtyRestore(paramsMap, result);
			} else if ("goods.psGoodsSaleQtyDeduction".equals(operateName)) {
				psGoodsService.psGoodsSaleQtyDeduction(paramsMap, result);
			} else if ("goods.gdFreeGetGoodsSaleQtyDeduction".equals(operateName)) {//0元购商品修改库存
				psGoodsService.gdFreeGetGoodsSaleQtyDeduction(paramsMap, result);
			} else if ("goods.newPsGoodsSaleQtyDeduction".equals(operateName)) {//新自营商品修改库存
				psGoodsService.newPsGoodsSaleQtyDeduction(paramsMap, result);
			} else if ("goods.wypGoodsSaleQtyDeduction".equals(operateName)) {
				psGoodsService.wypGoodsSaleQtyDeduction(paramsMap, result);
			} else if ("goods.wypGoodsSaleQtyRestore".equals(operateName)) {
				psGoodsService.handleWypGoodsSaleQtyRestore(paramsMap, result);
			} else if ("goods.wypGoodsSaleQtyValidate".equals(operateName)) {
				psGoodsService.wypGoodsSaleQtyValidate(paramsMap, result);
			} else if ("goods.wypGoodsFindForH5".equals(operateName)) {
				wypGoodsFindForH5(request, paramsMap, result);
			} else if ("goods.tsGoodsFindForH5".equals(operateName)) {
				// 接口修改成native
				tsGoodsListPageFind(request, paramsMap, result);
			} else if ("goods.wypGoodsDetailFindForH5".equals(operateName)) {
				psGoodsService.wypGoodsDetailFindForH5(paramsMap, result);
			} else if ("goods.psGoodsListPageFind".equals(operateName)) {
				psGoodsListPageFind(request, paramsMap, result);
			} else if ("goods.psUnionAllGoodsListFind".equals(operateName)) {
				psUnionAllGoodsListFind(request, paramsMap, result);
			} else if ("goods.psHuiGuoGoodsListPageFind".equals(operateName)){
				psHuiGuoGoodsListPageFind(request, paramsMap, result);
			} else if ("goods.psGoodsListPageFindTwo".equals(operateName)) {
				psGoodsListPageFindselectPsGoodsAndFiterOffDay(request, paramsMap, result);
			} else if ("goods.operate.gdGoodsItemListPageFindTwo".equals(operateName)) {
				gdGoodsItemListPageFindselectGdGoodsItemAndFiterOffDay(request, paramsMap, result);
			} else if ("goods.psGoodsDelete".equals(operateName)) {
				// 逻辑删除商品
				psGoodsService.psGoodsDelete(paramsMap, result);
			} else if ("goods.psGoodsActivityForOpFind".equals(operateName)) {
				psGoodsService.psGoodsActivityForOpFind(paramsMap, result);
			} else if ("goods.psGoodsActivityListFind".equals(operateName)) {
				psGoodsActivityListFind(paramsMap, result);
			} else if ("goods.psGoodsActivityFind".equals(operateName)) {
				logger.error("废弃接口->goods.psGoodsActivityFind");
				psGoodsActivityFind(paramsMap, result);
			} else if ("goods.fgGoodsActivityHomeForConsumerFind".equals(operateName)) {
				fgGoodsActivityHomeForConsumerFind(paramsMap, result);
			} else if ("goods.geGoodsActivityHomeForConsumerFind".equals(operateName)) {
				geGoodsActivityHomeForConsumerFind(paramsMap, result);
			} else if ("goods.groupGoodsActivityHomeForConsumerFind".equals(operateName)) {
				groupGoodsActivityHomeForConsumerFind(paramsMap, result);
			} else if ("goods.psGoodsDetailFind".equals(operateName)) {
				psGoodsService.psGoodsDetailFind(paramsMap, result);
			} else if ("goods.gdFreeGetGoodsDetailFind".equals(operateName)) {
				psGoodsService.gdFreeGetGoodsDetailFind(paramsMap, result);
			} else if ("goods.psGoodsSkuForOpFind".equals(operateName)) {
				psGoodsService.psGoodsSkuForOpFind(paramsMap, result);
			} else if ("goods.psGoodsSkuForStoresFind".equals(operateName)) {
				psGoodsService.psGoodsSkuForStoresFind(paramsMap, result);
			} else if ("goods.psGoodsFindForOrder".equals(operateName)) {
				psGoodsService.psGoodsFindForOrder(paramsMap, result);
			} else if ("goods.gdFreeGetGoodsFindForOrder".equals(operateName)) {
				psGoodsService.gdFreeGetGoodsFindForOrder(paramsMap, result);
			} else if ("goods.psGoodsFindForTsActivity".equals(operateName)) {
				psGoodsService.psGoodsFindForTsActivity(paramsMap, result);
			} else if ("goods.newestGoodsListPageFind".equals(operateName)) {
				newestGoodsListPageFind(request, paramsMap, result);
			} else if ("goods.freeGetGoodsStatusEdit".equals(operateName)) {
				psGoodsService.freeGetGoodsStatusEdit(paramsMap, result);
			} else if ("goods.freeGetGoodsListPageFind".equals(operateName)) {
				freeGetGoodsListPageFind(request, paramsMap, result);
			} else if ("goods.freeGetGoodsByLabelListPageFind".equals(operateName)) {
				freeGetGoodsByLabelListPageFind(request, paramsMap, result);
			} else if ("goods.freeGetGoodsNewestListPageFind".equals(operateName)) {
				freeGetGoodsNewestListPageFind(request, paramsMap, result);
			} else if ("goods.freeGetGoodsPreSaleListPageFind".equals(operateName)) {
				freeGetGoodsPreSaleListPageFind(request, paramsMap, result);
			} else if ("goods.goldExchangeGoodsPageFind".equals(operateName)) {
				goldExchangeGoodsPageFind(request, paramsMap, result);
			} else if ("goods.goldExchangeGoodsDetailFind".equals(operateName)) {
				psGoodsService.goldExchangeGoodsDetailFind(paramsMap, result);
			} else if ("goods.psGoodsActivitySync".equals(operateName)) {
				psGoodsService.handlePsGoodsActivitySync(paramsMap, result);
			} else if ("goods.gdViewAdd".equals(operateName)) {
				psGoodsService.gdViewAdd(paramsMap, result);
			} else if ("goods.gdSellAdd".equals(operateName)) {
				psGoodsService.gdSellAdd(paramsMap, result);
			} else if ("goods.goodsLogAdd".equals(operateName)) {
				psGoodsService.goodsLogAdd(paramsMap, result);
			} else if ("goods.goodsLogListPageFind".equals(operateName)) {
				goodsLogListPageFind(request, paramsMap, result);
			} else if ("goods.gdViewSellDetailPageFind".equals(operateName)) {
				gdViewSellDetailPageFind(request, paramsMap, result);
			} else if ("goods.ppActivityListPageFind".equals(operateName)) {
				ppActivityListPageFind(request, paramsMap, result);
			} else if ("goods.ppActivityListForWebPageFind".equals(operateName)) {
				ppActivityListForWebPageFind(request, paramsMap, result);
			} else if ("goods.ppActivityAdd".equals(operateName)) {
				ppActivityService.ppActivityAdd(paramsMap, result);
			} else if ("goods.ppActivityEdit".equals(operateName)) {
				ppActivityService.ppActivityEdit(paramsMap, result);
			} else if ("goods.ppActivityDetailListPageFind".equals(operateName)) {
				ppActivityDetailListPageFind(request, paramsMap, result);
			} else if ("goods.ppActivityDetailAdd".equals(operateName)) {
				ppActivityService.ppActivityDetailAdd(paramsMap, result);
			} else if ("goods.ppActivityDetailEdit".equals(operateName)) {
				ppActivityService.ppActivityDetailEdit(paramsMap, result);
			} else if ("goods.plActivityCreate".equals(operateName)) {
				plPartcipatorService.plActivityCreate(paramsMap, result);
			} else if ("goods.plActivityListForOpPageFind".equals(operateName)) {
				plActivityListForOpPageFind(request, paramsMap, result);
			} else if ("goods.plActivityCancel".equals(operateName)) {
				plPartcipatorService.plActivityCancel(paramsMap, result);
			} else if ("goods.plPartcipatorAdd".equals(operateName)) {
				plPartcipatorService.plPartcipatorAdd(paramsMap, result);
			} else if ("goods.plLuckyDeliveryListForConsumerFind".equals(operateName)) {
				plLuckyDeliveryListForConsumerFind(paramsMap, result);
			} else if ("goods.plLuckyDeliveryListForOpPageFind".equals(operateName)) {
				plLuckyDeliveryListForOpPageFind(request, paramsMap, result);
			} else if ("goods.plLuckyDeliveryDelivered".equals(operateName)) {
				plPartcipatorService.plLuckyDeliveryDelivered(paramsMap, result);
			} else if ("goods.plActivityListForConsumerPageFind".equals(operateName)) {
				plActivityListForConsumerPageFind(request, paramsMap, result);
			} else if ("goods.plActivityListForConsumerDetailFind".equals(operateName)) {
				plPartcipatorService.plActivityListForConsumerDetailFind(paramsMap, result);
			} else if ("goods.plPartcipatorListForConsumerPageFind".equals(operateName)) {
				plPartcipatorListForConsumerPageFind(request, paramsMap, result);
			} else if ("goods.ldActivitiesCreate".equals(operateName)) {
				ldActivityService.ldActivityCreate(paramsMap, result);
			} else if ("goods.ldYyyActivityCreate".equals(operateName)) { // 摇一摇活动创建
				ldActivityService.ldYyyActivityCreate(paramsMap, result);
			} else if ("goods.ldGglActivityCreate".equals(operateName)) { // 刮刮乐活动创建
				ldActivityService.ldGglActivityCreate(paramsMap, result);
			} else if ("goods.ldYyyActivityLuckDrawFind".equals(operateName)) { // 摇一摇抽奖机会获取，系统返回中奖号码与随机列表
				ldActivityService.ldYyyActivityLuckDrawFind(paramsMap, result);
			} else if ("goods.ldGglActivityLuckDrawFind".equals(operateName)) { // 刮刮乐抽奖机会获取
				ldActivityService.ldGglActivityLuckDrawFind(paramsMap, result);
			} else if ("goods.ldActivtiyWinning".equals(operateName)) { // 抽奖活动中奖
				ldActivityService.ldActivtiyWinning(paramsMap, result);
			} else if ("goods.nearbyLdActivityStoreFind".equals(operateName)) { // 附近有活动的门店列表
				nearbyLdActivityStoreFind(request, paramsMap, result);
			} else if ("goods.ldActivityListByStoresForConsumerPageFind".equals(operateName)) { // 消费者端获取门店正在进行活动的列表
				ldActivityListByStoresForConsumerPageFind(request, paramsMap, result);
			} else if ("goods.ldActivitiesDetailPageFind".equals(operateName)) {
				ldActivitiesDetailPageFind(request, paramsMap, result);
			} else if ("goods.ldActivitiesForMemberFind".equals(operateName)) {
				ldActivityService.ldActivityForMemberFind(paramsMap, result);
			} else if ("goods.ldActivitiesPageFind".equals(operateName)) { // 活动详情查询
				ldActivitiesPageFind(request, paramsMap, result);
			} else if ("goods.ldActivityForStorePageFind".equals(operateName)) { // 店东发起的活动列表
				ldActivityForStorePageFind(request, paramsMap, result);
			} else if ("goods.ldActivityPayInfoFind".equals(operateName)) {
				ldActivityService.ldActivityPayInfoFind(paramsMap, result);
			} else if ("goods.dskActivityProcessCreateNotify".equals(operateName)) {
				ldActivityService.dskActivityProcessCreateNotify(paramsMap, result);
			} else if ("goods.yyyActivityProcessCreateNotify".equals(operateName)) {
				ldActivityService.yyyActivityProcessCreateNotify(paramsMap, result);
			} else if ("goods.gglActivityProcessCreateNotify".equals(operateName)) {
				ldActivityService.gglActivityProcessCreateNotify(paramsMap, result);
			} else if ("goods.ldActivityProcessForStorePageFind".equals(operateName)) {
				ldActivityProcessForStorePageFind(request, paramsMap, result);
			} else if ("goods.ldActivityProcessForConsumerPageFind".equals(operateName)) {
				ldActivityProcessForConsumerPageFind(request, paramsMap, result);
			} else if ("goods.ldActivityConsumerListPageFind".equals(operateName)) { // 消费者查询活动的参与列表
				ldActivityConsumerListPageFind(request, paramsMap, result);
			} else if ("goods.ldActivityValidate".equals(operateName)) {
				ldActivityService.handleLdActivityValidate(paramsMap, result);
			} else if ("goods.ldActivityValidateFind".equals(operateName)) {
				ldActivityService.ldActivityValidateFind(paramsMap, result);
			} else if ("goods.ldActivityCancel".equals(operateName)) {
				ldActivityService.handleLdActivityCancel(paramsMap, result);
			} else if ("goods.couponIssueApply".equals(operateName)) {
				goodsService.handleCouponIssueApply(paramsMap, result);
			} else if ("goods.sysitemItemOffShelf".equals(operateName)) {
				sysitemItemOffShelf(paramsMap, result);
			} else if ("goods.couponAndStoreFindForShare".equals(operateName)) {
				goodsService.couponAndStoreFindForShare(paramsMap, result);
			} else if ("goods.couponListFindForConsumer".equals(operateName)) {
				goodsService.couponListFindForConsumer(paramsMap, result);
			} else if ("goods.couponListFindForStores".equals(operateName)) {
				goodsService.couponListFindForStores(paramsMap, result);
			} else if ("goods.couponDetailForConsumerFind".equals(operateName)) {
				goodsService.couponDetailForConsumerFind(paramsMap, result);
			} else if ("goods.couponDetailForStoresFind".equals(operateName)) {
				goodsService.couponDetailForStoresFind(paramsMap, result);
			} else if ("goods.couponFree".equals(operateName)) {
				goodsService.couponFree(paramsMap, result);
			} else if ("goods.couponPresented".equals(operateName)) {
				goodsService.handleCouponPresented(paramsMap, result);
			} else if ("goods.couponCreateNotify".equals(operateName)) {
				goodsService.handleCouponCreateNotify(paramsMap, result);
			} else if ("goods.couponDetailFind".equals(operateName)) {
				goodsService.couponDetailFind(paramsMap, result);
			} else if ("goods.couponValidate".equals(operateName)) {
				goodsService.handleCouponValidate(paramsMap, result);
			} else if ("goods.couponValidateFindForStores".equals(operateName)) {
				goodsService.couponValidateFindForStores(paramsMap, result);
			} else if ("goods.storesActivityCount".equals(operateName)) {
				goodsService.storesActivityCountFind(paramsMap, result);
			} else if ("goods.storesCouponTotal".equals(operateName)) {
				goodsService.storesCouponTotalFind(paramsMap, result);
			} else if ("goods.storesCouponCount".equals(operateName)) {
				goodsService.storesCouponCountFind(paramsMap, result);
			} else if ("goods.recommendStoresGoodsListFind".equals(operateName)) {
				goodsService.recommendStoresGoodsListFind(paramsMap, result);
			} else if ("goods.storesCouponIsExist".equals(operateName)) {
				goodsService.storesCouponIsExist(paramsMap, result);
			} else if ("goods.storesCouponProp".equals(operateName)) {
				storesCouponProp(paramsMap, result);
			} else if ("goods.memberGiftCardSend".equals(operateName)) {
				goodsService.memberGiftCardSend(paramsMap, result);
			} else if ("goods.memberGiftCardBatchSend".equals(operateName)) {
				goodsService.memberGiftCardBatchSend(paramsMap, result);
			} else if ("goods.memberLotteryNumFind".equals(operateName)) {
				goodsService.handleMemberLotteryNumFind(paramsMap, result);
			} else if ("goods.gdAdvertEdit".equals(operateName)) {
				goodsService.gdAdvertEdit(paramsMap, result);
			} else if ("goods.gdAdvertFind".equals(operateName)) {
				goodsService.gdAdvertFind(paramsMap, result);
			}else if ("goods.freeGoodsBannerImageFind".equals(operateName)) {
				goodsService.freeGoodsBannerImageFind(paramsMap, result);
			} else if ("goods.gcActivityListPageFind".equals(operateName)) {
				gcActivityListPageFind(request, paramsMap, result);
			} else if ("goods.gcActivityOpen".equals(operateName)) {
				gcActivityService.handleGcActivityOpen(paramsMap, result);
			} else if ("goods.gcActivityDetailListPageFind".equals(operateName)) {
				gcActivityDetailListPageFind(request, paramsMap, result);
			} else if ("goods.gcActivityDetailListForConsumerPageFind".equals(operateName)) {
				gcActivityDetailListForConsumerPageFind(request, paramsMap, result);
			} else if ("goods.gcActivityDetailCount".equals(operateName)) {
				gcActivityService.gcActivityDetailCountFind(paramsMap, result);
			} else if ("recharger.app.mobileRechargeTypeListFind".equals(operateName)) {
				mobileRechargeService.mobileRechargeTypeListFind(paramsMap, result);
			} else if ("recharger.app.mobileRechargeTypeDetailFind".equals(operateName)) {
				mobileRechargeService.mobileRechargeTypeDetailFind(paramsMap, result);
			} else if ("goods.updateGoodsSaleOrShelves".equals(operateName)) {
				updateGoodsSaleOrShelves(request, paramsMap, result);
			} else if("goods.gdCategoryCatListFind".equals(operateName)){
				//商品分类列表
				gdCategoryCatService.gdCategoryCatListFind(paramsMap, result);
			} else if("goods.queryOwnGoodsCategoryDetail".equals(operateName)){
				//商品分类详情
				gdCategoryCatService.getCategoryCatDetail(paramsMap, result);
			} else if("goods.queryCategoryCatPageFind".equals(operateName)){
				queryCategoryCatPageFind(request, paramsMap, result); 
			} else if("goods.gdCategoryCatCreate".equals(operateName)){//新增商品分类
				gdCategoryCatService.gdCategoryCatCreate(paramsMap, result);
			} else if("goods.gdCategoryCatEdit".equals(operateName)){//编辑商品分类
				gdCategoryCatService.gdCategoryCatEdit(paramsMap, result);
			} else if("goods.gdCategoryPropsListFind".equals(operateName)){
                gdCategoryCatService.gdCategoryPropsFind(paramsMap, result);
			} else if("goods.categoryPropsPageFind".equals(operateName)){
				gdCategoryPropsPageFind(request, paramsMap, result);
			} else if("goods.gdCategoryPropsCreate".equals(operateName)){
                gdCategoryCatService.gdCategoryPropsCreate(paramsMap, result);
			} else if("goods.gdCategoryPropsEdit".equals(operateName)){
                gdCategoryCatService.gdCategoryPropsEdit(paramsMap, result);
			} else if("goods.gdCategoryPropsDetailFind".equals(operateName)){
				gdCategoryCatService.gdCategoryPropsDetailFind(paramsMap, result);
			}else if ("goods.selectPsGoodsSkuid".equals(operateName)) {
				goodsService.selectPsGoodsSkuid(paramsMap, result);
			}else if ("goods.queryGdGoodsSkuidBySkuId".equals(operateName)) {
				psGoodsService.queryGdGoodsSkuidBySkuId(paramsMap, result);
			} else if ("goods.selectPsGoodsSkuidBySkuId".equals(operateName)) {
				goodsService.selectPsGoodsSkuidBySkuId(paramsMap, result);
			} else if ("goods.beikeMallGoodsFindForOrder".equals(operateName)) {
				psGoodsService.beikeMallGoodsFindForOrder(paramsMap, result);
			} else if ("goods.beikeMallGoodsDetailFind".equals(operateName)) {
				psGoodsService.beikeMallGoodsDetailFind(paramsMap, result);
			} else if ("goods.BeikeMallGoodsSaleQtyForOwnRestore".equals(operateName)) {
				psGoodsService.handleBeikeMallGoodsSaleQtyForOwnRestore(paramsMap, result);
			} else if ("goods.hongbaoGoodsDetailFind".equals(operateName)) {
				psGoodsService.hongbaoGoodsDetailFind(paramsMap, result);
			} else if ("goods.hongbaoGoodsSaleQtyUpdate".equals(operateName)) {
				psGoodsService.hongbaoGoodsSaleQtyUpdate(paramsMap, result);
			} else if ("goods.homeGoodsFind".equals(operateName)) {
				beikeMallGoodsService.homeGoodsFind(paramsMap, result);
			} else if ("goods.gdItemStoreSaleQtyForRestore".equals(operateName)) {
				itemStroeService.handleGdItemStoreSaleQtyForRestore(paramsMap, result);
			} else if ("goods.beikeMallGoodsSaleQtyDeduction".equals(operateName)) {//新自营商品修改库存
				psGoodsService.beikeMallGoodsSaleQtyDeduction(paramsMap, result);
			} else if ("goods.hongbaoGoodsSaleQtyDeduction".equals(operateName)) {//礼券专区修改库存
				hongbaoGoodsService.hongbaoGoodsSaleQtyUpdate(paramsMap, result);
			} else if ("goods.hongbaoGoodsFindForOrder".equals(operateName)) {
				hongbaoGoodsService.hongbaoGoodsFindForOrder(paramsMap, result);
			} else if ("goods.beikeMallGoodsDetailFindNew".equals(operateName)) {
				psGoodsService.beikeMallGoodsDetailFindNew(paramsMap, result);
			} else if ("goods.beikeMallGoodsSalesVolumeUpdate".equals(operateName)) {
				psGoodsService.beikeMallGoodsSalesVolumeUpdate(paramsMap, result);
			} else if ("goods.hongBaoGoodsSalesVolumeUpdate".equals(operateName)) {
				psGoodsService.hongBaoGoodsSalesVolumeUpdate(paramsMap, result);
			} else if ("goods.vipGoodsFind".equals(operateName)) {
				//查询vip页面活动商品
				beikeMallGoodsService.vipGoodsFind(paramsMap, result);
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
	 * 查询商品分类分页
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void queryCategoryCatPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdCategoryCatService.gdCategoryCatListFind(paramsMap, result);
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
     * 查询商品规格属性列表分页
     * @param request
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
	public void gdCategoryPropsPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gdCategoryCatService.gdCategoryPropsFind(paramsMap, result);
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
	 * 查询商品列表(搜索) 小程序端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	public void freeGetGoodsSearchListPageFindForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			psGoodsService.freeGetGoodsListBySearchFindForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(搜索)
	 */
	public void freeGetGoodsListBySearchPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			psGoodsService.freeGetGoodsListBySearchFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 店东助手查询商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void itemStorePageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			itemStroeService.itemStoreForStoresListFind(paramsMap, result);
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
	 * 消费者查询商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void itemStorePageFindForConsumer(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			itemStroeService.itemStoreForConsumerListFind(paramsMap, result);
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
	 * 消费者查询门店推荐的商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void recommendItemStoreForConsumerListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			paramsMap.put("is_recommend", "Y");
			itemStroeService.itemStoreForConsumerListFind(paramsMap, result);
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
	 * 我要批商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void wypGoodsFindForH5(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			List<String> categoryList = new ArrayList<String>();
			categoryList.add("预售");
			categoryList.add("批发");
			paramsMap.put("category_in", categoryList);
			String display_area = StringUtil.formatStr(paramsMap.get("display_area"));
			if (!StringUtils.isEmpty(display_area)) {
				List<String> list = StringUtil.str2List(display_area, ",");
				if (list.size() > 1) {
					paramsMap.remove("display_area");
					paramsMap.put("display_area_in", list);
				}
			}
			String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
			if (!StringUtils.isEmpty(label_promotion)) {
				List<String> list = StringUtil.str2List(label_promotion, ",");
				if (list.size() > 1) {
					paramsMap.remove("label_promotion");
					paramsMap.put("label_promotion_in", list);
				}
			}
			psGoodsService.psGoodsFindForH5(paramsMap, result);
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
	 * 供应商已上架所有商品总数
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void psGoodsSupplyCount(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		psGoodsService.psGoodsSupplyCount(paramsMap, result);
	}

	/**
	 * 伙拼团商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void tsGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			paramsMap.put("category", "团购");
			psGoodsService.tsGoodsListFind(paramsMap, result);
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
	 * 查询商品列表-店东助手端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListPageFindForStores(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)小程序端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)APP端 自营
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindForOwn(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindForOwn(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)APP端 新自营
	 */
	public void freeGetGoodsListPageFindForNewOwn(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindForNewOwn(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)APP端   贝壳商城
	 */
	public void beiKeGoodsForListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		PageParam pageParam = getPageParam(request);
		if (null != pageParam) {
			paramsMap.put("pageParam", pageParam);
		}
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		String goods_like = StringUtil.formatStr(paramsMap.get("goods_like"));
		int page_no = pageParam.getPage_no();
		int page_size = pageParam.getPage_size();
		String key = "【beiKeGoodsForListPageFind】" + label_promotion+"【"+page_no+"】"+goods_like+"【"+page_size+"】";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		if("居家清洁".equals(label_promotion)){
			paramsMap.put("label_promotion","1");
		} else if("卫浴用品".equals(label_promotion)){
			paramsMap.put("label_promotion","2");
		} else if("个人护理".equals(label_promotion)){
			paramsMap.put("label_promotion","3");
		} else if("家纺内衣".equals(label_promotion)){
			paramsMap.put("label_promotion","4");
		} else if("纸制品".equals(label_promotion)){
			paramsMap.put("label_promotion","5");
		}
		psGoodsService.beiKeGoodsForListPageFind(paramsMap, result);
		Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
		resultData.put("page", pageParam);
		// 设置缓存 120秒缓存
		redisUtil.setObj(key, resultData, 120);
	
	}
	/**
	 * 查询商品列表(全部)APP端   推荐商品
	 */
	public void findCommendGoods(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		PageParam pageParam = getPageParam(request);
		if (null != pageParam) {
			paramsMap.put("pageParam", pageParam);
		}
		int page_no = pageParam.getPage_no();
		int page_size = pageParam.getPage_size();
		String key = "【findCommendGoods】" + "【"+page_no+"】"+"【"+page_size+"】";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		beikeMallGoodsService.findCommendGoods(paramsMap, result);
		Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
		resultData.put("page", pageParam);
		// 设置缓存 120秒缓存
		redisUtil.setObj(key, resultData, 120);
	}
	
	/**
	 * 查询商品列表(全部)APP端   红包兑商城
	 */
	public void hongbaoGoodsForListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		PageParam pageParam = getPageParam(request);
		if (null != pageParam) {
			paramsMap.put("pageParam", pageParam);
		}
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		String goods_like = StringUtil.formatStr(paramsMap.get("goods_like"));
		int page_no = pageParam.getPage_no();
		int page_size = pageParam.getPage_size();
		String key = "【hongbaoGoodsForListPageFind】" + label_promotion+"【"+page_no+"】"+goods_like+"【"+page_size+"】";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		psGoodsService.hongbaoGoodsForListPageFind(paramsMap, result);
		Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
		resultData.put("page", pageParam);
		// 设置缓存 120秒缓存
		redisUtil.setObj(key, resultData, 120);
	}
	
	/**
	 * 查询商品列表(全部)APP端 会过
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFindForHuiguo(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFindForHuiguo(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)APP端 淘淘领
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsListFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(全部)APP端 0元购
	 */
	public void gdFreegetGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			
			psGoodsService.gdFreegetGoodsListPageFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 *  查询价格小于等于9.9已上架的商品列表  APP端 淘淘领
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void less9PsGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.less9PsGoodsListFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 *  查询价格小于等于9.9已上架的商品列表  APP端 淘淘领(新自营)
	 */
	public void less9PsGoodsListPageFindNewOwn(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.less9PsGoodsListFindNewOwn(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 小程序端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFindForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFindForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 APP端 会过
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFindForOwn(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFindForOwn(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询新自营商品列表 APP端 
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFindForNewOwn(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFindForNewOwn(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 APP端 会过
 	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFindForHuiguo(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFindForHuiguo(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 店东助手端 
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFindForStores(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 领有惠端 
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsByLabelListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsByLabelListFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询按标签查询商品列表 领有惠端(0元购) 
	 */
	public void gdFreeGetGoodsByLabelListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			
			psGoodsService.gdFreeGetGoodsByLabelListPageFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询上新商品列表 店东助手端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsNewestListPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsNewestListFindForStores(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询上新商品列表 领有惠端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsNewestListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);

			psGoodsService.freeGetGoodsNewestListFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询商品列表(预售) 小程序端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsPreSaleListPageFindForSmallProgram(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			psGoodsService.freeGetGoodsPreSaleListFindForSmallProgram(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询预售商品列表 店东主手端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsPreSaleListPageFindForStores(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			psGoodsService.freeGetGoodsPreSaleListFindForStores(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 查询预售商品列表 领有惠端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsPreSaleListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			psGoodsService.freeGetGoodsPreSaleListFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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
	 * 名品汇商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void goldExchangeGoodsPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			String key = "【goldExchangeGoodsPageFind】" + FastJsonUtil.toJson(paramsMap);
			Object obj = redisUtil.getObj(key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}

			psGoodsService.goldExchangeGoodsFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);

			// 设置缓存 60秒缓存
			redisUtil.setObj(key, resultData, 60);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public void psGoodsOnshelfCount(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id" });

	}

	/**
	 * 查询自营商贸商品
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void psGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = psGoodsListPageFindSetParams(request, paramsMap);
			psGoodsService.psGoodsListFind(paramsMap, result);
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
	 * 查询自营商贸商品
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void psUnionAllGoodsListFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = psGoodsListPageFindSetParams(request, paramsMap);
			psGoodsService.psUnionAllGoodsListFind(paramsMap, result);
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
	
	
	public void gdGoodsItemListPageFindselectGdGoodsItemAndFiterOffDay(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = psGoodsListPageFindSetParams(request, paramsMap);
			psGoodsService.selectGdGoodsItemAndFiterOffDay(paramsMap, result);
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
	
	public void psGoodsListPageFindselectPsGoodsAndFiterOffDay(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = psGoodsListPageFindSetParams(request, paramsMap);
			psGoodsService.selectPsGoodsAndFiterOffDay(paramsMap, result);
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

	private PageParam psGoodsListPageFindSetParams(HttpServletRequest request, Map<String, Object> paramsMap) throws Exception {
		// 引入分页查询
		PageParam pageParam = getPageParam(request);
		if (null != pageParam) {
            paramsMap.put("pageParam", pageParam);
        }

		String data_source = StringUtil.formatStr(paramsMap.get("data_source"));
		if (!StringUtils.isEmpty(data_source)) {
            List<String> list = StringUtil.str2List(data_source, ",");
            if (list.size() > 0) {
                paramsMap.remove("data_source");
                paramsMap.put("data_source_in", list);
            }
        }

		String data_source_type = StringUtil.formatStr(paramsMap.get("data_source_type"));
		paramsMap.put("data_source_type", data_source_type);

		String status = StringUtil.formatStr(paramsMap.get("status"));
		if (!StringUtils.isEmpty(status)) {
            List<String> list = StringUtil.str2List(status, ",");
            if (list.size() > 1) {
                paramsMap.remove("status");
                paramsMap.put("status_in", list);
            }
        }
		String category = StringUtil.formatStr(paramsMap.get("category"));
		if (!StringUtils.isEmpty(category)) {
            List<String> list = StringUtil.str2List(category, ",");
            if (list.size() > 1) {
                paramsMap.remove("category");
                paramsMap.put("category_in", list);
            }
        }
		String display_area = StringUtil.formatStr(paramsMap.get("display_area"));
		if (!StringUtils.isEmpty(display_area)) {
            List<String> list = StringUtil.str2List(display_area, ",");
            if (list.size() > 1) {
                paramsMap.remove("display_area");
                paramsMap.put("display_area_in", list);
            }
        }
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		if (!StringUtils.isEmpty(label_promotion)) {
            List<String> list = StringUtil.str2List(label_promotion, ",");
            if (list.size() > 1) {
                paramsMap.remove("label_promotion");
                paramsMap.put("label_promotion_in", list);
            }
        }
		String taobao_price = StringUtil.formatStr(paramsMap.get("taobao_price"));
		paramsMap.put("taobao_price", taobao_price);
		return pageParam;
	}

	/**
	 * 查询推荐商品(老接口)
	 * 
	 * @Title: psGoodsActivityFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void psGoodsActivityFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			psGoodsService.psGoodsActivityFind(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询推荐商品
	 * 
	 * @Title: psGoodsActivityListFind
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	public void psGoodsActivityListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			psGoodsService.psGoodsActivityListFind(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询最近的商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void newestGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsService.newestPsGoodsListForOpFind(paramsMap, result);
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
	 * 查询本地生活首页推荐商品列表 小程序端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void freeGetGoodsActivityHomeListPageFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			paramsMap.put("activity_type", "HDMS_04");
			psGoodsService.freeGetGoodsActivityHomeListFindForSmallProgram(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 查询本地生活首页推荐商品列表 APP端
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void fgGoodsActivityHomeForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			paramsMap.put("activity_type", "HDMS_04");
			psGoodsService.psGoodsActivityHomeForConsumerFind(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询本地生活首页推荐商品列表(名品汇)
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void geGoodsActivityHomeForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			paramsMap.put("activity_type", "HDMS_05");
			psGoodsService.psGoodsActivityHomeForConsumerFind(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询本地生活首页推荐商品列表(伙拼团)
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void groupGoodsActivityHomeForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			paramsMap.put("activity_type", "HDMS_06");
			psGoodsService.psGoodsActivityHomeForConsumerFind(paramsMap, result);
			diffTimeCacl(result, "valid_thru");
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 商品下架
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void sysitemItemOffShelf(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "item_id", "remark" });
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("item_id", paramsMap.get("item_id"));
			tempMap.put("status", Constant.STATUS_OFF_SHELF);
			tempMap.put("remark", paramsMap.get("remark"));
			goodsService.sysitemItemStatusEdit(tempMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询一元购活动详情列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ldActivitiesDetailPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			String status = StringUtil.formatStr(paramsMap.get("status"));
			if (!"".equals(status)) {
				List<String> list = StringUtil.str2List(status, ",");
				if (list.size() > 1) {
					paramsMap.remove("status");
					paramsMap.put("status_in", list);
				}
			}
			String stores_id = StringUtil.formatStr(paramsMap.get("stores_id"));
			if (!"".equals(stores_id)) {
				List<String> list = StringUtil.str2List(stores_id, ",");
				if (list.size() > 1) {
					paramsMap.remove("stores_id");
					paramsMap.put("stores_id_in", list);
				}
			}
			ldActivityService.ldActivityFind(paramsMap, result);
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
	 * 查询一元购活动详情列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ldActivitiesPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityListFind(paramsMap, result);
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

	/****
	 * 店东发起的活动列表
	 * 
	 * @author 丁硕
	 * @date 2016年12月10日
	 */
	public void ldActivityForStorePageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityForStoreListFind(paramsMap, result);
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
	 * 查询一元购购买记录
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ldActivityProcessForStorePageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "activity_id" });
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityProcessForStoreFind(paramsMap, result);
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
	 * 查询一元购购买记录
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ldActivityProcessForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "consumer_id" });
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityProcessForConsumerFind(paramsMap, result);
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
	 * 抽奖活动参与的消费者列表
	 * 
	 * @author 丁硕
	 * @date 2016年12月10日
	 */
	public void ldActivityConsumerListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "consumer_id" });
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityConsumerListFind(paramsMap, result);
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
	 * 查询参与的活动详情列表（消费者端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void plPartcipatorListForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			plPartcipatorService.plPartcipatorListForConsumerFind(paramsMap, result);
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

	/***
	 * 消费者端获取门店正在进行活动的列表
	 */
	public void ldActivityListByStoresForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ldActivityService.ldActivityListByStoresForConsumerFind(paramsMap, result);
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

	/***
	 * 查询附近有活动的门店列表
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author 丁硕
	 * @date 2016年12月10日
	 */
	public void nearbyLdActivityStoreFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			paramsMap.put("range", "20000");
			ldActivityService.nearbyLdActivityStoreFind(paramsMap, result);
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
	 * 查询热卖商品列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void gdViewSellDetailPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsService.gdViewSellDetailFind(paramsMap, result);
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
	 * 查询新品秀列表（运营端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ppActivityListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ppActivityService.ppActivityListFind(paramsMap, result);
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
	 * 查询新品秀列表(H5端)
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ppActivityListForWebPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ppActivityService.ppActivityListForWebFind(paramsMap, result);
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
	 * 查询新品秀报名列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void ppActivityDetailListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			ppActivityService.ppActivityDetailListFind(paramsMap, result);
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
	 * 查询商品操作日志列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void goodsLogListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			psGoodsService.goodsLogListFind(paramsMap, result);
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
	 * 查询会员优惠券列表
	 */
	public void gcActivityListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gcActivityService.gcActivityListFind(paramsMap, result);
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
	 * 查询会员红包列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void gcActivityDetailListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			gcActivityService.gcActivityDetailListFind(paramsMap, result);
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
	 * 查询会员红包列表
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void gcActivityDetailListForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			paramsMap.put("status_neq", "deleted");
			gcActivityService.gcActivityDetailListFind(paramsMap, result);
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
	 * 查询门店附近优惠券
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void storesCouponProp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "longitude", "latitude" });
			PageParam pageParam = new PageParam();
			paramsMap.put("pageParam", pageParam);
			goodsService.storesCouponProp(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询抽奖活动列表（运营端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void plActivityListForOpPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			plPartcipatorService.plActivityListForOpFind(paramsMap, result);
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
	 * 查询抽奖活动列表（消费者端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void plActivityListForConsumerPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			plPartcipatorService.plActivityListForConsumerFind(paramsMap, result);
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
	 * 查询中奖列表（消费者端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void plLuckyDeliveryListForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = new PageParam();
			paramsMap.put("pageParam", pageParam);
			plPartcipatorService.plLuckyDeliveryListForConsumerFind(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询参与活动的会员列表（运营端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Deprecated
	public void plPartcipatorListForOpPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			plPartcipatorService.plPartcipatorListForOpFind(paramsMap, result);
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
	 * 查询开奖列表（运营端）
	 * 
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void plLuckyDeliveryListForOpPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			plPartcipatorService.plLuckyDeliveryListForOpFind(paramsMap, result);
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
	 * 计算倒计时
	 * 
	 * @Title: diffTimeCacl
	 * @param result
	 * @param param_name
	 * @author tiny
	 */
	private void diffTimeCacl(ResultData result, String paramName) {
		Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
		List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
		for (Map<String, Object> map : list) {
			String dateStr = StringUtil.formatStr(map.get(paramName));
			Date caclDate = new Date();
			if (dateStr.length() == DateUtil.fmt_yyyyMMdd.length()) {
				caclDate = DateUtil.str2Date(dateStr, DateUtil.fmt_yyyyMMdd);
			} else if (dateStr.length() == DateUtil.fmt_yyyyMMddHHmmss.length()) {
				caclDate = DateUtil.str2Date(dateStr, DateUtil.fmt_yyyyMMddHHmmss);
			}
			map.put("diff_time", (caclDate.getTime() - new Date().getTime()) + "");
		}
	}


	/**
	 * 查询会过商贸商品
	 * 逍遥子 2018/02/01
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void psHuiGuoGoodsListPageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = psGoodsListPageFindSetParams(request, paramsMap);
			psGoodsService.psHuiGuoGoodsListFind(paramsMap, result);
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
	 * 会过商品上/下架
	 * 逍遥子 2018/02/10
	 * @param request
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void updateGoodsSaleOrShelves(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			psGoodsService.updateGoodsSaleOrShelves(paramsMap, result);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询按标签查询贝壳商品列表 APP端 
	 */
	public void beiKeGoodsForListByLabelListPageFind(HttpServletRequest request, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			PageParam pageParam = getPageParam(request);
			
			psGoodsService.beiKeGoodsForListByLabelListPageFind(paramsMap, result);
			// 内存分页
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			List<Map<String, Object>> list = (List<Map<String, Object>>) resultData.get("list");
			List<Map<String, Object>> pageList = memoryPage(list, pageParam);
			resultData.put("list", pageList);
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