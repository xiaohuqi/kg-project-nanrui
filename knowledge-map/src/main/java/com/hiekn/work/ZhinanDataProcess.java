package com.hiekn.work;

import com.data0123.common.util.file.ReadFileUtil;
import com.data0123.common.util.file.WriteFileUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaohuqi@126.com 2017-10-26 13:43
 **/
public class ZhinanDataProcess {

	public static void main(String[] args) {
		extract4Zhinan();
	}


	public static void extract4Zhinan(){
		JSONArray jsonArray = extract("D:\\work\\nanrui\\zhinan\\2018.txt");
		new WriteFileUtil().writeFileByCharSet(jsonArray.toString(),"d:/work/nanrui/zhinan/2018.json", "utf-8");

		System.out.println(jsonArray.size());
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String name = jsonObject.getString("name");
			System.out.println(name);
		}

		MapGenerator mapGenerator = new MapGenerator();

		ICSegregation seg = new ICSegregation("D:\\work\\nanrui\\dic");
		List<String> dicPathList = new ArrayList<String>();
		dicPathList.add("D:\\work\\nanrui\\dic\\idf\\idf.dic");
		dicPathList.add("D:\\work\\nanrui\\dic\\idf\\idf_1.dic");
		Map<String, Double> idfMap = mapGenerator.readIdfDic(dicPathList);

		for(int i=0;i<jsonArray.size();i++) {
			JSONObject domainJsonObject = jsonArray.getJSONObject(i);
			JSONArray projectJSONArray = domainJsonObject.getJSONArray("projects");
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < projectJSONArray.size(); j++) {
				JSONObject projectJsonObject = projectJSONArray.getJSONObject(j);
				if(projectJsonObject.containsKey("name")){
					sb.append(projectJsonObject.getString("name")).append("\n");
				}
				if(projectJsonObject.containsKey("zongtimubiao")){
					sb.append(projectJsonObject.getString("zongtimubiao")).append("\n");
				}if(projectJsonObject.containsKey("ketineirong")){
					sb.append(projectJsonObject.getString("ketineirong")).append("\n");
				}
			}
			mapGenerator.process(sb.toString(), seg, idfMap, 50,
					"d:/work/nanrui/zhinan/result/2018/" + (i + 1) + domainJsonObject.getString("name") + ".txt");
		}

	}

	public static JSONArray extract(String inputFilePath) {
		JSONArray jsonArray = new JSONArray();
		try{
			String input = new ReadFileUtil().readFileByCharSet(inputFilePath, "utf-8");
			String[] lineArray = input.split("\n");
			for(int i=0;i<lineArray.length;i++){
				String line = lineArray[i].trim();
				if(line.equals("")){
					continue;
				}
				if(line.matches("领域\\S{1,2}[:：][\\S\\s]+")){
					System.out.println(line);
					JSONObject domainJsonObject = new JSONObject();
					domainJsonObject.put("name", line.substring(1 + (line.indexOf(":") != -1 ? line.indexOf(":") : line.indexOf("："))));
					domainJsonObject.put("projects", new JSONArray());
					while(i < lineArray.length-1){
						line = lineArray[++i].trim();
						if(line.matches("领域\\S{1,2}[:：][\\S\\s]+")){
							-- i;
							break;
						}

						if(line.matches("项目 \\d+[：:][\\S\\s]+")){
							String projectName = line;
							while(!projectName.endsWith("申报指南")){
								line = lineArray[++i].trim();
								projectName = projectName.concat(line);
							}
							JSONObject projectJsonObject = new JSONObject();
							projectJsonObject.put("name", projectName.substring(1 + projectName.indexOf("《", projectName.indexOf("》"))));
							System.out.println("\t" + projectName);

							String ppv = "";
							while(i < lineArray.length-1){
								line = lineArray[++i].trim();
								if(line.matches("项目 \\d+[：:][\\S\\s]+") || line.matches("领域\\S{1,2}[:：][\\S\\s]+")){
									projectJsonObject.put("zhichijingfeixiane", ppv);
									-- i;
									break;
								}

								ppv = ppv.concat(line).concat("\n");
								if(line.equals("一、技术类别")){
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("二、总体目标")){
									projectJsonObject.put("jishuleibie", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("三、课题设置情况")){
									projectJsonObject.put("zongtimubiao", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("四、项目实施期限")){
									projectJsonObject.put("ketishezhiqingkuang", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("五、课题内容")){
									projectJsonObject.put("xiangmushishiqixian", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
									JSONArray subProjectJsonArray = new JSONArray();
									while(i < lineArray.length-1){
										line = lineArray[++i].trim();
										if(line.startsWith("六、支持经费限额")){
											-- i;
											break;
										}

										ppv = ppv.concat(line).concat("\n");
										if (line.matches("课题 \\d+[：:、][\\S\\s]+")) {
//											JsonObject subProjectJsonObject = new JsonObject();
											if (line.endsWith("主要研究内容：")) {
												line = line.substring(0, line.length() - 7);
											}
//											subProjectJsonObject.addProperty("name", line.substring(1 + line.indexOf("：")));
											int p = line.indexOf("：");
											if(p == -1){
												p = line.indexOf(":");
											}
											if(p == -1){
												p = line.indexOf("、");
											}
											subProjectJsonArray.add(line.substring(1 + p));
											System.out.println("\t\t\t" + line);

										}
									}
									projectJsonObject.put("subProjects", subProjectJsonArray);
								}
								else if(line.startsWith("六、支持经费限额")){
									projectJsonObject.put("ketineirong", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
							}
							domainJsonObject.getJSONArray("projects").add(projectJsonObject);
						}
					}

					jsonArray.add(domainJsonObject);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}

	public static JSONArray extract2015(String inputFilePath) {
		JSONArray jsonArray = new JSONArray();
		try{
			String input = new ReadFileUtil().readFileByCharSet(inputFilePath, "utf-8");
			String[] lineArray = input.split("\n");
			for(int i=0;i<lineArray.length;i++){
				String line = lineArray[i].trim();
				if(line.equals("")){
					continue;
				}
				if(line.matches("指南领域\\S{1,3}[:：][\\S\\s]+")){
					System.out.println(line);
					JSONObject domainJsonObject = new JSONObject();
					domainJsonObject.put("name", line.substring(1 + (line.indexOf(":") != -1 ? line.indexOf(":") : line.indexOf("："))));
					domainJsonObject.put("projects", new JSONArray());
					while(i < lineArray.length-1){
						line = lineArray[++i].trim();
						if(line.matches("指南领域\\S{1,3}[:：][\\S\\s]+")){
							-- i;
							break;
						}

						if(line.matches("项目 \\d+[：:\\.][\\S\\s]+")){
							String projectName = line;
							while(!projectName.endsWith("申报指南")){
								line = lineArray[++i].trim();
								projectName = projectName.concat(line);
							}
							JSONObject projectJsonObject = new JSONObject();
							projectJsonObject.put("name", projectName.substring(1 + projectName.indexOf("《", projectName.indexOf("》"))));
							System.out.println("\t" + projectName);

							String ppv = "";
							while(i < lineArray.length-1){
								line = lineArray[++i].trim();
								if(line.matches("项目 \\d+[：:][\\S\\s]+") || line.matches("指南领域\\S{1,3}[:：][\\S\\s]+")){
									projectJsonObject.put("zhichijingfeixiane", ppv);
									-- i;
									break;
								}

								ppv = ppv.concat(line).concat("\n");
								if(line.equals("一、技术类别")){
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("二、总体目标")){
									projectJsonObject.put("jishuleibie", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("三、课题设置情况")){
									projectJsonObject.put("zongtimubiao", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("四、项目实施期限")){
									projectJsonObject.put("ketishezhiqingkuang", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
								else if(line.equals("五、课题内容")){
									projectJsonObject.put("xiangmushishiqixian", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
									JSONArray subProjectJsonArray = new JSONArray();
									while(i < lineArray.length-1){
										line = lineArray[++i].trim();
										if(line.startsWith("六、支持经费限额")){
											-- i;
											break;
										}

										ppv = ppv.concat(line).concat("\n");
										if (line.matches("课题 \\d+[：:、][\\S\\s]+")) {
//											JsonObject subProjectJsonObject = new JsonObject();
											if (line.endsWith("主要研究内容：")) {
												line = line.substring(0, line.length() - 7);
											}
//											subProjectJsonObject.addProperty("name", line.substring(1 + line.indexOf("：")));
											int p = line.indexOf("：");
											if(p == -1){
												p = line.indexOf(":");
											}
											if(p == -1){
												p = line.indexOf("、");
											}
											subProjectJsonArray.add(line.substring(1 + p));
											System.out.println("\t\t\t" + line);

										}
									}
									projectJsonObject.put("subProjects", subProjectJsonArray);
								}
								else if(line.startsWith("六、支持经费限额")){
									projectJsonObject.put("ketineirong", ppv.substring(0, ppv.lastIndexOf("\n", ppv.length() - 1)));
									ppv = "";
									System.out.println("\t\t" + line);
								}
							}
							domainJsonObject.getJSONArray("projects").add(projectJsonObject);
						}
					}

					jsonArray.add(domainJsonObject);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}

	public static void extract2(String inputFilePath) {
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), "utf-8"));
			String line = null;

			while((line = br.readLine()) != null){
				line = line.trim();
				if(line.equals("")){
					continue;
				}
				if(line.matches("领域\\S{1,2}:\\S+")){
					System.out.println(line);
				}
				else if(line.matches("项目 \\d+：\\S+")){
					String projectName = line;
					while(!projectName.endsWith("申报指南")){
						line = br.readLine().trim();
						projectName = projectName.concat(line);
					}
					System.out.println("\t" + projectName);
				}
				else if(line.equals("一、技术类别")){
					System.out.println("\t\t" + line);

				}
				else if(line.equals("二、总体目标")){
					System.out.println("\t\t" + line);
				}
				else if(line.equals("三、课题设置情况")){
					System.out.println("\t\t" + line);
				}
				else if(line.equals("四、项目实施期限")){
					System.out.println("\t\t" + line);
				}
				else if(line.equals("五、课题内容")){
					System.out.println("\t\t" + line);
				}
				else if(line.matches("课题 \\d+：\\S+")){
					if(line.endsWith("主要研究内容：")){
						line = line.substring(0, line.length() - 7);
					}
					System.out.println("\t\t\t" + line);
				}
				else if(line.equals("六、支持经费限额")){
					System.out.println("\t\t" + line);
				}
			}
			br.close();

		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
