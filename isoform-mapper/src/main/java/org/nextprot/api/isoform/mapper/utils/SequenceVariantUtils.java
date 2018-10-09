package org.nextprot.api.isoform.mapper.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceVariantUtils {

    // TODO: It is false as isoform name can be of "non-iso" format (ex: GTBP-N for NX_P52701)
	public static boolean isIsoSpecific(String featureName) {
		if(featureName != null){
			return featureName.toLowerCase().contains("iso");
		}else return false;
	}

    // TODO: Also false for the same reason as above
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
