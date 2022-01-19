package org.nextprot.api.core.service.dbxref.conv;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.DbXref.EnsemblInfos;

import java.util.List;

public class EnsemblXrefPropertyConverterTest {

    
    @Test
    public void testConvertFromToProperty() {
    	
    	EnsemblInfos info1 = new EnsemblInfos(1234L, 5678L, 10L, "ENST1", "ENSG2", "ENSP3", "NX_A00001-1");
    	
    	DbXref.DbXrefProperty prop = info1.toDbXrefProperty();
    	Assert.assertEquals("nxmap", prop.getName());
    	Assert.assertEquals(new Long(1234), prop.getDbXrefId());
    	Assert.assertEquals(new Long(IdentifierOffset.XREF_PROPERTY_OFFSET+ 5678), prop.getPropertyId());
    	Assert.assertEquals("ENST1|ENSG2|ENSP3|NX_A00001-1|GOLD", prop.getValue());

    	EnsemblInfos info2 = new EnsemblInfos(prop);
    	
    	Assert.assertEquals(info1.getEnsg(), info2.getEnsg());
    	Assert.assertEquals(info1.getEnst(), info2.getEnst());
    	Assert.assertEquals(info1.getEnsp(), info2.getEnsp());
    	Assert.assertEquals(info1.getIso(), info2.getIso());
    	Assert.assertEquals(info1.getEnstIsoMapId(), info2.getEnstIsoMapId());
    	Assert.assertEquals(info1.getEnstIsoMapQual(), info2.getEnstIsoMapQual());
    	Assert.assertEquals(info1.getEnstXrefId(), info2.getEnstXrefId());

    }

}