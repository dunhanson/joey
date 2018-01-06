package cn.joey.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;

/**
 * Http请求工具类
 * @author dunhanson
 * @since 2017.12.15
 */
public class HttpUtils {

	/**
	 * 发送JSON请求
	 * @param url 网络地址
	 * @param json json字符串
	 * @return 响应结果
	 * @throws IOException 
	 */
	public static String httpJson(String url, String json) throws IOException{
		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
        //转换为字节数组
        byte[] param = json.getBytes();
		String result = "";
		try {
	        // 建立http连接
	        conn = getHttpURLConnection(url);
	        // 设置允许输出
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        // 设置不用缓存
	        conn.setUseCaches(false);
	        // 设置传递方式
	        conn.setRequestMethod("POST");
	        // 设置维持长连接
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        // 设置文件长度
	        conn.setRequestProperty("Content-Length", String.valueOf(param.length));
	        // 设置请求信息类型:
	        conn.setRequestProperty("Content-type", "application/json;charset=utf-8");
	        // 设置客户端接受信息类型
	        conn.setRequestProperty("Accept", "application/json");	        
	        // 开始连接请求
	        conn.connect();
	        // 获取连接输出流
	        out = conn.getOutputStream();     
	        // 写入请求的字符串
	        out.write(param);
	        out.flush();
	        // 请求返回的状态
	        if (conn.getResponseCode() == 200) {
	            // 请求返回的数据
	        	in = conn.getInputStream();
	            byte[] data = new byte[in.available()];
	            in.read(data);
	            result = new String(data);
	        }    				
		} finally {
			close(conn, out, in);
		}
		return result;
	}

	/**
	 * 发送POST请求
	 * @param url 网络地址
	 * @param param 请求字符串
	 * @return 响应结果
	 * @throws IOException 
	 */
	public static String httpPost(String url, String param) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		String result = "";
		try {
	        // 建立http连接
	        conn = getHttpURLConnection(url);
	        // 设置允许输出
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        // 设置不用缓存
	        conn.setUseCaches(false);
	        // 设置传递方式
	        conn.setRequestMethod("POST");
	        // 设置维持长连接
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        // 设置文件长度
	        conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
	        // 设置请求信息类型:
	        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	        // 设置客户端接受信息类型
	        conn.setRequestProperty("Accept", "application/json");	        
	        // 开始连接请求
	        conn.connect();
	        // 获取连接输出流
	        out = conn.getOutputStream();     
	        // 写入请求的字符串
	        out.write(param.getBytes());
	        out.flush();
	        // 请求返回的状态
	        if (conn.getResponseCode() == 200) {
	            // 请求返回的数据
	        	in = conn.getInputStream();
	            byte[] data = new byte[in.available()];
	            in.read(data);
	            result = new String(data);
	        }    			
		} catch (Exception e) {
			throw new RuntimeException("请求失败 " + e);
		} finally {
			close(conn, out, in);
		}
		return result;
	}
	
	/**
	 * 发送无参数POST请求
	 * @param url 网络地址
	 * @return 响应结果
	 */
	public static String httpPost(String url) {
		return httpPost(url, "");
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
	 * @param address 网络请求抵制
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
	
	/**
	 * 关闭资源
	 * @param out
	 * @param in
	 * @throws IOException 
	 */
	private static void close(HttpURLConnection conn , OutputStream out, InputStream in) {
		try {
			if(conn != null){
				conn.disconnect();
			}
			if(out != null){
				out.close();
			}
			if(in != null){
				in.close();
			}				
		} catch (Exception e) {
			
		}
	}
	

}
