package com.hiekn.work;

import com.data0123.common.util.file.IReadFileUtil;
import com.data0123.common.util.file.IWriteFileUtil;
import com.data0123.common.util.file.ReadFileUtil;
import com.data0123.common.util.file.WriteFileUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明
 *
 * @author xiaohuqi@126.com  2017/10/24
 **/
public class BaikeDataProcess {
	private static final String WORK_DIR = "d:/work/nanrui/baike/";

	public static void main(String[] args) {

		IReadFileUtil readFileUtil = new ReadFileUtil();

		StringBuilder stringBuilder = new StringBuilder();

		List<String> list = new ArrayList<>();
		for(File file : new File(WORK_DIR + "json").listFiles()){
			String src = readFileUtil.readFileByCharSet(file.getAbsolutePath(), "utf-8");

//			System.out.println(src.length());
			JSONObject jsonObject = JSONObject.fromObject(src);

			JSONArray jsonArray = jsonObject.getJSONArray("entry");

			for(int i=0;i<jsonArray.size();i++){
				JSONObject article = jsonArray.getJSONObject(i);
				String title = article.getString("title");
				stringBuilder.append(title).append("\n");
//				System.out.println(title);
				JSONObject content = article.getJSONObject("content");
				if(content.get("para") instanceof JSONArray) {
					JSONArray paras = content.getJSONArray("para");
					for (Object para : paras) {
						if(para instanceof  String){
							stringBuilder.append(para).append("\n");
							list.add((String)para);
//							if(((String) para).matches("[\\s\\S]*（[a-z ]{2,}）[\\s\\S]*"))
//							System.out.println(para);
						}
//						System.out.println(para);
					}
				}
				else{
//					System.out.println(content.getString("para"));
					String para = content.getString("para");
					stringBuilder.append(para).append("\n");
					list.add(para);
//					if(para.matches("[\\s\\S]*（[a-z ]{2,}）[\\s\\S]*"))
//						System.out.println(para);
				}
				stringBuilder.append("\n\n");
//				System.out.println("\n");

//				stringBuilder.append(title).append("\n");
			}
			System.out.println(jsonArray.size());
		}

		for(String para : list){
			String[] ta = para.split("[，。、；：]");
			for(String sentense : ta) {
				if (sentense.matches("[\\s\\S]*（[a-z ]{2,}）[\\s\\S]*")) {
					System.out.println(sentense);
				}
			}
		}

		IWriteFileUtil writeFileUtil = new WriteFileUtil();
		writeFileUtil.writeFileByCharSet(stringBuilder.toString(), WORK_DIR + "text.txt", "utf-8");
	}

}
