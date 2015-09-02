package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioEntryTest {

    @Test
    public void test() {

        BioEntry bioEntry = new BioEntry();
        bioEntry.setAccession("NX_P01308");

        Assert.assertEquals("neXtProt", bioEntry.getDatabase());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ENTRY, bioEntry.getBioType());
        Assert.assertEquals("NX_P01308", bioEntry.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioEntry.getResourceType());
    }

    @Test
    public void testSerialization() throws Exception {

        BioEntry bioEntry = new BioEntry();
        bioEntry.setAccession("NX_P01308");
        bioEntry.setContent(new Entry(""));

        serialize(bioEntry, "tempdata.ser");
        BioEntry be = deserialize("tempdata.ser", BioEntry.class);

        Assert.assertEquals("neXtProt", be.getDatabase());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ENTRY, be.getBioType());
        Assert.assertEquals("NX_P01308", be.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, be.getResourceType());
        Assert.assertNull(be.getContent());

        // Clean up the file
        new File("tempdata.ser").delete();
    }

    private static void serialize(Serializable object, String filename) throws IOException {

        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    private static <T extends Serializable> T deserialize(String filename, Class<T> clazz) throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T be = clazz.cast(ois.readObject());
        ois.close();
        fis.close();

        return be;
    }
}