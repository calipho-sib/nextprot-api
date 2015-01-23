package org.nextprot.api.commons.bio;

import org.biojava.bio.proteomics.IsoelectricPointCalc;
import org.biojava.bio.seq.ProteinTools;

/**
 * @author alexandre.masselot@genebio.com
 * 
 */
public class DescriptorPI  {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genebio.nextprot.tools.sequence.descriptors.SequenceDescriptor#compute
	 * (java.lang.String)
	 */
	public synchronized static Double compute(String sequence) {
		try {
			return new Double(IsoelectricPointCalc.getIsoelectricPoint(ProteinTools.createProtein(sequence)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
