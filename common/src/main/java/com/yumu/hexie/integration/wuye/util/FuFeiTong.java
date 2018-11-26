package com.yumu.hexie.integration.wuye.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FuFeiTong {
	
	
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("merId", "888891811535377");
		map.put("tid", "G0004655");
		String ss =  coverMap2String(map);
	}
	
	
	
	
	/**获取响应签名
     * @param map
     * @param
     * @return
     */
    public static String getReqSign(Map<String, String> map, String qufubaPriKey){
    	try {
            String signPlain = coverMap2String(map);
            String sign = JHRSACoder.sign(signPlain.getBytes("UTF-8"), qufubaPriKey);
            return sign;
        } catch (Exception e) {
            return "";
        }
    }
    
    private static String coverMap2String(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();

            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            sf.append(en.getKey() + "=" + en.getValue() + "&");
        }
        return sf.substring(0, sf.length() - 1);
    }
}
