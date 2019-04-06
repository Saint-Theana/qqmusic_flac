package com.qqmusic;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Util
{
	public static String http_dns(String host)
	{  
		try
		{  
			URL lll = new URL("http://119.29.29.29/d?dn="+host);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			String hosts = sb.toString();
			if(hosts.contains(";")){
				return hosts.split(";")[0];
			}else{
				return hosts;
			}
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
	
	
	public static String curl_with_referer(String ua,String url, String referer)
	{  
		try
		{  
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",ua);
			connection.setRequestProperty("Referer", referer);
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
	public static String Curl(String url)
	{
		try
		{
			InputStream is=new URL(url).openStream();
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			int b=-1;
			while ((b = is.read()) != -1)
				buffer.write(b);
			return new String(buffer.toByteArray());
		}
		catch (MalformedURLException e)
		{
			System.out.println(e.toString());
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
		}
		return null;
	}
	
	
	
	public static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session)
		{
            return true;
        }
    };

	public static void trustAllHttpsCertificates() throws Exception
	{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
			.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
																	.getSocketFactory());
	}

	public static class miTM implements javax.net.ssl.TrustManager,
	javax.net.ssl.X509TrustManager
	{
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		public static boolean isServerTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public static boolean isClientTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public void checkServerTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}

		public void checkClientTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}
	}
	
	
}
