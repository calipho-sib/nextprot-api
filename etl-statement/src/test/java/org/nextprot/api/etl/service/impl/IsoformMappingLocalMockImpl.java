package org.nextprot.api.etl.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.service.IsoMapperDictionary;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.FeatureQuerySuccess;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl.IsoformFeatureResult;
import org.nextprot.api.isoform.mapper.service.IsoformMappingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IsoformMappingLocalMockImpl implements IsoformMappingService {

	@Override
	public FeatureQueryResult validateFeature(SingleFeatureQuery featureQuery) {
		throw new NextProtException("Not implemented yet in local mock");
	}

	@Override
	public FeatureQueryResult propagateFeature(SingleFeatureQuery featureQuery) {

		IsoMapperDictionary imd = new IsoMapperDictionary();
		String json = imd.getIsoMapperResponse("propagate" + "-" + featureQuery.getFeatureType() + "-" + featureQuery.getFeature());
		return new FeatureQuerySuccessResultMock(json);

	}

	private static class FeatureQuerySuccessResultMock implements FeatureQuerySuccess {

		private static final long serialVersionUID = 1L;
		private Map<String, IsoformFeatureResult> data = null;
		private SingleFeatureQuery query = null;

		@SuppressWarnings("unchecked")
		public FeatureQuerySuccessResultMock(String json) {

			super();
			data = new HashMap<>();

			ObjectMapper om1 = new ObjectMapper();
			Map<String, Object> map;
			try {

				map = om1.readValue(json, Map.class);
				Map<String, Object> dataResult = (Map<String, Object>) map.get("data");

				ObjectMapper om2 = new ObjectMapper();
				om2.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				query = om2.readValue(om2.writeValueAsString(map.get("query")), SingleFeatureQuery.class);

				for (Map.Entry<String, Object> entry : dataResult.entrySet()) {
					Object o = entry.getValue();
					data.put(entry.getKey(), om2.readValue(om2.writeValueAsString(o), IsoformFeatureResult.class));
				}
			} catch (IOException e) {
				throw new NextProtException(e);
			}

		}

		public Map<String, IsoformFeatureResult> getData() {
			return data;
		}

		public SingleFeatureQuery getQuery() {
			return query;
		}
	}

	public static void main(String[] args) {
		IsoformMappingLocalMockImpl mi = new IsoformMappingLocalMockImpl();
		FeatureQueryResult fq = mi.propagateFeature(new SingleFeatureQuery("SCN9A-iso3-p.Met932Leu", "variant", null));
	}

}