package com.hiekn.work;

import com.data0123.common.util.file.IReadFileUtil;
import com.data0123.common.util.file.IWriteFileUtil;
import com.data0123.common.util.file.ReadFileUtil;
import com.data0123.common.util.file.WriteFileUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * 类说明
 *
 * @author xiaohuqi@126.com  2017/10/24
 **/
public class BaikeDataProcess {

	public static void main(String[] args) {

		IReadFileUtil readFileUtil = new ReadFileUtil();

		StringBuilder stringBuilder = new StringBuilder();
		for(File file : new File("Y:\\work\\guowan\\baike").listFiles()){
			String src = readFileUtil.readFileByCharSet(file.getAbsolutePath(), "utf-8");

//			System.out.println(src.length());
			JSONObject jsonObject = JSONObject.fromObject(src);

			JSONArray jsonArray = jsonObject.getJSONArray("entry");

			for(int i=0;i<jsonArray.size();i++){
				String title = jsonArray.getJSONObject(i).getString("title");
				stringBuilder.append(title).append("\n");
				System.out.println(title);
			}
			System.out.println(jsonArray.size());
		}

		IWriteFileUtil writeFileUtil = new WriteFileUtil();
		writeFileUtil.writeFileByCharSet(stringBuilder.toString(), "Y:\\work\\guowan\\baike\\title.txt", "utf-8");
	}

}
