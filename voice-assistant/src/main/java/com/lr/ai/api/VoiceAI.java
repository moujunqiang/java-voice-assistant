package com.lr.ai.api;

import com.lr.ai.code.CommonCode;
import com.lr.ai.tool.AudioConvert;
import com.lr.ai.tool.AudioTextDeal;
import org.bytedeco.javacpp.avcodec;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ran on 2018/11/15.
 */
@SuppressWarnings("unchecked")
public class VoiceAI {

    //标识符
//    private boolean audio2TextFlag = false;
//    private boolean text2AudioFlag = false;

//    线程
//    final WaitThread waitThread = new WaitThread();
//    final Audio2TextThread audio2TextThread = new Audio2TextThread();
//    final Text2AudioPathThread text2AudioPathThread = new Text2AudioPathThread();

    public Map hci(String voicePath) throws Exception {

        //初始化map对象
        Map<String, String> map = new HashMap<String, String>();

        //转换音频编码
        AudioConvert audioConvert = new AudioConvert();
        String workspace = System.getProperty("user.home");
        String newVoicePath = workspace + "/audio2text/peopleSpeak.mp3";
        audioConvert.convert(voicePath, newVoicePath, avcodec.AV_CODEC_ID_MP3, 16000, 16000, 1);

        //百度语音
        String token = null;
        BaiduAI baiduAI = new BaiduAI();
        try{
            token = baiduAI.getAccessToken();
        }catch (Exception e){
            System.out.println(e);
            map.put("answer", "请求超时：请检查网络连接");
            return map;
        }

        //创建线程 音频转文字
        String voiceQuestion = baiduAI.audio2TextPath(newVoicePath, token);
        AudioTextDeal audioTextDeal = new AudioTextDeal();
        map = audioTextDeal.getAudioContent(voiceQuestion);

        //语音合成
        String text2audioPath = baiduAI.text2AudioPath(map.get("answer"), token, CommonCode.SPEAKER_NUM );
        map.put("text2audioPath",text2audioPath);

        //输入交互内容
        System.out.println("=====================");
        System.out.println(voiceQuestion);
        System.out.println(map.get("answer"));
        System.out.println(map.get("url"));
        System.out.println("=====================");

        return map;
    }

    /**
     * 语音识别线程
     */
    public class Audio2TextThread extends Thread {
        public String go(BaiduAI baiduAI, String newVoicePath, String token) throws Exception {
            return baiduAI.audio2TextPath(newVoicePath, token);
        }
    }

    /**
     * 语音合成线程
     */
    public class Text2AudioPathThread extends Thread {
        public String go(BaiduAI baiduAI, String tlContentText, String token,String speaker) throws Exception {
            return baiduAI.text2AudioPath(tlContentText, token,speaker);
        }
    }

    /**
     * 计时结束录制
     */
    public class WaitThread extends Thread {
        public void run(){
            int time = 4;// 结束时间.
            long t1 = System.currentTimeMillis();
            while (time >= 0) {
                if (System.currentTimeMillis() - t1 == 1000) {
                    t1 = System.currentTimeMillis();
                    time--;// 减一秒
                    System.out.println(time);
                }
            }
//            audio2TextThread.stop();
//            audio2TextThread.interrupt();
        }
    }
}
