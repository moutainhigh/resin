/**
 * 工具类
 * @author Changefeng
 */
var request = require('request');
var ipware = require('ipware')();
var md5 = require('md5');
var appConfig = require('../app-config');
var errHandler = require('./err-handler');
var logger = require('./log4js').getLogger('biz-util');

module.exports = {
	/**
	 * 合并参数，将后一个参数合并到前一个参数中
	 */
	extend : function() {
		var src, copy, name, options,
		target = arguments[0] || {},
		i = 1,
		length = arguments.length;

		// Handle case when target is a string or something (possible in deep copy)
		if ( typeof target !== "object" ) {
			target = {};
		}

		for ( ; i < length; i++ ) {
			// Only deal with non-null/undefined values
			if ( (options = arguments[ i ]) != null ) {
				// Extend the base object
				for ( name in options ) {
					src = target[ name ];
					copy = options[ name ];

					// Prevent never-ending loop
					if ( target === copy ) {
						continue;
					}

					if ( copy !== undefined ) {
						target[ name ] = copy;
					}
				}
			}
		}

		// Return the modified object
		return target;
	},

	/**
	 * 克隆对象
	 */
	clone : function(obj) {
		var objS = JSON.stringify(obj);
		return JSON.parse(objS);
	},

	/**
	 * 验证user_token
	 */
	validUserToken : function(req, res, user_token, callback) {
		var formData = 'service=infrastructure.userValidate&params=' + JSON.stringify({user_token: user_token, request_info: ipware.get_ip(req).clientIp});
		request.post(appConfig.userPath, {form: formData}, function(error, response, body) {
			if(!error && response.statusCode == 200) {
				var tokenValidData = JSON.parse(body);
				if(tokenValidData.rsp_code != 'succ') {	// user_token校验失败
					errHandler.userTokenError(res);
					return;
				}
				if(callback) {	// 验证通过，执行回调函数
					callback(tokenValidData);
				}
			} else {
				errHandler.systemError(res, error);
				return;
			}
		});
	},

	/**
	 * 验证user_token（不需要request_info，用于从服务端调用）
	 */
	validUserTokenNoRequestInfo : function(res, user_token, callback) {
		var formData = 'service=infrastructure.userValidateNoRequestInfo&params=' + JSON.stringify({user_token: user_token});
		request.post(appConfig.userPath, {form: formData}, function(error, response, body) {
			if(!error && response.statusCode == 200) {
				var tokenValidData = JSON.parse(body);
				if(tokenValidData.rsp_code != 'succ') {	// user_token校验失败
					errHandler.userTokenError(res);
					return;
				}
				if(callback) {	// 验证通过，执行回调函数
					callback(tokenValidData);
				}
			} else {
				errHandler.systemError(res, error);
				return;
			}
		});
	},

	/**
	 * 验证app_token
	 */
	validAppToken : function(req, res, app_token, callback) {
		var formData = 'service=infrastructure.appValidate&params=' + JSON.stringify({app_token: app_token, request_info: ipware.get_ip(req).clientIp});
		request.post(appConfig.infrastructurePath, {form: formData}, function(error, response, body) {
			if(!error && response.statusCode == 200) {
				var tokenValidData = JSON.parse(body);
				if(tokenValidData.rsp_code != 'succ') {	// 校验失败
					errHandler.appTokenError(res);
					return;
				}
				if(callback) {	// 验证通过，执行回调函数
					callback(tokenValidData);
				}
			} else {
				errHandler.systemError(res, error);
				return;
			}
		});
	},

	/**
	 * 验证签名
	 */
	validSign : function(requestParams, securityCode) {
		//臨時屏蔽
		// return true;
		if(appConfig.needValidSign) {	// 根据配置文件判断是否需要验证签名，主要用于调试
			// 请求中的签名
			var requestSign = requestParams.sign;
			// 移除请求中的签名
			delete requestParams.sign;

			// 请求参数中的所有key值
			var keyArr = [];
			for(var key in requestParams) {
				if(key != '') {
					keyArr.push(key);
				}
			}

			// 将所有key按字母a~z排序
			keyArr.sort();
			

			// 临时数组，用于存放各kv字符串
			var tmpArray = [];

			for(var i=0; i<keyArr.length; i++) {
				var val = requestParams[keyArr[i]];
				if(typeof val != 'string') {
					val = JSON.stringify(val);
				}
				tmpArray.push(keyArr[i] + '=' + val);
			}

			// 待加密字符串
			var stringToBeMd5 = tmpArray.join('&') + securityCode;

			// MD5加密
			var thisSign = md5(stringToBeMd5);

			if(thisSign.toUpperCase() != requestSign.toUpperCase()) {	// 签名错误，记录日志
				logger.info('invalid sign');
				logger.info('sign from request:' + requestSign);
				logger.info('correct sign:' + thisSign);
			}

			return thisSign.toUpperCase() === requestSign.toUpperCase() ? true : false;
		} else {
			return true;
		}
	},

	/**
	 * 为快递鸟即时查询生成数据签名（将requestData和apikey字符串拼接后先做MD5，然后再做Base64，即为签名）
	 */
	genKDNDataSign : function(requestData) {
		// 待MD5字符串
		var stringToBeMd5 = JSON.stringify(requestData) + appConfig.apikey;
		// 待Base64字符串
		var md5ToBeBase64 = md5(stringToBeMd5);
		// 签名
		var dataSign = new Buffer(md5ToBeBase64).toString('base64');

		return dataSign;
	},

	/**
	 * 调用快递鸟API查询物流信息
	 */
	// queryLogisticInfo : function(logisticCode, succFn, errorFn) {
	// 	if(!logisticCode) {
	// 		errorFn('无物流单号')
	// 		return;
	// 	}
	// 	var bizUtils = this;
	// 	// STEP1：根据单号查询物流公司
	// 	var companyRequestData = {
	// 		LogisticCode: logisticCode
	// 	};
	// 	var companyFormData = {
	// 		RequestData: JSON.stringify(companyRequestData),
	// 		EBusinessID: appConfig.EBusinessID,
	// 		RequestType: '2002',
	// 		DataType: '2',
	// 		DataSign: bizUtils.genKDNDataSign(companyRequestData)
	// 	};
	// 	request.post(appConfig.kdnPath, {form: companyFormData}, function(error, response, companyBody) {
	// 		try {
	// 			if(!error && response.statusCode == 200) {
	// 				var companyResult = JSON.parse(companyBody);
	// 				if(!companyResult.Success) {	// 物流公司查询失败，直接返回
	// 					logger.info('物流公司信息查询失败');
	// 					logger.info(companyBody);
	// 					errorFn('物流信息查询失败');
	// 					return;
	// 				}

	// 				// STEP2：根据物流单号和物流公司查询物流信息
	// 				var logisticRequestData = {
	// 					ShipperCode: companyResult.Shippers[0].ShipperCode,
	// 					LogisticCode: logisticCode
	// 				}
	// 				var logisticFormData = {
	// 					RequestData: JSON.stringify(logisticRequestData),
	// 					EBusinessID: appConfig.EBusinessID,
	// 					RequestType: '1002',
	// 					DataType: '2',
	// 					DataSign: bizUtils.genKDNDataSign(logisticRequestData)
	// 				};
	// 				request.post(appConfig.kdnPath, {form: logisticFormData}, function(error, response, logisticBody) {
	// 					try {
	// 						if(!error && response.statusCode == 200) {
	// 							var logisticResult = JSON.parse(logisticBody);
	// 							if(!logisticResult.Success) {
	// 								logger.info('物流信息查询失败');
	// 								logger.info(logisticBody);
	// 								errorFn('物流信息查询失败');
	// 								return;
	// 							}
	// 							logisticResult.ShipperName = companyResult.Shippers[0].ShipperName;
	// 							succFn(logisticResult);
	// 							return;
	// 						} else {
	// 							logger.info('物流信息接口调用失败');
	// 							logger.info(error);
	// 							errorFn('物流信息查询失败');
	// 							return;
	// 						}
	// 					} catch(e) {
	// 						errorFn('系统错误2');
	// 						return;
	// 					}
	// 				});
	// 			} else {
	// 				logger.info('物流公司信息接口调用失败');
	// 				logger.info(error);
	// 				errorFn('物流信息查询失败');
	// 				return;
	// 			}
	// 		} catch(e) {
	// 			errorFn('系统错误1');
	// 			return;
	// 		}
	// 	});
	// },

	/**
	 * 处理兑换中心返回结果
	 */
	handleExchangeResult : function(res, body) {
		try {
			var bodyJson = JSON.parse(body);
			if(bodyJson.errcode == 0) {	// 兑换中心请求成功
				res.send({
					rsp_code: 'succ',
					data: bodyJson.data || {}
				});
			} else {
				res.send({
					rsp_code: 'fail',
					error_code: bodyJson.errcode || 'system error',
					error_msg: bodyJson.errmsg || '系统错误'
				});
			}
		} catch(e) {
			errHandler.systemError(res, e);
			return;
		}
	},

	/**
	 * 获取当前年月yyyy-MM
	 */
	getCurrentYearMonth : function() {
		var now = new Date();
		var month = now.getMonth() < 9 ? ('0'+(now.getMonth()+1)) : (now.getMonth()+1);
		return now.getFullYear() + '-' + month;
	},

	getCurrentDate : function() {
		var now = new Date();
		var month = now.getMonth() < 9 ? ('0'+(now.getMonth()+1)) : (now.getMonth()+1);
		var day = now.getDate() < 10 ? ('0'+now.getDate()) : now.getDate();
		return now.getFullYear() + '-' + month + '-' + day;
	},

	getyyyyMMddHHmm : function() {
		var d = new Date();
	    var curr_date = d.getDate();
	    var curr_month = d.getMonth() + 1;
	    String(curr_month).length < 2 ? (curr_month = "0" + curr_month): curr_month;
	    String(curr_date).length < 2 ? (curr_date = "0" + curr_date): curr_date;
	    var yyyyMMddHHmm = '' + d.getFullYear() + curr_month + curr_date + d.getHours() + d.getMinutes();
	    return yyyyMMddHHmm;
	},

	/**
	 * 生成加盟店编号
	 */
	generateStoreNo : function() {
		var d = new Date();
	    var curr_date = d.getDate();
	    var curr_month = d.getMonth() + 1;
	    String(curr_month).length < 2 ? (curr_month = "0" + curr_month): curr_month;
	    String(curr_date).length < 2 ? (curr_date = "0" + curr_date): curr_date;
	    var yyyyMMddHHmm = '' + d.getFullYear() + curr_month + curr_date + d.getHours() + d.getMinutes();
		return 'hyd3-' + yyyyMMddHHmm + parseInt((Math.random() * 1000));
	},

	/**
	 * 生成交易号
	 */
	generateTradeNo : function() {
		var d = new Date();
	    var curr_date = d.getDate();
	    var curr_month = d.getMonth() + 1;
	    String(curr_month).length < 2 ? (curr_month = "0" + curr_month): curr_month;
	    String(curr_date).length < 2 ? (curr_date = "0" + curr_date): curr_date;
	    var yyyyMMddHHmm = '' + d.getFullYear() + curr_month + curr_date + d.getHours() + d.getMinutes() + d.getSeconds() + d.getMilliseconds();
	    return yyyyMMddHHmm + parseInt((Math.random() * 1000000));
	},

	stringifyCookies : function(cookies) {
		var cookiesStrArr = [];
		for(var i=0; i<cookies.length; i++) {
			var cookieStr = '';
			cookieStr = cookies[i]['name'] + '=' + cookies[i]['value']
			if(cookieStr != '') {
				cookiesStrArr.push(cookieStr);
			}
		}
		return cookiesStrArr;
	},
	factoryPromise:function(url,data,res) {
		logger.info('查询参数及url %::%',url,data)
		return new Promise(function(resolve,reject) {
            request.post(url, {form: data}, function(error, response, body) {
				try {
					if(!error && response.statusCode == 200) {
						var jsondata = JSON.parse(body);
						resolve(jsondata);
					} else {
						reject(res,error)
					}
				} catch(e) {
					errHandler.systemError(res,e);
			        return;
				}
			})
		})
	},
	/**
	 * 检查数组是否存在当前元素
	 * params arr key
	 * return -1 || 1
	 */
	searchIndexOf:function(arr,key) {
		for(let i = 0; i < arr.length;i++) {
			if(key == arr[i]) {
                 return true;
			}
		}
		return false;
	}
}
