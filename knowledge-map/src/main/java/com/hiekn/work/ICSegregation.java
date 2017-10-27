package com.hiekn.work;

import com.data0123.common.util.file.ReadFileUtil;
import com.hiekn.util.MapSort;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICSegregation {
//	private int mode = 0;	//工作模式，0表示最细粒度，1表示最大长度
	
//	private String letterRegex = "[a-zA-Z]+";
	private String numberRegex = "[0-9０-９]+";
	private String lnRegex = "[0-9０-９a-zA-Z]+";
//	String clearRegex = "[\\s，。〈〉‖《》〔〕﹖？“”：、（）【】—～！‵‘’“”＝＋｜＠＃￥％…×——｛｝　`,\\.<>\\|\\[\\]\\?\"\\:\\(\\)\\-~!'=+#$@%^&*{};]";
	private String clearRegex = "[\\s，。]";
	
	private Set<String> dicSet;
	private Set<String> preSet;
	
//	private Map<String, Set<String>> prefixMap = Dictionary.getPrefixMap();
	
//	private Reader reader;	//输入
//	private static final int IO_BUFFER_SIZE = 4096;	//缓冲大小
//	private final char[] ioBuffer;	//缓冲字符数组
	
//	public ICSegregation(Reader reader) {
//		this.reader = reader;
//		ioBuffer = new char[IO_BUFFER_SIZE];
//	}
	
	public ICSegregation(String dicHome){
		dicSet = ICDictionary.getWordSet(dicHome);
		preSet = ICDictionary.getPrefixSet(dicHome);
	}
	
	public ICSegregation(int mode){
//		this.mode = mode;
	}

	public synchronized List<String> segregate1(String input){
		List<String> wordList = new ArrayList<String>();
		try{
			input = input.replaceAll(clearRegex, " ");
			Pattern p = Pattern.compile(numberRegex);
			Matcher m = p.matcher(input);
			while(m.find()){
				if(!dicSet.contains(m.group())){
					wordList.add(m.group());
				}
//				input.replaceFirst(m.group(), " ");
			}
			p = Pattern.compile(lnRegex);
			m = p.matcher(input);
			while(m.find()){
				if(!dicSet.contains(m.group())){
					wordList.add(m.group());
				}
//				input.replaceFirst(m.group(), " ");
			}
			
			Set<String> workSet = new HashSet<String>();	//工作表中记录的是前缀表中包含的记录
			Set<String> tempSet = new HashSet<String>();	//临时表中记录的是一次工作过程中生成的临时前缀			
//			char curChar = ' ';
			String curStr = "";
			String curWord = "";
			for(int i=0;i<input.length();i++){
//				curChar = input.charAt(i);
//				curStr = String.valueOf(curChar);
//				if(curChar == ' '){
				curStr = input.substring(i, i + 1);
				if(curStr.equals(" ")){	//是空格、标点、符号等
					workSet.clear();
					tempSet.clear();
					continue;
				}				
				tempSet.clear();
//				if(dicSet.contains(curStr)){	//当前字符为一个词
//					wordList.add(curStr);
//				}
				wordList.add(curStr);
				if(preSet.contains(curStr)){
					tempSet.add(curStr);
				}
				for(String pre : workSet){	//把当前词与前缀表中的词组合成新词，判断是否构成独立词和是否仍为前缀
					curWord = pre.concat(curStr);	//新词
					if(dicSet.contains(curWord)){	//本词为一独立词
						wordList.add(curWord);
					}
					if(preSet.contains(curWord)){	//本词为一前缀
						tempSet.add(curWord);
					}
				}
				workSet.clear();
				workSet.addAll(tempSet);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return wordList;
	}
	
	public synchronized List<String> segregate2(String input){
		List<String> wordList = new ArrayList<String>();
		try{			
			Set<String> workSet = new HashSet<String>();	//工作表中记录的是前缀表中包含的记录
			Set<String> tempSet = new HashSet<String>();	//临时表中记录的是一次工作过程中生成的临时前缀	
			String curStr = "";
			String curWord = "";
			for(int i=0;i<input.length();i++){
				curStr = input.substring(i, i + 1);
				if(curStr.equals(" ")){	//是空格
					workSet.clear();
					tempSet.clear();
					continue;
				}				
				tempSet.clear();
				if(dicSet.contains(curStr)){	//当前字符为一个词
					wordList.add(curStr);
				}
				if(preSet.contains(curStr)){
					tempSet.add(curStr);
				}
				for(String pre : workSet){	//把当前词与前缀表中的词组合成新词，判断是否构成独立词和是否仍为前缀
					curWord = pre.concat(curStr);	//新词
					if(dicSet.contains(curWord)){	//本词为一独立词
						wordList.add(curWord);
					}
					if(preSet.contains(curWord)){	//本词为一前缀
						tempSet.add(curWord);
					}
				}
				workSet.clear();
				workSet.addAll(tempSet);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return wordList;
	}

	public synchronized List<String> segregate3(String input){
		List<String> wordList = new ArrayList<String>();
		try{
			input = input.replaceAll(clearRegex, " ");
			Pattern p = Pattern.compile(numberRegex);
			Matcher m = p.matcher(input);
			while(m.find()){
				if(!dicSet.contains(m.group())){
					wordList.add(m.group());
				}
//				input.replaceFirst(m.group(), " ");
			}
			p = Pattern.compile(lnRegex);
			m = p.matcher(input);
			while(m.find()){
				if(!dicSet.contains(m.group())){
					wordList.add(m.group());
				}
//				input.replaceFirst(m.group(), " ");
			}
			
			Set<String> workSet = new HashSet<String>();	//工作表中记录的是前缀表中包含的记录
			Set<String> tempSet = new HashSet<String>();	//临时表中记录的是一次工作过程中生成的临时前缀			
//			char curChar = ' ';
			String curStr = "";
			String curWord = "";
			for(int i=0;i<input.length();i++){
//				curChar = input.charAt(i);
//				curStr = String.valueOf(curChar);
//				if(curChar == ' '){
				curStr = input.substring(i, i + 1);
				if(curStr.equals(" ")){	//是空格、标点、符号等
					workSet.clear();
					tempSet.clear();
					continue;
				}				
				tempSet.clear();
//				if(dicSet.contains(curStr)){	//当前字符为一个词
//					wordList.add(curStr);
//				}
				wordList.add(curStr);
				if(preSet.contains(curStr)){
					tempSet.add(curStr);
				}
				for(String pre : workSet){	//把当前词与前缀表中的词组合成新词，判断是否构成独立词和是否仍为前缀
					curWord = pre.concat(curStr);	//新词
					if(dicSet.contains(curWord)){	//本词为一独立词
						wordList.add(curWord);
					}
					if(preSet.contains(curWord)){	//本词为一前缀
						tempSet.add(curWord);
					}
				}
				workSet.clear();
				workSet.addAll(tempSet);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return wordList;
	}

	
	public static void main(String[] args){
		ICSegregation seg = new ICSegregation("D:\\work\\guowang\\dic");
//		String input = new ReadFileUtil().readFileByCharSet("x:/1.txt", "utf-8");

		String input = "《面向特高压交直流大受端电网的大规模负荷精准协调互动控制技术研究》项目";
//		input = new ReadFileUtil().readFileByCharSet("D:\\work\\guowang\\2017_menu.txt", "utf-8");
		input = new ReadFileUtil().readFileByCharSet("D:\\work\\guowang\\zhinan\\2018.txt", "utf-8");

//		System.out.println(ICDictionary.getWordSet().contains(input));
//		System.out.println(input);
//		List<String> wordList = new ArrayList<String>();

//		input = input.replaceAll(seg.numberRegex, " ");

		Map<String, Integer> map = new HashMap<String, Integer>();

		List<String> wordList = seg.segregate2(input);
		System.out.println(wordList.size());
		for(String word : wordList){
//			System.out.println(word);
			map.put(word, map.containsKey(word) ? 1 + map.get(word) : 1);
		}

		MapSort mapSort = new MapSort();
		List<String> keyList = new ArrayList<String>();
		List<Integer> valueList = new ArrayList<Integer>();
		mapSort.sortIntegerValueMap(map, keyList, valueList);

		for(int i=0;i<keyList.size();i++){
			if(keyList.get(i).length() < 2){
				continue;
			}
			System.out.println(keyList.get(i) + "\t" + valueList.get(i));
		}
//		for(String word : map.keySet()){
//			System.out.println(word + "\t" + map.get(word));
//		}
	}
}
