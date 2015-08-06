/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package translation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author yoUng @description 发送http请求 @filename HttpUtil.java @time 2011-6-15
 * 下午05:26:36
 * @version 1.0
 */
public class Post {

    public static String http(String url, Map<String, String> params) {
        URL u = null;
        HttpURLConnection con = null;
        StringBuffer buffer = new StringBuffer();
        //构建请求参数
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                //sb.append("&");//多个参数时需要加上
            }
            sb.substring(0, sb.length() - 1);
        }
        //        System.out.println("send_url:" + url);
        //        System.out.println("send_data:" + sb.toString());
        //尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setReadTimeout(3000);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        //读取返回内容

        try {
            if (con.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String temp;
                while ((temp = br.readLine()) != null) {
                    buffer.append(temp);
                    buffer.append("\n");
                }
            } else {
                buffer.append(con.getResponseCode());
            }
            //System.out.println(con.getResponseCode() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * 获取翻译结果
     *
     * @param tvalue 需要翻译的中文字符串
     * @return
     */
    public static String tg(String translate_site,String tvalue) {
        Map<String, String> map = new HashMap<String, String>();;
        map.put("origin", tvalue);
        return http(translate_site, map).trim();
    }
//    public static void main(String[] args) {
//        Map<String, String> map = new HashMap<String, String>();;
//        map.put("q", "中国");
//        System.out.println(http("http://zhangwei911.duapp.com/TranslateGet.jsp", map).trim());
//    }
}