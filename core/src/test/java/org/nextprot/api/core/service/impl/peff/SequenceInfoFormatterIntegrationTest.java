package org.nextprot.api.core.service.impl.peff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.service.PeffService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class SequenceInfoFormatterIntegrationTest extends CoreUnitBaseTest {

    @Autowired
    private PeffService peffService;

    /*
     * >nxp:NX_P52701-1
     * \DbUniqueId=NX_P52701-1
     * \PName=DNA mismatch repair protein Msh6 isoform GTBP-N
     * \GName=MSH6
     * \NcbiTaxId=9606
     * \TaxName=Homo Sapiens
     * \Length=1360
     * \SV=329
     * \EV=759
     * \PE=1
     * \ModResPsi=(8|MOD:00048|O4'-phospho-L-tyrosine)(14|MOD:00046|O-phospho-L-serine)(41|MOD:00046|O-phospho-L-serine)(43|MOD:00046|O-phospho-L-serine)(51|MOD:00046|O-phospho-L-serine)(65|MOD:00046|O-phospho-L-serine)(70|MOD:00064|N6-acetyl-L-lysine)(79|MOD:00046|O-phospho-L-serine)(86|MOD:00047|O-phospho-L-threonine)(87|MOD:00046|O-phospho-L-serine)(91|MOD:00046|O-phospho-L-serine)(137|MOD:00046|O-phospho-L-serine)(139|MOD:00047|O-phospho-L-threonine)(144|MOD:00046|O-phospho-L-serine)(200|MOD:00046|O-phospho-L-serine)(212|MOD:00047|O-phospho-L-threonine)(213|MOD:00047|O-phospho-L-threonine)(214|MOD:00048|O4'-phospho-L-tyrosine)(216|MOD:00047|O-phospho-L-threonine)(219|MOD:00046|O-phospho-L-serine)(227|MOD:00046|O-phospho-L-serine)(252|MOD:00046|O-phospho-L-serine)(254|MOD:00046|O-phospho-L-serine)(256|MOD:00046|O-phospho-L-serine)(261|MOD:00046|O-phospho-L-serine)(269|MOD:00047|O-phospho-L-threonine)(274|MOD:00046|O-phospho-L-serine)(275|MOD:00046|O-phospho-L-serine)(279|MOD:00046|O-phospho-L-serine)(280|MOD:00046|O-phospho-L-serine)(285|MOD:00046|O-phospho-L-serine)(292|MOD:00046|O-phospho-L-serine)(305|MOD:00047|O-phospho-L-threonine)(309|MOD:00046|O-phospho-L-serine)(328|MOD:00046|O-phospho-L-serine)(330|MOD:00046|O-phospho-L-serine)(331|MOD:00046|O-phospho-L-serine)(486|MOD:00047|O-phospho-L-threonine)(488|MOD:00047|O-phospho-L-threonine)(504|MOD:00064|N6-acetyl-L-lysine)(830|MOD:00046|O-phospho-L-serine)(840|MOD:00046|O-phospho-L-serine)(935|MOD:00046|O-phospho-L-serine)(1010|MOD:00047|O-phospho-L-threonine)
     * \VariantSimple=(2|L)(4|*)(5|T)(6|P)(8|H)(9|R)(9|G)(10|L)(11|L)(12|S)(12|T)(13|R)(13|T)(14|A)(14|F)(15|S)(15|L)(16|V)(18|T)(20|D)(20|V)(21|S)(21|K)(23|T)(24|L)(25|P)(25|S)(25|V)(26|W)(28|L)(29|S)(32|C)(32|D)(33|G)(33|C)(33|P)(34|V)(35|V)(36|V)(36|S)(37|V)(37|T)(37|G)(38|S)(39|A)(39|R)(39|E)(40|P)(40|V)(41|Y)(42|S)(43|T)(43|Y)(44|L)(46|R)(48|G)(49|D)(49|S)(49|T)(49|V)(50|R)(50|C)(51|R)(52|V)(54|A)(57|A)(59|S)(61|V)(62|S)(62|H)(63|P)(63|Y)(63|F)(64|P)(64|T)(65|L)(66|Q)(67|T)(68|R)(70|E)(72|V)(75|R)(76|P)(77|W)(78|K)(79|P)(81|V)(81|T)(83|T)(83|G)(84|V)(85|S)(86|S)(86|I)(88|W)(89|E)(91|L)(92|L)(95|V)(97|S)(98|S)(99|N)(103|H)(103|F)(107|L)(110|I)(112|S)(112|D)(114|R)(115|S)(117|E)(118|K)(120|V)(120|T)(121|C)(121|H)(122|K)(122|D)(123|N)(124|R)(125|E)(128|H)(128|L)(131|L)(132|E)(132|P)(132|H)(138|L)(140|M)(142|*)(143|I)(144|I)(147|H)(150|S)(150|R)(151|F)(155|R)(156|*)(159|V)(160|H)(162|A)(163|S)(164|L)(164|Y)(165|C)(168|G)(169|N)(169|E)(170|S)(171|G)(172|T)(174|K)(176|V)(177|*)(178|C)(178|H)(179|S)(181|K)(182|V)(185|T)(185|E)(186|E)(187|T)(189|*)(192|A)(192|V)(193|V)(195|F)(197|H)(198|A)(199|L)(200|*)(208|V)(209|*)(210|A)(211|S)(212|I)(213|P)(214|S)(214|*)(215|L)(215|G)(215|I)(216|I)(217|G)(217|Y)(218|*)(219|N)(220|A)(220|G)(220|D)(221|K)(221|D)(222|G)(223|D)(223|S)(224|*)(226|D)(226|G)(227|N)(227|I)(227|G)(228|K)(229|G)(229|K)(229|Q)(232|*)(233|S)(233|R)(236|*)(238|Y)(240|Q)(240|*)(241|R)(243|C)(243|H)(243|S)(244|*)(245|L)(247|N)(247|Q)(248|G)(248|Q)(248|P)(248|*)(249|M)(250|A)(251|M)(251|V)(252|A)(252|*)(254|F)(255|K)(258|T)(258|V)(261|F)(264|D)(265|C)(269|S)(270|M)(272|D)(272|*)(273|E)(273|R)(273|V)(274|N)(274|C)(275|T)(277|D)(277|G)(278|K)(279|N)(281|V)(285|C)(285|I)(286|K)(287|C)(287|T)(288|*)(289|D)(289|A)(289|E)(290|P)(291|D)(292|R)(292|I)(293|S)(295|I)(295|E)(295|R)(297|T)(298|Q)(298|*)(299|N)(300|L)(300|P)(300|W)(300|Q)(302|T)(302|K)(303|I)(304|M)(305|S)(306|R)(309|C)(309|F)(309|Y)(309|T)(310|P)(311|N)(312|K)(313|R)(314|R)(314|I)(314|N)(315|C)(315|F)(316|K)(317|R)(319|M)(319|A)(320|T)(320|L)(321|*)(322|T)(323|I)(324|N)(326|V)(327|A)(327|S)(328|R)(330|P)(330|*)(332|A)(333|I)(335|H)(336|S)(339|D)(339|P)(339|G)(340|S)(341|C)(342|S)(342|V)(343|L)(343|R)(344|E)(345|K)(345|S)(346|F)(346|C)(348|F)(349|R)(349|*)(350|S)(350|V)(351|R)(351|Y)(351|N)(352|I)(354|V)(355|S)(356|A)(357|N)(358|H)(358|E)(360|N)(360|G)(360|I)(361|C)(361|H)(362|L)(363|A)(365|R)(365|S)(366|F)(367|N)(369|I)(370|S)(373|P)(376|A)(376|G)(377|T)(377|Q)(377|N)(378|K)(378|S)(381|K)(382|Y)(383|G)(384|M)(385|W)(387|H)(388|D)(388|P)(389|L)(390|E)(390|N)(391|L)(392|E)(393|G)(395|I)(396|V)(397|H)(397|C)(398|E)(398|L)(399|L)(403|F)(404|S)(405|C)(408|L)(412|T)(413|*)(414|*)(419|E)(423|I)(423|V)(424|L)(425|V)(427|D)(428|E)(430|R)(431|T)(432|C)(432|S)(432|L)(433|*)(435|P)(439|G)(442|T)(446|D)(447|M)(448|R)(449|P)(450|A)(452|V)(453|R)(455|T)(456|*)(457|D)(457|P)(459|C)(460|R)(462|T)(463|*)(464|F)(468|C)(468|H)(469|F)(469|C)(469|*)(471|E)(472|C)(474|A)(480|L)(482|*)(482|Q)(483|L)(484|Q)(485|*)(486|I)(486|S)(488|A)(490|K)(491|T)(492|V)(493|*)(494|S)(495|*)(496|Y)(497|T)(498|R)(500|T)(501|Y)(502|T)(503|C)(508|M)(509|L)(509|A)(510|G)(510|K)(511|G)(513|V)(516|N)(519|N)(521|S)(521|I)(522|H)(522|K)(522|R)(523|P)(524|*)(525|N)(529|C)(529|V)(529|D)(531|T)(533|D)(535|C)(536|N)(536|G)(537|E)(538|S)(540|I)(540|V)(541|R)(541|G)(543|R)(546|G)(546|Q)(549|F)(549|Y)(549|C)(550|C)(551|D)(552|Q)(554|H)(554|C)(556|F)(556|C)(558|A)(559|Y)(560|L)(564|T)(564|*)(565|P)(566|A)(566|R)(567|N)(568|L)(568|V)(570|V)(571|R)(571|D)(572|H)(574|T)(574|*)(575|Y)(577|C)(577|G)(577|H)(578|Y)(580|L)(582|L)(585|P)(586|A)(588|R)(590|L)(591|S)(593|E)(594|I)(596|I)(597|Q)(598|R)(599|R)(601|V)(602|*)(604|G)(605|S)(605|A)(607|R)(608|V)(610|N)(612|*)(615|S)(615|F)(616|C)(617|I)(618|R)(618|*)(619|*)(619|D)(620|S)(622|L)(623|H)(623|S)(623|L)(623|A)(624|V)(624|S)(625|F)(628|*)(632|E)(639|K)(639|D)(641|*)(642|C)(644|S)(646|R)(648|I)(649|V)(651|V)(651|T)(651|M)(653|L)(653|A)(654|I)(654|T)(655|I)(665|G)(665|D)(666|P)(666|F)(667|V)(667|H)(668|C)(668|P)(669|T)(669|V)(670|V)(670|R)(673|A)(673|L)(675|D)(675|K)(676|R)(677|T)(678|Q)(679|S)(680|T)(681|F)(682|C)(682|F)(683|V)(684|V)(685|A)(686|D)(687|*)(689|V)(689|L)(690|F)(690|S)(691|F)(692|N)(692|Q)(694|R)(696|T)(698|K)(698|E)(700|F)(702|L)(702|*)(703|V)(704|G)(706|S)(709|*)(710|T)(711|S)(713|G)(713|N)(714|C)(714|F)(714|P)(716|I)(716|A)(719|I)(720|I)(721|G)(721|S)(721|I)(722|Y)(723|A)(724|G)(724|V)(725|V)(725|M)(726|Y)(726|S)(726|L)(727|N)(727|S)(727|A)(728|R)(728|T)(730|C)(731|*)(732|P)(732|*)(732|Q)(734|M)(735|I)(737|V)(740|*)(742|K)(742|T)(744|V)(745|M)(745|V)(747|R)(749|R)(750|P)(750|K)(753|C)(754|P)(761|G)(761|T)(761|K)(764|N)(764|I)(765|W)(766|Q)(767|S)(767|I)(768|A)(768|H)(770|V)(770|D)(771|M)(772|W)(772|Q)(773|P)(774|V)(775|Q)(777|R)(777|*)(780|G)(780|V)(781|T)(781|S)(783|S)(786|H)(787|V)(788|V)(791|S)(791|H)(791|C)(792|P)(795|T)(796|K)(796|G)(798|V)(800|I)(800|A)(800|L)(803|G)(804|*)(806|C)(807|K)(807|Q)(809|A)(810|V)(810|Q)(812|I)(815|I)(815|F)(815|R)(817|N)(818|V)(820|M)(823|G)(826|D)(827|D)(828|I)(828|A)(831|A)(832|R)(834|N)(835|E)(835|*)(837|Q)(837|Y)(838|L)(840|R)(842|P)(842|G)(844|I)(847|K)(847|G)(850|H)(850|*)(850|C)(851|N)(851|G)(854|I)(854|N)(854|M)(856|F)(857|N)(857|G)(860|F)(863|A)(863|G)(864|E)(865|L)(866|T)(867|G)(867|I)(868|I)(868|T)(869|R)(875|T)(877|K)(877|*)(878|G)(878|A)(880|Y)(880|E)(883|T)(884|C)(885|N)(885|*)(886|V)(888|N)(889|H)(889|P)(890|L)(891|M)(891|V)(893|Q)(893|V)(894|*)(895|R)(896|I)(897|H)(897|S)(898|R)(899|*)(899|K)(901|S)(901|C)(901|H)(904|E)(905|*)(905|M)(907|A)(908|*)(909|F)(911|L)(911|*)(911|Q)(913|G)(915|D)(917|E)(918|R)(922|L)(922|*)(922|Q)(924|S)(926|F)(927|T)(928|I)(928|N)(930|R)(930|E)(932|D)(936|E)(936|N)(939|*)(940|S)(942|D)(943|Y)(944|V)(946|*)(949|E)(950|I)(951|F)(952|P)(953|K)(953|G)(956|G)(957|E)(958|R)(959|C)(959|H)(960|T)(960|S)(961|T)(961|I)(962|T)(962|M)(967|V)(968|G)(969|C)(969|S)(969|F)(970|C)(974|G)(976|C)(976|H)(977|*)(978|*)(979|V)(979|P)(981|V)(981|M)(983|K)(983|D)(984|H)(984|T)(985|L)(987|I)(987|A)(988|C)(988|L)(988|P)(988|H)(989|S)(991|L)(991|A)(992|K)(992|D)(992|G)(993|K)(994|H)(995|*)(997|R)(998|T)(999|A)(1000|N)(1001|R)(1002|S)(1005|*)(1005|Q)(1006|H)(1007|C)(1007|*)(1008|I)(1009|I)(1009|E)(1010|A)(1010|I)(1013|N)(1013|T)(1014|R)(1015|F)(1016|V)(1018|I)(1021|G)(1021|D)(1023|G)(1023|*)(1024|W)(1024|Q)(1026|Y)(1027|L)(1028|L)(1029|F)(1031|V)(1033|R)(1034|G)(1034|P)(1034|Q)(1034|W)(1035|L)(1035|Q)(1035|P)(1035|*)(1036|Q)(1037|L)(1038|C)(1044|*)(1046|G)(1047|*)(1048|E)(1048|*)(1049|F)(1051|I)(1052|G)(1052|K)(1054|F)(1054|M)(1054|V)(1055|T)(1055|P)(1055|V)(1057|S)(1058|H)(1059|A)(1063|M)(1064|V)(1064|T)(1065|K)(1066|C)(1067|I)(1067|T)(1068|G)(1068|P)(1068|Q)(1068|*)(1069|R)(1069|E)(1070|D)(1070|C)(1072|V)(1073|S)(1073|A)(1073|R)(1074|V)(1076|G)(1076|H)(1076|C)(1077|Q)(1078|A)(1078|L)(1079|L)(1080|R)(1082|L)(1083|Q)(1085|S)(1086|R)(1086|H)(1086|A)(1087|H)(1087|A)(1087|L)(1087|R)(1087|S)(1087|T)(1088|C)(1088|L)(1090|D)(1091|R)(1093|R)(1093|V)(1094|L)(1094|A)(1095|C)(1095|H)(1096|R)(1097|L)(1097|S)(1098|Y)(1100|R)(1100|M)(1101|N)(1102|P)(1104|L)(1105|R)(1107|N)(1110|T)(1110|S)(1112|N)(1113|M)(1113|V)(1115|T)(1117|F)(1117|R)(1118|K)(1119|G)(1119|*)(1121|D)(1121|G)(1121|K)(1123|*)(1128|F)(1128|C)(1130|M)(1132|L)(1133|I)(1138|E)(1139|C)(1139|S)(1140|*)(1142|M)(1144|L)(1144|R)(1146|P)(1146|*)(1147|T)(1147|V)(1148|S)(1148|R)(1148|A)(1149|F)(1149|*)(1149|I)(1150|F)(1151|G)(1153|T)(1154|D)(1155|H)(1155|*)(1156|K)(1156|T)(1156|I)(1157|S)(1157|C)(1157|A)(1158|R)(1159|C)(1159|*)(1160|L)(1160|F)(1160|I)(1161|A)(1162|P)(1162|D)(1163|D)(1163|*)(1163|V)(1172|T)(1173|M)(1175|S)(1175|I)(1176|K)(1177|V)(1178|D)(1179|D)(1180|*)(1181|E)(1181|N)(1181|G)(1184|T)(1185|A)(1186|D)(1187|G)(1188|N)(1189|A)(1190|L)(1191|L)(1193|K)(1195|T)(1196|K)(1196|Q)(1196|*)(1197|A)(1199|T)(1200|M)(1200|V)(1201|F)(1201|V)(1202|V)(1202|T)(1203|Y)(1204|E)(1204|T)(1205|I)(1206|G)(1207|Q)(1207|D)(1210|L)(1211|P)(1212|M)(1213|V)(1214|*)(1216|V)(1217|K)(1217|G)(1218|S)(1219|N)(1219|I)(1220|T)(1221|R)(1222|V)(1225|K)(1225|M)(1226|E)(1227|T)(1227|L)(1228|P)(1229|S)(1229|H)(1230|V)(1230|G)(1232|D)(1232|L)(1233|Q)(1233|T)(1234|*)(1234|Q)(1235|V)(1237|D)(1238|S)(1239|T)(1241|Y)(1242|L)(1242|S)(1242|H)(1243|S)(1243|A)(1243|K)(1247|A)(1247|S)(1248|Y)(1248|D)(1250|R)(1253|E)(1253|L)(1253|A)(1254|D)(1256|S)(1256|N)(1256|*)(1258|E)(1258|*)(1260|I)(1261|V)(1262|L)(1263|C)(1263|H)(1266|L)(1267|T)(1268|E)(1269|S)(1270|T)(1270|V)(1270|K)(1271|L)(1273|S)(1273|K)(1274|K)(1278|T)(1279|N)(1279|R)(1280|*)(1281|D)(1281|Q)(1282|P)(1282|N)(1283|V)(1284|M)(1285|L)(1286|P)(1289|L)(1293|G)(1294|F)(1295|L)(1296|E)(1296|Q)(1298|C)(1303|T)(1304|K)(1304|S)(1307|D)(1307|H)(1310|D)(1311|D)(1311|K)(1313|T)(1316|R)(1316|A)(1317|Q)(1317|D)(1319|R)(1320|S)(1321|S)(1321|G)(1322|V)(1322|*)(1324|Q)(1324|D)(1325|M)(1326|T)(1326|L)(1326|I)(1327|H)(1327|S)(1328|R)(1329|L)(1330|R)(1331|L)(1331|*)(1332|S)(1332|I)(1333|L)(1334|Q)(1334|W)(1335|A)(1335|D)(1335|G)(1335|K)(1336|I)(1339|G)(1341|G)(1342|T)(1342|S)(1343|*)(1345|I)(1346|N)(1347|P)(1348|D)(1350|L)(1352|Q)(1353|W)(1354|P)(1354|Q)(1355|S)(1356|F)(1358|E)(1358|N)(164|P)(457|V)(757|I)(1082|S)(358|N)(875|I)(1183|K)(1189|I)(1238|A)(1275|Y)(1278|R)(1278|L)(1299|D)(1303|G)(1331|Q)(1350|A)(1357|N)(322|V)(677|I)(861|S)(1045|T)(1359|K)(1359|*)
     * \VariantComplex=(53|53|AGP)(57|57|PRP)(57|57|PGP)(259|259|)(272|272|)(282|282|VG)(374|374|)(385|385|)(491|491|)(492|492|)(538|539|*V)(546|546|)(640|640|)(641|641|)(754|754|)(768|768|)(809|809|)(852|852|)(854|854|)(881|881|KS)(882|885|*)(887|887|LI)(942|942|)(1013|1013|)(1014|1014|)(1014|1014|KL)(1129|1130|L)(1158|1158|)(1162|1162|)(1162|1162|AA)(1173|1176|)(1183|1183|)(1231|1231|)(1232|1232|)(1242|1242|)(1242|1243|P)(1244|1244|LL)(1244|1244|)(1248|1257|)(1253|1253|VE)(1254|1254|EE)(1254|1254|)(1254|1255|DY)(1282|1282|TI)(1310|1310|EE)(1317|1317|HRKAREFEKN)(1325|1325|)(1343|1343|ST)(1345|1347|)
     * \Processed=(1|1360|mature protein)
     */
    @Test
    public void testUniqueIdFormat() throws Exception {

        Assert.assertEquals("\\DbUniqueId=NX_P52701-1", peffService.formatIsoformAccession("NX_P52701-1"));
    }

    @Test
    public void testPNameFormat() throws Exception {

        Assert.assertEquals("\\PName=DNA mismatch repair protein Msh6 isoform GTBP-N", peffService.formatProteinName("NX_P52701-1"));
    }

    @Test
    public void testGNameFormat() throws Exception {

        Assert.assertEquals("\\GName=MSH6", peffService.formatGeneName("NX_P52701-1"));
    }

    @Test
    public void testNcbiTaxonomyIdentifierFormat() throws Exception {

        Assert.assertEquals("\\NcbiTaxId=9606", peffService.formatNcbiTaxonomyIdentifier("NX_P52701-1"));
    }

    @Test
    public void testTaxonomyNameFormat() throws Exception {

        Assert.assertEquals("\\TaxName=Homo Sapiens", peffService.formatTaxonomyName("NX_P52701-1"));
    }

    @Test
    public void testSequenceLengthFormat() throws Exception {

        Assert.assertEquals("\\Length=1360", peffService.formatSequenceLength("NX_P52701-1"));
    }

    @Test
    public void testSequenceVersionFormat() throws Exception {

        Assert.assertEquals("\\SV=2", peffService.formatSequenceVersion("NX_P52701-1"));
    }

    @Test
    public void testEntryVersionFormat() throws Exception {

        Assert.assertEquals("\\EV=202", peffService.formatEntryVersion("NX_P52701-1"));
    }

    @Test
    public void testProteinEvidenceFormat() throws Exception {

        Assert.assertEquals("\\PE=1", peffService.formatProteinEvidence("NX_P52701-1"));
    }

    @Test
    public void testModResPsiFormat() throws Exception {

        Assert.assertEquals("\\ModResPsi=(8|MOD:00048|O4'-phospho-L-tyrosine)(14|MOD:00046|O-phospho-L-serine)(41|MOD:00046|O-phospho-L-serine)(43|MOD:00046|O-phospho-L-serine)(51|MOD:00046|O-phospho-L-serine)(65|MOD:00046|O-phospho-L-serine)(70|MOD:00064|N6-acetyl-L-lysine)(79|MOD:00046|O-phospho-L-serine)(86|MOD:00047|O-phospho-L-threonine)(87|MOD:00046|O-phospho-L-serine)(91|MOD:00046|O-phospho-L-serine)(137|MOD:00046|O-phospho-L-serine)(139|MOD:00047|O-phospho-L-threonine)(144|MOD:00046|O-phospho-L-serine)(200|MOD:00046|O-phospho-L-serine)(212|MOD:00047|O-phospho-L-threonine)(213|MOD:00047|O-phospho-L-threonine)(214|MOD:00048|O4'-phospho-L-tyrosine)(216|MOD:00047|O-phospho-L-threonine)(219|MOD:00046|O-phospho-L-serine)(227|MOD:00046|O-phospho-L-serine)(252|MOD:00046|O-phospho-L-serine)(254|MOD:00046|O-phospho-L-serine)(256|MOD:00046|O-phospho-L-serine)(261|MOD:00046|O-phospho-L-serine)(269|MOD:00047|O-phospho-L-threonine)(274|MOD:00046|O-phospho-L-serine)(275|MOD:00046|O-phospho-L-serine)(279|MOD:00046|O-phospho-L-serine)(280|MOD:00046|O-phospho-L-serine)(285|MOD:00046|O-phospho-L-serine)(292|MOD:00046|O-phospho-L-serine)(305|MOD:00047|O-phospho-L-threonine)(309|MOD:00046|O-phospho-L-serine)(328|MOD:00046|O-phospho-L-serine)(330|MOD:00046|O-phospho-L-serine)(331|MOD:00046|O-phospho-L-serine)(334|MOD:00134|N6-glycyl-L-lysine)(486|MOD:00047|O-phospho-L-threonine)(488|MOD:00047|O-phospho-L-threonine)(504|MOD:00064|N6-acetyl-L-lysine)(519|MOD:00134|N6-glycyl-L-lysine)(610|MOD:00134|N6-glycyl-L-lysine)(632|MOD:00134|N6-glycyl-L-lysine)(728|MOD:00134|N6-glycyl-L-lysine)(771|MOD:00134|N6-glycyl-L-lysine)(824|MOD:00134|N6-glycyl-L-lysine)(830|MOD:00046|O-phospho-L-serine)(840|MOD:00046|O-phospho-L-serine)(935|MOD:00046|O-phospho-L-serine)(1010|MOD:00047|O-phospho-L-threonine)(1296|MOD:00134|N6-glycyl-L-lysine)(1315|MOD:00134|N6-glycyl-L-lysine)(1325|MOD:00134|N6-glycyl-L-lysine)(1352|MOD:00134|N6-glycyl-L-lysine)(1358|MOD:00134|N6-glycyl-L-lysine)",
                peffService.formatModResPsi("NX_P52701-1"));
    }

    @Test
    public void testModResFormat() throws Exception {

        // NOTE: THIS FORMAT \ModRes=(28||O-linked (GalNAc...))(49||Disulfide)(85||Disulfide)(84||Disulfide)(97||Disulfide)(473||Disulfide)(478||Disulfide)(74||Disulfide)(339||Disulfide)(214||Disulfide)(317||Disulfide)
        Assert.assertEquals("\\ModRes=(28||O-linked (GalNAc...))(49||Disulfide)(85||Disulfide)(74||Disulfide)(339||Disulfide)(84||Disulfide)(97||Disulfide)(214||Disulfide)(317||Disulfide)(473||Disulfide)(478||Disulfide)",
                peffService.formatModRes("NX_Q15582-1"));
    }

    @Test
    public void testVariantSimpleFormat() throws Exception {

        Assert.assertEquals("\\VariantSimple=(2|L)(4|*)(5|T)(6|P)(8|H)(9|G)(9|R)(10|L)(11|L)(12|S)(12|T)(13|R)(13|T)(14|A)(14|F)(15|L)(15|S)(16|V)(18|T)(20|D)(20|V)(21|K)(21|S)(23|T)(24|L)(25|P)(25|S)(25|V)(26|W)(28|L)(29|S)(32|C)(32|D)(33|C)(33|G)(33|P)(34|V)(35|V)(36|S)(36|V)(37|G)(37|T)(37|V)(38|S)(39|A)(39|E)(39|R)(40|P)(40|V)(41|Y)(42|S)(43|T)(43|Y)(44|L)(46|R)(48|G)(49|D)(49|S)(49|T)(49|V)(50|C)(50|R)(51|R)(52|V)(54|A)(57|A)(59|S)(61|V)(62|H)(62|S)(63|F)(63|P)(63|Y)(64|P)(64|T)(65|L)(66|Q)(67|T)(68|R)(70|E)(72|V)(75|R)(76|P)(77|W)(78|K)(79|P)(81|T)(81|V)(83|G)(83|T)(84|V)(85|S)(86|I)(86|S)(88|W)(89|E)(91|L)(92|L)(95|V)(97|S)(98|S)(99|N)(102|V)(103|F)(103|H)(107|L)(110|I)(112|D)(112|S)(114|R)(115|S)(117|E)(118|K)(120|T)(120|V)(121|C)(121|H)(122|D)(122|K)(123|N)(124|R)(125|E)(128|H)(128|L)(131|L)(132|E)(132|H)(132|P)(138|L)(140|M)(142|*)(143|I)(144|I)(147|H)(150|R)(150|S)(151|F)(155|R)(156|*)(159|V)(160|H)(162|A)(163|S)(164|L)(164|P)(164|Y)(165|C)(168|G)(169|E)(169|N)(170|S)(171|G)(172|T)(174|K)(176|V)(177|*)(178|C)(178|H)(179|S)(181|K)(182|V)(185|E)(185|T)(186|E)(187|T)(189|*)(192|A)(192|V)(193|V)(195|F)(197|H)(198|A)(199|L)(200|*)(208|V)(209|*)(210|A)(211|S)(212|I)(213|P)(214|*)(214|S)(215|G)(215|I)(215|L)(216|I)(217|G)(217|Y)(218|*)(219|N)(220|A)(220|D)(220|G)(221|D)(221|K)(222|G)(223|D)(223|S)(224|*)(226|D)(226|G)(227|G)(227|I)(227|N)(228|K)(229|G)(229|K)(229|Q)(232|*)(233|R)(233|S)(236|*)(238|Y)(240|*)(240|Q)(241|R)(243|C)(243|H)(243|S)(244|*)(245|L)(247|N)(247|Q)(248|*)(248|G)(248|P)(248|Q)(249|M)(250|A)(251|M)(251|V)(252|*)(252|A)(254|F)(255|K)(258|T)(258|V)(261|F)(264|D)(265|C)(269|S)(270|M)(272|*)(272|D)(273|E)(273|R)(273|V)(274|C)(274|N)(275|T)(277|D)(277|G)(278|K)(279|N)(281|V)(285|C)(285|I)(286|K)(287|C)(287|T)(288|*)(289|A)(289|D)(289|E)(290|P)(291|D)(292|I)(292|R)(293|S)(295|E)(295|I)(295|R)(297|T)(298|*)(298|Q)(299|N)(300|L)(300|P)(300|Q)(300|W)(301|E)(302|K)(302|T)(303|I)(304|M)(305|S)(306|R)(309|C)(309|F)(309|T)(309|Y)(310|P)(311|N)(312|K)(313|R)(314|I)(314|N)(314|R)(315|C)(315|F)(316|K)(317|R)(319|A)(319|M)(320|L)(320|T)(321|*)(322|T)(322|V)(323|I)(324|N)(326|V)(327|A)(327|S)(328|R)(330|*)(330|P)(332|A)(333|I)(335|H)(336|S)(339|D)(339|G)(339|P)(340|S)(341|C)(342|S)(342|V)(343|L)(343|R)(344|E)(345|K)(345|S)(346|C)(346|F)(348|F)(349|*)(349|R)(350|S)(350|V)(351|N)(351|R)(351|Y)(352|I)(354|V)(355|S)(356|A)(357|N)(358|E)(358|H)(358|N)(360|G)(360|I)(360|N)(361|C)(361|H)(362|L)(363|A)(365|R)(365|S)(366|F)(367|N)(369|I)(370|S)(373|P)(376|A)(376|G)(377|N)(377|Q)(377|T)(378|K)(378|S)(380|A)(381|K)(382|Y)(383|G)(384|M)(385|W)(387|H)(388|D)(388|P)(389|L)(390|E)(390|N)(391|L)(392|E)(393|G)(395|I)(396|V)(397|C)(397|H)(398|E)(398|L)(399|L)(403|F)(404|S)(405|C)(408|L)(412|T)(413|*)(414|*)(419|E)(423|I)(423|V)(424|L)(425|V)(427|D)(428|E)(430|R)(431|T)(432|C)(432|L)(432|S)(433|*)(435|P)(439|G)(442|T)(446|D)(447|M)(448|R)(449|P)(450|A)(452|V)(453|R)(455|T)(456|*)(457|D)(457|P)(457|V)(459|C)(460|R)(462|T)(463|*)(464|F)(468|C)(468|H)(469|*)(469|C)(469|F)(469|I)(471|E)(472|C)(474|A)(480|L)(482|*)(482|Q)(483|L)(484|Q)(485|*)(486|I)(486|S)(488|A)(490|K)(491|T)(492|V)(493|*)(494|S)(495|*)(496|Y)(497|T)(498|R)(500|T)(501|Y)(502|T)(503|C)(508|M)(509|A)(509|L)(510|G)(510|K)(511|G)(513|V)(516|N)(519|N)(521|I)(521|S)(522|H)(522|K)(522|R)(523|P)(524|*)(525|N)(529|C)(529|D)(529|V)(531|T)(533|D)(535|C)(536|G)(536|N)(537|E)(538|S)(540|I)(540|V)(541|G)(541|R)(543|R)(546|G)(546|Q)(549|C)(549|F)(549|Y)(550|C)(551|D)(552|Q)(554|C)(554|H)(556|C)(556|F)(558|A)(559|Y)(560|L)(564|*)(564|T)(565|P)(566|A)(566|R)(567|N)(568|L)(568|V)(570|V)(571|D)(571|R)(572|H)(574|*)(574|T)(575|Y)(577|C)(577|G)(577|H)(578|Y)(580|L)(582|L)(585|P)(586|A)(588|R)(590|L)(591|S)(593|E)(594|I)(596|I)(597|Q)(598|R)(599|R)(601|V)(602|*)(604|G)(605|A)(605|S)(607|R)(608|V)(610|N)(612|*)(615|F)(615|S)(616|C)(617|I)(618|*)(618|R)(619|*)(619|D)(620|S)(622|L)(623|A)(623|H)(623|L)(623|S)(624|S)(624|V)(625|F)(628|*)(632|E)(637|P)(639|D)(639|K)(641|*)(642|C)(644|S)(646|R)(648|I)(648|M)(649|V)(651|M)(651|T)(651|V)(653|A)(653|L)(654|I)(654|T)(655|I)(665|D)(665|G)(666|F)(666|P)(667|H)(667|V)(668|C)(668|P)(669|T)(669|V)(670|R)(670|V)(673|A)(673|L)(675|D)(675|K)(676|R)(677|I)(677|T)(678|Q)(679|S)(680|T)(681|F)(682|C)(682|F)(683|V)(684|V)(685|A)(686|D)(687|*)(689|L)(689|V)(690|F)(690|S)(691|F)(692|N)(692|Q)(694|R)(695|P)(696|T)(698|E)(698|K)(700|F)(702|*)(702|L)(703|V)(704|G)(706|S)(709|*)(710|T)(711|S)(713|G)(713|N)(714|C)(714|F)(714|P)(716|A)(716|I)(719|I)(720|I)(721|G)(721|I)(721|S)(722|Y)(723|A)(724|G)(724|V)(725|M)(725|V)(726|L)(726|S)(726|Y)(727|A)(727|N)(727|S)(728|R)(728|T)(730|C)(731|*)(732|*)(732|P)(732|Q)(734|M)(735|I)(737|V)(740|*)(742|K)(742|T)(744|V)(745|M)(745|V)(747|R)(749|R)(750|K)(750|P)(753|C)(754|P)(757|I)(761|G)(761|K)(761|T)(764|I)(764|N)(765|W)(766|Q)(767|I)(767|S)(768|-)(768|A)(768|H)(770|D)(770|V)(771|M)(772|Q)(772|W)(773|P)(774|V)(775|Q)(777|*)(777|R)(780|G)(780|V)(781|S)(781|T)(783|S)(786|H)(787|V)(788|V)(791|C)(791|H)(791|S)(792|P)(795|T)(796|G)(796|K)(798|V)(800|A)(800|I)(800|L)(803|G)(804|*)(806|C)(807|K)(807|Q)(809|A)(810|Q)(810|V)(812|I)(815|F)(815|I)(815|R)(817|N)(818|V)(820|M)(823|G)(826|D)(827|D)(828|A)(828|I)(831|A)(832|R)(834|N)(835|*)(835|E)(837|Q)(837|Y)(838|L)(840|R)(842|G)(842|P)(844|I)(847|G)(847|K)(850|*)(850|C)(850|H)(851|G)(851|N)(854|I)(854|M)(854|N)(856|F)(857|G)(857|N)(860|F)(861|S)(863|A)(863|G)(864|E)(865|L)(866|T)(867|G)(867|I)(868|I)(868|T)(869|R)(875|I)(875|T)(877|*)(877|K)(878|A)(878|G)(879|*)(880|E)(880|Y)(883|T)(884|C)(885|*)(885|N)(886|V)(888|N)(889|H)(889|P)(890|L)(891|M)(891|V)(893|Q)(893|V)(894|*)(895|R)(896|I)(897|H)(897|S)(898|R)(899|*)(899|K)(901|C)(901|H)(901|S)(904|E)(905|*)(905|M)(907|A)(908|*)(909|F)(911|*)(911|L)(911|Q)(913|G)(915|D)(917|E)(918|R)(922|*)(922|L)(922|Q)(924|S)(926|F)(927|T)(928|I)(928|N)(930|E)(930|R)(932|D)(936|E)(936|N)(939|*)(940|S)(942|D)(943|Y)(944|V)(946|*)(949|E)(950|I)(951|F)(952|P)(953|G)(953|K)(956|G)(957|E)(958|R)(959|C)(959|H)(960|S)(960|T)(961|I)(961|T)(962|M)(962|T)(967|V)(968|G)(969|C)(969|F)(969|S)(970|C)(974|G)(976|C)(976|H)(977|*)(978|*)(979|P)(979|V)(981|M)(981|V)(983|D)(983|K)(983|Q)(984|H)(984|T)(985|L)(987|A)(987|I)(988|C)(988|H)(988|L)(988|P)(989|S)(991|A)(991|L)(992|D)(992|G)(992|K)(993|K)(994|*)(994|H)(995|*)(997|R)(998|T)(999|A)(1000|N)(1001|R)(1002|S)(1005|*)(1005|Q)(1006|H)(1007|*)(1007|C)(1008|I)(1009|E)(1009|I)(1010|A)(1010|I)(1013|N)(1013|T)(1014|R)(1015|F)(1016|V)(1018|I)(1021|D)(1021|G)(1023|*)(1023|G)(1024|Q)(1024|W)(1026|Y)(1027|L)(1028|L)(1029|F)(1031|V)(1033|R)(1034|G)(1034|P)(1034|Q)(1034|W)(1035|*)(1035|L)(1035|P)(1035|Q)(1036|Q)(1037|L)(1038|C)(1044|*)(1045|T)(1046|G)(1047|*)(1048|*)(1048|E)(1049|F)(1051|I)(1052|G)(1052|K)(1054|F)(1054|M)(1054|V)(1055|P)(1055|T)(1055|V)(1057|S)(1058|H)(1059|A)(1063|M)(1064|T)(1064|V)(1065|K)(1066|C)(1067|I)(1067|T)(1068|*)(1068|G)(1068|P)(1068|Q)(1069|E)(1069|R)(1070|C)(1070|D)(1072|V)(1073|A)(1073|R)(1073|S)(1074|S)(1074|V)(1076|C)(1076|G)(1076|H)(1077|Q)(1078|A)(1078|L)(1079|L)(1080|R)(1082|L)(1082|P)(1082|S)(1083|Q)(1085|S)(1086|A)(1086|H)(1086|R)(1087|A)(1087|H)(1087|L)(1087|R)(1087|S)(1087|T)(1088|C)(1088|L)(1088|P)(1088|S)(1090|D)(1091|R)(1093|R)(1093|V)(1094|A)(1094|L)(1095|C)(1095|H)(1096|R)(1097|L)(1097|S)(1098|Y)(1100|M)(1100|R)(1101|N)(1102|P)(1104|L)(1105|R)(1107|N)(1110|S)(1110|T)(1112|N)(1113|M)(1113|V)(1115|T)(1117|F)(1117|R)(1118|K)(1119|*)(1119|G)(1121|D)(1121|G)(1121|K)(1123|*)(1128|C)(1128|F)(1130|M)(1132|L)(1133|I)(1138|E)(1139|C)(1139|S)(1140|*)(1142|M)(1144|L)(1144|R)(1146|*)(1146|P)(1147|T)(1147|V)(1148|A)(1148|R)(1148|S)(1149|*)(1149|F)(1149|I)(1150|F)(1151|G)(1153|T)(1154|D)(1155|*)(1155|H)(1156|I)(1156|K)(1156|T)(1157|A)(1157|C)(1157|S)(1158|R)(1159|*)(1159|C)(1160|F)(1160|I)(1160|L)(1161|A)(1162|D)(1162|P)(1163|*)(1163|D)(1163|V)(1172|K)(1172|T)(1173|M)(1175|I)(1175|S)(1176|K)(1177|V)(1178|D)(1179|D)(1180|*)(1181|E)(1181|G)(1181|N)(1183|K)(1184|T)(1185|A)(1186|D)(1187|G)(1188|N)(1189|A)(1189|I)(1190|L)(1191|L)(1193|K)(1195|T)(1196|*)(1196|K)(1196|Q)(1197|A)(1199|T)(1200|M)(1200|V)(1201|F)(1201|V)(1202|T)(1202|V)(1203|Q)(1203|Y)(1204|E)(1204|T)(1205|I)(1206|G)(1207|D)(1207|Q)(1210|L)(1211|P)(1212|M)(1213|V)(1214|*)(1216|V)(1217|G)(1217|K)(1218|R)(1218|S)(1219|D)(1219|I)(1219|N)(1220|T)(1221|R)(1222|V)(1225|K)(1225|M)(1226|E)(1227|L)(1227|T)(1228|P)(1229|H)(1229|S)(1230|G)(1230|V)(1232|D)(1232|L)(1233|Q)(1233|T)(1234|*)(1234|Q)(1235|V)(1236|P)(1237|D)(1238|A)(1238|S)(1239|T)(1241|Y)(1242|H)(1242|L)(1242|S)(1243|A)(1243|K)(1243|S)(1247|A)(1247|S)(1248|D)(1248|Y)(1250|R)(1253|A)(1253|E)(1253|L)(1254|D)(1256|*)(1256|N)(1256|S)(1258|*)(1258|E)(1260|I)(1261|V)(1262|L)(1263|C)(1263|H)(1266|L)(1267|T)(1268|E)(1269|M)(1269|S)(1270|K)(1270|T)(1270|V)(1271|L)(1273|K)(1273|S)(1274|K)(1275|Y)(1278|L)(1278|R)(1278|T)(1278|Y)(1279|N)(1279|R)(1280|*)(1281|D)(1281|Q)(1282|N)(1282|P)(1283|V)(1284|M)(1284|N)(1285|L)(1286|P)(1289|L)(1293|G)(1294|F)(1295|L)(1296|E)(1296|Q)(1298|C)(1299|D)(1303|G)(1303|T)(1304|K)(1304|S)(1307|D)(1307|H)(1310|D)(1311|D)(1311|K)(1313|T)(1316|A)(1316|R)(1317|D)(1317|Q)(1319|R)(1320|S)(1321|G)(1321|S)(1322|*)(1322|V)(1324|D)(1324|Q)(1325|M)(1326|I)(1326|L)(1326|T)(1327|H)(1327|S)(1328|R)(1329|L)(1330|R)(1331|*)(1331|L)(1331|Q)(1332|I)(1332|S)(1333|L)(1334|Q)(1334|W)(1335|A)(1335|D)(1335|G)(1335|K)(1336|I)(1339|G)(1339|V)(1341|G)(1342|S)(1342|T)(1343|*)(1345|I)(1346|N)(1347|P)(1348|D)(1350|A)(1350|L)(1352|Q)(1353|W)(1354|P)(1354|Q)(1355|S)(1356|F)(1357|N)(1358|E)(1358|N)(1359|*)(1359|K)",
                peffService.formatVariantSimple("NX_P52701-1"));
    }

    @Test
    public void testVariantComplexFormat() throws Exception {

        Assert.assertEquals("\\VariantComplex=(53|53|AGP)(57|57|PGP)(57|57|PRP)(259|259|)(272|272|)(282|282|VG)(374|374|)(385|385|)(491|491|)(492|492|)(538|539|*V)(546|546|)(640|640|)(641|641|)(754|754|)(768|768|)(809|809|)(852|852|)(854|854|)(881|881|KS)(882|885|*)(887|887|LI)(942|942|)(1013|1013|)(1014|1014|)(1014|1014|KL)(1129|1130|L)(1158|1158|)(1162|1162|)(1162|1162|AA)(1173|1176|)(1183|1183|)(1231|1231|)(1232|1232|)(1242|1242|)(1242|1243|P)(1244|1244|)(1244|1244|LL)(1248|1257|)(1253|1253|VE)(1254|1254|)(1254|1254|EE)(1254|1255|DY)(1282|1282|TI)(1310|1310|EE)(1317|1317|HRKAREFEKN)(1325|1325|)(1343|1343|ST)(1345|1347|)",
                peffService.formatVariantComplex("NX_P52701-1"));
    }

    @Test
    public void testProcessedMoleculeFormat() throws Exception {

        Assert.assertEquals("\\Processed=(1|1360|mature protein)", peffService.formatProcessedMolecule("NX_P52701-1"));
    }

    @Test
    public void testProcessedInsulinMoleculeFormat() throws Exception {

        Assert.assertEquals("\\Processed=(1|24|signal peptide)(25|54|mature protein)(57|87|maturation peptide)(90|110|mature protein)", peffService.formatProcessedMolecule("NX_P01308-1"));
    }
}