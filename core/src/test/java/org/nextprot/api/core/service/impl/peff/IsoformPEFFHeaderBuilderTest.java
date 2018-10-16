package org.nextprot.api.core.service.impl.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.IsoformUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles({ "dev", "cache" })
public class IsoformPEFFHeaderBuilderTest extends CoreUnitBaseTest {

    @Autowired
    private TerminologyService terminologyService;

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private OverviewService overviewService;

    private IsoformPEFFHeaderBuilder newIsoformPEFFHeaderBuilder(String isoName) {

        String entryAccession = IsoformUtils.findEntryAccessionFromIsoformAccession(isoName);

        Isoform isoform = isoformService.findIsoform(isoName);
        List<Annotation> isoformAnnotations = annotationService.findAnnotations(entryAccession).stream()
                .filter(annotation -> annotation.isSpecificForIsoform(isoName))
                .collect(Collectors.toList());
        Overview overview = overviewService.findOverviewByEntry(entryAccession);

        return new IsoformPEFFHeaderBuilder(isoform, isoformAnnotations, overview,
                terminologyService::findPsiModAccession, terminologyService::findPsiModName);
    }

    @Test
    public void testUniqueIdFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withEverything().build();

        Assert.assertEquals("\\DbUniqueId=NX_P52701-1", peff.getIsoformAccessionFormat());
    }

    @Test
    public void testPNameFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withProteinNameFormat().build();

        Assert.assertEquals("\\PName=DNA mismatch repair protein Msh6 isoform GTBP-N", peff.getProteinNameFormat());
    }

    @Test
    public void testGNameFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withGeneNameFormat().build();

        Assert.assertEquals("\\GName=MSH6", peff.getGeneNameFormat());
    }

    @Test
    public void testNcbiTaxonomyIdentifierFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withNcbiTaxonomyIdentifierFormat().build();

        Assert.assertEquals("\\NcbiTaxId=9606", peff.getNcbiTaxonomyIdentifierFormat());
    }

    @Test
    public void testTaxonomyNameFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withTaxonomyNameFormat().build();

        Assert.assertEquals("\\TaxName=Homo Sapiens", peff.getTaxonomyNameFormat());
    }

    @Test
    public void testSequenceLengthFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withSequenceLengthFormat().build();

        Assert.assertEquals("\\Length=1360", peff.getSequenceLengthFormat());
    }

    @Test
    public void testSequenceVersionFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withSequenceVersionFormat().build();

        Assert.assertEquals("\\SV=2", peff.getSequenceVersionFormat());
    }

    @Test
    public void testEntryVersionFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withEntryVersionFormat().build();

        Assert.assertTrue(peff.getEntryVersionFormat().matches("\\\\EV=\\d+"));
    }

    @Test
    public void testProteinEvidenceFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withProteinEvidenceFormat().build();

        Assert.assertEquals("\\PE=1", peff.getProteinEvidenceFormat());
    }

    @Test
    public void testModResPsiFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withModResFormats().build();

        Assert.assertEquals("\\ModResPsi=(8|MOD:00048|O4'-phospho-L-tyrosine)(14|MOD:00046|O-phospho-L-serine)(41|MOD:00046|O-phospho-L-serine)(43|MOD:00046|O-phospho-L-serine)(51|MOD:00046|O-phospho-L-serine)(65|MOD:00046|O-phospho-L-serine)(70|MOD:00064|N6-acetyl-L-lysine)(79|MOD:00046|O-phospho-L-serine)(86|MOD:00047|O-phospho-L-threonine)(87|MOD:00046|O-phospho-L-serine)(91|MOD:00046|O-phospho-L-serine)(137|MOD:00046|O-phospho-L-serine)(139|MOD:00047|O-phospho-L-threonine)(144|MOD:00046|O-phospho-L-serine)(200|MOD:00046|O-phospho-L-serine)(212|MOD:00047|O-phospho-L-threonine)(213|MOD:00047|O-phospho-L-threonine)(214|MOD:00048|O4'-phospho-L-tyrosine)(216|MOD:00047|O-phospho-L-threonine)(219|MOD:00046|O-phospho-L-serine)(227|MOD:00046|O-phospho-L-serine)(252|MOD:00046|O-phospho-L-serine)(254|MOD:00046|O-phospho-L-serine)(256|MOD:00046|O-phospho-L-serine)(261|MOD:00046|O-phospho-L-serine)(269|MOD:00047|O-phospho-L-threonine)(274|MOD:00046|O-phospho-L-serine)(275|MOD:00046|O-phospho-L-serine)(279|MOD:00046|O-phospho-L-serine)(280|MOD:00046|O-phospho-L-serine)(285|MOD:00046|O-phospho-L-serine)(292|MOD:00046|O-phospho-L-serine)(305|MOD:00047|O-phospho-L-threonine)(309|MOD:00046|O-phospho-L-serine)(328|MOD:00046|O-phospho-L-serine)(330|MOD:00046|O-phospho-L-serine)(331|MOD:00046|O-phospho-L-serine)(334|MOD:00134|N6-glycyl-L-lysine)(486|MOD:00047|O-phospho-L-threonine)(488|MOD:00047|O-phospho-L-threonine)(504|MOD:00064|N6-acetyl-L-lysine)(519|MOD:00134|N6-glycyl-L-lysine)(610|MOD:00134|N6-glycyl-L-lysine)(632|MOD:00134|N6-glycyl-L-lysine)(728|MOD:00134|N6-glycyl-L-lysine)(771|MOD:00134|N6-glycyl-L-lysine)(824|MOD:00134|N6-glycyl-L-lysine)(830|MOD:00046|O-phospho-L-serine)(840|MOD:00046|O-phospho-L-serine)(935|MOD:00046|O-phospho-L-serine)(1010|MOD:00047|O-phospho-L-threonine)(1296|MOD:00134|N6-glycyl-L-lysine)(1315|MOD:00134|N6-glycyl-L-lysine)(1325|MOD:00134|N6-glycyl-L-lysine)(1352|MOD:00134|N6-glycyl-L-lysine)(1358|MOD:00134|N6-glycyl-L-lysine)",
                peff.getModResPsiFormat());
    }

    @Test
    public void testModResFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_Q15582-1")
                .withModResFormats().build();

        Assert.assertEquals("\\ModRes=(28||O-linked (GalNAc...) serine)", peff.getModResFormat());
    }

    @Test
    public void testModResPsiFormat2() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_Q15582-1")
                .withModResFormats().build();

        Assert.assertEquals("\\ModResPsi=(37|MOD:00046|O-phospho-L-serine)(49|MOD:00798|half cystine)(85|MOD:00798|half cystine)(65|MOD:00765|cysteinylation (disulfide with free L-cysteine))(74|MOD:00798|half cystine)(339|MOD:00798|half cystine)(84|MOD:00798|half cystine)(97|MOD:00798|half cystine)(214|MOD:00798|half cystine)(317|MOD:00798|half cystine)(473|MOD:00798|half cystine)(478|MOD:00798|half cystine)(649|MOD:00046|O-phospho-L-serine)",
                peff.getModResPsiFormat());
    }

    @Test
    public void testVariantSimpleFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withVariantSimpleFormat().build();

        String expected = "\\VariantSimple=(2|L)(4|*)(5|T)(6|P)(8|H)(9|G)(9|R)(10|L)(11|L)(12|S)(12|T)(13|Q)(13|R)(13|T)(14|A)(14|F)(15|L)(15|S)(16|V)(18|T)(20|D)(20|V)(21|K)(21|S)(22|E)(23|G)(23|T)(24|L)(25|P)(25|S)(25|V)(26|G)(26|W)(28|L)(29|S)(32|C)(32|D)(33|P)(34|V)(35|V)(36|S)(36|V)(37|G)(37|T)(37|V)(38|S)(39|A)(39|E)(39|R)(40|P)(40|V)(41|Y)(42|S)(43|T)(43|Y)(44|L)(46|R)(48|G)(49|D)(49|S)(49|T)(49|V)(50|C)(50|R)(51|R)(52|V)(54|A)(57|A)(59|S)(59|T)(61|V)(62|C)(62|H)(62|L)(62|P)(62|S)(63|C)(63|F)(63|P)(63|Y)(64|P)(64|T)(65|L)(66|Q)(67|T)(68|R)(70|E)(72|V)(75|R)(76|P)(77|W)(78|K)(79|P)(81|T)(81|V)(83|T)(84|V)(85|L)(85|S)(86|I)(86|S)(88|W)(88|Y)(89|E)(91|L)(92|L)(95|V)(96|F)(97|S)(98|S)(99|N)(102|V)(103|F)(103|H)(105|C)(107|L)(110|I)(112|D)(112|S)(114|R)(115|S)(116|N)(117|E)(118|K)(119|C)(120|T)(120|V)(121|C)(121|H)(122|D)(122|K)(123|N)(124|R)(125|E)(128|H)(128|L)(131|L)(132|E)(132|H)(132|P)(138|L)(139|A)(142|*)(144|I)(147|H)(150|R)(150|S)(151|F)(151|H)(153|S)(155|R)(156|*)(157|M)(159|V)(160|H)(162|A)(163|S)(164|L)(164|P)(164|Y)(165|C)(168|E)(168|G)(169|E)(169|N)(170|S)(174|K)(175|G)(176|V)(177|*)(177|H)(178|C)(178|H)(178|S)(179|S)(181|K)(182|V)(185|E)(185|T)(186|E)(187|T)(189|*)(189|R)(192|A)(192|V)(195|F)(197|H)(198|A)(199|L)(200|*)(202|R)(207|*)(208|I)(208|V)(209|*)(210|A)(211|S)(212|I)(213|P)(214|*)(214|S)(215|G)(215|I)(215|L)(216|I)(217|G)(217|V)(217|Y)(218|*)(219|N)(220|A)(220|D)(220|G)(221|D)(221|K)(222|G)(223|D)(223|S)(224|*)(224|K)(225|M)(226|D)(226|G)(227|G)(227|I)(227|N)(228|K)(229|G)(229|K)(229|Q)(232|*)(233|R)(233|S)(236|*)(238|Y)(240|*)(240|Q)(241|N)(241|R)(242|T)(243|C)(243|H)(243|S)(244|*)(245|L)(247|N)(247|Q)(248|*)(248|G)(248|P)(248|Q)(249|M)(250|A)(251|M)(251|V)(252|*)(252|A)(254|F)(258|F)(258|T)(258|V)(261|F)(265|C)(269|S)(270|M)(272|*)(272|D)(273|E)(273|R)(273|V)(274|C)(274|N)(275|T)(277|D)(277|G)(278|K)(281|V)(285|C)(285|I)(286|K)(287|C)(287|T)(288|*)(289|A)(289|D)(289|E)(290|P)(291|D)(292|I)(292|R)(293|S)(295|E)(295|I)(295|R)(297|T)(298|*)(298|Q)(299|N)(300|L)(300|P)(300|Q)(300|W)(301|E)(302|I)(302|K)(302|T)(303|I)(305|S)(306|R)(309|C)(309|F)(309|T)(309|Y)(310|P)(310|V)(312|K)(313|R)(314|I)(314|N)(314|R)(315|C)(315|F)(316|K)(319|A)(319|M)(320|L)(320|S)(320|T)(321|*)(322|T)(322|V)(323|I)(324|N)(326|V)(327|A)(327|S)(328|R)(328|T)(330|*)(330|P)(332|A)(333|A)(333|I)(335|H)(336|S)(339|D)(339|G)(339|P)(340|S)(341|C)(342|S)(342|V)(343|L)(343|R)(344|E)(345|K)(345|S)(346|C)(346|F)(348|F)(349|*)(349|R)(350|S)(350|V)(351|N)(352|I)(354|V)(355|S)(356|A)(357|N)(358|E)(358|H)(358|N)(360|C)(360|G)(360|I)(360|N)(361|C)(361|H)(362|L)(363|A)(363|M)(365|R)(365|S)(366|F)(367|N)(369|I)(370|S)(373|F)(373|P)(376|A)(376|G)(377|N)(377|Q)(377|R)(377|T)(378|K)(378|S)(380|A)(381|K)(382|Y)(383|G)(384|G)(384|K)(384|M)(385|W)(387|H)(388|D)(388|P)(389|L)(390|E)(390|N)(391|L)(392|E)(393|G)(395|I)(395|K)(396|V)(397|C)(397|H)(398|E)(398|L)(398|M)(399|L)(403|F)(404|S)(405|C)(408|L)(408|R)(412|T)(413|*)(413|S)(414|C)(419|E)(423|I)(423|V)(424|L)(425|V)(427|D)(428|E)(430|R)(431|T)(432|C)(432|L)(432|S)(433|*)(435|P)(439|G)(442|T)(446|D)(447|M)(448|R)(449|P)(450|A)(452|L)(452|V)(453|R)(455|T)(456|*)(457|D)(457|P)(457|V)(459|C)(460|R)(462|T)(463|*)(464|F)(468|C)(468|H)(468|P)(469|*)(469|C)(469|F)(469|I)(471|E)(472|C)(473|V)(474|A)(480|L)(482|*)(482|Q)(483|L)(484|K)(484|Q)(485|*)(486|I)(486|S)(488|A)(490|K)(491|T)(492|V)(493|*)(494|S)(495|*)(496|Y)(497|T)(498|R)(500|T)(501|Y)(502|T)(503|C)(508|M)(509|A)(509|L)(510|G)(510|K)(511|G)(512|G)(513|T)(513|V)(516|N)(519|N)(521|I)(521|S)(522|H)(522|K)(522|R)(523|P)(524|*)(524|H)(525|N)(526|M)(529|C)(529|D)(529|V)(531|T)(533|D)(535|C)(536|G)(536|N)(537|E)(538|S)(540|I)(540|V)(541|G)(541|R)(543|R)(546|G)(546|Q)(549|C)(549|F)(549|Y)(550|C)(551|D)(552|Q)(554|C)(554|H)(556|C)(556|F)(556|H)(558|A)(559|Y)(560|L)(564|*)(565|P)(566|A)(566|R)(567|N)(568|L)(568|V)(570|V)(571|D)(571|R)(572|H)(574|*)(574|T)(575|Y)(577|C)(577|G)(577|H)(578|Y)(580|L)(582|L)(585|P)(586|L)(587|V)(588|R)(590|L)(591|S)(593|E)(594|I)(596|I)(597|Q)(598|R)(599|R)(601|V)(602|*)(604|G)(605|A)(605|S)(607|R)(608|V)(610|N)(612|*)(615|F)(615|S)(616|C)(617|I)(618|*)(618|R)(619|*)(619|D)(620|S)(622|L)(623|A)(623|H)(623|L)(623|S)(624|S)(624|V)(625|F)(626|H)(632|E)(637|*)(637|P)(639|D)(639|K)(641|*)(642|C)(644|S)(646|R)(648|M)(649|V)(651|M)(651|T)(651|V)(653|A)(653|L)(653|M)(654|I)(654|T)(655|I)(660|Q)(662|V)(665|D)(665|G)(666|F)(666|P)(667|H)(667|V)(668|C)(669|T)(669|V)(670|R)(670|V)(673|A)(673|L)(675|D)(675|K)(676|R)(677|I)(677|T)(678|Q)(679|S)(680|T)(681|F)(682|C)(682|F)(683|V)(684|V)(685|A)(686|C)(686|D)(687|*)(689|L)(689|V)(690|F)(690|S)(691|F)(692|N)(692|Q)(692|R)(694|R)(695|P)(696|T)(698|E)(698|K)(700|F)(700|I)(702|*)(702|L)(703|V)(704|G)(706|S)(709|*)(710|T)(711|S)(713|N)(714|C)(714|F)(714|P)(716|A)(716|I)(719|I)(720|I)(721|G)(721|I)(721|S)(722|Y)(723|A)(724|G)(724|V)(725|M)(725|V)(726|L)(726|S)(726|Y)(727|A)(727|N)(727|S)(728|R)(728|T)(730|C)(731|*)(732|*)(732|P)(732|Q)(734|E)(734|M)(735|I)(737|G)(737|V)(740|*)(742|K)(742|T)(744|V)(745|M)(745|V)(747|R)(749|E)(749|R)(750|K)(750|P)(753|C)(754|P)(754|S)(757|I)(761|G)(761|K)(761|T)(763|G)(764|I)(764|N)(765|W)(766|Q)(766|R)(767|I)(767|S)(768|A)(768|H)(768|R)(770|D)(770|V)(771|M)(772|Q)(772|W)(773|P)(774|V)(775|Q)(777|*)(777|R)(780|G)(780|V)(781|S)(781|T)(783|S)(785|Q)(786|H)(787|V)(788|V)(791|C)(791|H)(791|S)(792|P)(795|T)(796|G)(796|K)(798|V)(800|A)(800|I)(800|L)(803|G)(804|*)(806|C)(807|K)(807|Q)(809|A)(810|Q)(810|V)(815|F)(815|I)(815|R)(817|N)(818|F)(818|V)(826|D)(827|D)(828|A)(828|I)(831|A)(832|R)(834|N)(835|*)(835|E)(837|Q)(837|Y)(838|L)(840|R)(842|G)(842|P)(843|V)(844|I)(847|G)(847|K)(850|*)(850|C)(850|H)(851|G)(851|N)(854|I)(854|M)(854|N)(856|F)(857|G)(857|N)(860|F)(861|S)(863|A)(863|G)(864|E)(865|L)(866|T)(867|G)(867|I)(868|I)(868|T)(869|R)(872|V)(875|I)(875|T)(877|*)(877|G)(878|A)(878|G)(879|*)(880|E)(880|Y)(881|S)(883|T)(884|C)(885|*)(885|N)(886|V)(888|N)(889|H)(889|P)(890|F)(890|L)(891|M)(891|T)(891|V)(893|Q)(893|V)(894|*)(895|R)(896|I)(897|H)(897|S)(899|*)(899|K)(901|C)(901|H)(901|S)(903|L)(904|E)(905|*)(905|M)(907|A)(908|*)(908|K)(909|F)(911|*)(911|L)(911|Q)(912|*)(912|R)(913|G)(915|D)(917|E)(918|R)(919|D)(921|V)(922|*)(922|L)(922|Q)(924|S)(925|*)(926|F)(926|P)(927|M)(927|T)(928|A)(928|I)(928|N)(930|E)(930|R)(932|D)(936|E)(936|N)(939|*)(940|S)(942|D)(943|Y)(944|V)(946|*)(949|E)(950|I)(951|F)(952|P)(953|G)(953|K)(956|G)(956|K)(958|R)(959|C)(959|H)(960|S)(960|T)(961|I)(961|T)(962|M)(962|T)(967|V)(968|G)(969|C)(969|F)(969|S)(970|C)(974|G)(976|C)(976|H)(977|*)(978|*)(978|E)(979|P)(979|V)(981|V)(983|D)(983|K)(983|Q)(984|H)(984|T)(985|L)(987|A)(987|I)(988|C)(988|H)(988|L)(988|P)(989|S)(991|A)(991|L)(992|D)(992|G)(992|K)(993|K)(994|*)(994|H)(995|*)(995|K)(997|R)(998|T)(999|A)(1000|N)(1001|R)(1002|S)(1005|*)(1005|Q)(1006|H)(1007|*)(1007|C)(1008|I)(1009|E)(1009|I)(1010|A)(1010|I)(1013|N)(1013|T)(1014|R)(1015|F)(1017|H)(1018|I)(1021|D)(1021|G)(1023|*)(1023|G)(1024|Q)(1024|W)(1026|H)(1026|Y)(1027|I)(1027|L)(1028|L)(1029|F)(1031|V)(1033|R)(1034|G)(1034|P)(1034|Q)(1034|W)(1035|*)(1035|L)(1035|P)(1035|Q)(1036|Q)(1037|L)(1038|C)(1038|H)(1044|*)(1045|T)(1046|G)(1047|*)(1048|*)(1048|E)(1049|F)(1051|I)(1052|G)(1052|K)(1054|F)(1054|M)(1054|V)(1055|P)(1055|T)(1055|V)(1056|L)(1057|S)(1059|A)(1063|M)(1063|R)(1064|T)(1064|V)(1065|K)(1066|C)(1067|I)(1067|T)(1068|*)(1068|G)(1068|L)(1068|P)(1068|Q)(1069|E)(1069|R)(1070|C)(1070|D)(1072|D)(1072|V)(1073|A)(1073|R)(1073|S)(1074|L)(1074|S)(1074|T)(1074|V)(1076|C)(1076|G)(1076|H)(1077|Q)(1078|A)(1078|L)(1080|R)(1082|L)(1082|P)(1082|S)(1083|Q)(1085|S)(1086|A)(1086|H)(1086|L)(1086|R)(1087|A)(1087|H)(1087|L)(1087|R)(1087|S)(1087|T)(1088|C)(1088|L)(1088|P)(1088|S)(1090|D)(1090|R)(1091|R)(1091|V)(1093|R)(1094|A)(1094|L)(1095|C)(1095|H)(1095|S)(1096|R)(1097|L)(1097|S)(1098|Y)(1100|M)(1100|R)(1101|N)(1102|P)(1104|L)(1105|R)(1105|V)(1107|N)(1110|S)(1110|T)(1112|N)(1113|M)(1113|T)(1113|V)(1115|T)(1117|F)(1117|R)(1117|Y)(1118|K)(1119|*)(1121|D)(1121|G)(1121|K)(1122|E)(1123|*)(1127|T)(1128|C)(1128|F)(1129|G)(1130|M)(1132|L)(1138|E)(1139|C)(1139|S)(1140|*)(1142|M)(1144|L)(1144|R)(1146|*)(1146|P)(1147|T)(1147|V)(1148|A)(1148|R)(1148|S)(1149|*)(1149|F)(1149|I)(1150|F)(1151|G)(1153|T)(1154|D)(1155|*)(1155|H)(1155|R)(1156|I)(1156|K)(1156|T)(1156|V)(1157|A)(1157|C)(1157|S)(1158|R)(1159|*)(1159|C)(1160|F)(1160|I)(1160|L)(1161|A)(1162|D)(1162|P)(1163|*)(1163|D)(1163|G)(1163|V)(1172|K)(1172|T)(1173|M)(1175|I)(1175|S)(1176|*)(1177|V)(1178|D)(1179|D)(1180|*)(1181|E)(1181|G)(1181|N)(1183|K)(1184|T)(1185|A)(1186|D)(1186|R)(1187|G)(1188|N)(1189|A)(1189|I)(1190|L)(1191|L)(1193|K)(1195|T)(1196|*)(1196|K)(1196|Q)(1197|A)(1199|N)(1199|T)(1200|M)(1200|V)(1201|F)(1201|V)(1202|R)(1202|T)(1202|V)(1203|Q)(1203|Y)(1204|E)(1204|M)(1204|T)(1205|I)(1206|G)(1207|D)(1207|Q)(1207|Y)(1210|L)(1211|P)(1212|A)(1212|M)(1213|V)(1214|*)(1216|V)(1217|G)(1217|K)(1218|R)(1218|S)(1219|D)(1219|I)(1219|N)(1220|T)(1222|V)(1225|K)(1225|M)(1226|E)(1227|L)(1227|T)(1228|P)(1229|H)(1229|S)(1230|G)(1230|V)(1232|D)(1232|L)(1233|Q)(1233|T)(1234|*)(1234|K)(1234|Q)(1236|P)(1237|D)(1238|A)(1238|P)(1238|S)(1239|T)(1241|Y)(1242|C)(1242|H)(1242|L)(1242|S)(1243|A)(1243|K)(1243|S)(1244|V)(1247|A)(1247|S)(1248|D)(1248|Y)(1250|R)(1253|A)(1253|E)(1253|I)(1253|L)(1254|D)(1256|*)(1256|N)(1256|S)(1258|*)(1258|E)(1260|I)(1261|V)(1262|L)(1263|C)(1263|H)(1266|L)(1266|Y)(1267|T)(1268|E)(1269|M)(1269|S)(1270|K)(1270|T)(1270|V)(1271|L)(1273|K)(1273|S)(1274|K)(1275|Y)(1278|L)(1278|R)(1278|T)(1278|Y)(1279|N)(1280|*)(1281|D)(1281|Q)(1282|N)(1282|P)(1283|V)(1284|M)(1284|N)(1285|L)(1286|F)(1286|P)(1289|L)(1293|G)(1294|F)(1295|L)(1296|E)(1296|Q)(1298|C)(1299|D)(1303|G)(1303|T)(1304|K)(1304|S)(1307|D)(1307|H)(1310|D)(1311|D)(1311|K)(1316|E)(1316|R)(1317|D)(1317|Q)(1318|K)(1319|R)(1320|S)(1321|*)(1321|G)(1321|S)(1322|*)(1322|V)(1324|D)(1324|Q)(1325|*)(1325|M)(1325|R)(1326|I)(1326|L)(1326|T)(1327|H)(1327|S)(1329|L)(1330|R)(1331|*)(1331|L)(1331|Q)(1332|I)(1332|S)(1333|L)(1334|P)(1334|Q)(1334|W)(1335|A)(1335|D)(1335|G)(1335|K)(1336|I)(1339|G)(1339|V)(1341|G)(1342|S)(1342|T)(1343|*)(1345|I)(1346|N)(1347|P)(1348|D)(1350|A)(1350|L)(1352|Q)(1353|W)(1354|P)(1354|Q)(1355|S)(1356|F)(1357|M)(1357|N)(1358|E)(1358|N)(1359|*)(1359|K)";
        String result = peff.getVariantSimpleFormat();
        System.out.println("expect:"+expected);
        System.out.println("result:"+result);
        System.out.print("differ:");
        for (int i=0;i<Math.min(expected.length(),result.length());i++) {
            String diff = expected.charAt(i) != result.charAt(i) ? "x" : " ";
            System.out.print(diff);
        }
        System.out.println("");
    }

    @Test
    public void testVariantComplexFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withVariantComplexFormat().build();

        String expected = "\\VariantComplex=(53|53|AGP)(55|55|PGP)(57|57|PGP)(59|59|PRP)(259|259|)(272|272|)(282|282|VG)(362|362|PT)(374|374|)(385|385|)(405|405|)(492|492|)(510|510|)(538|539|*V)(540|540|)(543|544|)(546|546|)(638|638|)(641|641|)(754|754|)(768|768|)(809|809|)(854|854|)(881|881|KS)(882|885|*)(887|887|LI)(942|942|)(942|943|VH)(971|971|)(1014|1014|)(1014|1014|KL)(1067|1071|)(1088|1088|)(1107|1107|)(1119|1120|G*)(1129|1130|L)(1158|1158|)(1162|1162|)(1162|1162|AA)(1173|1176|)(1173|1176|SVY*)(1183|1183|)(1201|1201|LI)(1232|1232|)(1242|1242|)(1242|1243|P)(1244|1244|)(1244|1244|LL)(1248|1257|)(1254|1254|)(1254|1254|EE)(1254|1255|DY)(1282|1282|TI)(1301|1304|)(1310|1310|EE)(1317|1317|HRKAREFEKN)(1320|1321|EA)(1320|1321|GK)(1325|1325|)(1337|1337|CLAS)(1343|1343|ST)(1345|1347|)(1356|1357|*S)";
        String result = peff.getVariantComplexFormat();
        System.out.println("expect:"+expected);
        System.out.println("result:"+result);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testProcessedMoleculeFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P52701-1")
                .withProcessedMoleculeFormat().build();

        Assert.assertEquals("\\Processed=(1|1360|mature protein)", peff.getProcessedMoleculeFormat());
    }

    @Test
    public void testProcessedInsulinMoleculeFormat() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_P01308-1")
                .withProcessedMoleculeFormat().build();

        Assert.assertEquals("\\Processed=(1|24|signal peptide)(25|54|mature protein)(57|87|maturation peptide)(90|110|mature protein)",
                peff.getProcessedMoleculeFormat());
    }

    @Test
    public void testUndefinedPsiPTM() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_Q02880-1")
                .withModResFormats().build();

        String modResPSI = peff.getModResPsiFormat();
        Assert.assertTrue(!modResPSI.contains("(969|"));

        String modRes = peff.getModResFormat();
        Assert.assertEquals("\\ModRes=(969||PolyADP-ribosyl aspartic acid)", modRes);
    }

    @Test
    public void testUndefinedPsiPTM2() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_Q96B02-1")
                .withModResFormats().build();

        String modResPSI = peff.getModResPsiFormat();
        Assert.assertTrue(!modResPSI.contains("(1|"));

        String modRes = peff.getModResFormat();
        Assert.assertEquals("\\ModRes=(1||Peptide (Met-Gly) (interchain with G-...))", modRes);
    }

    @Test
    public void testDisulfidePosIsNotNull() throws Exception {

        IsoformPEFFHeader peff = newIsoformPEFFHeaderBuilder("NX_A2VEC9-1")
                .withModResFormats().build();
        Assert.assertTrue(!peff.getModResFormat().contains("null"));

        peff = newIsoformPEFFHeaderBuilder("NX_O43240-1")
                .withModResFormats().build();
        Assert.assertTrue(!peff.getModResFormat().contains("null"));

        peff = newIsoformPEFFHeaderBuilder("NX_P04275-1")
                .withModResFormats().build();
        Assert.assertTrue(!peff.getModResFormat().contains("null"));
    }
}