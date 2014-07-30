package org.nextprot.api.commons.bio;


import java.util.HashMap;
import java.util.Scanner;

/**
 * return the molecular weight corresponding to a sequence
 * 
 * @author alexandre.masselot@genebio.com, copied by pam today aaa
 * 
 */
public class DescriptorMass {
	static final String                         massDefText = "A,71.03711,71.0788;C,103.00919,103.1388;D,115.02694,115.0886;E,129.04259,129.1155;F,147.06841,147.1766;G,57.02146,57.0519;H,137.05891,137.1411;I,113.08406,113.1594;K,128.09496,128.1741;L,113.08406,113.1594;M,131.04049,131.1926;N,114.04293,114.1038;P,97.05276,97.1167;Q,128.05858,128.1307;R,156.10111,156.1875;S,87.03203,87.0782;T,101.04768,101.1051;U,150.953636,150.0388;V,99.06841,99.1326;W,186.07931,186.2132;Y,163.06333,163.1760";
	static HashMap<MassType, double[]> massDefList;
	static final double                         H2O_mono    = 18.01056;
	static final double                         H2O_avg     = 18.01524;

	public static enum MassType { MONOISOTOPIC, AVERAGE };

	private static MassType massType=MassType.AVERAGE;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.genebio.nextprot.tools.sequence.descriptors.SequenceDescriptor#compute
	 * (java.lang.String)
	 */
	public static Double compute(String sequence) {
		if (massDefList == null) {
			initMassDefList();
		}

		double m=0.;
		switch (massType) {
			case MONOISOTOPIC:
				m=H2O_mono;
				break;
			case AVERAGE:
				m=H2O_avg;
				break;
		}
		double[] massList=massDefList.get(massType);
		for(char aa:sequence.toCharArray()){
			if(aa == '*')
				aa='U';
			m+=massList[aa2int(aa)];
		}
		
		return m;
	}

	/**
	 * convert the String massDefText into two list
	 */
	private static void initMassDefList() {
		massDefList = new HashMap<MassType, double[]>();
		massDefList.put(MassType.MONOISOTOPIC, new double[26]);
		massDefList.put(MassType.AVERAGE, new double[26]);

		Scanner scanList = new Scanner(massDefText);
		scanList.useDelimiter(";");
		while (scanList.hasNext()) {
			Scanner scanAA = new Scanner(scanList.next());
			scanAA.useDelimiter(",");
			String aa = scanAA.next();
			double massIso = scanAA.nextDouble();
			double massAvg = scanAA.nextDouble();
			int iaa = aa2int(aa.charAt(0));

			massDefList.get(MassType.MONOISOTOPIC)[iaa]=massIso;
			massDefList.get(MassType.AVERAGE)[iaa]=massAvg;
		}
	}
	
	static int aa2int(char aa){
		return ((int) aa) - ((int) 'A');
	}

	public MassType getMassType() {
		return massType;
	}

	public void setMassType(MassType massType) {
		if (DescriptorMass.massType == massType) return;
		DescriptorMass.massType = massType;
	}

}
