package com.lr.ai.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.lr.ai.code.CommonCode;

/**
 * Created by ran on 2018/11/15.
 */
public class TLAI {

    public net.sf.json.JSONObject go(String question) throws IOException {
        //String question = "你是？";//这是上传给云机器人的问题
        //String INFO = URLEncoder.encode("北京今日天气", "utf-8"); 
        String INFO = URLEncoder.encode(question, "utf-8");
        String getURL = "http://www.tuling123.com/openapi/api?key=" + CommonCode.AI_KEY + "&info=" + INFO;
        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.connect();

        // 取得输入流，并使用Reader读取 
        BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        //返回json对象
        net.sf.json.JSONObject jsonObject =  net.sf.json.JSONObject.fromObject(sb.toString());
        if(null== sb){
            jsonObject.put("text","对不起，你说的话真是太高深了……");
        }

        // 断开连接
        reader.close();
        connection.disconnect();
        System.out.println(sb);

        return jsonObject;
    }
}