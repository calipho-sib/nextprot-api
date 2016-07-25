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
	public static String getIsoformName(String featureName) {
		
		if(isIsoSpecific(featureName)){

			Pattern p = Pattern.compile("\\w+-iso(\\w+)-p.+");
			Matcher m = p.matcher(featureName);

			if (m.find()) {
				return String.valueOf(m.group(1));
			}

		}
		return null;


	}

}
