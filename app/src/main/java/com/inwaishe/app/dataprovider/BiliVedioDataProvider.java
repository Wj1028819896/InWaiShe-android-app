package com.inwaishe.app.dataprovider;


import com.inwaishe.app.entity.video.BiliVideoInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BiliVedioDataProvider {

	public static void main(String[] args) throws Exception {
//		String avid = "1960277"; //https://www.bilibili.com/video/av12674268/  后面 av后面的数字就是avid
//		System.out.println("代码托管于:ziyuan.bejson.com");
//		getVideoInfo(avid);
//		JSONArray ja = getCids(avid);
//		downloadVideos(avid,ja);
	}

	private static ArrayList<String> durls = new ArrayList<>();

	public static BiliVideoInfo getFirstVideo(String avid) throws Exception{
		System.out.println("代码托管于:ziyuan.bejson.com");
		BiliVideoInfo biliVideoInfo = new BiliVideoInfo();
		getVideoInfo(avid,biliVideoInfo);
		downloadVideosNew(avid,biliVideoInfo);
		return biliVideoInfo;
	}

	private static void downloadVideosNew(String avid,BiliVideoInfo biliVideoInfo) throws Exception {
		//durls.clear();
		for(int i=0;i < 1;i++){
			System.out.println("现在解析第"+i+"段视频,视频名称:" + "");
			String durl = parseAndDownloadVideo(avid,i,biliVideoInfo);
			//durls.add(durl);
		}
	}
	
	private static void downloadVideos(String avid,JSONArray ja) throws Exception {
		for(int i=0;i<ja.length();i++){
			String pname = ja.getJSONObject(i).getString("pagename");
			System.out.println("现在解析第"+i+"段视频,视频名称:"+pname);
			//parseAndDownloadVideo(avid,i);
		}
	}
	
	

	private static String parseAndDownloadVideo(String avid,int i,BiliVideoInfo biliVideoInfo) throws Exception {
		String url = "http://api.bilibili.com/playurl?callback=cb&aid="+avid+"&page=1&platform=html5&quality=1&vtype=mp4";
		String urlInfo = sendGet(url);
		System.out.println(urlInfo);
		JSONObject jo = new JSONObject(urlInfo);
		String durl = jo.getJSONArray("durl").getJSONObject(0).getString("url");
		System.out.println("解析到地址为:"+durl);
		biliVideoInfo.url = durl;
		biliVideoInfo.cid = jo.getString("cid");
		return ""+ durl;
	}

	//获取视频的基本信息 
	//因为json里的中文是编码的。 大家可以取 www.bejson.com 里进行格式化并转码
	private static void getVideoInfo(String avid,BiliVideoInfo biliVideoInfo) throws Exception{
		JSONObject jo = new JSONObject(sendGet("http://api.bilibili.com/view?type=json&appkey=f3475f94b513da26&id="+avid+"&page=1"));
		String title = jo.getString("title");
		String desc =  jo.getString("description");
		String author =  jo.getString("author");
		System.out.println("视频信息如下:");
		System.out.println("标题:"+title);
		System.out.println("简介:"+desc);
		System.out.println("作者:"+author);
		System.out.println("JSON:"+ jo.toString());
		biliVideoInfo.title = title;
		biliVideoInfo.author = author;
		biliVideoInfo.description = desc;
		biliVideoInfo.pic = jo.getString("pic");
		biliVideoInfo.offsite = jo.getString("offsite");
	}
	
	private static JSONArray getCids(String avid) throws Exception{
		System.out.println("获取分页信息:");
		JSONArray ja = new JSONArray(sendGet("http://www.bilibili.com/widget/getPageList?aid="+avid));
		System.out.println("一共解析到有"+ja.length()+"个分段");
		return ja;
	}
	
	private static Map<String ,String> cookiesMap = new HashMap<String, String>();

	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     //* @param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
	 * @throws IOException 
	 * @throws MalformedURLException 
     */
    public static String sendGet(String url) throws MalformedURLException, IOException {
    	//Map header =  new HashMap();
    	//header.put("Host", "api.bilibili.com");
    	//header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
    	//header.put("Cookie", "buvid3=DAB");

		OkHttpClient ok = new OkHttpClient().newBuilder().followRedirects(true).build();
		OkHttpClient okHttpClient = ok;

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Host", "api.bilibili.com")
				//.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36")
				.addHeader("User-Agent","Mozilla/5.0 (Linux; Android 7.0; STF-AL10 Build/HUAWEISTF-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043508 Safari/537.36 V1_AND_SQ_7.2.0_730_YYB_D QQ/7.2.0.3270 NetType/4G WebP/0.3.0 Pixel/1080")
				.addHeader("Cookie", "buvid3=DAB")
				.build();

		Call call = okHttpClient.newCall(request);
		Response response = call.execute();
		String result = response.body().string();

		//String result = Jsoup.connect(url).headers(header).ignoreContentType(true).followRedirects(true).get().text();

    	return result;
    }
	

}
