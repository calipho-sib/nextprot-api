package org.nextprot.api.core.service.impl.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev","cache" })
public class PEFFVariantIntegrationTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void formatSimpleVariant() throws Exception {

        Entry entry = entryBuilderService.buildWithEverything("NX_P43246");

        PEFFVariantSimple variantSimple = new PEFFVariantSimple(entry, "NX_P43246-1");

        String expectedFormat = variantSimple.format();

        Assert.assertEquals("\\VariantSimple=(1|L)(2|E)(2|T)(2|V)(4|*)(4|K)(4|L)(5|L)(5|Q)(6|E)(6|N)(7|A)(8|M)(8|S)(10|*)(10|H)(10|K)(13|I)(13|N)(13|R)(14|T)(15|G)(16|A)(16|D)(17|F)(17|G)(17|I)(19|L)(21|G)(21|L)(22|L)(23|L)(25|C)(25|D)(25|S)(26|L)(27|L)(27|R)(27|T)(28|*)(28|K)(29|*)(30|L)(30|Q)(30|R)(30|T)(31|I)(31|S)(32|P)(32|S)(33|A)(33|I)(33|P)(35|C)(35|H)(38|E)(38|Y)(39|P)(40|S)(40|V)(41|E)(41|Y)(42|L)(43|*)(43|C)(43|H)(44|K)(44|M)(45|S)(45|V)(46|Q)(47|R)(48|*)(49|E)(49|V)(50|E)(52|P)(53|S)(53|T)(54|S)(54|T)(54|V)(55|G)(55|Q)(56|*)(56|K)(56|V)(57|M)(58|L)(59|E)(59|R)(61|*)(61|H)(61|P)(62|R)(67|K)(67|V)(70|E)(70|T)(70|V)(71|R)(72|T)(73|E)(73|N)(74|H)(76|*)(76|E)(76|H)(77|C)(78|I)(81|R)(82|*)(83|L)(84|I)(86|D)(86|Q)(87|C)(87|Y)(89|A)(89|I)(89|L)(91|V)(92|F)(92|V)(93|F)(96|C)(96|H)(97|*)(97|K)(98|*)(98|C)(99|S)(101|*)(102|I)(103|C)(103|H)(104|N)(106|K)(107|G)(107|P)(109|D)(109|S)(110|Q)(110|R)(110|T)(112|C)(112|F)(113|K)(114|*)(115|K)(117|S)(121|*)(121|C)(121|N)(122|M)(122|Q)(123|G)(123|T)(123|V)(125|L)(125|S)(126|R)(126|S)(127|I)(127|S)(127|T)(128|R)(128|V)(129|C)(129|F)(130|*)(131|V)(133|G)(135|F)(135|V)(137|C)(137|R)(138|S)(139|S)(140|Y)(141|T)(141|V)(142|*)(143|T)(144|C)(145|M)(145|T)(145|V)(146|V)(147|G)(147|I)(149|D)(153|C)(154|T)(155|D)(157|A)(158|*)(159|I)(160|*)(161|D)(161|I)(162|A)(162|R)(163|A)(163|D)(163|G)(164|E)(164|R)(164|W)(165|D)(167|E)(167|H)(168|P)(169|M)(169|T)(169|V)(170|*)(170|E)(170|L)(171|K)(173|P)(173|R)(174|E)(175|P)(177|*)(177|H)(177|K)(177|V)(179|L)(182|Y)(183|*)(185|F)(186|D)(186|H)(186|S)(187|P)(187|R)(187|V)(189|G)(189|S)(189|T)(190|P)(190|V)(192|F)(192|M)(192|V)(193|*)(194|T)(196|L)(196|S)(197|E)(197|Q)(198|*)(198|G)(198|K)(199|R)(199|Y)(200|D)(202|R)(203|R)(204|*)(204|E)(204|R)(205|*)(205|Q)(206|S)(207|S)(207|T)(207|V)(209|Y)(210|I)(211|R)(214|I)(215|*)(216|*)(216|L)(216|T)(216|V)(217|V)(218|*)(219|T)(220|*)(220|V)(221|R)(221|V)(222|F)(222|L)(223|P)(224|M)(227|G)(227|K)(227|S)(228|E)(229|*)(233|C)(234|I)(234|R)(235|E)(236|G)(237|V)(238|D)(239|*)(239|R)(240|G)(242|S)(243|Q)(243|W)(245|F)(246|*)(246|Q)(248|E)(248|N)(249|N)(250|*)(250|E)(250|V)(251|K)(252|*)(252|E)(252|H)(252|P)(252|R)(253|I)(255|G)(255|N)(255|T)(256|T)(257|I)(258|F)(259|S)(261|L)(261|T)(262|D)(263|H)(263|K)(264|*)(264|H)(266|V)(268|A)(268|L)(269|*)(269|L)(270|V)(271|C)(271|F)(271|R)(271|Y)(272|M)(272|V)(273|A)(273|I)(273|K)(274|V)(277|*)(279|F)(279|P)(279|V)(281|*)(281|P)(282|G)(282|H)(282|Y)(283|V)(283|Y)(284|Y)(285|K)(285|S)(287|*)(287|A)(288|*)(289|L)(290|*)(291|V)(292|S)(295|E)(296|L)(297|R)(298|*)(298|H)(299|H)(300|I)(300|V)(301|*)(302|*)(302|S)(305|E)(305|T)(308|K)(309|T)(310|P)(310|R)(310|V)(312|V)(313|S)(315|D)(315|V)(316|Y)(319|N)(319|V)(320|A)(321|P)(321|S)(322|D)(322|S)(322|V)(323|C)(323|F)(323|Y)(324|*)(324|V)(325|F)(328|A)(328|P)(328|S)(328|V)(329|F)(330|P)(331|D)(331|S)(333|R)(333|Y)(334|*)(335|I)(335|P)(335|S)(336|A)(336|S)(337|*)(338|A)(338|E)(338|R)(338|V)(339|P)(341|P)(341|V)(342|I)(343|D)(343|K)(344|*)(344|H)(344|K)(345|*)(348|R)(349|A)(349|L)(349|R)(350|F)(351|I)(351|V)(352|H)(353|N)(356|T)(357|*)(357|A)(357|D)(357|Q)(358|A)(359|*)(359|I)(359|K)(359|S)(359|T)(361|S)(362|F)(363|L)(367|A)(367|I)(367|L)(370|V)(371|G)(371|Q)(372|V)(373|K)(374|*)(374|H)(374|R)(375|I)(375|S)(376|S)(377|*)(377|E)(377|R)(378|A)(379|Y)(380|S)(382|C)(382|H)(383|*)(383|L)(383|P)(383|Q)(385|A)(385|L)(385|S)(386|E)(387|F)(389|*)(389|Q)(390|F)(390|V)(391|T)(391|V)(392|M)(393|*)(393|M)(393|Q)(394|L)(395|*)(395|R)(397|*)(397|E)(397|H)(397|R)(398|T)(398|V)(402|*)(402|E)(402|K)(404|F)(405|*)(406|*)(406|Q)(408|*)(408|C)(408|F)(409|E)(409|K)(410|C)(410|S)(411|L)(412|H)(413|*)(413|P)(414|P)(414|R)(415|A)(415|S)(417|G)(418|L)(418|M)(418|T)(419|*)(419|K)(420|P)(421|M)(421|P)(422|*)(423|E)(423|R)(424|R)(424|Y)(425|E)(427|G)(428|Q)(429|*)(430|*)(431|*)(434|V)(435|I)(437|L)(439|L)(439|S)(440|F)(440|P)(441|P)(443|I)(443|V)(444|L)(446|E)(447|L)(447|V)(448|C)(449|*)(449|N)(451|*)(451|E)(451|R)(452|*)(452|K)(453|K)(454|L)(454|T)(454|V)(456|A)(456|I)(458|*)(460|I)(460|V)(461|A)(461|G)(461|V)(462|*)(462|H)(464|*)(464|G)(466|R)(466|Y)(467|*)(467|K)(469|F)(469|V)(470|A)(470|E)(471|N)(473|*)(473|L)(476|S)(477|H)(478|F)(479|N)(479|R)(481|*)(482|*)(483|*)(484|V)(485|I)(485|V)(487|E)(488|V)(489|K)(490|R)(492|I)(492|L)(492|V)(493|*)(493|L)(494|*)(494|L)(494|P)(495|I)(496|*)(497|V)(498|R)(499|P)(501|K)(502|G)(502|H)(503|P)(504|C)(504|R)(506|Y)(508|A)(508|S)(510|*)(510|H)(516|G)(516|I)(516|N)(516|T)(517|V)(518|*)(518|L)(519|L)(521|*)(522|*)(523|I)(523|S)(524|C)(524|H)(524|L)(524|P)(526|P)(528|M)(528|Q)(528|R)(530|*)(532|A)(532|G)(533|V)(534|C)(534|G)(534|H)(534|L)(534|P)(534|S)(536|S)(537|T)(539|L)(540|G)(541|A)(541|I)(543|N)(544|T)(546|N)(547|S)(548|C)(549|I)(550|T)(551|L)(552|P)(553|Y)(554|C)(554|G)(554|N)(554|R)(554|T)(556|L)(556|S)(557|P)(557|S)(559|*)(559|I)(561|*)(561|K)(562|D)(562|V)(563|S)(564|A)(565|*)(566|H)(567|*)(567|E)(567|R)(569|G)(569|I)(569|R)(570|C)(572|A)(573|T)(574|*)(575|G)(576|P)(577|T)(577|V)(578|G)(580|*)(580|D)(581|V)(583|I)(583|S)(583|T)(587|R)(588|*)(590|D)(591|L)(592|V)(593|*)(594|A)(595|F)(595|R)(596|S)(597|A)(597|N)(598|M)(599|S)(600|G)(600|S)(600|T)(600|V)(601|*)(601|R)(602|V)(603|E)(603|G)(603|N)(603|Y)(605|F)(605|I)(605|L)(606|A)(606|D)(606|F)(607|G)(609|S)(609|V)(610|N)(610|Y)(611|M)(612|*)(613|H)(613|Y)(615|V)(616|R)(616|S)(619|*)(619|C)(621|*)(621|G)(621|L)(621|Q)(622|A)(622|L)(622|R)(622|T)(623|G)(625|V)(627|N)(628|R)(629|*)(629|R)(630|V)(631|S)(633|T)(633|V)(636|D)(636|P)(636|V)(638|G)(639|Q)(639|R)(639|Y)(640|S)(641|G)(641|Y)(642|I)(643|K)(645|E)(645|H)(646|A)(646|G)(647|*)(647|K)(647|Q)(648|N)(648|T)(648|V)(649|T)(649|V)(651|V)(652|H)(652|T)(654|E)(655|I)(656|*)(656|C)(656|F)(656|H)(656|S)(658|G)(659|T)(660|E)(660|G)(662|*)(662|H)(663|I)(663|L)(663|V)(667|V)(669|A)(669|D)(669|R)(669|S)(669|V)(670|H)(670|L)(671|D)(671|I)(671|K)(671|Y)(672|R)(672|V)(674|A)(674|D)(674|R)(674|S)(675|A)(675|E)(676|L)(676|P)(677|R)(678|H)(679|T)(680|*)(680|G)(681|E)(681|H)(682|I)(683|A)(683|R)(683|V)(683|W)(684|L)(685|M)(687|P)(688|I)(688|R)(688|V)(689|S)(689|V)(690|E)(691|M)(691|T)(692|E)(692|R)(692|V)(692|W)(695|L)(696|A)(696|L)(696|S)(697|*)(697|F)(697|R)(697|Y)(699|*)(699|L)(700|E)(701|*)(701|A)(701|G)(701|K)(702|G)(703|Y)(704|T)(704|V)(706|E)(707|R)(707|S)(707|Y)(708|N)(708|V)(709|V)(710|D)(710|G)(710|V)(711|*)(711|Q)(713|E)(713|V)(714|V)(717|G)(717|N)(718|*)(719|W)(720|E)(721|*)(721|E)(722|I)(723|F)(724|K)(724|M)(726|I)(727|P)(727|S)(729|I)(729|V)(731|*)(732|I)(732|S)(733|T)(735|V)(736|F)(736|I)(739|T)(741|*)(743|*)(743|L)(744|*)(747|N)(747|R)(748|H)(748|Y)(749|*)(749|G)(749|K)(751|R)(753|E)(754|A)(754|S)(756|A)(756|S)(757|*)(757|S)(758|N)(758|Y)(759|*)(759|E)(761|A)(761|R)(761|V)(763|V)(764|*)(764|C)(765|T)(766|V)(770|T)(770|V)(773|N)(774|F)(774|S)(778|*)(779|I)(782|A)(783|R)(785|P)(785|R)(788|P)(789|V)(792|S)(793|E)(793|H)(793|K)(793|P)(795|Q)(796|I)(796|S)(798|D)(798|K)(798|S)(799|K)(802|A)(803|I)(804|T)(805|F)(805|V)(806|I)(807|I)(807|N)(807|S)(808|*)(809|K)(811|*)(811|S)(813|I)(813|T)(813|V)(814|L)(816|*)(816|R)(820|A)(820|D)(822|*)(822|F)(824|*)(824|E)(827|R)(828|S)(829|Q)(829|R)(832|D)(833|V)(834|T)(835|D)(835|H)(835|S)(836|S)(839|D)(839|Q)(839|R)(840|I)(841|L)(842|V)(843|G)(843|Y)(845|E)(846|*)(846|R)(848|S)(848|T)(849|V)(851|I)(851|V)(852|*)(852|D)(852|Q)(853|A)(853|G)(854|C)(855|R)(856|*)(856|C)(856|F)(856|H)(857|V)(858|A)(858|R)(858|V)(859|*)(859|K)(860|*)(860|L)(861|*)(861|Q)(862|R)(864|A)(865|T)(865|V)(868|A)(868|S)(869|E)(870|G)(871|N)(871|R)(872|R)(873|G)(874|*)(874|C)(874|D)(878|D)(878|G)(878|Q)(879|*)(879|K)(881|Q)(883|M)(884|F)(884|S)(885|*)(886|G)(895|R)(898|A)(898|D)(899|I)(900|*)(902|D)(903|I)(903|S)(903|Y)(905|I)(905|R)(906|M)(906|R)(906|T)(906|V)(909|I)(909|R)(910|E)(910|K)(911|R)(912|Q)(913|G)(914|*)(916|L)(917|T)(917|V)(923|E)(923|L)(924|S)(925|K)(926|M)(926|N)(926|V)(928|*)(928|A)(928|P)(929|*)(929|Q)(930|K)(930|M)(931|T)(933|I)(934|K)(934|M)(934|S)",
                expectedFormat);
    }

    @Test
    public void formatComplexVariant() throws Exception {

        Entry entry = entryBuilderService.buildWithEverything("NX_P43246");

        PEFFVariantComplex variantComplex = new PEFFVariantComplex(entry, "NX_P43246-1");

        String expectedFormat = variantComplex.format();

        Assert.assertEquals("\\VariantComplex=(1|1|MAVQPKE)(7|7|EAVQPKE)(39|41|)(46|55|RTARTRCWPP)(90|91|KVY)(92|92|)(94|94|)(101|102|)(101|102|S*)(157|157|)(175|188|)(188|190|)(191|191|)(265|314|)(271|272|LS)(271|272|VS)(287|289|)(344|344|QL)(411|426|)(412|422|)(412|422|KSTT*CYTGSG)(417|417|VV)(439|439|)(440|440|)(486|487|KC)(492|493|I*)(531|537|)(596|596|)(606|606|)(660|661|E*)(732|732|)(745|746|)(746|746|II)(746|747|)(835|835|A*)(852|852|)(853|853|)(859|861|)(865|865|)",                expectedFormat);
    }

    /*@Ignore // should not be integration tests, mock deps
    @ActiveProfiles({ "dev" })
    public class PEFFVariantTest {

        @Test
        public void formatSimpleVariant() {

            Entry entry = Mockito.mock(Entry.class);
            Mockito.when(entry.getAnnotationsByIsoform("NX_P43246-1")).thenReturn(new ArrayList<>());

            PEFFVariantSimple variantSimple = new PEFFVariantSimple(entry, "NX_P43246-1");

            String expectedFormat = variantSimple.format();

            Assert.assertEquals("\\VariantSimple=(1|L)(2|E)(2|T)(2|V)(4|*)(4|K)(4|L)(5|L)(5|Q)(6|E)(6|N)(7|A)(8|M)(8|S)(10|*)(10|H)(10|K)(13|I)(13|N)(14|T)(15|G)(16|A)(17|F)(17|G)(17|I)(19|L)(21|G)(21|H)(22|L)(23|L)(24|*)(25|C)(25|D)(25|S)(26|L)(27|L)(27|R)(27|T)(28|*)(28|K)(30|L)(30|Q)(30|R)(30|T)(31|I)(31|S)(32|S)(33|A)(33|I)(33|P)(38|E)(38|Y)(39|P)(40|S)(40|V)(41|E)(41|Y)(42|L)(43|*)(43|C)(43|H)(44|K)(44|M)(45|S)(45|V)(46|Q)(47|R)(48|*)(49|E)(49|V)(50|E)(52|P)(53|S)(54|S)(54|T)(54|V)(55|G)(55|Q)(56|*)(56|K)(56|V)(57|M)(58|L)(59|E)(59|R)(61|*)(61|H)(61|P)(62|A)(62|R)(67|K)(67|V)(70|T)(70|V)(71|R)(72|T)(73|E)(73|N)(74|H)(76|*)(76|E)(76|H)(77|C)(78|I)(81|R)(82|*)(83|L)(84|I)(86|D)(86|Q)(87|C)(89|A)(89|L)(91|V)(92|V)(93|F)(96|C)(96|H)(97|*)(98|C)(99|S)(101|*)(102|I)(103|C)(103|H)(104|N)(106|K)(107|G)(107|P)(109|D)(109|S)(110|Q)(110|R)(110|T)(112|C)(113|K)(114|*)(115|K)(117|S)(121|*)(121|C)(121|N)(122|M)(122|Q)(123|G)(123|T)(123|V)(125|L)(125|S)(126|S)(127|I)(127|S)(128|R)(128|V)(129|C)(129|F)(131|V)(135|F)(135|V)(137|C)(137|R)(138|S)(139|S)(140|Y)(141|T)(141|V)(142|*)(143|T)(144|C)(145|M)(145|T)(145|V)(146|V)(147|G)(147|I)(149|D)(153|C)(154|T)(155|D)(157|A)(158|*)(159|I)(160|*)(161|D)(161|I)(162|A)(162|R)(163|D)(163|G)(164|E)(164|R)(164|W)(165|D)(167|E)(167|H)(168|P)(169|M)(169|V)(170|*)(170|E)(170|L)(171|K)(173|P)(173|R)(174|E)(175|P)(177|*)(177|H)(177|K)(177|V)(182|Y)(183|*)(185|F)(186|D)(186|S)(187|P)(187|R)(187|V)(189|G)(189|S)(189|T)(190|P)(190|V)(192|F)(192|V)(193|*)(194|T)(196|L)(196|S)(197|Q)(198|*)(198|G)(198|K)(199|R)(199|Y)(200|D)(203|R)(204|*)(204|E)(204|R)(205|*)(205|Q)(206|S)(207|S)(207|T)(207|V)(209|Y)(210|I)(211|R)(214|I)(215|*)(216|*)(216|L)(216|T)(216|V)(217|V)(218|*)(219|T)(220|*)(220|V)(221|R)(221|V)(222|F)(222|L)(224|M)(227|G)(227|K)(227|S)(228|E)(229|*)(233|C)(234|I)(235|E)(236|G)(237|V)(239|*)(239|R)(240|G)(242|S)(243|Q)(243|W)(245|F)(246|*)(246|Q)(248|E)(250|*)(250|E)(250|V)(251|K)(252|*)(252|E)(252|H)(252|R)(255|G)(255|N)(255|T)(256|T)(257|I)(258|F)(259|S)(261|L)(261|T)(262|D)(263|H)(263|K)(264|*)(264|H)(264|R)(266|V)(268|A)(268|L)(269|L)(270|V)(271|F)(271|Y)(272|M)(272|V)(273|A)(273|I)(273|K)(274|V)(277|*)(279|V)(281|*)(281|P)(282|G)(282|H)(282|Y)(283|V)(283|Y)(284|Y)(287|*)(287|A)(288|*)(290|*)(291|V)(292|S)(295|E)(296|L)(297|R)(298|*)(298|H)(299|*)(299|H)(300|I)(300|V)(301|*)(302|*)(305|E)(305|T)(308|K)(309|T)(310|P)(310|R)(310|V)(312|V)(313|S)(315|D)(315|V)(316|Y)(319|N)(319|V)(320|A)(321|P)(321|S)(322|D)(322|S)(323|C)(323|F)(323|Y)(324|*)(324|V)(325|F)(328|A)(328|P)(328|S)(328|V)(330|P)(331|D)(331|S)(333|R)(333|Y)(334|*)(335|I)(335|P)(336|A)(336|S)(337|*)(338|E)(338|R)(338|V)(341|P)(341|V)(342|I)(343|D)(344|*)(344|H)(344|K)(345|*)(348|R)(349|A)(349|L)(349|R)(350|F)(351|I)(351|V)(352|H)(353|N)(355|I)(356|T)(357|*)(357|A)(357|D)(357|Q)(358|A)(359|*)(359|I)(359|K)(359|S)(359|T)(361|S)(362|F)(363|L)(367|A)(367|I)(370|V)(371|G)(372|V)(373|K)(374|*)(374|H)(374|R)(375|I)(376|S)(377|*)(377|E)(377|R)(378|A)(379|Y)(380|S)(382|C)(382|H)(383|*)(383|L)(383|P)(383|Q)(385|L)(385|S)(386|E)(387|F)(389|*)(389|Q)(390|F)(391|T)(391|V)(392|M)(393|*)(393|M)(393|Q)(394|L)(395|*)(395|R)(397|*)(397|E)(397|R)(402|*)(402|K)(404|F)(405|*)(406|*)(406|Q)(408|*)(408|C)(408|F)(409|E)(409|K)(410|C)(410|S)(411|L)(412|H)(413|*)(413|P)(414|P)(414|R)(415|A)(415|S)(417|G)(418|L)(418|M)(418|T)(419|*)(419|K)(420|P)(421|M)(421|P)(422|*)(423|E)(423|R)(424|R)(424|Y)(425|E)(426|R)(427|G)(428|Q)(429|*)(430|*)(431|*)(434|V)(435|I)(437|L)(439|L)(439|S)(440|P)(441|P)(443|V)(444|L)(446|E)(447|L)(447|V)(448|C)(449|*)(449|N)(449|R)(451|*)(451|E)(451|R)(452|*)(452|K)(453|K)(454|L)(454|V)(456|A)(456|I)(458|*)(460|I)(460|V)(461|A)(461|G)(461|V)(462|*)(462|H)(464|*)(464|G)(466|R)(466|Y)(467|*)(467|K)(469|F)(469|V)(470|A)(470|E)(471|N)(473|*)(473|L)(476|S)(477|H)(478|F)(479|N)(479|R)(481|*)(482|*)(483|*)(485|I)(485|V)(487|E)(488|V)(489|K)(492|I)(492|L)(492|V)(493|*)(493|L)(494|L)(494|P)(495|A)(495|I)(496|*)(497|V)(498|R)(501|K)(502|G)(502|H)(503|P)(504|C)(504|R)(506|Y)(508|A)(508|S)(509|*)(510|*)(510|H)(516|G)(516|I)(516|N)(516|T)(517|V)(518|*)(518|L)(519|L)(521|F)(522|*)(523|I)(523|S)(524|C)(524|H)(524|L)(524|P)(526|P)(528|M)(528|Q)(528|R)(530|*)(532|A)(532|G)(533|V)(534|C)(534|G)(534|H)(534|L)(534|P)(534|S)(536|S)(537|T)(539|L)(540|G)(541|A)(541|I)(543|N)(544|T)(546|N)(547|S)(548|C)(549|I)(550|T)(551|L)(552|P)(553|Y)(554|C)(554|G)(554|N)(554|R)(554|T)(556|L)(556|S)(557|P)(557|S)(559|I)(561|*)(561|K)(562|D)(562|V)(563|S)(564|A)(565|*)(566|H)(567|*)(567|E)(569|G)(569|I)(570|C)(572|A)(573|T)(574|*)(575|G)(576|P)(577|T)(577|V)(578|G)(580|*)(580|D)(581|V)(583|S)(583|T)(587|R)(588|*)(590|D)(591|L)(592|V)(593|*)(594|A)(595|F)(595|R)(596|S)(597|A)(597|N)(598|M)(599|S)(600|S)(600|T)(600|V)(601|*)(601|R)(602|V)(603|G)(603|N)(603|Y)(605|F)(605|I)(605|L)(606|A)(606|D)(606|F)(607|G)(609|S)(609|V)(610|N)(610|Y)(611|M)(612|*)(613|H)(615|V)(616|R)(616|S)(619|*)(619|C)(621|*)(621|G)(621|L)(621|Q)(622|A)(622|L)(622|T)(623|G)(625|V)(627|N)(628|E)(628|R)(629|*)(629|R)(630|V)(631|S)(633|T)(633|V)(636|D)(636|P)(636|V)(638|G)(639|Q)(639|R)(639|Y)(640|S)(641|G)(641|Y)(642|I)(643|K)(645|E)(645|H)(646|A)(646|G)(647|*)(647|K)(647|Q)(648|N)(648|T)(648|V)(649|T)(649|V)(651|V)(652|H)(652|T)(655|I)(656|*)(656|C)(656|F)(656|H)(656|S)(658|G)(659|T)(660|E)(660|G)(662|*)(662|H)(663|I)(663|L)(663|V)(667|V)(669|A)(669|D)(669|R)(669|S)(669|V)(670|L)(671|I)(671|K)(671|Y)(672|R)(672|V)(674|A)(674|D)(674|R)(674|S)(675|A)(676|L)(676|P)(677|R)(678|H)(679|T)(680|*)(680|G)(681|E)(681|H)(682|I)(683|A)(683|R)(683|W)(687|P)(688|I)(688|R)(688|V)(690|E)(691|M)(691|T)(692|R)(692|V)(692|W)(695|L)(696|A)(696|L)(697|*)(697|F)(697|R)(697|Y)(699|*)(700|E)(701|*)(701|A)(701|K)(702|G)(703|Y)(704|T)(704|V)(706|E)(707|R)(707|S)(707|Y)(708|N)(708|V)(710|D)(710|G)(711|*)(711|Q)(713|E)(713|V)(714|V)(717|G)(717|N)(718|*)(719|W)(720|E)(721|E)(722|I)(723|F)(724|A)(724|K)(724|M)(726|I)(727|P)(727|S)(729|I)(729|V)(731|*)(732|I)(732|S)(733|T)(735|V)(736|F)(736|I)(739|T)(741|*)(742|N)(743|*)(743|L)(744|*)(747|R)(748|H)(748|Y)(749|*)(749|G)(749|K)(751|R)(753|E)(754|A)(754|S)(756|A)(756|S)(757|*)(757|S)(758|N)(758|Y)(759|*)(759|E)(761|A)(761|V)(763|V)(764|*)(764|C)(764|G)(765|T)(766|V)(767|*)(770|T)(770|V)(773|N)(774|F)(774|S)(778|*)(779|I)(782|A)(783|R)(785|P)(785|R)(788|P)(789|V)(792|S)(793|E)(793|H)(793|P)(795|Q)(796|I)(796|S)(798|D)(798|K)(798|S)(799|K)(802|A)(803|I)(804|T)(805|F)(805|V)(806|I)(807|I)(807|N)(807|S)(808|*)(809|K)(811|*)(813|I)(813|T)(813|V)(814|L)(816|*)(816|R)(820|A)(820|D)(822|*)(822|F)(824|*)(824|E)(827|R)(828|S)(829|R)(832|*)(832|D)(833|V)(834|T)(835|D)(835|H)(835|S)(839|D)(839|Q)(839|R)(840|I)(841|L)(842|V)(843|G)(843|Y)(845|E)(846|*)(846|R)(848|S)(848|T)(849|V)(851|I)(851|V)(852|*)(852|D)(852|Q)(853|A)(853|G)(854|C)(855|R)(856|*)(856|C)(856|F)(856|H)(857|V)(858|A)(858|R)(858|V)(859|*)(859|K)(860|*)(860|L)(861|*)(862|R)(864|A)(865|T)(868|A)(868|S)(869|E)(870|G)(871|N)(871|R)(872|R)(873|G)(874|*)(874|C)(874|D)(878|D)(878|Q)(879|*)(879|K)(881|Q)(883|M)(884|F)(884|S)(885|*)(886|G)(895|R)(899|I)(900|*)(902|D)(903|I)(903|S)(903|Y)(905|I)(905|R)(906|M)(906|R)(906|T)(906|V)(909|I)(909|Q)(909|R)(910|E)(910|K)(911|R)(914|*)(916|L)(917|T)(917|V)(923|E)(923|L)(924|S)(925|K)(926|M)(926|N)(928|*)(928|A)(929|*)(929|Q)(930|K)(930|M)(931|T)(933|I)(934|K)(934|M)(934|S)", expectedFormat);    }

        @Test
        public void formatComplexVariant() {

            Entry entry = Mockito.mock(Entry.class);

            PEFFVariantComplex variantComplex = new PEFFVariantComplex(entry, "NX_P43246-1");

            String expectedFormat = variantComplex.format();

            Assert.assertEquals("\\VariantComplex=(1|1|MAVQPKE)(7|7|EAVQPKE)(39|41|)(90|91|KVY)(92|92|)(94|94|)(157|157|)(175|188|)(188|190|)(191|191|)(265|314|)(287|289|)(344|344|QL)(411|426|)(417|417|VV)(440|440|)(492|493|I*)(531|537|)(596|596|)(606|606|)(732|732|)(745|746|)(746|746|II)(846|847|)(852|852|)(859|861|)(865|865|)",
                    expectedFormat);
        }
    }*/
}