package org.nextprot.utils;

import org.nextprot.api.domain.exception.NextProtException;

public class NPreconditions {

	public static void checkNotNull(Object o, String message) {
		if (o == null)
			throw new NextProtException(message);
	}

	public static void checkTrue(boolean condition, String message) {
		if (!condition)
			throw new NextProtException(message);
	}
}
