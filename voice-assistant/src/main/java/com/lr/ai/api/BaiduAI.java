package com.lr.ai.api;

import com.alibaba.fastjson.JSON;
import com.lr.ai.code.CommonCode;
import com.lr.ai.tool.Audio2Text;
import com.lr.ai.tool.ConvertMP32PCM;
import com.lr.ai.tool.Text2Audio;
import com.lr.ai.util.Base64Util;
import com.lr.ai.util.FileUtil;
import com.lr.ai.util.HttpUtil;
import com.lr.ai.tool.RandomStringGenerator;
import com.lr.ai.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ran on 2018/11/15.
 */
public class BaiduAI {

    /**
     * 返回百度授权码
     * @return
     */
    public String getAccessToken(){
        String url = "https://openapi.baidu.com/oauth/2.0/token";
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("grant_type","client_credentials");
        parameters.put("client_id", CommonCode.API_KEY);
        parameters.put("client_secret", CommonCode.SECRET_KEY);;
        String result = HttpUtil.httpGet(url,parameters,parameters,"UTF-8");
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
        String access_token = jsonObject.get("access_token").toString();
        return access_token;
    }

    /**
     * 返回文字变成语音路径地址
     * @param text 文字
     * @param token 授权码
     * @param per	选填	发音人选择, 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
     * @return
     * @throws Exception
     */
    public String text2AudioPath(String text,String token,String per) throws Exception {
        Text2Audio text2Audio = new Text2Audio();
        return text2Audio.text2Audio(text, token, "1", RandomStringGenerator.getRandomStringByLength(60),per);
    }

    /**
     * 返回语音识别的文字的内容
     * @param path 路径
     * @param token 授权码
     * @return
     * @throws Exception
     */
    public String audio2TextPath(String path,String token) throws Exception {
        //语音识别Java-API JSON上传方式示例代码
        //合成的MP3语音文件
        String mp3filepath = path;
        //MP3转pcm要保存的路径和文件名
        String pcmfilepath = path.replace(".mp3",".pcm");
        ConvertMP32PCM.convertMP32PCM(mp3filepath, pcmfilepath);
        // 对语音二进制数据进行识别
        byte[] data = FileUtil.readFileByBytes(pcmfilepath);    //readFileByBytes仅为获取二进制数据示例
        String speech = Base64Util.encode(data);
        File file = new File(pcmfilepath);
        long len = file.length();
        // JSON方式上传
        Audio2Text a = new Audio2Text();
        String result2 = a.Audio2text("pcm", 16000, RandomStringGenerator.getRandomStringByLength(60),token, speech, len);
        if(StringUtil.isNullOrEmpty(result2)){
            return "";
        }else if(result2.contains("error")){
            return "error";
        }
        net.sf.json.JSONObject jsonObject2 = net.sf.json.JSONObject.fromObject(result2);
        List<String> content = JSON.parseArray(jsonObject2.get("result").toString(), String.class);
        return content.get(0).substring(0, content.get(0).length() - 1);
    }
}
