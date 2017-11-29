package com.hiekn.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 创建时间：2010-5-18 下午08:59:35
 * 说明
 */

public class CommonUtil {
	
	public String getCurrentDateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return sdf.format(new Date());
	}
	
	
	
//	/**
//	 * author xiaohuqi
//	 * @param str 源字符串
//	 * @return	分词后的字符串
//	 * 对字符串分词
//	 */
//	public String parseString(String str){
//		String returnStr = "";
//		try{
//			StringReader reader = new StringReader(str);
//			TokenStream ts = analyzer.tokenStream("", reader);
//			TermAttribute termAtt = (TermAttribute)ts.getAttribute(TermAttribute.class);
//			while(ts.incrementToken()){
//				returnStr = returnStr.concat(termAtt.term()).concat(" ");
//			}	
//			returnStr = returnStr.trim();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return returnStr;
//	} 
	
	/**
	 * @author xiaohuqi
	 * @param fileName 属性文件
	 * @param key 	要查询的属性key
	 * @return 属性key值
	 * 获取属性key值
	 */
	public String getPropertys(String fileName, String key){
		InputStream is = getClass().getResourceAsStream(fileName); 
		Properties prop = new Properties();   
		try{
			prop.load(is);  			
		}catch(Exception e){
			System.out.println(e);
		}
		String keyValue = prop.get(key) != null ? prop.get(key).toString() : "";  
		return keyValue;
	}
	
	/**
	 * 替换SQL中的特殊字符
	 * @param input
	 * @return
	 */
	public String sqlReplace(String input){
		try{
			return input.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\");
		}catch(Exception e){
			return input;
		}
	} 
	
	public String getMatchedContent(String sourceString, String regex, int groupNo){
		String matchedString = "";
		try{
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sourceString);
			if(m.find()){
				matchedString = m.group(groupNo);
			}
			m = null;
			p = null;
		}catch(Exception e){
			e.printStackTrace();
		}
		return matchedString;
	}
	
	public String getMatchedContent(String sourceString, String regex, int groupNo, int no){
		String matchedString = "";
		try{
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sourceString);
			int i=0;
			while(i ++ < no && m.find()){
//				matchedString = m.group(groupNo);
			}
			if(m.find()){
				matchedString = m.group(groupNo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return matchedString;
	}
	
	/**
	 * @param sourceString	源字符串
	 * @param regex	匹配正则表达式
	 * @param groupNo 要获得结果位于正则表达式中的具体group，返回全部匹配的结果时为0
	 * @return
	 * 从一个源字符串中获得所有匹配到的内容
	 */
	public List<String> getAllMatchedContent(String sourceString, String regex, int groupNo){
		List<String> result = new ArrayList<String>();
		try{
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sourceString);
			while(m.find()){
				result.add(m.group(groupNo));
			}
			m = null;
			p = null;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @param sourceString	源字符串
	 * @param regex	匹配正则表达式
	 * @param groupNo 要获得结果位于正则表达式中的具体group，返回全部匹配的结果时为0
	 * @return
	 * 从一个源字符串中获得所有匹配到的内容，并消除重复记录
	 */
	public Set<String> getAllMatchedContentNoDuplicate(String sourceString, String regex, int groupNo){
		Set<String> result = new HashSet<String>();
		try{
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sourceString);
			while(m.find()){
				result.add(m.group(groupNo));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 分离数组，找出na2中未在na1中出现的元素，即na2相对于na1的新元素，空元素自动去除
	 * @param na1
	 * @param na2
	 * @return 有新元素则返回其数组，否则返回null
	 */
	public ArrayList<String> detachArrays(String[] na1, String[] na2){
		try{
			ArrayList<String> list = new ArrayList<String>();
			Set<String> set = new HashSet<String>();
			for(int i=0;i<na1.length;i++){
				set.add(na1[i]);
			}			
			for(int i=0;i<na2.length;i++){
				if(!set.contains(na2[i])){
					if(!na2[i].equals("")){
						list.add(na2[i]);
					}
				}
			}
			if(list.size() > 0){
				return list;				
			}
			else{
				list = null;
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String chineseTrim(String input){
		input = input.trim();
		int p = 0;
		for(;p<input.length();p++){
			if(input.charAt(p) != '　'){
				break;
			}
		}
		input = input.substring(p);

		p = input.length() - 1;
		for(;p>=0;p--){
			if(input.charAt(p) != '　'){
				break;
			}
		}
		input = input.substring(0, 1 + p);
		return input;
	}
	
	public int object2Int(Object obj){
		try{
			if(obj == null || obj.toString().trim().equals("")){
				return 0;
			}
			else{
				return Integer.parseInt(obj.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public long object2Long(Object obj){
		try{
			if(obj == null || obj.toString().trim().equals("")){
				return 0L;
			}
			else{
				return Long.parseLong(obj.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0L;
		}
	}
	
	public float object2Float(Object obj){
		try{
			if(obj == null || obj.toString().trim().equals("")){
				return 0.0f;
			}
			else{
				return Float.parseFloat(obj.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0.0f;
		}
	}
	
	public double object2Double(Object obj){
		try{
			if(obj == null || obj.toString().trim().equals("")){
				return 0.0;
			}
			else{
				return Double.parseDouble(obj.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			return 0.0;
		}
	}
	
	public String object2String(Object obj){
		try{
			if(obj == null){
				return null;
			}
			else{
				return obj.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
