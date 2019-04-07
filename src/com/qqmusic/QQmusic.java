package com.qqmusic;
import java.util.*;
import java.util.regex.*;
import org.json.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import javax.net.ssl.*;

public class QQmusic extends HttpServlet {
	
	String header = "<html><head>";
	String meta = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";
	String style = "<style>#container {width: 500px;height: 820px;margin: 0 auto;}div.search {padding: 30px 0;} form {position: relative;width: 300px;margin: 0 auto;} input, button {border: none;outline: none;} input {width: 100%;height: 42px;padding-left: 13px;} button {height: 42px;width: 42px;cursor: pointer;position: absolute;}.bar6 {background: #F9F0DA;}.bar6 input {border: 2px solid #c5464a;border-radius: 5px;background: transparent;top: 0;right: 0;}.bar6 button {background: #c5464a;border-radius: 0 5px 5px 0;width: 60px;top: 0;right: 0;}.bar6 button:before {content: \"搜索\";font-size: 13px;color: #F9F0DA;}.item1{border-radius: 0 5px 5px 0;text-align: center;margin: 0px auto;}.item2{background: #F9F0DA;height: 26px;}.item3{background: #c5464a;height: 24px;cursor: pointer;position: absolute;}.item2 button {font-size: 13px;color: #F9F0DA;}.item-left{background: #F9F0DA;height: 26px;width:33.3%;float:left;}.item-left button {font-size: 13px;color: #F9F0DA;}.item-right{background: #F9F0DA;height: 26px;width:33.3%;float:right;}.item-right button {font-size: 13px;color: #F9F0DA;}.item-center{background: #F9F0DA;height: 26px;width:33.3%;}.item-center button {font-size: 13px;color: #F9F0DA;}.login-button { 	height: 40px; 	border-width: 0px; 	border-radius: 3px; 	background: #c5464a; 	cursor: pointer; 	outline: none; 	font-family: Microsoft YaHei; 	color: white; 	font-size: 17px; width:33.3%;}</style>";
	String headerend = "</head>";
	String body = "<body> <div class=\"search bar6\"><form method=\"get\" action=\"/qqmusic\">";
	String bodyend = "<button type=\"submit\"></button></form></div>";
	String end ="<div class=\"item1\"><a href=\"https://github.com/Saint-Theana\" >Creaed By: Saint-Theana</a></div></body></html>";
	
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
		String page = req.getParameter("page");
		writer.println(header);
		writer.println(meta);
		writer.println(style);
		writer.println(headerend);
		writer.println(body);
		if(keyword==null){
			writer.println("<input type=\"text\" placeholder=\"请输入关键词(歌手/歌名...)\" name=\"keyword\">");
		}else{
			writer.println("<input type=\"text\" value=\""+keyword+"\" name=\"keyword\">");
		}
		if(page==null){
			page="1";
		}
		int realpage=0;
		try{
		    realpage = Integer.parseInt(page);
		}catch(NumberFormatException e){
			return;
		}
		if(realpage<1){
			realpage=1;
		}
		page =String.valueOf(realpage);
		
		writer.println(bodyend);
		if(keyword!=null){
			this.music(writer,keyword,ua,page);
		}
		if(page.equals("1")){
		    writer.print("<div class=\"item-left\"><a ><button class=\"login-button\">上一页</button></a></div>");
		}else{
			writer.print("<div class=\"item-left\"><a href=\"/qqmusic/?keyword="+keyword+"&page="+String.valueOf(realpage-1)+"\" ><button class=\"login-button\">上一页</button></a></div>");
		}
		writer.print("<div class=\"item-right\"><a href=\"/qqmusic/?keyword="+keyword+"&page="+String.valueOf(realpage+1)+"\" ><button class=\"login-button\">下一页</button></a></div><div class=\"item-center\"><a ><button class=\"login-button\">当前第"+page+"页</button></a></div>");
		writer.print("<br>");
		writer.print(end);
		writer.flush();
		writer.close();
	}

	private void music(PrintWriter writer,String keyword,String ua,String page)
	{
		keyword=keyword.replaceAll(" ","+");
		String info = Util.curl_with_referer(ua,"https://"+Util.http_dns("c.y.qq.com")+"/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p="+page+"&n=10&w="+keyword+"&&jsonpCallback=searchCallbacksong2020&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0","https://y.qq.com/portal/profile.html");
		
		try{
			JSONObject json_root = new JSONObject(info.replaceAll("^callback\\(","").replaceAll("\\)$",""));
		    JSONArray song_list = json_root.getJSONObject("data").getJSONObject("song").getJSONArray("list");
			for (int time = 0; time < song_list.length(); time++)
			{
				writer.println("<div class=\"item2\">");
				String vkey=this.getvkey(ua);
				
				JSONObject song_root = song_list.getJSONObject(time);
				JSONObject song_files = song_root.getJSONObject("file");
				String author_name = song_root.getJSONArray("singer").getJSONObject(0).getString("name");
				String song_name = song_root.getString("title");
				writer.write("<text>"+song_name +" " +author_name+" "+"</text>");
				String song_id = song_files.getString("media_mid");
				long quality_128 = song_files.getLong("size_128");
				long quality_320 = song_files.getLong("size_320");
				long quality_ape = song_files.getLong("size_ape");
				long quality_flac = song_files.getLong("size_flac");
				if(quality_128!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/M500"+song_id+".mp3?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=8\" class=\"button\"><button class=\"item3\">128K</button></a>");
					writer.print("&emsp;&emsp;&emsp;");
				}
				if(quality_320!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/M800"+song_id+".mp3?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\"><button class=\"item3\">320K</button></a>");
					writer.print("&emsp;&emsp;&emsp;");
				}
				if(quality_ape!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/A000"+song_id+".ape?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\"><button class=\"item3\">ape</button></a>");
					writer.print("&emsp;&emsp;&emsp;");
				}
				if(quality_flac!=0){
					writer.print("<a href=\"http://mobileoc.music.tc.qq.com/F000"+song_id+".flac?vkey="+vkey+"&guid=FUCK&uin=0&fromtag=53\" class=\"button\"><button class=\"item3\">flac</button></a>");
					writer.print("&emsp;&emsp;&emsp;");
				}
			
				
				writer.println("</div>");
				writer.println("<br>");
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
