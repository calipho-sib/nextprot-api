package org.nextprot.api.commons.exception;

import java.util.Collection;

public class NPreconditions {
	
	private NPreconditions(){}

	public static void checkNull(Object o, String message) {
		if (o != null){
			throw new NextProtException(message);
		}
	}
	
	
	public static void checkNotEmpty(Collection<?> c, String message) {
		if (c.isEmpty()){
			throw new NextProtException(message);
		}
	}

	public static void checkNotNull(Object o, String message) {
		if (o == null){
			throw new NextProtException(message);
		}
	}

	public static void checkTrue(boolean condition, String message) {
		if (!condition){
			throw new NextProtException(message);
		}
	}
}
