package org.nextprot.api.core.service.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

public class PhenotypeUtils {

    public static void updatePhenotypicEffectProperty(List<Annotation> annotations, String entryName) {

    	// iterate over phenotypic annotations
    	Map<String,List<String>> varEffects = new HashMap<>();
    	for (Annotation a: annotations) {
    		if (a.getAPICategory()==null) continue; // this should not accur but it DOES occurs in a mockito test i don't understand
    		System.out.println("api cat:" + a.getAPICategory());
    		if (a.getAPICategory().equals(AnnotationCategory.PHENOTYPIC_VARIATION) || 
    			a.getAPICategory().equals(AnnotationCategory.MAMMALIAN_PHENOTYPE)) {
    			if (a.getSubjectComponents()==null) continue;
    			// for each variant involved in the phenotypic variation
    			for (String id: a.getSubjectComponents()) {
    				// add its effect in its list of effects
    				if (! varEffects.containsKey(id)) varEffects.put(id,  new ArrayList<String>());
    				String effect = a.getDescription();
    				int pos = effect.indexOf(") ");
    				if (pos != -1) effect = effect.substring(pos+2);
    				// if the effect involves another variant, modify the effect
    				if (a.getSubjectComponents().size()>1) effect = effect + " when associated with another variant";
    				varEffects.get(id).add(effect);
    			}
    		}
    	}
    	
//    	for (String k: varEffects.keySet()) {
//    		int count = varEffects.get(k).size();
//    		int idx = 0;
//    		for (String eff: varEffects.get(k)) {
//    			idx++;
//    			System.out.println("var : " + k + " has effect " + idx + " /" + count + " : " + eff);
//    		}
//    	}
    	
    	// now for each variant
    	for (Annotation a: annotations) {
    		if (a.getAPICategory()==null) continue; // this should not accur but it DOES occurs in a mockito test i don't understand
    		if (a.getAPICategory().equals(AnnotationCategory.VARIANT) || 
	    		a.getAPICategory().equals(AnnotationCategory.MUTAGENESIS)) {
    			// for which we have collected one or more effects
    			if (varEffects.containsKey(a.getAnnotationHash())) {
    				List<String> effects = varEffects.get(a.getAnnotationHash());
    				// set the default property value
    				String label = "has effects";
    				// replace label with effect if there is only a single effect for this variant
    				if (effects.size()==1) label = effects.get(0);
    				// capitalize first char
    				label = label.substring(0,1).toUpperCase() + label.substring(1);
    				// build corresponding property
    				AnnotationProperty p = new AnnotationProperty();
    				p.setAnnotationId(a.getAnnotationId());
    				p.setName("phenotypic effect");
    				p.setValue(label);
    				// add property to variant to be used by UI
    				a.addProperty(p);
    			}
    		}
    	}

    }

}
