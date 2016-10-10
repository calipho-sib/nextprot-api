package org.nextprot.api.etl.utils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class TargetIsoformUtils {
	
	
	static Set<TargetIsoformStatementPosition> getIsosByDefault(List<String> isoformNames) {

		Set<TargetIsoformStatementPosition> isoSpecForAllByDefault = new TreeSet<>();

		isoformNames.forEach(i -> {
			isoSpecForAllByDefault.add(new TargetIsoformStatementPosition(i, IsoTargetSpecificity.BY_DEFAULT.name(), null));
		});
		
		return isoSpecForAllByDefault;
	}
		

	public static String getTargetIsoformForObjectSerialized(Statement subject, List<String> isoformNames) {
		return TargetIsoformSerializer.serializeToJsonString(getTargetIsoformForObject(subject, isoformNames));
	}

	
	/*public static String getTargetIsoformForPhenotypeSerialized(Statement subject, List<String> isoformNames, boolean isIsoSpecific, String isoSpecificName, String name) {
		return TargetIsoformSerializer.serializeToJsonString(getTargetIsoformForPhenotype(subject, isoformNames, isIsoSpecific, isoSpecificName, name));
	}
	
	public static Set<TargetIsoformStatementPosition> getTargetIsoformForPhenotype(Statement subject, List<String> isoformNames, boolean isIsoSpecific, String isoSpecificName, String name) {

		String targetIsoformForSubject = subject.getValue(StatementField.TARGET_ISOFORMS);
		Set<TargetIsoformStatementPosition> targetIsoformForPhenotype = null;

		// Only for Entry annotations
		if (targetIsoformForSubject == null) {
			throw new NextProtException("Can't map to isoforms if target isoforms is null for the subject");
		}

		if (isIsoSpecific) {
			Set<TargetIsoformStatementPosition> tispSubject = TargetIsoformSerializer.deSerializeFromJsonString(targetIsoformForSubject);
			//Take the iso from the subject and set it to be Specific and not propagate to others
			TargetIsoformStatementPosition tisp = new TargetIsoformStatementPosition(isoSpecificName, IsoTargetSpecificity.SPECIFIC.name(), name);
			targetIsoformForPhenotype = new TreeSet<TargetIsoformStatementPosition>(Arrays.asList((tisp)));
		}else {
			targetIsoformForPhenotype = getIsosByDefault(isoformNames);
		}
		
		return targetIsoformForPhenotype;

	} */

	
	public static Set<TargetIsoformStatementPosition> getTargetIsoformForObject(Statement subject, List<String> isoformNames) {

		String targetIsoformForSubject = subject.getValue(StatementField.TARGET_ISOFORMS);

		// Only for Entry annotations
		if (targetIsoformForSubject == null) {
			throw new NextProtException("Can't map to isofroms if target isoforms is null for the subject");
		}
		
		return getIsosByDefault(isoformNames);

	}
	
}
