package cn.joey.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * Http请求工具类
 * @author dunhanson
 * @since 2017.12.15
 */
public class HttpUtils {

	/**
	 * 发送POST请求
	 * @param url
	 * @return
	 */
	public static String httpPost(String url) {
		return httpPost(url, null);
	}

	/**
	 * 发送POST请求
	 *
	 * @param url
	 * @return
	 */
	public static String httpPost(String url, Map<String, Object> param) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<>();
			if(param != null) { //不为空
				param.keySet().forEach(key -> {
					params.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
				});
				httpPost.setEntity(new UrlEncodedFormEntity(params));
			}
			response = HttpClients.createDefault().execute(httpPost);
			return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/**
	 * 发送Get请求
	 * @param url 网络地址
	 * @return 响应结果
	 */
	public static String httpGet(String url) {
		return httpGet(url, null);
	}

	/**
	 * 发送Get请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpGet(String url, Map<String, Object> param) {
		List<NameValuePair> params = new ArrayList<>();
		if(param != null) { //参数不为空
			//遍历参数
			param.keySet().forEach(key->{
				params.add(new BasicNameValuePair(key, String.valueOf(param.get(key))));
			});
			String paramStr = URLEncodedUtils.format(params, "UTF-8");
			//检查参数
			if(StringUtils.isNotBlank(paramStr)) {
				url = url + "?" + paramStr;
			}
		}
		HttpGet httpGet = new HttpGet(url);
		try (CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet)) {
			return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getIP(HttpServletRequest request) throws UnknownHostException{
		String ipAddress = request.getHeader("x-forwarded-for");
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
				//根据网卡取本机配置的IP
				InetAddress inet=null;
				inet = InetAddress.getLocalHost();
				ipAddress= inet.getHostAddress();
			}
		}
		//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
			if(ipAddress.indexOf(",")>0){
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	/**
	 * 下载文件转输入流
	 * @return 输入流
	 * @throws IOException
	 */
	public static InputStream download(String url) throws IOException{
		URLConnection connection = getURLConnection(url);
		return connection.getInputStream();
	}

	/**
	 * 获取URLConnection对象
	 * @param url 网络地址
	 * @return URLConnection对象
	 * @throws IOException
	 */
	public static URLConnection getURLConnection(String url) throws IOException{
		return new URL(url).openConnection();
	}

	/**
	 * 获取HttpURLConnection对象
	 * @param url 网络地址
	 * @return HttpURLConnection对象
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String url) throws IOException{
		return (HttpURLConnection) new URL(url).openConnection();
	}

}
