package org.nextprot.api.web.service.impl.writer;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class EntryPEFFStreamWriterTest extends WebIntegrationBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
    private EntryReportStatsService entryReportStatsService;

    @Test
    public void testPeffExportWithRightTermNameForMOD_00156() throws Exception {
        // new name in psi-mod.obo from
        // https://raw.githubusercontent.com/HUPO-PSI/psi-mod-CV/master/PSI-MOD.obo
        // copied as a resource in /nextprot-api-core/src/main/resources/org/nextprot/api/core/service/impl/peff/PSI-MOD.obo

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EntryPEFFStreamWriter exporter = new EntryPEFFStreamWriter(out, entryBuilderService, entryReportStatsService);
        exporter.write(Arrays.asList("NX_O75106"));
        Assert.assertTrue(out.toString().contains("MOD:00156|L-2',4',5'-topaquinone"));        
        exporter.close();
    }
    
    @Test
    public void testPeffExportWithRightTermNameFor_MOD_00186() throws Exception {
        // new name in psi-mod.obo from
        // https://raw.githubusercontent.com/HUPO-PSI/psi-mod-CV/master/PSI-MOD.obo
        // copied as a resource in /nextprot-api-core/src/main/resources/org/nextprot/api/core/service/impl/peff/PSI-MOD.obo

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EntryPEFFStreamWriter exporter = new EntryPEFFStreamWriter(out, entryBuilderService, entryReportStatsService);
        exporter.write(Arrays.asList("NX_P01266"));
        Assert.assertTrue(out.toString().contains("MOD:00186|3,3',5-triiodo-L-thyronine"));

        exporter.close();
    }
    
    
    @Test
    public void testPeffExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EntryPEFFStreamWriter exporter = new EntryPEFFStreamWriter(out, entryBuilderService, entryReportStatsService);

        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"));

        Assert.assertTrue(out.toString().contains(">nxp:NX_P06213-1"));
        Assert.assertTrue(out.toString().contains(">nxp:NX_P01308-1"));
        Assert.assertTrue(out.toString().contains("MATGGRRGAAAAPLLVAVAALLLGAAGHLYPGEVCPGMDIRNNLTRLHELENCSVIEGHL\r\n" +
                "QILLMFKTRPEDFRDLSFPKLIMITDYLLLFRVYGLESLKDLFPNLTVIRGSRLFFNYAL\r\n" +
                "VIFEMVHLKELGLYNLMNITRGSVRIEKNNELCYLATIDWSRILDSVEDNYIVLNKDDNE\r\n" +
                "ECGDICPGTAKGKTNCPATVINGQFVERCWTHSHCQKVCPTICKSHGCTAEGLCCHSECL\r\n" +
                "GNCSQPDDPTKCVACRNFYLDGRCVETCPPPYYHFQDWRCVNFSFCQDLHHKCKNSRRQG\r\n" +
                "CHQYVIHNNKCIPECPSGYTMNSSNLLCTPCLGPCPKVCHLLEGEKTIDSVTSAQELRGC\r\n" +
                "TVINGSLIINIRGGNNLAAELEANLGLIEEISGYLKIRRSYALVSLSFFRKLRLIRGETL\r\n" +
                "EIGNYSFYALDNQNLRQLWDWSKHNLTITQGKLFFHYNPKLCLSEIHKMEEVSGTKGRQE\r\n" +
                "RNDIALKTNGDQASCENELLKFSYIRTSFDKILLRWEPYWPPDFRDLLGFMLFYKEAPYQ\r\n" +
                "NVTEFDGQDACGSNSWTVVDIDPPLRSNDPKSQNHPGWLMRGLKPWTQYAIFVKTLVTFS\r\n" +
                "DERRTYGAKSDIIYVQTDATNPSVPLDPISVSNSSSQIILKWKPPSDPNGNITHYLVFWE\r\n" +
                "RQAEDSELFELDYCLKGLKLPSRTWSPPFESEDSQKHNQSEYEDSAGECCSCPKTDSQIL\r\n" +
                "KELEESSFRKTFEDYLHNVVFVPRKTSSGTGAEDPRPSRKRRSLGDVGNVTVAVPTVAAF\r\n" +
                "PNTSSTSVPTSPEEHRPFEKVVNKESLVISGLRHFTGYRIELQACNQDTPEERCSVAAYV\r\n" +
                "SARTMPEAKADDIVGPVTHEIFENNVVHLMWQEPKEPNGLIVLYEVSYRRYGDEELHLCV\r\n" +
                "SRKHFALERGCRLRGLSPGNYSVRIRATSLAGNGSWTEPTYFYVTDYLDVPSNIAKIIIG\r\n" +
                "PLIFVFLFSVVIGSIYLFLRKRQPDGPLGPLYASSNPEYLSASDVFPCSVYVPDEWEVSR\r\n" +
                "EKITLLRELGQGSFGMVYEGNARDIIKGEAETRVAVKTVNESASLRERIEFLNEASVMKG\r\n" +
                "FTCHHVVRLLGVVSKGQPTLVVMELMAHGDLKSYLRSLRPEAENNPGRPPPTLQEMIQMA\r\n" +
                "AEIADGMAYLNAKKFVHRDLAARNCMVAHDFTVKIGDFGMTRDIYETDYYRKGGKGLLPV\r\n" +
                "RWMAPESLKDGVFTTSSDMWSFGVVLWEITSLAEQPYQGLSNEQVLKFVMDGGYLDQPDN\r\n" +
                "CPERVTDLMRMCWQFNPKMRPTFLEIVNLLKDDLHPSFPEVSFFHSEENKAPESEELEME\r\n" +
                "FEDMENVPLDRSSHCQREEAGGRDGGSSLGFKRSYEEHIPYTHMNGGKKNGRILTLPRSN\r\n" +
                "PS\r\n"));
        Assert.assertFalse(out.toString().contains("\\DbUniqueId"));
        
        exporter.close();
    }
}