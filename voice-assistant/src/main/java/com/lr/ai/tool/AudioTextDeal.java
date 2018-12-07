package com.lr.ai.tool;

import com.alibaba.fastjson.JSONArray;
import com.lr.ai.api.TLAI;
import com.lr.ai.code.CommonCode;
import com.lr.ai.util.StringUtil;
import net.sf.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ran on 2018/11/20.
 */
public class AudioTextDeal {

    //定制化服务
    String tlContentText = "";
    String tlContentURL = "";

    //返回对象
    Map<String, String> map = new HashMap<String, String>();

    /**
     * 处理需要识别的内容
     * @param audioContent 音频文字
     * @return 返回交互所需要的参数：问题、答案、地址、播报员
     * @throws IOException
     */
    public Map getAudioContent(String audioContent) throws IOException {

        //定制化内容（后期链接数据库）
        boolean flag = customizedContent(audioContent);

        //判断是否已经有返回的结果
        if(!flag) {
            TLAI tlai = new TLAI();//图灵机器人
            JSONObject jsonObject = tlai.go(audioContent);
            Set set = jsonObject.keySet();
            for (Object key : set) {
                System.out.println(key);
                if (key.toString().contains("text")) {
                    tlContentText = jsonObject.get("text").toString();
                } else if (key.toString().contains("url")) {
                    tlContentURL = jsonObject.get("url").toString();
                } else if (key.toString().contains("list")) {
                   JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("list").toString());
                    tlContentURL = jsonArray.getJSONObject(0).get("detailurl").toString();
                }
            }
        }

        //选择播报员
        selectSpeaker(audioContent);

        //组装返回参数
        map.put("question", audioContent);
        map.put("answer", tlContentText);
        map.put("url", tlContentURL);
        map.put("speaker",CommonCode.SPEAKER_NUM);
        if(audioContent.contains("error")){
            map.put("question", "");
        }
        return map;
    }

    /**
     * 选择播报员
     * 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
     * @param audioContent 音频文字
     */
    public void selectSpeaker(String audioContent){
        if (audioContent.contains("小明在不")) {
            CommonCode.SPEAKER_NUM = "3";
        }else if (audioContent.contains("小红在不")) {
            CommonCode.SPEAKER_NUM = "4";
        }
    }

    /**
     * 定制化内容（后期链接数据库）
     * @param audioContent 音频文字
     * @throws IOException
     */
    public boolean customizedContent(String audioContent) throws IOException {
        if (StringUtil.isNullOrEmpty(audioContent)) {
            tlContentText = "请求超时：当前网络状况较差";
            return true;
        } else if (audioContent.contains("error")) {
            tlContentText = "很抱歉没能听清楚你在说什么";
            return true;
        }else if (audioContent.contains("爸") || audioContent.contains("父亲")) {
            tlContentText = "我的父亲是来自河海大学文天学院的李冉";
            return true;
        } else if (audioContent.contains("妈") || audioContent.contains("母亲")) {
            tlContentText = "爸爸说先考研，妈妈什么的以后会有的";
            return true;
        } else if (audioContent.contains("TIM") || audioContent.contains("QQ")) {
            tlContentText = "正在为您打开TIM";
            Desktop.getDesktop().open(new File("D:\\Program Files (x86)\\Tencent\\TIM\\Bin\\QQScLauncher.exe\\"));
            return true;
        } else if (audioContent.contains("音乐") || audioContent.contains("歌")) {
            tlContentText = "正在为您打开网易云音乐";
            Desktop.getDesktop().open(new File("D:\\Program Files (x86)\\Netease\\CloudMusic\\cloudmusic.exe"));
            return true;
        } else if (audioContent.contains("关闭") || audioContent.contains("自爆")) {
            System.exit(0);
        }
        return false;
    }
}
