package com.hiekn.work;

import com.data0123.common.util.file.ReadFileUtil;
import com.hiekn.util.CommonResource;
import com.hiekn.util.MapSort;
import com.hiekn.util.MongoDBConnectionUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.io.*;
import java.util.*;

/**
 * @author xiaohuqi@126.com 2017-10-26 10:07
 **/
public class MapGenerator {
	public static void main(String[] args) {
//		new MapGenerator().process1();
		new MapGenerator().generateCorpus4MapCompute();
	}


	public void process(String input, ICSegregation seg, Map<String, Double> idfMap, int outputSize){

		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> wordList = seg.segregate2(input);
		System.out.println(wordList.size());
		for(String word : wordList){
//			System.out.println(word);
			map.put(word, map.containsKey(word) ? 1 + map.get(word) : 1);
		}

		Map<String, Double> scoreMap = new HashMap<String, Double>();

		for(String word : map.keySet()){
			double score = 13.1;
			if(idfMap.containsKey(word)){
				score = idfMap.get(word);
			}
			scoreMap.put(word, (1 + map.get(word) * Math.pow(score, 3) / 10000));
		}


		MapSort mapSort = new MapSort();
		List<String> keyList = new ArrayList<String>();
		List<Double> valueList = new ArrayList<Double>();
		mapSort.sortDoubleValueMap(scoreMap, keyList, valueList);

		for(int i=0;i<outputSize;i++){
			if(valueList.get(i) < 1){
				break;
			}
			String word = keyList.get(i);
			if(word.length() < 2){
				continue;
			}

			List<String> longWordList = new ArrayList<String>();
			int max = Math.min(outputSize*5, keyList.size());
			for(int j=0;j<max;j++){
				if(i == j){
					continue;
				}
				if(keyList.get(j).contains(word)){
					longWordList.add(keyList.get(j));
				}
			}
			System.out.print(word + "\t" + valueList.get(i) + "\t");
			for(String longWord : longWordList){
				System.out.print("\t" + longWord);
			}

			System.out.println();
		}
	}

	public void process1(){
		ICSegregation seg = new ICSegregation("D:\\work\\guowang\\dic");

		Map<String, Integer> map = new HashMap<String, Integer>();
		String input = new ReadFileUtil().readFileByCharSet("D:\\work\\guowang\\zhinan\\2017.txt", "utf-8");
		List<String> wordList = seg.segregate2(input);
		System.out.println(wordList.size());
		for(String word : wordList){
//			System.out.println(word);
			map.put(word, map.containsKey(word) ? 1 + map.get(word) : 1);
		}

		Map<String, Double> scoreMap = new HashMap<String, Double>();
		List<String> dicPathList = new ArrayList<String>();
		dicPathList.add("D:\\work\\guowang\\dic\\idf\\idf.dic");
		dicPathList.add("D:\\work\\guowang\\dic\\idf\\idf_1.dic");
		Map<String, Double> idfMap = readIdfDic(dicPathList);
		for(String word : map.keySet()){
			double score = 13.0;
			if(idfMap.containsKey(word)){
				score = idfMap.get(word);
			}
			scoreMap.put(word, (1 + map.get(word) * Math.pow(score, 3) / 100000));
		}


		MapSort mapSort = new MapSort();
		List<String> keyList = new ArrayList<String>();
		List<Double> valueList = new ArrayList<Double>();
		mapSort.sortDoubleValueMap(scoreMap, keyList, valueList);

		for(int i=0;i<2000;i++){
			String word = keyList.get(i);
			if(word.length() < 2){
				continue;
			}

			List<String> longWordList = new ArrayList<String>();
			for(int j=0;j<keyList.size();j++){
				if(i == j){
					continue;
				}
				if(keyList.get(j).contains(word)){
					if(scoreMap.get(keyList.get(j)) > 1) {
						longWordList.add(keyList.get(j));
					}
				}
			}
			System.out.print(word + "\t" + valueList.get(i) + "\t");
			for(String longWord : longWordList){
				System.out.print("\t" + longWord);
			}

			System.out.println();
		}
	}

	public Map<String, Double> readIdfDic(List<String> dicPathList){
		Map<String, Double> idfMap = new HashMap<String, Double>();
		for(String dicPath : dicPathList) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					int p = line.indexOf('\t');
					idfMap.put(line.substring(0, p), Double.valueOf(line.substring(1 + p)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return idfMap;
	}

	public void generateCorpus4MapCompute(){
		try{
			ICSegregation segregation = new ICSegregation(CommonResource.WORK_HOME.concat("dic"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CommonResource.WORK_HOME.concat("kmap/corpus1.txt")), "utf-8"));
			BufferedWriter bw0 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CommonResource.WORK_HOME.concat("kmap/corpus_doc.txt")), "utf-8"));

			MongoCollection mongoCollection = MongoDBConnectionUtil.getMongoCollection("source_data", "paper");
			MongoCursor<Document> mongoCursor = mongoCollection.find().iterator();
			while(mongoCursor.hasNext()){
				Document document = mongoCursor.next();
				StringBuilder sb = new StringBuilder();
				if(document.containsKey("keywords")) {
					List<String> keywords = (List<String>) document.get("keywords");
					for(String word : keywords){
						sb.append(word.toLowerCase()).append(" ");
					}
//					System.out.println(keywords.size());
				}
				if(document.containsKey("authors")) {
					List<Document> authors = (List<Document>) document.get("authors");
					for(Document document1 : authors){
						if(document1.containsKey("interests")){
							List<String> keywords = (List<String>) document1.get("interests");
							for(String word : keywords){
								sb.append(word.toLowerCase()).append(" ");
							}
						}
					}
				}
				sb.append("\n");
				if(document.containsKey("title")){
					String title = document.getString("title");
					List<String> wordList = segregation.segregate2(title.toLowerCase());
					for(String word : wordList){
						sb.append(word).append(" ");
					}
				}
				sb.append("\n");
				if(document.containsKey("abstract")){
					String abstract0 = document.getString("abstract");
					List<String> wordList = segregation.segregate2(abstract0.toLowerCase());
					for(String word : wordList){
						sb.append(word).append(" ");
					}
				}
				sb.append("\n");
				bw.write(sb.toString());
				bw0.write(sb.toString().replaceAll("\n", " ").concat("\n"));
			}
			mongoCursor.close();

			mongoCollection = MongoDBConnectionUtil.getMongoCollection("source_data", "patent");
			mongoCursor = mongoCollection.find().iterator();
			while(mongoCursor.hasNext()){
				Document document = mongoCursor.next();
				StringBuilder sb = new StringBuilder();
				if(document.containsKey("title")){
					Document titleDoc = (Document)document.get("title");
					if(titleDoc.containsKey("original")){
						List<String> wordList = segregation.segregate2(titleDoc.getString("original").toLowerCase());
						for(String word : wordList){
							sb.append(word).append(" ");
						}
					}
				}
				sb.append("\n");
				if(document.containsKey("abstract")){
					Document absDoc = (Document)document.get("abstract");
					if(absDoc.containsKey("original")){
						List<String> wordList = segregation.segregate2(absDoc.getString("original").toLowerCase());
						for(String word : wordList){
							sb.append(word).append(" ");
						}
					}
				}
				sb.append("\n");
				bw.write(sb.toString());
			}
			mongoCursor.close();

			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
