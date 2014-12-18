package org.nextprot.api.user.utils;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService.Operator;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class UserProteinListUtils {

	/**
	 * Apply the given operator to two user protein lists in a new instance of {@code UserProteinList}
	 *
	 * @param l1 first user protein list
	 * @param l2 second user protein list
	 * @param operator operator applied to operands
	 * @param name combined list name
	 * @param description combined list description
	 * @return a new user protein list combining l1 and l2
	 */
	public static UserProteinList combine(UserProteinList l1, UserProteinList l2, Operator operator, String name, String description) {

		NPreconditions.checkNotNull(l1, "The first user protein list should not be null");
		NPreconditions.checkNotNull(l2, "The second user protein list should not be null");
		NPreconditions.checkNotNull(operator, "The combine operator should not be null");
		NPreconditions.checkNotNull(name, "The user protein list name should not be null");

		NPreconditions.checkTrue(!l1.equals(l2), "Can't make combination with the same lists");

		Set<String> combined = new HashSet<String>();

		if (operator.equals(Operator.AND)) {
			combined.addAll(Sets.intersection(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		} else if (operator.equals(Operator.OR)) {
			combined = Sets.union(l1.getAccessionNumbers(), l2.getAccessionNumbers());
		} else if (operator.equals(Operator.NOT_IN)) {
			combined.addAll(Sets.difference(l1.getAccessionNumbers(), l2.getAccessionNumbers()));
		}

		if (combined.isEmpty())
			throw new NextProtException("The combined list is empty. Only combinations resulting on non-empty lists are saved.");

		UserProteinList combinedProteinList = new UserProteinList();
		combinedProteinList.setName(name);
		combinedProteinList.setDescription(description);
		combinedProteinList.setAccessions(combined);

		return combinedProteinList;
	}

	/**
	 * Extract the set of accession numbers from uploaded file.
	 * Only nextprot and uniprot accession numbers found in {@code validAccessionNumbers} are allowed.
	 *
	 * <p>uniprot accession numbers should be converted in nextprot (prefixed with "NX_")</p>
	 *
	 * @param reader the reader
	 * @param validAccessionNumbers a set of possible nextprot accession numbers
	 * @return a set of valid accession numbers
	 * @throws IOException if input exception occurred
	 * @throws EntryNotFoundException if at least one entry was not found in validAccessionNumbers
	 */
	public static Set<String> parseAccessionNumbers(Reader reader, Set<String> validAccessionNumbers) throws IOException {

		NPreconditions.checkNotNull(reader, "The reader should not be null");
		NPreconditions.checkNotNull(validAccessionNumbers, "The valid accession numbers should not be null");
		NPreconditions.checkTrue(!validAccessionNumbers.isEmpty(), "The valid accession numbers should not be null");

		Set<String> accessions = new HashSet<String>();

		BufferedReader br = new BufferedReader(reader);

		String line;
		int ln=0;
		while ((line = br.readLine()) != null) {

			String trimmed = line.trim().toUpperCase();

			if (line.charAt(0) != '#') {

				if (!trimmed.startsWith("NX_"))
					trimmed = "NX_" + trimmed;

				if (!validAccessionNumbers.contains(trimmed))
					throw new EntryNotFoundException("at line "+ln+": entry "+trimmed+" was not found");

				accessions.add(trimmed);
			}

			ln++;
		}

		return accessions;
	}

	/**
	 * Extract set of accession numbers from uploaded file.
	 * Only nextprot or uniprot accession numbers allowed.
	 *
	 * <p>uniprot accession numbers should be converted in nextprot (prefixed with "NX_")</p>
	 *
	 * @param file the uploaded file
	 * @return a set of accession numbers
	 * @throws IOException input exception occurred
	 */
	public static Set<String> parseAccessionNumbers(MultipartFile file, Set<String> validAccessionNumbers) throws IOException {

		NPreconditions.checkNotNull(file, "The uploaded file should not be null");

		InputStream inputStream = file.getInputStream();

		if (file.getInputStream() != null)
			return parseAccessionNumbers(new InputStreamReader(inputStream), validAccessionNumbers);

		return new HashSet<String>();
	}
}
