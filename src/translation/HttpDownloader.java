package translation;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader {

    private static URL url = null;
    private static String tagstr = null;
    private static String tagstrtemp = null;
    private static String tagstrtemp2 = null;
    //private static String nl=System.lineSeparator();
    private static String newline = System.getProperty("line.separator");
    private static int counter = 0;

    public static String download(String urlPath) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader br = null;

        try {
            //create a URL object  
            url = new URL(urlPath);
            //create http connection  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //use i/o stream to read data  
            br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line + newline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tagstr = sb.toString();
        return tagstr;
    }

    public static String getHtml(String urlPath, String c) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader br = null;

        try {
            //create a URL object  
            url = new URL(urlPath);
            //create http connection  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //use i/o stream to read data  
            br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), c));
            while ((line = br.readLine()) != null) {
                sb.append(line + newline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tagstr = sb.toString();
        return tagstr;
    }

    public static int stringNumbers(String str, String find) {
        if (str.indexOf("<" + find) == -1) {
            return 0;
        } else if (str.indexOf("<" + find) != -1) {
            counter++;
            tagstrtemp = tagstrtemp.replaceFirst("<" + find, "<" + counter + find);
            tagstrtemp = tagstrtemp.replaceFirst("</" + find, "</" + counter + find);
            stringNumbers(str.substring(str.indexOf("<" + find) + find.length() + 1), find);
            return counter;
        }
        return 0;
    }

    public static void main(String[] args) {
        /**
         * //download("http://zhangwei911.duapp.com/static/IDCardProof.html");
         * download("http://www.baidu.com"); tagstrtemp=tagstr; String []
         * tags=tagstr.split(nl); for(int i=0;i<tags.length;i++){
         * System.out.println((i+1)+"----------->>> "+tags[i]); } int
         * c=stringNumbers(tagstr,"div");System.out.println(c);
         * //tagstr.replaceFirst("<"+tag, "<"+i+tag); String []
         * tagstemp=tagstrtemp.split(nl);
         * System.out.println("----------------------------"); for(int
         * i=0;i<tagstemp.length;i++){ System.out.println((i+1)+"----------->>>
         * "+tagstemp[i]); }
         */
        try {
            File f = new File("D:\\12.txt");
            System.out.println(f.getAbsoluteFile());
            if (!f.exists()) {
                f.createNewFile();
            System.out.println(f.exists());
            }
//            FileOutputStream out = new FileOutputStream(f, true);//第二参数为true时，从文件结尾添加
//            out.write((newline + "123").getBytes());
//            //out.flush();
//            out.close();
            FileWriter fw = new FileWriter(f,true);  
            PrintWriter pw=new PrintWriter(fw);  
            pw.println("append content");  
            pw.close () ;  
            fw.close () ; 
            System.out.println("123");
        } catch (Exception e) {
        }
    }
}
