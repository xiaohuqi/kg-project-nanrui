package com.hiekn.work;

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
//		processDic("D:\\work\\guowang\\dic\\paper.dic");
		compareDic("D:/work/guowang/dic/paper.dic", "D:/work/guowang/dic/process/paper.dic.dic");
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

				line.replaceAll("[\\(（][^\\(（\\)）]+?[\\)）]", ""); //去除括号中内容

				if(line.startsWith("“") && line.endsWith("”")){
					line = line.substring(1);
					line = line.substring(0, line.length() - 1);
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



	/**
	 * 比较词典，找出dic2中不在dic1中的词
	 * @param dicPath1	词典路径1
	 * @param dicPath2	词典路径2
	 */
	public static void compareDic(String dicPath1, String dicPath2){
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

			br = new BufferedReader(new InputStreamReader(new FileInputStream(dicPath2), "utf-8"));
			while((line = br.readLine()) != null){
				line = line.trim();
				if(!dicSet.contains(line)){
					System.out.println(line);
				}
			}
			br.close();
		}catch (Exception e){

		}
	}
}
