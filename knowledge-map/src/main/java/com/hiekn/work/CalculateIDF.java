package com.hiekn.work;

import com.hiekn.util.CommonResource;

import java.io.*;
import java.util.*;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 2012-10-9
 * 
 */
public class CalculateIDF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		parseAndCalculate();
//		filter();
	}
	
	public static void parseAndCalculate(){
		try{
			ICSegregation segregation = new ICSegregation(CommonResource.WORK_HOME.concat("dic"));

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CommonResource.WORK_HOME.concat("corpus/corpus_doc.txt")), "utf-8"));
			String line = null;
			Set<String> wSet = new HashSet<String>();
			Map<String, Integer> map = new HashMap<String, Integer>();
			int docNum = 0;
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.length() < 5){
					continue;
				}
				++ docNum;

				List<String> wordList = segregation.segregate2(line);
				Set<String> set = new HashSet<String>();
				for(int j=0;j<wordList.size();j++){
					set.add(wordList.get(j));
				}
				for(String w : set){
					if(map.containsKey(w)){
						map.put(w, map.get(w) + 1);
					}
					else{
						map.put(w, 1);
					}
				}
			}
			br.close();
			System.out.println(map.size());
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CommonResource.WORK_HOME.concat("dic/idf/idf0_no.dic")), "utf-8"));
			for(String word : map.keySet()){
				bw.write(word + "\t" + map.get(word) + "\n");
			}
			bw.close();
			
			Map<String, Double> idfMap = new HashMap<String, Double>();
			for(String word : map.keySet()){
				idfMap.put(word, Math.log(docNum / map.get(word)));
			}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CommonResource.WORK_HOME.concat("dic/idf/idf0.dic")), "utf-8"));
			for(String word : idfMap.keySet()){
				bw.write(word + "\t" + idfMap.get(word) + "\n");
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void filter(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("d:/save/idf.dic"), "utf-8"));
			String line = null;
			Map<String, Double> map = new HashMap<String, Double>();
			while((line = br.readLine()) != null){
				line = line.trim();
				String ta[] = line.split("\t");
				if(ta.length != 2){
					continue;
				}
				double d = Double.parseDouble(ta[1]);
				if(d > 10){
					continue;
				}
				map.put(ta[0], Double.parseDouble(ta[1]));
			}
			br.close();
			Set<Double> set = new HashSet<Double>();
			set.addAll(map.values());
			Double[] da = new Double[set.size()];
			set.toArray(da);
			Arrays.sort(da);
			for(int i=0;i<da.length;i++){
				System.out.println(da[i]);
			}
			
			System.out.println(map.size());
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/save/idf_.dic"), "utf-8"));
			for(String word : map.keySet()){
				bw.write(word + "\t" + map.get(word) + "\n");
			}
			bw.close();
			
			System.out.println(set.size());
//			Thread.sleep(1000000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
