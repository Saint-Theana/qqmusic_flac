package com.qqmusic;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import javax.net.ssl.*;

public class QQmusic extends HttpServlet {
	
	String header = "<html><head></head><form method=\"get\" action=\"/qqmusic\"><body><input type=\"text\" placeholder=\"";
	String body = "\" name=\"keyword\"><button type=\"submit\">搜索</button></form>";
	String end ="</body></html>";
	
	public QQmusic(){
		try
		{
			Util.trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(Util.hv);
		}
		catch (Exception e)
		{}
	}
	
	
	@Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		resp.setContentType("text/html;Charset=UTF-8");
		String ua = resp.getHeader("user-agent");
		PrintWriter writer = resp.getWriter();
		String keyword = req.getParameter("keyword");
		if(keyword==null){
			keyword="请输入关键词(歌手/歌名...)";
		}
		writer.println(header+keyword+body);
		if(!keyword.equals("请输入关键词(歌手/歌名...)")){
			this.music(writer,keyword,ua);
		}
		writer.write("<br>");
		
		writer.print(end);
		writer.flush();
		writer.close();
	}

	private void music(PrintWriter writer,String keyword,String ua)
	{
		keyword=keyword.replaceAll(" ","+");
		String info = Util.curl_with_referer(ua,"https://"+Util.http_dns("c.y.qq.com")+"/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=10&w="+keyword+"&&jsonpCallback=searchCallbacksong2020&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0","https://y.qq.com/portal/profile.html");
		
		try{
			JSONObject json_root = new JSONObject(info.replaceAll("^callback\\(","").replaceAll("\\)$",""));
		    JSONArray song_list = json_root.getJSONObject("data").getJSONObject("song").getJSONArray("list");
			for (int time = 0; time < song_list.length(); time++)
			{
				String vkey=this.getvkey(ua);
				
				JSONObject song_root = song_list.getJSONObject(time);
				JSONObject song_files = song_root.getJSONObject("file");
				String author_name = song_root.getJSONArray("singer").getJSONObject(0).getString("name");
				String song_name = song_root.getString("title");
				writer.write(song_name +" " +author_name+" ");
				String song_id = song_files.getString("media_mid");
				long quality_128 = song_files.getLong("size_128");
				long quality_320 = song_files.getLong("size_320");
				long quality_ape = song_files.getLong("size_ape");
				long quality_flac = song_files.getLong("size_flac");
				if(quality_128!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/M500"+song_id+".mp3?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=8\" class=\"button\">128k </a>");
				}
				if(quality_320!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/M800"+song_id+".mp3?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\">320k </a>");
				}
				if(quality_ape!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/A000"+song_id+".ape?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\">ape </a>");
				}
				if(quality_flac!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/F000"+song_id+".flac?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\">flac </a>");
				}
				
				
				writer.write("<br>");
				
			}
			
		}catch(JSONException e){
			e.printStackTrace();
			
		}
		
	}
	
	
	
	
	private String getvkey(String ua)
	{
		String result = Util.curl_with_referer(ua,"https://"+Util.http_dns("c.y.qq.com")+"/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=556936094&loginUin=0&hostUin=0&format=json&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid=003a1tne1nSz1Y&filename=C400003a1tne1nSz1Y.m4a&guid=FUCK","https://y.qq.com/portal/profile.html");
		try
		{
			JSONObject json_root = new JSONObject(result);
			return json_root.getJSONObject("data").getJSONArray("items").getJSONObject(0).getString("vkey");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	
	}
}
