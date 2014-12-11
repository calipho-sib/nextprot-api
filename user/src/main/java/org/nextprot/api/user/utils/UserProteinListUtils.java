package org.nextprot.api.user.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService.Operations;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Sets;

public class UserProteinListUtils {

	public static UserProteinList combine(UserProteinList l1, UserProteinList l2, Operations op, String name, String description) {

		Set<String> combined = new HashSet<String>();

		if (op.equals(Operations.AND)) {
			combined.addAll(Sets.intersection(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		} else if (op.equals(Operations.OR)) {
			combined = Sets.union(l1.getAccessionNumbers(), l2.getAccessionNumbers()).immutableCopy();
		} else if (op.equals(Operations.NOT_IN)) {
			combined.addAll(Sets.difference(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		}

		UserProteinList combinedProteinList = new UserProteinList();
		combinedProteinList.setName(name);
		combinedProteinList.setDescription(description);
		combinedProteinList.setAccessions(combined);

		return combinedProteinList;

	}

	public static Set<String> parseAccessionFromFile(MultipartFile file) throws IOException {

		InputStream inputStream = file.getInputStream();

		StringBuilder stringBuilder = new StringBuilder();

		if (file.getInputStream() != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			char[] charBuffer = new char[128];
			int bytesRead;

			while ((bytesRead = reader.read(charBuffer)) > 0) {
				stringBuilder.append(charBuffer, 0, bytesRead);
			}
		} else {
			stringBuilder.append("");
		}

		String[] readLines = stringBuilder.toString().split("\n");

		String trimmed;
		Set<String> accessions = new HashSet<String>();

		for (String line : readLines) {
			trimmed = line.trim();
			if (line.charAt(0) != '#') {
				accessions.add(trimmed);
			}
		}
		
		return accessions;
	}

}
