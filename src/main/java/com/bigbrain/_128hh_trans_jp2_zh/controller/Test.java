package com.bigbrain._128hh_trans_jp2_zh.controller;

import java.net.URLDecoder;

import com.bigbrain._128hh_trans_jp2_zh.util.TranslateApi_Jp2Zh;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Test {

	public static void main(String[] args) throws Exception{
		TranslateApi_Jp2Zh tool = new TranslateApi_Jp2Zh("_128hh_trans_jp2_zh", "夜空には宝石を散りばめたような星々が瞬き、伸ばしたオレの腕を淡く照らし出している。");
		String result = tool.getTransResult();
		JsonObject jobj = JsonParser.parseString(result).getAsJsonObject();
		JsonArray jarr = jobj.get("trans_result").getAsJsonArray();
		String src = jarr.get(0).getAsJsonObject().get("src").getAsString();
		String trans = jarr.get(0).getAsJsonObject().get("dst").getAsString();
		src = URLDecoder.decode(src, "utf-8");
		trans = URLDecoder.decode(trans,"utf-8");
		System.out.println("翻译后："+trans);
	}
}
