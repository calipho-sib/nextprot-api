package org.nextprot.api.commons.bio;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Chromosome {

	Chr1("1"),
	Chr2("2"),
	Chr3("3"),
	Chr4("4"),
	Chr5("5"),
	Chr6("6"),
	Chr7("7"),
	Chr8("8"),
	Chr9("9"),
	Chr10("10"),
	Chr11("11"),
	Chr12("12"),
	Chr13("13"),
	Chr14("14"),
	Chr15("15"),
	Chr16("16"),
	Chr17("17"),
	Chr18("18"),
	Chr19("19"),
	Chr20("20"),
	Chr21("21"),
	Chr22("22"),
	ChrX("X"),
	ChrY("Y"),
	ChrMT("MT"),
	ChrUNK("unknown")
	;

	private static List<String> names;

	static {
		names = Arrays.stream(values())
				.map(Chromosome::getName)
				.collect(Collectors.toList());
	}

	String name;

	Chromosome(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static boolean exists(String name) {

		return names.contains(name);
	}

	public static List<String> getNames() {

		return names;
	}
}
