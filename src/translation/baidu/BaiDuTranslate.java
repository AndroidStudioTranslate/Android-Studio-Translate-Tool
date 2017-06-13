/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translation.baidu;

/**
 *
 * @author swtf
 */
public class BaiDuTranslate {
    private static final String APP_ID = "";
    private static final String SECURITY_KEY = "";
    public static String translate(String query,String to){
        BaiDuTranslateApi api = new BaiDuTranslateApi(APP_ID, SECURITY_KEY);
        return api.getTransResult(query, "auto", to);
    }
}
