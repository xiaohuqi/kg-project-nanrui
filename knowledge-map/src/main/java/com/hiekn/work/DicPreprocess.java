package com.hiekn.work;

import com.hiekn.util.CommonResource;
import com.hiekn.util.MongoDBConnectionUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaohuqi@126.com 2017-10-25 23:41
 **/
public class DicPreprocess {

	public static void main(String[] args) {
//		System.out.println("doubly-fed wind generators（dfwgs）".replaceAll("[\\(（][^\\(（\\)）]+?[\\)）]", ""));

//		processDic(CommonResource.WORK_HOME.concat("dic/process/paper0.dic"));
//		excludeStopword(CommonResource.WORK_HOME.concat("dic/process/paper0.dic.dic"), CommonResource.WORK_HOME.concat("dic/process/stopword.dic"));
//		compareDic("D:/work/nanrui/dic/paper.dic", "D:/work/nanrui/dic/process/paper.dic.dic", "D:/work/nanrui/dic/process/stopword.dic");
//		generateDicFromPaper(CommonResource.WORK_HOME.concat("dic/process/paper0.dic"));

		findShortEnWords(CommonResource.WORK_HOME.concat("dic/paper.dic"), 2);
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
			Set<String> set = new HashSet<String>();
			MongoCollection mongoCollection = MongoDBConnectionUtil.getMongoCollection("source_data", "paper");
			MongoCursor<Document> mongoCursor = mongoCollection.find().iterator();
			while(mongoCursor.hasNext()){
				Document document = mongoCursor.next();
				if(document.containsKey("keywords")) {
					List<String> keywords = (List<String>) document.get("keywords");
					for(String word : keywords){
						set.add(word.toLowerCase());
					}
//					System.out.println(keywords.size());
				}
				if(document.containsKey("authors")) {
					List<Document> authors = (List<Document>) document.get("authors");
					for(Document document1 : authors){
						if(document1.containsKey("interests")){
							List<String> keywords = (List<String>) document1.get("interests");
							for(String word : keywords){
								set.add(word.toLowerCase());
							}
						}
					}

				}
			}
			mongoCursor.close();
			System.out.println(set.size());

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath), "utf-8"));
			for(String word : set){
				bw.write(word.concat("\n"));
			}
			bw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void findShortEnWords(String dicFilePath, int len){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicFilePath), "utf-8"));
			String line = null;
			Set<String> set = new HashSet<String>();

			while((line = br.readLine()) != null){
				if(set.contains(line)){
					System.out.println(line);
				}
				set.add(line);
//				if(line.length() == len && line.length() == line.getBytes("utf-8").length){
//					System.out.println(line);
//				}
			}

			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
