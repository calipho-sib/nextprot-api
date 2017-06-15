package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
@Ignore
public class EntryUtilsUnconfirmedPE1Test extends CoreUnitBaseTest{
        
    @Autowired private EntryBuilderService entryBuilderService = null;
    @Autowired private MasterIdentifierService masterIdentifierService = null;
        
    @Test // run successfully with np_20170413
    public void testWouldUpgradeToPE1_1() {  
    	Entry e = entryBuilderService.build(EntryConfig.newConfig("NX_A0AVI2").withEverything()); // YES: is in ftp file
    	Assert.assertEquals(true, EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e));
    }

    @Test // run successfully with np_20170413
    public void testWouldUpgradeToPE1_2() {
    	Entry e = entryBuilderService.build(EntryConfig.newConfig("NX_P69849").withEverything()); // NO: only 1 proteotypic peptide > 7aa 
    	Assert.assertEquals(false, EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e));
    }
    
    @Test // run successfully with np_20170413
    public void testWouldUpgradeToPE1_3() {
    	Entry e = entryBuilderService.build(EntryConfig.newConfig("NX_Q9UK00").withEverything()); // YES: 2 proteotypic peptide > 7aa 
    	Assert.assertEquals(true, EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e));
    }

    @Test // run successfully with np_20170413
    public void testWouldUpgradeToPE1_4() {
    	Entry e = entryBuilderService.build(EntryConfig.newConfig("NX_Q9NV72").withEverything()); // YES: 1 proteotypic peptide > 9aa 
    	Assert.assertEquals(true, EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e));
    }
    
    @Test // run successfully with np_20170413
    public void testShouldBeUnconfirmedPE1() {
    	int errCnt=0;
    	int startAtIdx=0;
    	for (int i=startAtIdx;i<msuEntries.size(); i++) {
    		String ac = msuEntries.get(i);
        	Entry e = entryBuilderService.build(EntryConfig.newConfig(ac).withEverything());  
    		if (EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e)) {
    			System.out.println("entry " + i + "/" + msuEntries.size() + ": " +  ac + "  would upgrade as expected");
    		} else {
    			System.out.println("entry " + i + "/" + msuEntries.size() + ": " +  ac + " would NOT upgrade as expected: ERROR");    	
    			errCnt++;
    		}
    	}
    	Assert.assertEquals(0, errCnt);
    }
    
    @Test  // run successfully with np_20170413
    public void testShouldNotBeUnconfirmedPE1() {
    	int errCnt=0;
    	int startAtIdx=0;
    	List<String> negEntries = find100EntriesWhichAreNotUnconfirmedPE1();
    	for (int i=startAtIdx;i<negEntries.size(); i++) {
    		String ac = negEntries.get(i);
        	Entry e = entryBuilderService.build(EntryConfig.newConfig(ac).withEverything());  
    		if (EntryUtils.wouldUpgradeToPE1AccordingToOldRule(e) == false) {
    			System.out.println("entry " + i + "/" + negEntries.size() + ": " +  ac + "  would NOT upgrade as expected");
    		} else {
    			System.out.println("entry " + i + "/" + negEntries.size() + ": " +  ac + " would upgrade: ERROR");    	
    			errCnt++;
    		}
    	}
    	Assert.assertEquals(0, errCnt);
    }
    
    private List<String> find100EntriesWhichAreNotUnconfirmedPE1() {
    	Set<String> acsPos = new HashSet<>(msuEntries);
    	List<String> acsNeg = new ArrayList<>();
    	for (String ac: masterIdentifierService.findUniqueNames()) {
    		if (! acsPos.contains(ac) ) acsNeg.add(ac);
    		if (acsNeg.size()==100) break;
    	}
    	return acsNeg;
    }
    
    // list of entries in ftp://ftp.nextprot.org/pub/current_release/custom/hpp/HPP_entries_with_unconfirmed_MS_data.txt
	private List<String> msuEntries = Arrays.asList("NX_A0A075B6I9", "NX_A0A075B6K6", "NX_A0A075B6S2", "NX_A0A075B6S6",
			"NX_A0A087WSY6", "NX_A0A0A0MT36", "NX_A0A0A0MT89", "NX_A0A0B4J2H0", "NX_A0A0C4DH24", "NX_A0A0C4DH39",
			"NX_A0A0C4DH42", "NX_A0A0C4DH67", "NX_A0A0C4DH69", "NX_A0A0J9YXX1", "NX_A0AVI2", "NX_A2RTY3", "NX_A2RU54",
			"NX_A2RUU4", "NX_A4D0V7", "NX_A4FU28", "NX_A5LHX3", "NX_A5PLN7", "NX_A6BM72", "NX_A6H8M9", "NX_A6NCQ9",
			"NX_A6NDE8", "NX_A6NDK9", "NX_A6NEC2", "NX_A6NEQ0", "NX_A6NF36", "NX_A6NFC5", "NX_A6NFT4", "NX_A6NFX1",
			"NX_A6NGB0", "NX_A6NGH7", "NX_A6NGY5", "NX_A6NH21", "NX_A6NIK2", "NX_A6NIZ1", "NX_A6NK89", "NX_A6NKT7",
			"NX_A6NKU9", "NX_A6NM10", "NX_A6NMK8", "NX_A6NMU1", "NX_A6NN14", "NX_A6NNA2", "NX_A6NNM3", "NX_A8MT70",
			"NX_A8MTJ6", "NX_A8MTY7", "NX_A8MUP2", "NX_A8MWE9", "NX_A8MX76", "NX_A8MXK1", "NX_A8MXT2", "NX_A8MXV6",
			"NX_A8MXZ3", "NX_A8MZ36", "NX_A8MZA4", "NX_B1AL88", "NX_B2RC85", "NX_B5ME19", "NX_B9A064", "NX_B9EJG8",
			"NX_B9ZVM9", "NX_C9J3V5", "NX_C9JJ37", "NX_E2RYF7", "NX_E9PJ23", "NX_F5GYI3", "NX_H3BMG3", "NX_H3BQJ8",
			"NX_H3BU77", "NX_K7EIQ3", "NX_L0R8F8", "NX_O00591", "NX_O14904", "NX_O14921", "NX_O15016", "NX_O15218",
			"NX_O15375", "NX_O15391", "NX_O15482", "NX_O43304", "NX_O43374", "NX_O43610", "NX_O43680", "NX_O60290",
			"NX_O60330", "NX_O75019", "NX_O75310", "NX_O75474", "NX_O76014", "NX_O76050", "NX_O94778", "NX_O94844",
			"NX_O95567", "NX_O95780", "NX_O95922", "NX_P04211", "NX_P05015", "NX_P09131", "NX_P0C264", "NX_P0C2L3",
			"NX_P0C7A2", "NX_P0C7H9", "NX_P0C7X5", "NX_P0C851", "NX_P0C881", "NX_P0CJ79", "NX_P0CW00", "NX_P0DJD1",
			"NX_P0DJR0", "NX_P0DM35", "NX_P0DMQ5", "NX_P0DN24", "NX_P0DP06", "NX_P10072", "NX_P17035", "NX_P17538",
			"NX_P17658", "NX_P18825", "NX_P20264", "NX_P20853", "NX_P22003", "NX_P22532", "NX_P24046", "NX_P31268",
			"NX_P47872", "NX_P47881", "NX_P47944", "NX_P48664", "NX_P48742", "NX_P52738", "NX_P58505", "NX_P59025",
			"NX_P59827", "NX_P60331", "NX_P60606", "NX_P78412", "NX_Q00444", "NX_Q02325", "NX_Q03936", "NX_Q05C16",
			"NX_Q07627", "NX_Q08708", "NX_Q12999", "NX_Q13304", "NX_Q14236", "NX_Q14406", "NX_Q14916", "NX_Q15391",
			"NX_Q15695", "NX_Q16099", "NX_Q16478", "NX_Q16589", "NX_Q17RM4", "NX_Q17RQ9", "NX_Q17RR3", "NX_Q1W4C9",
			"NX_Q1XH10", "NX_Q2KHN1", "NX_Q2M5E4", "NX_Q2VY69", "NX_Q309B1", "NX_Q3B7I2", "NX_Q3KNT9", "NX_Q3LI59",
			"NX_Q3LI61", "NX_Q3LI63", "NX_Q3LI64", "NX_Q3SXZ3", "NX_Q3SY17", "NX_Q3ZCT8", "NX_Q3ZCX4", "NX_Q495B1",
			"NX_Q49A92", "NX_Q4G0M1", "NX_Q4KMZ8", "NX_Q4V339", "NX_Q4VNC0", "NX_Q50LG9", "NX_Q52WX2", "NX_Q5BIV9",
			"NX_Q5BKT4", "NX_Q5GAN4", "NX_Q5GH73", "NX_Q5HY64", "NX_Q5HYW3", "NX_Q5M9Q1", "NX_Q5QGS0", "NX_Q5SXM1",
			"NX_Q5T1S8", "NX_Q5T4I8", "NX_Q5T5M9", "NX_Q5T619", "NX_Q5T8R8", "NX_Q5TA89", "NX_Q5TG30", "NX_Q5U5X8",
			"NX_Q5UAW9", "NX_Q5VIY5", "NX_Q5VW00", "NX_Q5VX71", "NX_Q5VXU1", "NX_Q5XG99", "NX_Q5XKL5", "NX_Q658L1",
			"NX_Q68D42", "NX_Q68DY1", "NX_Q69YG0", "NX_Q69YZ2", "NX_Q6ICC9", "NX_Q6NT89", "NX_Q6NUN7", "NX_Q6NXP2",
			"NX_Q6P4F1", "NX_Q6P9A1", "NX_Q6PDA7", "NX_Q6PI77", "NX_Q6PIS1", "NX_Q6PJE2", "NX_Q6PKH6", "NX_Q6QAJ8",
			"NX_Q6S8J3", "NX_Q6S9Z5", "NX_Q6T310", "NX_Q6TDP4", "NX_Q6UDR6", "NX_Q6UE05", "NX_Q6UWF9", "NX_Q6UWH6",
			"NX_Q6UWQ5", "NX_Q6UX40", "NX_Q6UXA7", "NX_Q6UXB3", "NX_Q6UXD1", "NX_Q6UXN7", "NX_Q6UXU6", "NX_Q6UXZ0",
			"NX_Q6UYE1", "NX_Q6W3E5", "NX_Q6ZN06", "NX_Q6ZN19", "NX_Q6ZNA1", "NX_Q6ZNG0", "NX_Q6ZP65", "NX_Q6ZR85",
			"NX_Q6ZW05", "NX_Q6ZWI9", "NX_Q7RTU1", "NX_Q7RTU7", "NX_Q7Z2Y8", "NX_Q7Z4T8", "NX_Q7Z5D8", "NX_Q7Z5M5",
			"NX_Q7Z5S9", "NX_Q7Z6R9", "NX_Q7Z713", "NX_Q7Z769", "NX_Q86SI9", "NX_Q86T96", "NX_Q86TB3", "NX_Q86UB2",
			"NX_Q86UP9", "NX_Q86VI1", "NX_Q86VR8", "NX_Q86VV4", "NX_Q86WR6", "NX_Q86X67", "NX_Q86YR7", "NX_Q8IUB2",
			"NX_Q8IUB5", "NX_Q8IUG1", "NX_Q8IVW1", "NX_Q8IWX5", "NX_Q8IWZ4", "NX_Q8IXT1", "NX_Q8IYL9", "NX_Q8IZA3",
			"NX_Q8IZF3", "NX_Q8IZJ4", "NX_Q8N2E2", "NX_Q8N3F9", "NX_Q8N402", "NX_Q8N4B4", "NX_Q8N4H0", "NX_Q8N4W6",
			"NX_Q8N4W9", "NX_Q8N609", "NX_Q8N688", "NX_Q8N6I4", "NX_Q8N6L7", "NX_Q8N6M9", "NX_Q8N7X8", "NX_Q8N815",
			"NX_Q8N878", "NX_Q8N957", "NX_Q8N972", "NX_Q8N9B8", "NX_Q8N9L1", "NX_Q8N9R6", "NX_Q8N9Z0", "NX_Q8NAP8",
			"NX_Q8NAV2", "NX_Q8NBF1", "NX_Q8NBL3", "NX_Q8NBT3", "NX_Q8NC01", "NX_Q8NCK7", "NX_Q8NCU7", "NX_Q8NDV1",
			"NX_Q8NE18", "NX_Q8NEQ6", "NX_Q8NEX6", "NX_Q8NFJ8", "NX_Q8NFQ6", "NX_Q8NG35", "NX_Q8NGL1", "NX_Q8NGU9",
			"NX_Q8NHP1", "NX_Q8TAV4", "NX_Q8TAX0", "NX_Q8TBE1", "NX_Q8TBJ5", "NX_Q8TCV5", "NX_Q8TD47", "NX_Q8TD86",
			"NX_Q8TDD5", "NX_Q8TDN7", "NX_Q8TDS5", "NX_Q8TEF2", "NX_Q8TF20", "NX_Q8WTQ1", "NX_Q8WV48", "NX_Q8WW27",
			"NX_Q8WWY6", "NX_Q8WWZ4", "NX_Q8WXB4", "NX_Q92819", "NX_Q92839", "NX_Q92858", "NX_Q969M2", "NX_Q96A84",
			"NX_Q96AQ2", "NX_Q96BR6", "NX_Q96BV0", "NX_Q96CE8", "NX_Q96DE9", "NX_Q96DM1", "NX_Q96DU9", "NX_Q96EK2",
			"NX_Q96FV3", "NX_Q96HH4", "NX_Q96I13", "NX_Q96J86", "NX_Q96JQ5", "NX_Q96KW2", "NX_Q96LT6", "NX_Q96M60",
			"NX_Q96MI6", "NX_Q96NG5", "NX_Q96P67", "NX_Q96PF1", "NX_Q96QS6", "NX_Q96RD0", "NX_Q96RD6", "NX_Q96S07",
			"NX_Q96S95", "NX_Q99457", "NX_Q99811", "NX_Q9BQ87", "NX_Q9BQI4", "NX_Q9BQW3", "NX_Q9BRJ9", "NX_Q9BRU2",
			"NX_Q9BSN7", "NX_Q9BV87", "NX_Q9BWV7", "NX_Q9BXC1", "NX_Q9BYE4", "NX_Q9BYQ8", "NX_Q9BYW1", "NX_Q9BZ19",
			"NX_Q9BZJ6", "NX_Q9C009", "NX_Q9C0F0", "NX_Q9GZZ0", "NX_Q9H106", "NX_Q9H1C0", "NX_Q9H1U9", "NX_Q9H1Z8",
			"NX_Q9H255", "NX_Q9H2C1", "NX_Q9H2J1", "NX_Q9H2L4", "NX_Q9H3V2", "NX_Q9H3W5", "NX_Q9H6D8", "NX_Q9HB14",
			"NX_Q9HBJ0", "NX_Q9HBL6", "NX_Q9HBT7", "NX_Q9HC97", "NX_Q9HCC6", "NX_Q9HCL3", "NX_Q9HCQ5", "NX_Q9NQS5",
			"NX_Q9NRR2", "NX_Q9NS82", "NX_Q9NUB4", "NX_Q9NUH8", "NX_Q9NUR3", "NX_Q9NV72", "NX_Q9NXT0", "NX_Q9NY30",
			"NX_Q9NY84", "NX_Q9NYM4", "NX_Q9P055", "NX_Q9P109", "NX_Q9P2K9", "NX_Q9UBC0", "NX_Q9UBG7", "NX_Q9UC06",
			"NX_Q9UF02", "NX_Q9UK00", "NX_Q9UK10", "NX_Q9UKQ9", "NX_Q9UL58", "NX_Q9UL68", "NX_Q9UN75", "NX_Q9UN88",
			"NX_Q9UNT1", "NX_Q9UPC5", "NX_Q9UPX6", "NX_Q9UQ74", "NX_Q9Y236", "NX_Q9Y2G7", "NX_Q9Y536", "NX_Q9Y5E3",
			"NX_Q9Y5E5", "NX_Q9Y5G3", "NX_Q9Y5G4", "NX_Q9Y5G6", "NX_Q9Y5G7", "NX_Q9Y5G8", "NX_Q9Y5H4", "NX_Q9Y5H5",
			"NX_Q9Y5I0", "NX_Q9Y5I2", "NX_Q9Y5R2");
   
}