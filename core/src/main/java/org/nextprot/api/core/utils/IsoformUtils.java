package org.nextprot.api.core.utils;

import org.nextprot.api.core.domain.Isoform;

import java.util.Comparator;
import java.util.List;


/**
 * Utils about isoforms
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class IsoformUtils {

	/**
	 * Gets the isoform by its name
	 * @param isoforms
	 * @param isoformName
	 * @return
	 */
	public static Isoform getIsoformByIsoName(List<Isoform> isoforms, String isoformName) {
		//TODO the isoforms should be stored in a map at the level of the Entry
		for(Isoform iso : isoforms){
			if(iso.getUniqueName().replaceAll("NX_", "").equals(isoformName.replaceAll("NX_", "")))
				return iso;	
		}
		return null;
	}

	/**
	 * Compare isoform names by lexicographic order of master accession then numerically by isoform number.
	 */
	public static class ByIsoformUniqueNameComparator implements Comparator<String> {

		@Override
		public int compare(String uniqueName1, String uniqueName2) {

			if (uniqueName1.contains("-") && uniqueName2.contains("-")) {

				String[] accessionAndNumber1 = uniqueName1.split("-");
				String[] accessionAndNumber2 = uniqueName2.split("-");

				// compare lexicographically by master accessions
				int comp = accessionAndNumber1[0].compareTo(accessionAndNumber2[0]);

				// if equals -> compare numerically by isoform number
				if (comp == 0) {

					int isoNumber1 = Integer.parseInt(accessionAndNumber1[1]);
					int isoNumber2 = Integer.parseInt(accessionAndNumber2[1]);

					comp = isoNumber1 - isoNumber2;
				}

				return comp;
			}

			return uniqueName1.compareTo(uniqueName2);
		}
	}
}
