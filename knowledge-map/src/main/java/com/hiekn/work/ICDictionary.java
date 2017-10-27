package com.hiekn.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class ICDictionary {

	private static Set<String> wordSet = new HashSet<String>();
	private static Set<String> prefixSet = new HashSet<String>();
	
	/**
	 * 获取词典，如果词典为空，则重新读取
	 * @param dicHome 词典目录
	 * @return 词典词条列表
	 */
	public static Set<String> getWordSet(String dicHome){
		if(wordSet.size() == 0){
			try{
				for(File dicFile : new File(dicHome).listFiles()){	//依次加入词典词条
					if(!dicFile.getName().endsWith(".dic")){
						continue;
					}
					BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(dicFile), "utf-8"));
					String aLine = "";
					while((aLine = bReader.readLine()) != null){
						if(aLine.trim() != null){
							wordSet.add(aLine.trim());
						}
					}
					bReader.close();
				}		
				System.out.println("------wordSet: " + wordSet.size());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return wordSet;
	}	
	
	/**
	 * 获取词典词条前缀，如果为空，则重新读取
	 * @param dicHome 词典目录
	 * @return 词典词条前缀列表
	 */
	public static Set<String> getPrefixSet(String dicHome){
		if(prefixSet.size() == 0){
			for(String word : getWordSet(dicHome)){
				for(int i=1;i<word.length();i++){
					prefixSet.add(word.substring(0, i));
				}
			}
		}
		return prefixSet;
	}

	/**
	 * @param dicHome 词典目录
	 * 刷新词典
	 */
	public static void refreshWordSet(String dicHome){
		try{
			wordSet.clear();
			prefixSet.clear();
			getWordSet(dicHome);
			getPrefixSet(dicHome);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加新词
	 * @param newWord
	 */
	public static synchronized void addNewWord(String newWord){
		try{
//			RandomAccessFile raf = new RandomAccessFile(CommonResource.DIC_HOME.concat("newword.dic"), "rw");
//			raf.seek(raf.length());
//			raf.write(newWord.concat("\n").getBytes("utf-8"));
//			raf.close();
			wordSet.add(newWord);
			for(int i=1;i<newWord.length();i++){
				prefixSet.add(newWord.substring(0, i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加新词
	 * @param newWordSet
	 */
	public static synchronized void addNewWord(Set<String> newWordSet){
		try{
//			RandomAccessFile raf = new RandomAccessFile(CommonResource.DIC_HOME.concat("newword.dic"), "rw");
//			raf.seek(raf.length());
//			for(String word : newWordSet){
//				raf.write(word.concat("\n").getBytes("utf-8"));
//				System.out.println("添加新词:\t" + word);
//			}
//			raf.close();
			for(String word : newWordSet){
				wordSet.add(word);
				for(int i=1;i<word.length();i++){
					prefixSet.add(word.substring(0, i));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
