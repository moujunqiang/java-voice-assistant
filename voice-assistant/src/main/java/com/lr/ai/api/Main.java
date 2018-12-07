package com.lr.ai.api;


import com.lr.ai.code.CommonCode;
import com.lr.ai.tool.AudioTextDeal;
import com.lr.ai.tool.MP3Player;
import com.lr.ai.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by ran on 2018/11/15.
 */
public class Main {

    /**
     * 主函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("主人你好~ 有什么可以帮您");
        //定义一个键盘录入对象
        Scanner scan = new Scanner(System.in);
        //使用while控制程序的执行次数，
        while(true){
            String str = scan.nextLine();
            //如果录入的数据为end，则结束这个程序，
            if(str.equals("end")){
                break;
            }else {
                //原窗体界面中的音频识别的文字内容
                String voiceQuestion = str;

                //初始化map对象
                Map<String, String> map = new HashMap<String, String>();

                //百度语音
                String token = null;
                BaiduAI baiduAI = new BaiduAI();
                try {
                    token = baiduAI.getAccessToken();
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("请求超时：请检查网络连接");
                }
                //创建线程 音频转文字
                AudioTextDeal audioTextDeal = new AudioTextDeal();
                map = audioTextDeal.getAudioContent(voiceQuestion);

                //语音合成
                String text2audioPath = baiduAI.text2AudioPath(map.get("answer"), token, CommonCode.SPEAKER_NUM);
                map.put("text2audioPath", text2audioPath);

                //输入交互内容
                System.out.println("=====================");
                System.out.println(voiceQuestion);
                System.out.println(map.get("answer"));
                System.out.println(map.get("url"));
                System.out.println("====================="+"\n");

                //播放音频交互 打开网页
                new PlayMp3Thread().play(text2audioPath);
                if(!StringUtil.isNullOrEmpty(map.get("url"))){
                    openWebSite(map.get("url"));
                }
            }
        }
    }

    /**
     * 播放音频交互
     */
    static class PlayMp3Thread extends Thread{
        public void play(String text2audioPath){
            MP3Player mp3 = new MP3Player(text2audioPath);
            mp3.play();
        }
    }

    /**
     * 打开web界面
     * @param url
     */
    public static void openWebSite(String url){
        try {
            java.net.URI uri = java.net.URI.create(url);
            // 获取当前系统桌面扩展
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                //File file = new File("D:\\aa.txt");
                //dp.edit(file);// 　编辑文件
                dp.browse(uri);// 获取系统默认浏览器打开链接
                // dp.open(file);// 用默认方式打开文件
                // dp.print(file);// 用打印机打印文件
            }
        } catch (java.lang.NullPointerException e) {
            // 此为uri为空时抛出异常
            e.printStackTrace();
        } catch (java.io.IOException e) {
            // 此为无法获取系统默认浏览器
            e.printStackTrace();
        }
    }
}
