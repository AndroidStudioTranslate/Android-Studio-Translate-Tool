///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package translation;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.*;
//import java.util.Map.Entry;
//import net.sf.json.JSONArray;
//
//import net.sf.json.JSONObject;
//
///**
// * @author yoUng @description 发送http请求 @filename HttpUtil.java @time 2011-6-15
// * 下午05:26:36
// * @version 1.0
// */
//public class Post {
//
//    public static String http(String url, Map<String, String> params) {
//        URL u = null;
//        HttpURLConnection con = null;
//        StringBuffer buffer = new StringBuffer();
//        //构建请求参数
//        StringBuffer sb = new StringBuffer();
//        if (params != null) {
//            for (Entry<String, String> e : params.entrySet()) {
//                sb.append(e.getKey());
//                sb.append("=");
//                sb.append(e.getValue());
//                sb.append("&");//多个参数时需要加上
//            }
//            sb.substring(0, sb.length() - 1);
//        }
//        //        System.out.println("send_url:" + url);
//        //        System.out.println("send_data:" + sb.toString());
//        //尝试发送请求
//        try {
//            u = new URL(url);
//            con = (HttpURLConnection) u.openConnection();
//            con.setRequestMethod("POST");
//            con.setDoOutput(true);
//            con.setDoInput(true);
//            con.setUseCaches(false);
//            con.setReadTimeout(3000);
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
//            osw.write(sb.toString());
//            osw.flush();
//            osw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//
//        //读取返回内容
//
//        try {
//            if (con.getResponseCode() == 200) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//                String temp;
//                while ((temp = br.readLine()) != null) {
//                    buffer.append(temp);
//                    buffer.append("\n");
//                }
//            } else {
//                buffer.append(con.getResponseCode());
//            }
//            //System.out.println(con.getResponseCode() + "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return buffer.toString();
//    }
//
//    /**
//     * 获取翻译结果
//     *
//     * @param tvalue 需要翻译的中文字符串
//     * @return
//     */
//    public static String tg(String tvalue) {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("client_id", client_id);
//        map.put("from", "en");
//        map.put("to", "zh");
//        map.put("q", tvalue);
//        String tempt=WordsTransfer.unicodeToUtf8(http("http://openapi.baidu.com/public/2.0/bmt/translate", map).trim().replace("\\", "\\\\"));
//        JSONObject json=JSONObject.fromObject(tempt);
//        //json=JSONObject.fromObject(json.get("trans_result").toString().replace("[", "").replace("]", ""));
//        JSONArray ja=JSONArray.fromObject(json.get("trans_result"));
//        json=JSONObject.fromObject(ja.getString(0));
//        //return  (String)json.get("dst");
//        return  (String)json.getString("dst");
//    }
//    /**
//     * 获取翻译结果(比用JSON速度更快)
//     *
//     * @param tvalue 需要翻译的中文字符串
//     * @return
//     */
//    public static String tg1(String tvalue) {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("client_id", client_id);
//        map.put("from", "en");
//        map.put("to", "zh");
//        map.put("q", tvalue);
//        String tempt=WordsTransfer.unicodeToUtf8(http("http://openapi.baidu.com/public/2.0/bmt/translate", map).trim());
//        tempt=tempt.substring(tempt.lastIndexOf(":")+1,tempt.length()-3).replaceAll("\"","");
//        return  tempt;
//    }
//   /**public static void main(String[] args) {
//        long l=System.currentTimeMillis();
//        //System.out.println(tg1("what's your name? How Are You?铅笔"));
//       String[] a={"1","2","3","12","32","233","3222","2222","3333333","2234","3443","432","324","2345","314","221","213"};
//       List list=Arrays.asList(a);
//       if(list.contains("2234")){
//           System.out.println("contains 1");
//       }else{
//           System.out.println("not contains 1");
//       }
//        long l1=System.currentTimeMillis();
//        System.out.println("time1: "+(l1-l));
//        long l2=System.currentTimeMillis();
//        //System.out.println(tg("what's your name? How Are You?铅笔"));
//       String[] a1={"1","2","3","12","32","233","3222","2222","3333333","2234","3443","432","324","2345","314","221","213"};
//       for(int i=0;i<a1.length;i++){
//           if("000223400".contains(a1[i])){
//               System.out.println("contains 1");
//           }
//       }
//        long l3=System.currentTimeMillis();
//        System.out.println("time2: "+(l3-l2));
//    }*/
//    
//    private static String client_id="uELYiE9kdatIj0uhMWFz0sVi";
//}