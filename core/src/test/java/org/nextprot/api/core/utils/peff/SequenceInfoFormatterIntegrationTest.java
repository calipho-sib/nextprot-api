package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class SequenceInfoFormatterIntegrationTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    /*
     * >nxp:NX_A0A087X1C5-1
     *   \DbUniqueId=NX_A0A087X1C5-1
     *   \PName=Putative cytochrome P450 2D7 isoform Iso 1
     *   \GName=CYP2D7
     *   \NcbiTaxId=9606
     *   \TaxName=Homo Sapiens
     *   \Length=515
     *   \SV=30
     *   \EV=52
     *   \PE=5
     *   \ModRes=(416||N-linked (GlcNAc...))
     *   \VariantSimple=(8|S)(10|V)(11|V)(12|L)(13|M)(21|H)(24|Y)(25|W)(25|Q)(26|R)(28|C)(28|S)(30|D)(31|P)(32|C)(32|H)(33|C)(34|S)(37|A)(39|S)(42|R)(43|Q)(44|S)(44|C)(49|M)(55|T)(56|*)(59|N)(60|*)(64|H)(65|L)(66|E)(69|L)(70|N)(76|M)(79|I)(80|G)(81|F)(82|S)(82|K)(85|V)(86|T)(87|M)(88|H)(88|C)(89|K)(90|G)(91|L)(96|Q)(96|K)(99|S)(100|H)(100|N)(100|V)(101|H)(102|L)(103|H)(104|V)(107|N)(107|S)(108|H)(109|I)(111|S)(112|C)(112|I)(113|R)(114|L)(115|P)(118|R)(118|E)(120|F)(122|A)(122|L)(122|W)(123|H)(123|C)(124|C)(124|N)(124|*)(126|H)(126|S)(127|T)(128|G)(133|C)(135|F)(136|M)(137|P)(138|N)(140|C)(141|K)(142|S)(143|D)(144|R)(145|V)(146|N)(148|*)(149|V)(151|R)(151|E)(155|K)(155|*)(157|D)(158|T)(159|Y)(162|D)(163|T)(166|N)(166|Y)(167|H)(168|S)(169|R)(170|C)(171|L)(173|C)(173|H)(176|V)(177|F)(178|W)(182|M)(182|A)(187|V)(187|D)(189|F)(190|S)(192|R)(193|C)(193|H)(194|C)(194|H)(195|V)(196|K)(196|A)(198|N)(199|H)(201|P)(201|S)(201|C)(203|F)(204|G)(207|N)(208|I)(210|K)(211|K)(211|D)(213|P)(214|R)(215|G)(216|K)(216|A)(216|D)(219|L)(221|C)(222|Q)(222|K)(223|M)(227|A)(227|I)(228|S)(228|L)(229|I)(231|L)(234|L)(235|E)(235|V)(237|S)(238|D)(242|G)(242|H)(242|C)(243|S)(245|N)(245|T)(252|N)(257|K)(258|Y)(262|*)(263|G)(264|L)(267|T)(267|L)(268|S)(269|Q)(269|L)(269|*)(271|V)(276|M)(278|E)(279|E)(279|M)(279|N)(281|N)(282|G)(283|R)(285|N)(285|R)(288|G)(292|H)(295|V)(296|G)(296|C)(296|H)(297|L)(297|T)(297|V)(298|L)(300|A)(300|D)(301|D)(302|V)(303|L)(304|P)(304|F)(306|R)(307|I)(309|N)(310|P)(310|A)(311|*)(311|L)(312|I)(313|M)(314|M)(317|V)(318|F)(319|P)(321|I)(322|F)(324|L)(324|R)(325|P)(326|Y)(327|M)(328|E)(328|*)(329|*)(330|R)(331|K)(332|K)(333|L)(333|M)(334|Y)(334|C)(335|S)(337|Y)(338|T)(340|M)(341|R)(341|E)(342|R)(342|M)(343|R)(345|F)(346|T)(346|R)(347|G)(348|H)(348|C)(349|A)(349|G)(350|E)(351|*)(352|K)(354|N)(355|Y)(355|N)(355|G)(356|M)(356|A)(360|A)(360|M)(361|Q)(361|W)(361|G)(362|Q)(362|*)(370|R)(372|A)(373|Y)(373|G)(374|N)(374|I)(376|D)(377|M)(380|K)(381|L)(383|Y)(383|D)(383|R)(386|Y)(388|I)(389|L)(390|V)(391|D)(391|S)(394|N)(395|I)(397|P)(398|H)(398|C)(402|L)(403|H)(403|*)(404|D)(406|C)(406|H)(408|S)(411|M)(412|I)(414|V)(416|D)(418|L)(419|L)(420|M)(423|N)(426|I)(428|N)(428|*)(428|E)(430|L)(432|H)(432|C)(436|D)(436|K)(439|V)(448|L)(450|T)(453|L)(458|H)(458|C)(459|G)(459|H)(459|C)(465|L)(467|D)(468|C)(468|H)(469|I)(480|*)(484|L)(486|M)(488|S)(488|T)(489|R)(490|*)(492|L)(492|Q)(492|W)(494|N)(497|C)(497|G)(497|H)(498|L)(499|F)(499|I)(500|G)(501|L)(504|S)(505|S)(506|C)(508|F)(509|D)(509|K)(511|R)(514|L)(515|S)(515|C)(515|H)
     *   \VariantComplex=(29|32|)(36|40|V)(280|280|)(320|320|LI)(337|337|CS)(369|373|VHMPY)
     *   \Processed=(1|515|mature protein)
     */
    @Test
    public void testMatureProteinAnnotation() throws Exception {

        Entry entry = entryBuilderService.buildWithEverything("NX_A0A087X1C5");

        String peff = new PeffHeaderFormatterImpl(entry, entry.getIsoforms().get(0)).format();

        Assert.assertEquals("\\Processed=(17|609|CHAIN)", peff);
    }
}