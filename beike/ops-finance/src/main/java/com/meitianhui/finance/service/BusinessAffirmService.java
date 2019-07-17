package com.meitianhui.finance.service;

import java.util.List;
import java.util.Map;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.entity.FDTransactionsResult;

public interface BusinessAffirmService {

	List<FDTransactionsResult> insertFdTransactionsResult(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

}
