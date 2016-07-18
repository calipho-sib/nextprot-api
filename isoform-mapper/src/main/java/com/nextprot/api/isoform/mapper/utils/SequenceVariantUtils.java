package com.nextprot.api.isoform.mapper.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceVariantUtils {

	public static boolean isIsoSpecific(String featureName) {
		if(featureName != null){
			return featureName.toLowerCase().contains("iso");
		}else return false;
	}

	/**
	 * Returns null if not found
	 * @param name
	 * @return
	 */
	public static Integer getIsoformNumber(String featureName) {
		
		if(isIsoSpecific(featureName)){

			Pattern p = Pattern.compile("\\w+-iso(\\d)-p.+");
			Matcher m = p.matcher(featureName);

			if (m.find()) {
				return Integer.valueOf(m.group(1));
			}

		}
		return null;


	}

}
