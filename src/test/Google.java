/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import translation.Post;

/**
 *
 * @author vi
 */
public class Google {

    public static void main(String[] args) {
        String words = "this is a test!\nthis is a test!\nthis is a test!";
        try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("client", "t");
                    map.put("text", words);
                    map.put("sl", "en");
                    map.put("tl", "zh_CN");
                    String dst=Post.http("http://translate.google.com/translate_a/t", map);
            System.out.println(dst);
            //System.out.println(TranslateUtil.en2cn(words));
        } catch (Exception ex) {
            Logger.getLogger(Google.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
