package org.nextprot.api.commons.dbunit;

import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * A FlatXMLDataSet that ignores dtd metadata file.
 *
 * Created by fnikitin on 16/10/14.
 */
public class DTDIgnoredFlatXMLDataSet extends FlatXmlDataSetLoader {

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

        builder.setDtdMetadata(false);

        InputStream is = resource.getInputStream();

        try {

            return builder.build(is);
        } finally {

            is.close();
        }
    }
}
