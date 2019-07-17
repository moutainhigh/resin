package com.meitianhui.finance.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.finance.constant.RspCode;

/**
 * User: Tiny Date: 2014/10/29 Time: 14:36
 */

/**
 * 微信支付获取订单请求
 * 
 * @author Tiny
 *
 */
public class WechatRequest {

	/** 微信扫码支付-交易请求 **/
	public static String MICROPAY_REQUST_URL = "https://api.mch.weixin.qq.com/pay/micropay";
	/** 微信扫码支付-订单查询 **/
	public static String MICROPAY_ORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	/** 微信扫码支付-订单撤销 **/
	public static String MICROPAY_REVERSE_URL = "https://api.mch.weixin.qq.com/secapi/pay/reverse";
	/** 统一下单URL **/
	public static String UNIFIEDORDER_REQUST_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 下载对账单 **/
	public static String DOWNLOADBILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";

	/** jscode2session **/
	public static String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";	
	
	private static Logger log = Logger.getLogger(WechatRequest.class);

	// 连接超时时间，默认10秒
	private int socketTimeout = 10000;

	// 传输超时时间，默认30秒
	private int connectTimeout = 30000;

	// 请求器的配置
	private RequestConfig requestConfig;

	// HTTP请求器
	private CloseableHttpClient httpClient;

	public WechatRequest() {
		
	}

	public WechatRequest(String cert_local_path, String cert_password) throws UnrecoverableKeyException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		init(cert_local_path, cert_password);
	}

	private void init(String cert_local_path, String cert_password) throws IOException, KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		// 加载本地的证书进行https加密传输
		FileInputStream instream = new FileInputStream(
				new File(System.getProperty("mth.ops.finance.root") + cert_local_path));
		try {
			// 设置证书密码
			keyStore.load(instream, cert_password.toCharArray());
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, cert_password.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		// 根据默认超时限制初始化requestConfig
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
	}

	/**
	 * 通过Https往API post xml数据
	 *
	 * @param url
	 *            API地址
	 * @param xmlObj
	 *            要提交的XML数据对象
	 * @return API回包的实际数据
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */

	public String sendPost(String url, Map<String, Object> xmlObj) throws IOException, KeyStoreException,
			UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		// 将要提交给API的数据对象转换成XML格式数据Post给API
		String postDataXML = StringUtil.mapToXML(xmlObj);
		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.setEntity(postEntity);
		// 设置请求器的配置
		httpPost.setConfig(requestConfig);
		try {
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");
		} catch (ConnectionPoolTimeoutException e) {
			log.error("http get throw ConnectionPoolTimeoutException(wait time out)");
		} catch (ConnectTimeoutException e) {
			log.error("http get throw ConnectTimeoutException");
		} catch (SocketTimeoutException e) {
			log.error("http get throw SocketTimeoutException");
		} catch (Exception e) {
			log.error("http get throw Exception");
		} finally {
			httpPost.abort();
		}
		return result;
	}

	/**
	 * 通过Https往API post xml数据
	 *
	 * @param url
	 *            API地址
	 * @param xmlObj
	 *            要提交的XML数据对象
	 * @return API回包的实际数据
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */

	public void downloadBillPost(Map<String, Object> xmlObj, String filepath) throws BusinessException, Exception {
		HttpPost httpPost = new HttpPost(DOWNLOADBILL_URL);
		// 将要提交给API的数据对象转换成XML格式数据Post给API
		String postDataXML = StringUtil.mapToXML(xmlObj);
		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
		httpPost.setEntity(postEntity);
		// 设置请求器的配置
		httpPost.setConfig(requestConfig);
		InputStream is = null;
		FileOutputStream fileout = null;
		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			Header content_type = entity.getContentType();
			//如果返回类型是text/plain,则说明文件未获取
			if(content_type.getValue().contains("text/plain")){
				String responseString = EntityUtils.toString(entity, "UTF-8");
				Map<String, Object> resultMap = StringUtil.xml2Map(responseString);
				if (!"SUCCESS".equals(resultMap.get("return_code")) || !"SUCCESS".equals(resultMap.get("result_code"))) {
					throw new BusinessException(RspCode.WECHAT_ERROR, (String) resultMap.get("return_msg"));
				}
			}
			is = entity.getContent();
			File file = new File(filepath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			final int cache = 10 * 1024;
			byte[] buffer = new byte[cache];
			int ch = 0;
			while ((ch = is.read(buffer)) != -1) {
				fileout.write(buffer, 0, ch);
			}
			fileout.flush();
			is.close();
			fileout.close();
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				if (null != fileout) {
					fileout.close();
				}
				if (null != httpPost) {
					httpPost.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置连接超时时间
	 *
	 * @param socketTimeout
	 *            连接时长，默认10秒
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		resetRequestConfig();
	}

	/**
	 * 设置传输超时时间
	 *
	 * @param connectTimeout
	 *            传输时长，默认30秒
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		resetRequestConfig();
	}

	private void resetRequestConfig() {
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
	}

	/**
	 * 允许商户自己做更高级更复杂的请求器配置
	 *
	 * @param requestConfig
	 *            设置HttpsRequest的请求器配置
	 */
	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	public static String mapToXMLWeChat(Map<String, Object> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "<xml>";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key).toString();
			prestr += "<" + key + "><![CDATA[" + value + "]]></" + key + ">";
		}
		return prestr + "</xml>";
	}
	
	
	/**   
     * 微信小程序的https请求
     * @param requestUrl请求地址   
     * @param requestMethod请求方法   
     * @param outputStr参数   
     */     
    public String httpRequest(String requestUrl,String requestMethod,String outputStr){     
        // 创建SSLContext     
        StringBuffer buffer = null;     
        try{     
            URL url = new URL(requestUrl);     
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();     
            conn.setRequestMethod(requestMethod);     
            conn.setDoOutput(true);     
            conn.setDoInput(true);     
            conn.connect();     
            //往服务器端写内容     
            if(null !=outputStr){     
                OutputStream os=conn.getOutputStream();     
                os.write(outputStr.getBytes("utf-8"));     
                os.close();     
            }     
            // 读取服务器端返回的内容     
            InputStream is = conn.getInputStream();     
            InputStreamReader isr = new InputStreamReader(is, "utf-8");     
            BufferedReader br = new BufferedReader(isr);     
            buffer = new StringBuffer();     
            String line = null;     
            while ((line = br.readLine()) != null) {     
                buffer.append(line);     
            }     
        }catch(Exception e){     
            e.printStackTrace();     
        }  
        return buffer.toString();  
    }     
    
    
    
    /**
     * post请求并得到返回结果
     * @param requestUrl
     * @param requestMethod
     * @param output
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String output) {
        try{
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            if (null != output) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(output.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            connection.disconnect();
            return buffer.toString();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

}
