package org.nextprot.api.commons.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = 8435227163614921614L;
	private A first;
	private B second;

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

	public static <A, B> Pair<A, B> pair(A first, B second) {
		return new Pair<A, B>(first, second);
	}

	public static <A, B> Map<A, B> map(Pair<A, B>... pairs) {
		Map<A, B> map = new HashMap<A, B>(pairs.length);
		for (Pair<A, B> pair : pairs)
			map.put(pair.getFirst(), pair.getSecond());

		return map;
	}

	public static <A, B> Pair<B, A> invert(Pair<A, B> oldpair) {
		return Pair.pair(oldpair.getSecond(), oldpair.getFirst());
	}

	public static <A, B> Map<A, List<B>> multimap(Pair<A, B>... pairs) {
		Map<A, List<B>> multimap = new HashMap<A, List<B>>();

		for (Pair<A, B> pair : pairs) {
			if (!multimap.containsKey(pair.getFirst()))
				multimap.put(pair.getFirst(), new ArrayList<B>());
			multimap.get(pair.getFirst()).add(pair.getSecond());
		}
		return multimap;
	}

	public static <A, B> boolean equals(Pair<A, B> p1, Pair<A, B> p2) {
		if (p1.getFirst().equals(p2.getSecond())
				&& p1.getFirst().equals(p2.getSecond()))
			return true;
		return false;
	}

	public String toString() {
		return "(" + this.first + ":" + this.second + ")";
	}

	public static <T, S> Pair<T, S> create(T first, S second) {
		return new Pair<T, S>(first, second);
	}

}
