package com.bigbrain._128hh_trans_jp2_zh.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bigbrain
 *
 */
public class TranslateApi_Jp2Zh {
	/**
	 * VIP翻译接口（需要付费）
	 */
    private static final String TRANS_API_HOST = "https://web1001.q-plants.com/fanyi/jp2zh";
    
    	private Map<String,String> params = new HashMap<String, String>();

    /**
     * 	客户端标志和要翻译的文本
     * @param clientId
     * @param text
     */
    public TranslateApi_Jp2Zh(String clientId, String text) {
    	params.clear();
    	params.put("clientId", clientId);
    	params.put("text", text);
    }

    public String getTransResult() {
        return HttpGet.get(TRANS_API_HOST,params);
    }
}
