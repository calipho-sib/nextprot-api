package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public class AnnotationUtils {
	
	public static List<Annotation> filterAnnotationsByCategory(List<Annotation> annotations, String categoryName){
		List<Annotation> annotationList = new ArrayList<Annotation>(); 
		for(Annotation a : annotations){
			System.out.println(a.getRdfPredicate());
			System.out.println(categoryName);
			
			if(a.getRdfPredicate().equals(categoryName)){
				System.out.println("Youuuu");
				annotationList.add(a);
			}
		}
		return annotationList;
	}

}
