package org.nextprot.api.commons.exception;


public class NPreconditions {

	public static void checkNull(Object o, String message) {
		if (o != null){
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
