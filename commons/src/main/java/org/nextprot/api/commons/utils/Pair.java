package org.nextprot.api.commons.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// TODO: nothing is been serialized there as A and B are not explicitly Serializable
public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 8435227163614921614L;
	private final A first;
	private final B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	/**
	 * @deprecated call Pair.create() instead
	 */
	@Deprecated
	public static <A, B> Pair<A, B> pair(A first, B second) {
		return new Pair<>(first, second);
	}

	public static <A, B> Map<A, B> map(Pair<A, B>... pairs) {
		Map<A, B> map = new HashMap<>(pairs.length);
		for (Pair<A, B> pair : pairs)
			map.put(pair.getFirst(), pair.getSecond());

		return map;
	}

	@Override
	public String toString() {
		return "(" + this.first + ":" + this.second + ")";
	}

	public static <T, S> Pair<T, S> create(T first, S second) {
		return new Pair<>(first, second);
	}

}
