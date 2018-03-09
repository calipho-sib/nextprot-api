package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

import java.util.ArrayList;
import java.util.List;

import static org.nextprot.api.core.utils.exon.ExonCategorizerTest.createMockExonList;

/**
 *
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptExonsAnalyserTest {

    @Test
    public void testanalyseInfosNX_Q9Y281_3() throws Exception {

        List<Exon> exons = createMockExonList(134, 286, 1263, 1570, 1688, 1764, 1847, 4437);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MASGVTVNDEVIKVFNDMKVRKSSTQEEIKKRKKAVLFCLSDDKRQIIVEEAKQILVGDIGDTVEDPYTSFVKLLPLNDCRYALYDATYETKESKKEDLVFIFWAPESAPLKSKMIYASSKDAIKKKFTGIKHEWQVNGLDDIKDRSTLGEKLGGNVVVSLEGKPL", 284, 1956, exons);

        /**
         name	positions	gene_id	sequence
         NX_Q9Y281-3	[1311=1570, 1688=1764, 1847=1956]	266594	MKVRKSSTQEEIKKRKKAVLFCLSDDKRQIIVEEAKQILVGDIGDTVEDPYTSFVKLLPLNDCRYALYDATYETKESKKEDLVFIFWAPESAPLKSKMIYASSKDAIKKKFTGIKHEWQVNGLDDIKDRSTLGEKLGGNVVVSLEGKPL
         iso	transcript	exons	accession
         NX_Q9Y281-3	NX_ENST00000298159	[gene-pos=[134,286], gene-pos=[1263,1570], gene-pos=[1688,1764], gene-pos=[1847,4437]]	ENST00000298159
         */

        Assert.assertEquals(4, collector.size());
        assertInfoEquals(collector.getInfoAt(0), 'M', 1, 0, 'M', 1, 0, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(1), 'A', 2, 0, 'W', 104, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(2), 'W', 104, 2, 'G', 130, 1, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'G', 130, 1, 'L', 166, 0, ExonCategory.STOP);

    }

    @Test
    public void testanalyseInfosNX_Q96M20() throws Exception {

        List<Exon> exons = createMockExonList(34,224,4040,4177,7360,7413,11870,12033,15393,15549,16038,16189,18806,18944,26449,26563,39708,39885,42548,42668,61768,62080);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MRRHMVTYAWQLLKKELGLYQLAMDIIIMIRVCKMFRQGLRGFREYQIIETAHWKHPIFSFWDKKMQSRVTFDTMDFIAEEGHFPPKAIQIMQKKPSWRTEDEIQAVCNILQVLDSYRNYAEPLQLLLAKVMRFERFGRRRVIIKKGQKGNSFYFIYLGTVAITKDEDGSSAFLDPHPKLLHKGSCFGEMDVLHASVRRSTIVCMEETEFLVVDREDFFANKLDQEVQKDAQYRFEFFRKMELFASWSDEKLWQLVAMAKIERFSYGQLISKDFGESPFIMFISKGSCEVLRLLDLGASPSYRRWIWQHLELIDGRPLKTHLSEYSPMERFKEFQIKSYPLQDFSSLKLPHLKKAWGLQGTSFSRKIRTSGDTLPKMLGPKIQSRPAQSIKCAMINIKPGELPKEAAVGAYVKVHTVEQGEIL", 174, 61767, exons);

        /**
         name	positions	gene_id	sequence
         NX_Q96M20-3	[174=224, 4040=4177, 7360=7413, 11870=12033, 15393=15549, 16038=16189, 18806=18944, 26449=26563, 39708=39885, 42548=42668, 61768=61767]	507928	MRRHMVTYAWQLLKKELGLYQLAMDIIIMIRVCKMFRQGLRGFREYQIIETAHWKHPIFSFWDKKMQSRVTFDTMDFIAEEGHFPPKAIQIMQKKPSWRTEDEIQAVCNILQVLDSYRNYAEPLQLLLAKVMRFERFGRRRVIIKKGQKGNSFYFIYLGTVAITKDEDGSSAFLDPHPKLLHKGSCFGEMDVLHASVRRSTIVCMEETEFLVVDREDFFANKLDQEVQKDAQYRFEFFRKMELFASWSDEKLWQLVAMAKIERFSYGQLISKDFGESPFIMFISKGSCEVLRLLDLGASPSYRRWIWQHLELIDGRPLKTHLSEYSPMERFKEFQIKSYPLQDFSSLKLPHLKKAWGLQGTSFSRKIRTSGDTLPKMLGPKIQSRPAQSIKCAMINIKPGELPKEAAVGAYVKVHTVEQGEIL
         iso	transcript	exons	accession
         NX_Q96M20-3	NX_ENST00000538900	[gene-pos=[34,224], gene-pos=[4040,4177], gene-pos=[7360,7413], gene-pos=[11870,12033], gene-pos=[15393,15549], gene-pos=[16038,16189], gene-pos=[18806,18944], gene-pos=[26449,26563], gene-pos=[39708,39885], gene-pos=[42548,42668], gene-pos=[61768,62080]]	ENST00000538900

         */

        Assert.assertEquals(11, collector.size());
        assertInfoEquals(collector.getInfoAt(0), 'M', 1, 0, 'L', 17, 0, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(1), 'G', 18, 0, 'D', 63, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(2), 'K', 64, 0, 'E', 81, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'G',  82, 0, 'R', 136, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(4), 'R', 136, 2, 'G', 188, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(5), 'E', 189, 0, 'R', 239, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(6), 'R', 239, 2, 'K', 285, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(7), 'G', 286, 0, 'E', 324, 1, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(8), 'E', 324, 1, 'Q', 383, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(9), 'Q', 383, 2, 'L', 423, 0, ExonCategory.CODING);
        Assert.assertEquals(ExonCategory.STOP_ONLY, collector.getInfoAt(10).getExonCategory());
        Assert.assertEquals(null, collector.getInfoAt(10).getFirstAA());
        Assert.assertEquals(null, collector.getInfoAt(10).getLastAA());

    }

    @Test
    public void testanalyseInfosNX_P20592_2() throws Exception {

        List<Exon> exons = createMockExonList(8143, 8407, 14894, 15213, 15847, 16039, 20468, 20622, 33645, 33843, 35671, 35749, 36955, 37000);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MSKAHKPWPYRRRSQFSSRKYLKKEMNSFQQQPPPFGTVPPQMMFPPNWQGAEKDAAFLAKDFNFLTLNNQPPPGNRSQPRAMGPENNLYSQYEQKVRPCIDLIDSLRALGVEQDLALPAIAVIGDQSSGKSSVLEALSGVALPRGSAQNVMAGNGRGISHELISLEITSPEVPDLTIIDLPGITRVAVDNQPRDIGLQVS", 14965, 33650, exons);

        Assert.assertEquals(7, collector.size());

        Assert.assertEquals(ExonCategory.NOT_CODING_PRE, collector.getInfoAt(0).getExonCategory());
        Assert.assertNull(collector.getInfoAt(0).getFirstAA());
        Assert.assertNull(collector.getInfoAt(0).getLastAA());
        assertInfoEquals(collector.getInfoAt(1), 'M', 1, 0, 'M', 83, 0, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(2), 'G', 84, 0, 'A', 148, 1, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'A', 148, 1, 'Q', 199, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(4), 'V', 200, 0, 'S', 201, 0, ExonCategory.STOP);
        Assert.assertEquals(ExonCategory.NOT_CODING_POST, collector.getInfoAt(5).getExonCategory());
        Assert.assertNull(collector.getInfoAt(5).getFirstAA());
        Assert.assertNull(collector.getInfoAt(5).getLastAA());

        Assert.assertEquals(ExonCategory.NOT_CODING_POST, collector.getInfoAt(6).getExonCategory());
        Assert.assertNull(collector.getInfoAt(6).getFirstAA());
        Assert.assertNull(collector.getInfoAt(6).getLastAA());
    }

    @Test
    public void testanalyseInfosMonoNX_O15541() throws Exception {

        List<Exon> exons = createMockExonList(1, 1295);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MAEQLSPGKAVDQVCTFLFKKPGRKGAAGRRKRPACDPEPGESGSSSDEGCTVVRPEKKRVTHNPMIQKTRDSGKQKAAYGDLSSEEEEENEPESLGVVYKSTRSAKPVGPEDMGATAVYELDTEKERDAQAIFERSQKIQEELRGKEDDKIYRGINNYQKYMKPKDTSMGNASSGMVRKGPIRAPEHLRATVRWDYQPDICKDYKETGFCGFGDSCKFLHDRSDYKHGWQIERELDEGRYGVYEDENYEVGSDDEEIPFKCFICRQSFQNPVVTKCRHYFCESCALQHFRTTPRCYVCDQQTNGVFNPAKELIAKLEKHRATGEGGASDLPEDPDEDAIPIT", 216, 1244, exons);

        Assert.assertEquals(1, collector.size());
        assertInfoEquals(collector.getInfoAt(0), 'M', 1, 0, 'T', 343, 0, ExonCategory.MONO);
    }

    @Test
    public void testanalyseInfosNX_Q8NFW8_2() throws Exception {

        List<Exon> exons = createMockExonList(52, 390, 8976, 9118, 9282, 9437, 12394, 12527, 14659, 14753, 16108, 16261, 18948, 19501);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MDSVEKGAATSVSNPRGRPSRGRPPKLQRNSRGGQGRGVEKPPHLAALILARGGSKGIPLKNIKHLAGVPLIGWVLRAALDSGAFQSVWVSTDHDEIENVAKQFGAQVHRRSSEVSKDSSTSLDAIIEFLNYHNEVDIVGNIQATSPCLHPTDLQKVAEMIREEGYDSVFSVVRRHQFRWSEIQKGVREVTEPLNLNPAKRPRRQDWDGELYENGSFYFAKRHLIEMGYLQGGKMAYYEMRAEHSVDIDVDIDWPIAEQRVLR", 131, 16108, exons);

        Assert.assertEquals(7, collector.size());
        assertInfoEquals(collector.getInfoAt(0), 'M', 1,   0, 'S', 87, 2,  ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(1), 'S', 87,  2, 'E', 135, 1, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(2), 'E', 135, 1, 'V', 187, 1, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'V', 187, 1, 'Q', 231, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(4), 'G', 232, 0, 'R', 263, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(5), 'R', 263, 2, 'R', 263, 0, ExonCategory.STOP);

        Assert.assertEquals(ExonCategory.NOT_CODING_POST, collector.getInfoAt(6).getExonCategory());
        Assert.assertNull(collector.getInfoAt(6).getFirstAA());
        Assert.assertNull(collector.getInfoAt(6).getLastAA());
    }

    @Test
    public void testanalyseInfosMiniExons() throws Exception {

        List<Exon> exons = createMockExonList(1, 11, 100, 100, 150, 151, 200, 300);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MRTEQ", 10, 209, exons);

        Assert.assertEquals(4, collector.size());

        assertInfoEquals(collector.getInfoAt(0), 'M', 1, 0, 'M', 1, 2, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(1), 'M', 1, 2, 'M', 1, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(2), 'R', 2, 0, 'R', 2, 2, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'R', 2, 2, 'Q', 5, 0, ExonCategory.STOP);
    }

    @Test
    public void testanalyseInfosNX_Q5JQC4_1_ENST00000416816AndException() throws Exception {

        List<Exon> exons = createMockExonList(1, 997, 1885, 2040, 54668, 54808);

        InfoCollectorAnalysis collector = new InfoCollectorAnalysis();
        TranscriptExonsAnalyser analyser = new TranscriptExonsAnalyser(collector);

        analyser.analyse("MSATGDRHPTQGDQEAPVSQEGAQAEAAGAGNQEGGDSGPDSSDVVPAAEVVGVAGPVEGLGEEEGEQAAGLAAVPRGGSAEEDSDIGPATEEEEEEEGNEAANFDLAVVARRYPASGIHFVLLDMVHSLLHRLSHNDHILIENRQLSRLMVGPHAAARNLWGNLPPLLLPQRLGAGAAARAGEGLGLIQEAASVPEPAVPADLAEMAREPAEEAAEEKLSEEATEEPDAEEPATEEPTAQEATAPEEVTKSQPEKWDEEAQDAAGEEEKEQEKEKDAENKVKNSKGT", 256, 53495, exons);

        Assert.assertEquals(1, collector.size());

        assertInfoEquals(collector.getInfoAt(0), 'M', 1, 0, 'E', 248, 1, ExonCategory.START);
        //assertInfoEquals(collector.getInfoAt(1), 'E', 248, 1, 'T', 288, 1, ExonCategory.CODING);
        //Assert.assertEquals(ExonCategory.NOT_CODING_POST, collector.getInfoAt(2).getExonCategory());
        //Assert.assertNull(collector.getInfoAt(2).getFirstAA());
        //Assert.assertNull(collector.getInfoAt(2).getLastAA());
        Assert.assertTrue(collector.hasError());
    }

    /*@Test
    public void testanalyseInfosNX_Q658P3Iso3() throws Exception {

        List<Exon> exons = createMockExonList(1, 81, 6813, 7227, 21682, 22181, 23872, 24399, 30877, 31041, 39250, 41842);

        TranscriptInfoCollector collector = new TranscriptInfoCollector();
        TranscriptInfosanalyser analyser = new TranscriptInfosanalyser(collector);

        analyser.analyse("NX_Q658P3-3.ENST00000354888", "MPEEMDKPLISLHLVDSDSSLAKVPDEAPKVGILGSGDFARSLATRLVGSGFKVVVGSRNPKRTARLFPSAAQVTFQEEAVSSPEVIFVAVFREHYSSLCSLSDQLAGKILVDVSNPTEQEHLQHRESNAEYLASLFPTCTVVKAFNVISAWTLQAGPRDGNRQVPICGDQPEAKRAVSEMALAMGFMPVDMGSLASAWEVEAMPLRLLPAWKVPTLLALGLFVCFYAYNFVRDVLQPYVQESQNKFFKLPVSVVNTTLPCVAYVLLSLVYLPGVLAAALQLRRGTKYQRFPDWLDHWLQHRKQIGLLSFFCAALHALYSFCLPLRRAHRYDLVNLAVKQVLANKSHLWVEEVWRMEIYLSLGVLALGTLSLLAVTSLPSIANSLNWREFSFVQSSLGFVALVLSTLHTLTYGWTRAFEESRYKFYLPPTFTLTLLVPCVVILAKALFLLPCISRRLARIRRGWERESTIKFTLPTDHALAEKTSHV", 21690, 39528, exons);

        Assert.assertEquals(5, collector.size());

        Assert.assertEquals(ExonCategory.NOT_CODING_PRE, collector.getInfoAt(0).getExonCategory());
        Assert.assertNull(collector.getInfoAt(0).getFirstAA());
        Assert.assertNull(collector.getInfoAt(0).getLastAA());
        assertInfoEquals(collector.getInfoAt(1), 'M', 1, 0, 'Q', 164, 0, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(2), 'V', 165, 0, 'Q', 340, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'V', 341, 0, 'V', 350, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(4), 'E', 351, 0, 'Q', 394, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(5), 'S', 395, 0, 'V', 487, 0, ExonCategory.STOP);
    }

    @Test
    public void testanalyseInfosNX_Q658P3Iso3np1() throws Exception {

        List<Exon> exons = createMockExonList(46, 81, 21682, 22181, 23872, 24399, 30877, 30906, 30910, 31041, 39250, 39891);

        TranscriptInfoCollector collector = new TranscriptInfoCollector();
        TranscriptInfosanalyser analyser = new TranscriptInfosanalyser(collector);

        analyser.analyse("NX_Q658P3-3.ENST00000354888", "MPEEMDKPLISLHLVDSDSSLAKVPDEAPKVGILGSGDFARSLATRLVGSGFKVVVGSRNPKRTARLFPSAAQVTFQEEAVSSPEVIFVAVFREHYSSLCSLSDQLAGKILVDVSNPTEQEHLQHRESNAEYLASLFPTCTVVKAFNVISAWTLQAGPRDGNRQVPICGDQPEAKRAVSEMALAMGFMPVDMGSLASAWEVEAMPLRLLPAWKVPTLLALGLFVCFYAYNFVRDVLQPYVQESQNKFFKLPVSVVNTTLPCVAYVLLSLVYLPGVLAAALQLRRGTKYQRFPDWLDHWLQHRKQIGLLSFFCAALHALYSFCLPLRRAHRYDLVNLAVKQVLANKSHLWVEEVWRMEIYLSLGVLALGTLSLLAVTSLPSIANSLNWREFSFVQSSLGFVALVLSTLHTLTYGWTRAFEESRYKFYLPPTFTLTLLVPCVVILAKALFLLPCISRRLARIRRGWERESTIKFTLPTDHALAEKTSHV", 21690, 39528, exons);

        Assert.assertEquals(6, collector.size());

        Assert.assertEquals(ExonCategory.NOT_CODING_PRE, collector.getInfoAt(0).getExonCategory());
        Assert.assertNull(collector.getInfoAt(0).getFirstAA());
        Assert.assertNull(collector.getInfoAt(0).getLastAA());
        assertInfoEquals(collector.getInfoAt(1), 'M', 1, 0, 'Q', 164, 0, ExonCategory.START);
        assertInfoEquals(collector.getInfoAt(2), 'V', 165, 0, 'Q', 340, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(3), 'V', 341, 0, 'V', 350, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(4), 'E', 351, 0, 'Q', 394, 0, ExonCategory.CODING);
        assertInfoEquals(collector.getInfoAt(5), 'S', 395, 0, 'V', 487, 0, ExonCategory.STOP);
    }*/

    private void assertInfoEquals(ExonInfo info, char firstAA, int firstPos, int startPhase,  char lastAA, int lastPos, int endPhase, ExonCategory type) {

        Assert.assertEquals(type, info.getExonCategory());

        Assert.assertEquals(firstAA, info.getFirstAA().getBase());
        Assert.assertEquals(firstPos, info.getFirstAA().getPosition());
        Assert.assertEquals(startPhase, info.getFirstAA().getPhase());

        Assert.assertEquals(lastAA, info.getLastAA().getBase());
        Assert.assertEquals(lastPos, info.getLastAA().getPosition());
        Assert.assertEquals(endPhase, info.getLastAA().getPhase());
    }

    public static class ExonInfo {

        private ExonCategory exonCategory;
        private AminoAcid firstAA;
        private AminoAcid lastAA;

        public ExonCategory getExonCategory() {
            return exonCategory;
        }

        public void setExonCategory(ExonCategory exonCategory) {
            this.exonCategory = exonCategory;
        }

        public AminoAcid getFirstAA() {
            return firstAA;
        }

        public void setFirstAA(AminoAcid firstAA) {
            this.firstAA = firstAA;
        }

        public AminoAcid getLastAA() {
            return lastAA;
        }

        public void setLastAA(AminoAcid lastAA) {
            this.lastAA = lastAA;
        }
    }

    private class InfoCollectorAnalysis implements ExonsAnalysisListener {

        private final List<ExonInfo> exonInfos;
        private ExonInfo exonInfo;
        private boolean error;

        private InfoCollectorAnalysis() {
            this.exonInfos = new ArrayList<>();
        }

        @Override
        public void started() {}

        @Override
        public void startedExon(Exon exon) {
            exonInfo = new ExonInfo();
        }

        @Override
        public void analysedCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {
            exonInfo.setFirstAA(first);
            exonInfo.setLastAA(last);
            exonInfo.setExonCategory(category);
        }

        @Override
        public void analysedCodingExonFailed(Exon exon, ExonOutOfBoundError exonOutOfBoundError) {
            error = true;
        }

        @Override
        public void analysedNonCodingExon(Exon exon, ExonCategory cat) {
            exonInfo.setExonCategory(cat);
        }

        @Override
        public void terminated(Exon exon) {
            exonInfos.add(exonInfo);
        }

        @Override
        public void terminated() {}

        public ExonInfo getInfoAt(int index) {
            Preconditions.checkElementIndex(index, exonInfos.size());
            return exonInfos.get(index);
        }

        public boolean hasError() {
            return error;
        }

        public int size() {
            return exonInfos.size();
        }
    }
}