package org.nextprot.api.web.xstream.converters;

import org.nextprot.api.core.domain.Isoform;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

//@NextprotConverter
public class IsoformConverter implements Converter {

        public IsoformConverter() {
                super();
        }

        public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
                return Isoform.class.isAssignableFrom(clazz);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        	Isoform isoform = (Isoform) value;
        	
            writer.startNode("sequence");
            writer.addAttribute("value", isoform.getSequence());
            writer.endNode();

        }

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return null;
		}


}
