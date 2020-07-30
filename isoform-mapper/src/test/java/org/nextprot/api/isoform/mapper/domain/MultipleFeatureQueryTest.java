package org.nextprot.api.isoform.mapper.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.isoform.mapper.domain.query.MultipleFeatureQuery;

import java.util.*;

public class MultipleFeatureQueryTest {

    @Test
    public void testEmptyFeatureList() throws Exception {

        MultipleFeatureQuery query = new MultipleFeatureQuery();

        Assert.assertNull(query.getAccession());
        Assert.assertNull(query.getFeatureType());
        Assert.assertTrue(query.getFeatureList().isEmpty());
        Assert.assertTrue(query.getFeatureMaps().isEmpty());
    }

    @Test
    public void getFeatureListNoAccession() throws Exception {

        MultipleFeatureQuery query = new MultipleFeatureQuery();

        query.setFeatureList(Arrays.asList("SCN11A-p.Leu1158Pro", "SCN11A-p.Leu1158Pro"));
        query.setFeatureType(AnnotationCategory.VARIANT.getApiTypeName());

        Assert.assertNull(query.getAccession());
        Assert.assertEquals(Arrays.asList("SCN11A-p.Leu1158Pro", "SCN11A-p.Leu1158Pro"), query.getFeatureList());
        Assert.assertEquals("Variant", query.getFeatureType());
    }

    @Test
    public void getFeatureListWithAccession() throws Exception {

        MultipleFeatureQuery query = new MultipleFeatureQuery();

        query.setFeatureList(Arrays.asList("SCN11A-p.Leu1158Pro", "SCN11A-p.Leu1158Pro"));
        query.setFeatureType(AnnotationCategory.VARIANT.getApiTypeName());
        query.setAccession("NX_Q9UI33");

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals(Arrays.asList("SCN11A-p.Leu1158Pro", "SCN11A-p.Leu1158Pro"), query.getFeatureList());
        Assert.assertEquals("Variant", query.getFeatureType());
    }

    @Test
    public void getFeatureMapList() throws Exception {

        MultipleFeatureQuery query = new MultipleFeatureQuery();

        List<Map<String, String>> mapList = new ArrayList<>();
        mapList.add(newMap("SCN11A-p.Leu1158Pro", "NX_Q9UI33"));

        query.setFeatureMaps(mapList);
        query.setFeatureType(AnnotationCategory.VARIANT.getApiTypeName());

        Assert.assertEquals(null, query.getAccession());
        Assert.assertEquals(1, query.getFeatureMaps().size());
        Assert.assertEquals("Variant", query.getFeatureType());
    }

    private Map<String, String> newMap(String feature, String accession) {

        Map<String, String> map = new HashMap<>();

        map.put("feature", feature);
        map.put("accession", accession);

        return map;
    }

    /*
	{
		"featureType": "variant",
		"featureList": [
			"SCN11A-p.Leu1158Pro",
			"SCN11A-p.Leu1158Pro"
		],
		"accession": "NX_Q9UI33" // feature list accession: optional if deducible from gene defined in feature
	}

	or

	{
		"featureType": "variant",
		"featureMaps": [
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			},
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			}
		]
	}

	or both

	{
		"featureType": "variant",
		"featureList": [
			"SCN11A-p.Leu1158Pro",
			"SCN11A-p.Leu1158Pro"
		],
		"accession": "NX_Q9UI33",
		"featureMaps": [
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33" // optional if deducible from gene defined in feature
			},
			{
				"feature": "SCN11A-p.Leu1158Pro",
				"accession": "NX_Q9UI33"
			}
		]
	}
	 */
}