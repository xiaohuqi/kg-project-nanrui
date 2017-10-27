package com.hiekn.work;

import com.hiekn.util.CommonResource;
import com.hiekn.util.MongoDBConnectionUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaohuqi@126.com 2017-10-25 23:41
 **/
public class DicPreprocess {

	public static void main(String[] args) {
//		System.out.println("erp(enterprise resource planning)".replaceAll("[\\(（][^\\(（\\)）]+?[\\)）]", ""));

//		generateDicFromPaper(CommonResource.WORK_HOME.concat("dic/process/paper1.dic"));	//第1步
//		processDic(CommonResource.WORK_HOME.concat("dic/process/paper1.dic"));	//第2步
		excludeStopword(CommonResource.WORK_HOME.concat("dic/process/paper1.dic.dic"), CommonResource.WORK_HOME.concat("dic/process/stopword.dic"));	//第3步
//		compareDic("D:/work/nanrui/dic/paper.dic", "D:/work/nanrui/dic/process/paper.dic.dic", "D:/work/nanrui/dic/process/stopword.dic");
//		deleteUnEnWords(CommonResource.WORK_HOME.concat("dic/process/paper0.dic.dic.dic"), CommonResource.WORK_HOME.concat("dic/english/en.dic"));	//


//		extracEnDic();
	}



	public static void processDic(String dicPath){
		String clearRegex = "[\\s，。〈〉‖《》〔〕﹖？“”：、（）【】—～！‵‘’“”＝＋｜＠＃￥％…×——｛｝　`,\\.<>\\|\\[\\]\\?\"\\:\\(\\)\\-~!'=+#$@%^&*{};]";

		try {
			Pattern p = Pattern.compile(clearRegex);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath + ".dic"), "utf-8"));
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath), "utf-8"));
			String line = null;

			while((line = br.readLine()) != null){
				line = line.trim();
				Matcher m = p.matcher(line);
				if (m.find()) {
//					System.out.println(line);
				}

				if(line.length() < 2){	//去掉单字
					continue;
				}

				line = line.replaceAll("[\\(（][^\\(（\\)）]+?[\\)）]", "").trim(); //去除括号中内容
				if(line.contains("(")){
					line = line.substring(0, line.indexOf("(")).trim();
					if(line.length() < 2){
						continue;
					}
				}

				if(line.startsWith("“") && line.endsWith("”")){
					line = line.substring(1);
					line = line.substring(0, line.length() - 1).trim();
				}
				if(line.startsWith("\"") && line.endsWith("\"")){
					line = line.substring(1);
					line = line.substring(0, line.length() - 1).trim();
				}

				List<String> lineList = new ArrayList<String>();
				if(line.indexOf(" ") != -1){
					StringBuilder sb = new StringBuilder();
					int b = -1;
					while((b = line.indexOf(" ", b + 1)) != -1){
						String bs = line.substring(b - 1, b);
						String as = line.substring(b + 1, b + 2);
						if(bs.getBytes().length > 1 && as.getBytes().length > 1){
							sb.append(line.substring(0, b)).append("\n");
							line = line.substring(b + 1);
							b = -1;
						}
					}
					sb.append(line).append("\n");
					String la[] = sb.toString().trim().split("\n");
					for(String line0 : la){
						lineList.add(line0);
					}
				}
				else{
					lineList.add(line);
				}

				StringBuilder sb = new StringBuilder();
				for(int i=0;i<lineList.size();i++){
					String line0 = lineList.get(i);
					String[] la0 = line0.split("[,，;；。]");
					for(int j=0;j<la0.length;j++){
						if(!la0[j].trim().equals("")){
							sb.append(la0[j].trim()).append("\n");
						}
					}
				}
				bw.write(sb.toString());

//				if(line.length() < 2){
//					System.out.println(line);
//				}
			}
			br.close();
			bw.close();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void excludeStopword(String dicPath, String sotpwordDicPath){
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sotpwordDicPath), "utf-8"));
			String line = null;
			Set<String> set = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				set.add(line);
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath.concat(".dic")), "utf-8"));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath), "utf-8"));
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!line.equals("") && !set.contains(line)){
					if(line.contains(" ") && line.getBytes("utf-8").length > line.length()){
//						System.out.println(line);
						continue;
					}
					bw.write(line.concat("\n"));
				}
				if(line.contains(" ") && line.matches("[\\s\\S]*[0-9]+[\\s\\S]*") && line.matches("[\\s\\S]*[a-z]+[\\s\\S]*")){
					System.out.println(line);
				}
			}
			br.close();
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 比较词典，找出dic2中不在dic1中的词，写入到dic3中
	 * @param dicPath1	词典路径1
	 * @param dicPath2	词典路径2
	 * @param dicPath3	词典路径3
	 */
	public static void compareDic(String dicPath1, String dicPath2, String dicPath3){
		try {
			Set<String> dicSet = new HashSet<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath1), "utf-8"));
			String line = null;
			while((line = br.readLine()) != null){
				line = line.trim();
				if(!line.equals("")){
					dicSet.add(line);
				}
			}
			br.close();
			System.out.println(dicSet.size());

			Set<String> set = new HashSet<String>();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath2), "utf-8"));
			while((line = br.readLine()) != null){
				line = line.trim();
				if(!dicSet.contains(line)){
					set.add(line);
//					System.out.println(line);
				}
			}
			br.close();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath3), "utf-8"));
			for(String word : set){
				bw.write(word.concat("\n"));
			}
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void generateDicFromPaper(String dicPath){
		try{
			Map<String, Integer> map = new HashMap<String, Integer>();
			MongoCollection mongoCollection = MongoDBConnectionUtil.getMongoCollection("source_data", "paper");
			MongoCursor<Document> mongoCursor = mongoCollection.find().iterator();
			while(mongoCursor.hasNext()){
				Document document = mongoCursor.next();
				if(document.containsKey("keywords")) {
					List<String> keywords = (List<String>) document.get("keywords");
					for(String word : keywords){
						word = word.toLowerCase();
						map.put(word, map.containsKey(word) ? 1 + map.get(word) : 1);
					}
//					System.out.println(keywords.size());
				}
				if(document.containsKey("authors")) {
					List<Document> authors = (List<Document>) document.get("authors");
					for(Document document1 : authors){
						if(document1.containsKey("interests")){
							List<String> keywords = (List<String>) document1.get("interests");
							for(String word : keywords){
								word = word.toLowerCase();
								map.put(word, map.containsKey(word) ? 1 + map.get(word) : 1);
							}
						}
					}

				}
			}
			mongoCursor.close();
			System.out.println(map.size());

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath), "utf-8"));
			for(String word : map.keySet()){
				if(map.get(word) > 1) {
					bw.write(word.concat("\n"));
				}
			}
			bw.close();

//			int c = 0;
//			Map<String, Integer> map0 = new HashMap<String, Integer>();
//			for(String word : map.keySet()){
////				if(map.get(word) >= 10){
////					System.out.println(word);
////					++ c;
////				}
//				if(word.length() == word.getBytes("utf-8").length){
//					map0.put(word, map.get(word));
//					if(map.get(word) == 3){
//						System.out.println(word);
//						++ c;
//					}
//				}
//			}
//			System.out.println(c);
//
//			MapSort mapSort = new MapSort();
//			List<String> keyList = new ArrayList<String>();
//			List<Integer> valueList = new ArrayList<Integer>();
//			mapSort.sortIntegerValueMap(map0, keyList, valueList);
//			for(int i=0;i<keyList.size();i++){
////				System.out.println(keyList.get(i) + "\t" + valueList.get(i));
//			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void deleteUnEnWords(String dicFilePath, String enDicFilePath){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(enDicFilePath), "utf-8"));
			String line = null;
			Set<String> enSet = new HashSet<String>();

			while((line = br.readLine()) != null){
				if(enSet.contains(line)){
					System.out.println(line);
				}
				enSet.add(line);
//				if(line.length() == len && line.length() == line.getBytes("utf-8").length){
//					System.out.println(line);
//				}
			}
			br.close();

			Set<String> set = new HashSet<String>();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dicFilePath), "utf-8"));
			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.equals("")){
					continue;
				}
				if(line.length() == line.getBytes("utf-8").length && !line.contains(" ") && !enSet.contains(line)){
					continue;
				}
				set.add(line);
			}
			br.close();;
			System.out.println(set.size());

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicFilePath.concat(".dic")), "utf-8"));
			for(String word : set){
				bw.write(word.concat("\n"));
			}
			bw.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void extracEnDic(){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\work\\nanrui\\dic\\english\\英汉词典.txt"), "utf-8"));
			String line = null;
			Set<String> set = new HashSet<String>();

			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.length() < 2){
					continue;
				}
				int p = line.indexOf("   ");
				if(p == -1){
					p = line.indexOf(" ");
				}
				String word = line.substring(0, p);
				if(set.contains(word)){
					System.out.println(word);
				}
				set.add(word);
			}
			br.close();
			System.out.println(set.size());

			br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\work\\nanrui\\dic\\english\\官方英语单词.txt"), "utf-8"));
			while((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() < 2) {
					continue;
				}
				if(line.matches("[\\s\\S]*\\([,\\. a-z]+?\\)[\\s\\S]*")){
//					if(line.indexOf(" ") == -1)
//					System.out.println(line);
					set.add(line.substring(0, line.indexOf(" ")));

					Pattern p = Pattern.compile("\\([,\\. a-z]+?\\)");
					Matcher m = p.matcher(line);
					if(m.find()){
						String words = m.group();
						words = words.substring(1);
						words = words.substring(0, words.length() - 1);
						if(words.length() > 1){
							if(words.startsWith("pl.")){
								words = words.substring(3).trim();
							}
							String wa[] = words.split(",");
							for(String word : wa){
								word = word.trim();
								if(!word.equals("")){
									set.add(word);
								}
							}
						}
					}
				}
				else{
					int p = line.indexOf(" ");
					if(p != -1){
						set.add(line.substring(0, p));
					}
				}

			}
			br.close();
			System.out.println(set.size());

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\work\\nanrui\\dic\\english\\en.dic"), "utf-8"));
			for(String word : set){
				if(word.contains("(")){
					System.out.println(word);
				}
				bw.write(word.concat("\n"));
			}
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
