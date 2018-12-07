package com.lr.ai.tool;

import java.util.HashMap;

import com.lr.ai.code.CommonCode;
import com.lr.ai.util.HttpUtil;
import net.sf.json.JSONObject;

/**
 * Created by ran on 2018/11/15.
 */
public class Audio2Text {

    /**
     * 语音识别Java-API JSON方式上传
     * @param format	必填	语音文件的格式，pcm 或者 wav 或者 amr。不区分大小写。推荐pcm文件
     * @param rate	必填	采样率， 8000 或者 16000， 推荐 16000 采用率
     * @param channel	必填	声道数，仅支持单声道，请填写固定值 1
     * @param cuid	必填	用户唯一标识，用来区分用户，计算UV值。建议填写能区分用户的机器 MAC 地址或 IMEI 码，长度为60字符以内。
     * @param token	必填	开放平台获取到的access_token, 见上面的“鉴权认证机制”段落
     * @param lan	选填	语种选择，默认中文（zh）。 中文=zh、粤语=ct、英文=en，不区分大小写
     * @param url	选填	可下载的语音下载地址，与callback连一起使用，确保百度服务器可以访问。
     * @param callback	选填	用户服务器的识别结果回调地址，确保百度服务器可以访问
     * @param speech	选填	本地语音文件的的二进制语音数据 ，需要进行base64 编码。与len参数连一起使用。
     * @param len	选填	本地语音文件的的字节数，单位字节
     */
    public String Audio2text(String format,Integer rate,String cuid,String token,String speech,long len) throws Exception{
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("speech",speech);
        paramMap.put("format", format);
        paramMap.put("rate", rate);
        paramMap.put("channel",1);
        paramMap.put("cuid",cuid);
        paramMap.put("token", token);
        paramMap.put("len", len);
        JSONObject params = JSONObject.fromObject(paramMap);
        String data = HttpUtil.postASR(CommonCode.SERVERAPI_URL,params.toString());
//        System.out.println("语音文件识别的内容:"+data);
        return data;
    }
}