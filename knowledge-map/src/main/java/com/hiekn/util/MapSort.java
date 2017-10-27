package com.hiekn.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author xiaohuqi@126.com 2017-10-25 19:59
 **/
public class MapSort {

	public void sortIntegerValueMap(Map<String, Integer> map, List<String> keyList, List<Integer> valueList){
		valueList.addAll(map.values());
		Collections.sort(valueList);

		for(String key : map.keySet()){
			int score = map.get(key);
			int i = 0;
			int size = valueList.size();
			for(;i<size;i++){
				if(score >= valueList.get(i)){
					break;
				}
			}
			keyList.add(i, key);
			valueList.add(i, map.get(key));
		}
	}

	public void sortDoubleValueMap(Map<String, Double> map, List<String> keyList, List<Double> valueList){
		valueList.addAll(map.values());
		Collections.sort(valueList);

		for(String key : map.keySet()){
			int i=0;
			for(;i<valueList.size();i++){
				if(map.get(key) >= valueList.get(i)){
					break;
				}
			}
			keyList.add(i, key);
			valueList.add(i, map.get(key));
		}
	}
}
