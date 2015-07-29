package org.nextprot.api.user.utils;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService.Operator;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * This utility class provides methods operating on <tt>UserProteinList</tt> instances
 *
 * @author fnikitin
 * @author dteixeira
 */
public class UserProteinListUtils {

	/**
	 * Apply the given operator to two user protein lists in a new instance of
	 * {@code UserProteinList}
	 *
	 * @param l1 first user protein list
	 * @param l2 second user protein list
	 * @param operator operator applied to operands
	 * @param username combined list user name
	 * @param name combined list name
	 * @param description combined list description
	 * @return a new user protein list combining l1 and l2
	 */
	public static UserProteinList combine(UserProteinList l1, UserProteinList l2, Operator operator,
										  String username, String name, String description) {

		NPreconditions.checkNotNull(l1, "The first user protein list should not be null");
		NPreconditions.checkNotNull(l2, "The second user protein list should not be null");
		NPreconditions.checkNotNull(operator, "The combine operator should not be null");
		NPreconditions.checkNotNull(name, "The user protein list name should not be null");
		NPreconditions.checkNotNull(username, "The user protein list user name should not be null");

		NPreconditions.checkTrue(!l1.equals(l2), "Can't make combination with the same lists");

		Set<String> combined = new HashSet<>();

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
		combinedProteinList.setOwner(username);
		combinedProteinList.setDescription(description);
		combinedProteinList.setAccessions(combined);

		return combinedProteinList;
	}

	/**
	 * Extract the set of accession numbers from uploaded file. Only nextprot
	 * and uniprot accession numbers found in {@code validAccessionNumbers} are
	 * allowed.
	 *
	 * <p>
	 * uniprot accession numbers should be converted in nextprot (prefixed with
	 * "NX_")
	 * </p>
	 *
	 * @param reader
	 *            the reader
	 * @param validAccessionNumbers
	 *            a set of possible nextprot accession numbers
	 * @return a set of valid accession numbers
	 * @throws IOException
	 *             if input exception occurred
	 * @throws EntryNotFoundException
	 *             if at least one entry was not found in validAccessionNumbers
	 */
	public static Set<String> parseAccessionNumbers(Reader reader, Set<String> validAccessionNumbers) throws IOException {

		NPreconditions.checkNotNull(reader, "The reader should not be null");
		NPreconditions.checkNotNull(validAccessionNumbers, "The valid accession numbers should not be null");
		NPreconditions.checkTrue(!validAccessionNumbers.isEmpty(), "The valid accession numbers should not be null");

		Set<String> collector = new HashSet<>();

		BufferedReader br = new BufferedReader(reader);

		String line;
		int ln = 0;
		while ((line = br.readLine()) != null) {

			try {
				checkFormatAndCollectValidAccessionNumber(line, collector, validAccessionNumbers);
			} catch (EntryNotFoundException e) {

				throw new EntryNotFoundException("at line " + (ln + 1) + ": ", e.getEntry());
			}

			ln++;
		}

		return collector;
	}

	/**
	 * Extract set of accession numbers from uploaded file. Only nextprot or
	 * uniprot accession numbers allowed.
	 *
	 * <p>
	 * uniprot accession numbers should be converted in nextprot (prefixed with
	 * "NX_")
	 * </p>
	 *
	 * @param file
	 *            the uploaded file
	 * @return a set of accession numbers
	 * @throws IOException
	 *             input exception occurred
	 */
	public static Set<String> parseAccessionNumbers(MultipartFile file, Set<String> validAccessionNumbers) throws NextProtException{

		NPreconditions.checkNotNull(file, "The uploaded file should not be null");

		InputStream inputStream;
		try {
			inputStream = file.getInputStream();
			if (file.getInputStream() != null)
				return parseAccessionNumbers(new InputStreamReader(inputStream), validAccessionNumbers);

		} catch (IOException e) {
			throw new NextProtException(e);
		}

		return new HashSet<>();
	}

	/**
	 * Apply nextprot format on if needed and check for validity
	 *
 	 * @param uncheckedAccessionNumbers set of accession numbers to check
	 * @param validAccessionNumbers set of all valid entries
	 * @return a well formatted set of accession numbers
	 */
	public static Set<String> checkAndFormatAccessionNumbers(Collection<String> uncheckedAccessionNumbers, Set<String> validAccessionNumbers) {

		NPreconditions.checkNotNull(uncheckedAccessionNumbers, "The collection of accessions should not be null");
		NPreconditions.checkNotNull(validAccessionNumbers, "The valid accession numbers should not be null");
		NPreconditions.checkTrue(!validAccessionNumbers.isEmpty(), "The valid accession numbers should not be null");

		Set<String> collector = new HashSet<>(uncheckedAccessionNumbers.size());

		for (String uncheckedAccessionNumber : uncheckedAccessionNumbers) {

			checkFormatAndCollectValidAccessionNumber(uncheckedAccessionNumber, collector, validAccessionNumbers);
		}

		return collector;
	}

	/**
	 * Apply nextprot format on uncheckedAccessionNumber if needed, check for validity and give it to collector
	 *
	 * @param uncheckedAccessionNumber accession number to check for validity
	 * @param allNPAccessionNumbers set of all valid entries
	 * @param validAccessionNumberCollector a collector of all valid accession numbers
	 * @throws EntryNotFoundException if invalid accession number
	 */
	public static void checkFormatAndCollectValidAccessionNumber(String uncheckedAccessionNumber, Set<String> validAccessionNumberCollector, Set<String> allNPAccessionNumbers) {

		NPreconditions.checkNotNull(allNPAccessionNumbers, "The collector should not be null");

		String trimmed = uncheckedAccessionNumber.trim().toUpperCase();

		if (uncheckedAccessionNumber.charAt(0) != '#') {

			if (!trimmed.startsWith("NX_"))
				trimmed = "NX_" + trimmed;

			if (!allNPAccessionNumbers.contains(trimmed)) throw new EntryNotFoundException(trimmed);

			validAccessionNumberCollector.add(trimmed);
		}
	}
}
