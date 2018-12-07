package com.lr.ai.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * http 工具类
 */
public class HttpUtil {

    public static String post(String requestUrl, String accessToken, String params) throws Exception {
        System.out.println(params);
        String generalUrl = "";
        generalUrl = requestUrl + "?access_token=" + accessToken;
        System.out.println("发送的连接为:"+generalUrl);
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("打开链接，开始发送请求"+new Date().getTime()/1000);
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
//            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.out.println("请求结束"+new Date().getTime()/1000);
        System.out.println("result:" + result);
        return result;
    }
    public static String postUnit(String requestUrl, String accessToken, String params) throws Exception {
        String generalUrl = requestUrl + "?access_token=" + accessToken;
        System.out.println("发送的连接为:"+generalUrl);
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("打开链接，开始发送请求"+new Date().getTime()/1000);
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
        out.write(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.out.println("请求结束"+new Date().getTime()/1000);
        System.out.println("result:" + result);
        return result;
    }
    /**
     * 语音合成HTTP方法
     * @param requestUrl 请求的接口地址 拼接access_token后的
     * @param params 语音合成的参数
     * @throws Exception
     */
    public static String postVoice(String requestUrl,String params) throws Exception {
        String workspace = System.getProperty("user.home");
        String path = workspace+"/text2audio/";
        try {
            if (!(new File(path).isDirectory())) {
                new File(path).mkdir();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        String filePath = path+"VOICE"+new Date().getTime()/1000+".mp3";
        String generalUrl = requestUrl;
        URL url = new URL(generalUrl);
        System.out.println(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("打开链接，开始发送请求"+new Date().getTime()/1000);
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len=inputStream.read(buffer))!=-1) {
            outputStream.write(buffer,0,len);
        }
        outputStream.close();
        System.out.println("请求结束"+new Date().getTime()/1000);
        System.out.println("MP3文件保存目录:" + filePath);
        return filePath;
    }
    /**
     * 获取语音识别内容
     * @param requestUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static String postASR(String requestUrl, String params) throws Exception {

        //标识符 监控请求时间
        boolean flag = false;

        //请求的相关参数
        System.out.println(params);
        String generalUrl = requestUrl;
        System.out.println("发送的连接为:"+generalUrl);
        URL url = new URL(generalUrl);

        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("打开链接，开始发送请求"+new Date().getTime()/1000);
        connection.setRequestMethod("POST");

        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
//            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
//        System.out.println("请求结束"+new Date().getTime()/1000);
//        System.out.println("result:" + result);
        flag = true;
        return result;
    }
    public static String postTest(String requestUrl,String params) throws Exception {
        String workspace = System.getProperty("user.home");
        String path = workspace+"/text2audio/";
        try {
            if (!(new File(path).isDirectory())) {
                new File(path).mkdir();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        String filePath = path+"VOICE"+new Date().getTime()/1000+".apk";
        String generalUrl = requestUrl;
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("打开链接，开始发送请求"+new Date().getTime()/1000);
        connection.setRequestMethod("GET");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        InputStream inputStream = connection.getInputStream();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FileOutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len=inputStream.read(buffer))!=-1) {
            outputStream.write(buffer,0,len);
        }
        outputStream.close();
        System.out.println("请求结束"+new Date().getTime()/1000);
        System.out.println("MP3文件保存目录:" + filePath);
        return filePath;
    }
    public static String post(String requestUrl,String params) throws Exception {
        String generalUrl = requestUrl;
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();
        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.out.println("result:" + result);
        return result;
    }
    /**
     * NLP接口HTTP请求方法
     * @param requestUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static String postNLP(String requestUrl,String params) throws Exception {
        String encoding = "";
        if(requestUrl.contains("nlp")){
            encoding = "GBK";
        }else{
            encoding = "UTF-8";
        }
        String generalUrl = requestUrl;
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(params.getBytes(encoding));
        out.flush();
        out.close();
        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.out.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        if (requestUrl.contains("nlp_wordseg")||requestUrl.contains("nlp_wordpos")||requestUrl.contains("nlp_wordner")||requestUrl.contains("nlp_wordsyn"))
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
        else
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.out.println("result:" + result);
        return result;
    }

    /**
     *
     * @param url 请求的url
     * @param parameters 请求的参数
     * @return
     */
    public static String httpGet(String url, Map<String, String> parameters,Map<String, String> header,String unicode)
    {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        StringBuffer sb = new StringBuffer();// 存储参数
        String params = "";// 编码之后的参数
        String full_url = "";
        try {
            // 编码请求参数
            if(parameters.size() == 0){
                full_url = url;
            }else if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name),
                                    unicode));
                }
                params = sb.toString();
                full_url = url + "?" + params;
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name),
                                    unicode)).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
                full_url = url + "?" + params;
            }

//            logger.error(full_url);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(full_url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");

            //加载头部参数
            if(header.size() > 0){
                for(String name : header.keySet()){
                    httpConn.setRequestProperty(name,header.get(name));
                }
            }

            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
//                System.out.println(key + "\t：\t" + headers.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), unicode));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }

//            logger.error(String.format("get请求发出的时间%s",String.valueOf(new Date())));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    public static String httpPost(String url, Map<String, String> parameters, Map<String, String> header)
    {
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name),
                                    "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(parameters.get(name) == null ? "" : parameters.get(name),
                                    "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();

            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");

            //加载头部参数
            if(header.size() > 0){
                for(String name : header.keySet()){
                    httpConn.setRequestProperty(name,header.get(name));
                }
            }

            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}