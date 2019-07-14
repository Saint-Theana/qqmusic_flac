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
	String style = "<style>#container {	width: 500px;	height: 820px;	margin: 0 auto;}div.search {	padding: 30px 0;}form {	position: relative;	width: 300px;	margin: 0 auto;}input, button {	border: none;	outline: none;}input {	width: 100%;	height: 42px;	padding-left: 13px;}button {	height: 42px;	width: 42px;	cursor: pointer;	position: absolute;}.bar6 {	}.bar6 input {	border: 2px solid #c5464a;	border-radius: 5px;	background: transparent;	top: 0;	right: 0;}.bar6 button {	background: #c5464a;	border-radius: 0 5px 5px 0;	width: 60px;	top: 0;	right: 0;}.bar6 button:before {	content: \"搜索\";	font-size: 13px;	color: #F9F0DA;}.item1 {	border-radius: 0 5px 5px 0;	text-align: center;	margin: 0px auto;}.item3 {	background: #c5464a;	height: 24px;	cursor: pointer;	position: absolute;}.item-left {	background: #F9F0DA;	height: 26px;	width: 33.3%;	float: left;}.item-left button {	font-size: 13px;	color: #F9F0DA;}.item-right {	background: #F9F0DA;	height: 26px;	width: 33.3%;	float: right;}.item-right button {	font-size: 13px;	color: #F9F0DA;}.item-center {	background: #F9F0DA;	height: 26px;	width: 33.3%;}.item-center button {	font-size: 13px;	color: #F9F0DA;}.login-button {	height: 40px;	border-width: 0px;	border-radius: 3px;	background: #c5464a;	cursor: pointer;	outline: none;	font-family: Microsoft YaHei;	color: white;	font-size: 17px;	width: 33.3%;}.musicname {padding-top:15px;text-align:center;font-size:15px;}.singername {padding-top:15px;text-align:center;font-size:15px;}.card {box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);transition: 0.3s;width: 100%;height: 100px;border-radius: 5px;position:relative;}​.test{}.test span{display: none;}.test:hover span{ display:block;transition: 0.3s;border-radius: 5px;box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);position:absolute;top:100px;width:100%;height:100px;background-color:white;color:black; z-index:2;}​.audio {width:100%;}.songimg {float:left;transition: 0.3s;border-radius: 5px;}.srcbutton {transition: 0.3s; width:25%;background-color:white;border-radius: 5px;}</style>";
	String headerend = "</head>";
	String body = "<body> <div class=\"search bar6\"><form method=\"get\" action=\"/qqmusic\">";
	String bodyend = "<button type=\"submit\"></button></form></div>";
	String end ="<div class=\"item1\"><a href=\"https://github.com/Saint-Theana\" >Creaed By: Saint-Theana</a></div></body></html>";
	String authst;
	public QQmusic(){
		try
		{
			Util.trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(Util.hv);
			this.authst =this.getauthst();
		}
		catch (Exception e)
		{}
	}

	private String getauthst()
	{
		String a = Util.Curl("http://114.115.239.164:8080/api/Authst").replaceAll("\"","");
		String textData = new StringBuilder(new String(Base64.getDecoder().decode(new StringBuilder(a).reverse().toString()))).reverse().toString();
		return new String(Base64.getDecoder().decode(textData.substring(1, textData.length() - 2)));
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
		if(keyword!=null&&!keyword.isEmpty()){
			this.music(writer,keyword,ua,page);
		}else{
			writer.flush();
			writer.close();
			return;
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
				writer.println("<div class=\"card\">");
				JSONObject song_root = song_list.getJSONObject(time);
				JSONObject song_files = song_root.getJSONObject("file");
				String author_name = song_root.getJSONArray("singer").getJSONObject(0).getString("name");
				String song_name = song_root.getString("title");
				String album_id =song_root.getJSONObject("album").getString("mid");
				String song_id = song_files.getString("media_mid");
				String mid = song_root.getString("mid");
				String img ="";
				if(!album_id.isEmpty()&&!(album_id==null)&&album_id.split("").length>2){
				   img= "http://imgcache.qq.com/music/photo/mid_album_500/"+album_id.split("")[album_id.split("").length -2]+"/"+album_id.split("")[album_id.split("").length -1]+"/"+album_id+".jpg";
				}else{
					img="http://imgcache.qq.com/music/photo/mid_album_500/M/g/001ZaCQY2OxVMg.jpg";
				}
				writer.print("<div class=\"songimg\"> <img class=\"songimg\" src=\""+img+"\" width=\"100\" height=\"100\" /></div>");
				writer.write("<div class=\"test\"><div class=\"musicname\" >"+song_name+"</div><div class=\"singername\">"+author_name+"</div>");
				writer.write("<span><div class=\"audio\"><audio src=\""+this.geturl(mid,"M500"+song_id+".mp3")+"\" controls=\"controls\">your browser does not support the audio element</audio></div>");
				writer.write("<div>");
				long quality_128 = song_files.getLong("size_128");
				long quality_320 = song_files.getLong("size_320");
				long quality_ape = song_files.getLong("size_ape");
				long quality_flac = song_files.getLong("size_flac");
				int possition =0;
				if(quality_128!=0){
					writer.print("<div style=\"padding-left:"+possition+"%;\"><a href=\""+this.geturl(mid,"M500"+song_id+".mp3")+"\"><button class=\"srcbutton\">128K</button></a></div>");
				}
				if(quality_320!=0){
					possition+=25;
					writer.print("<div style=\"padding-left:"+possition+"%;\"><a href=\""+this.geturl(mid,"M800"+song_id+".mp3")+"\"><button class=\"srcbutton\">320K</button></a></div>");
									}
				if(quality_ape!=0){
					possition+=25;
					writer.print("<div style=\"padding-left:"+possition+"%;\"><a href=\""+this.geturl(mid,"A000"+song_id+".ape")+"\"><button class=\"srcbutton\">ape</button></a></div>");
				}
				if(quality_flac!=0){
					possition+=25;
					writer.print("<div style=\"padding-left:"+possition+"%;\"><a href=\""+this.geturl(mid,"F000"+song_id+".flac")+"\"><button class=\"srcbutton\">flac</button></a></div>");
				}
				writer.write("</div></span></div>");
				
				writer.println("</div>");
				writer.println("<br>");
			}
			
		}catch(JSONException e){
			e.printStackTrace();
			
		}
		
	}

	
	private String geturl(String mid, String file)
	{
		
		String pd = "{\"modulevkey\":{\"method\":\"CgiGetVkey\",\"module\":\"vkey.GetVkeyServer\",\"param\":{\"uin\":\"1920363953\",\"filename\":[\""+file+"\"],\"ctx\":1,\"guid\":\"QMD\",\"referer\":\"y.qq.com\",\"songmid\":[\""+mid+"\"]}},\"comm\":{\"authst\":\""+this.authst+"\",\"chid\":\"10034015\",\"ct\":\"3\",\"cv\":\"4120104\",\"qq\":\"1920363953\"}}";
		
		String data = Util.post_with_data("https://u.y.qq.com/cgi-bin/musicu.fcg",pd);
		
		try
		{
			JSONObject y = new JSONObject(data);
			return "http://mobileoc.music.tc.qq.com/"+y.getJSONObject("modulevkey").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
			
		}
		catch (JSONException e)
		{
			return null;
		}

	
	}
}
