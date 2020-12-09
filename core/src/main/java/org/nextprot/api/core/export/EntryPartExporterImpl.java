package org.nextprot.api.core.export;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.*;

import static org.nextprot.api.core.export.EntryPartExporter.Header.*;

/**
 * Export data depending on the type of annotation to export (expression-profile, variants, ...)
 */
public class EntryPartExporterImpl implements EntryPartExporter {

    private final Map<Header, Integer> headerMap;
    private final List<Header> headerList;
    private final List<SortCriterion> sortCriteria; 

    private EntryPartExporterImpl(Builder builder) {

        headerMap = builder.headers;
        headerList = buildList(headerMap);
        sortCriteria = builder.getSortCriteria();
    }

    public static EntryPartExporterImpl fromSubPart(String... subParts) {

        Builder builder = new Builder();

        for (String subPart : subParts) {

            if ("expression-profile".equals(subPart)) {
                builder.addColumns(8, 
                		EXPRESSION_LEVEL, STAGE_ACCESSION, STAGE_NAME);
                builder.setSortCriteria(
                		SortCriterion.CRITERION_TERM_NAME_ASC, 
                		SortCriterion.CRITERION_ECO_NAME_ASC, 
                		SortCriterion.CRITERION_EXPRESSION_LEVEL_ASC,
                		SortCriterion.CRITERION_STAGE_AC_ASC
                		);
            }
        }

        return builder.build();
    }

    private static List<Header> buildList(Map<Header, Integer> headers) {

        Header[] a = new Header[headers.size()];

        headers.forEach((key, value) -> a[value] = key);

        return Arrays.asList(a);
    }

    @Override
    public List<Header> exportHeaders() {

        return headerList;
    }

    @Override
    public List<Row> exportRows(Entry entry) {

        List<Row> rows = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotations()) {
            collectRows(rows, entry, annotation);
        }
        if (this.sortCriteria.size()>0) {
        	rows.sort(new RowComparator(this.sortCriteria));
        }
        return rows;
    }

    /*
    protected List<Row> sortRows(List<Row> rows, Comparator<Row> comparator) {
    	return rows.stream().sorted(comparator).collect(Collectors.toList());
    }
    */
    
    private void collectRows(List<Row> rows, Entry entry, Annotation annotation) {

        for (AnnotationEvidence evidence : annotation.getEvidences()) {

            Row row = new Row(headerList.size());

            setRowValue(row, ENTRY_ACCESSION,     entry.getUniqueName());
            setRowValue(row, CATEGORY,            StringUtils.camelToKebabCase(annotation.getApiTypeName()));
            setRowValue(row, TERM_ACCESSION,      annotation.getCvTermAccessionCode());
            setRowValue(row, TERM_NAME,           annotation.getCvTermName());
            setRowValue(row, ECO_ACCESSION,       evidence.getEvidenceCodeAC());
            setRowValue(row, ECO_NAME,            evidence.getEvidenceCodeName());
            setRowValue(row, QUALITY,             evidence.getQualityQualifier());
            setRowValue(row, NEGATIVE,            String.valueOf(evidence.isNegativeEvidence()));
            setRowValue(row, EXPRESSION_LEVEL,    evidence.getExpressionLevel());
            setRowValue(row, SOURCE,              evidence.getAssignedBy());
            setRowValue(row, URL,                 entry.getXref(evidence.getResourceId()).map(DbXref::getResolvedUrl).orElse("null"));
            
//            System.out.println(
//	            annotation.getAnnotationId() + " " +
//	            annotation.getCategoryName()  + " " +
//	            annotation.getCvTermName()  + " " +
//	            evidence.getEvidenceId()  + " " +
//	            evidence.getEvidenceCodeAC()  + " " +
//	            evidence.getExperimentalContextId()  + " " +
//	            evidence.getAssignedBy() + " ");
                        
            setExperimentalContextRowValues(row, entry, evidence);

            rows.add(row);
        }
    }

    private void setExperimentalContextRowValues(Row row , Entry entry, AnnotationEvidence evidence) {

        ExperimentalContext ec = entry.getExperimentalContext(evidence.getExperimentalContextId())
                .orElseThrow(() -> new NextProtException("missing experimental context with id " + evidence.getExperimentalContextId() + ": " + evidence.getEvidenceCodeAC()));

        if (ec.getDevelopmentalStage() != null) {
            setRowValue(row, STAGE_ACCESSION, ec.getDevelopmentalStageAC());
            setRowValue(row, STAGE_NAME, ec.getDevelopmentalStage().getName());
        }

        if (ec.getCellLine() != null) {
            setRowValue(row, CELL_LINE_ACCESSION, ec.getCellLineAC());
            setRowValue(row, CELL_LINE_NAME, ec.getCellLine().getName());
        }

        if (ec.getDisease() != null) {
            setRowValue(row, DISEASE_ACCESSION, ec.getDiseaseAC());
            setRowValue(row, DISEASE_NAME, ec.getDisease().getName());
        }

        if (ec.getOrganelle() != null) {
            setRowValue(row, ORGANELLE_ACCESSION, ec.getOrganelleAC());
            setRowValue(row, ORGANELLE_NAME, ec.getOrganelle().getName());
        }
    }

    private void setRowValue(Row row, Header header, String value) {

        if (!headerList.contains(header)) {
            return;
        }
        row.setValue(headerMap.get(header), value);
    }

    static class RowComparator implements Comparator<Row> {

    	private List<SortCriterion> criteria;
    	
    	public RowComparator(List<SortCriterion> criteria) {
    		this.criteria=criteria;
    	}
    	
		@Override
		public int compare(Row o1, Row o2) {
			for (SortCriterion criterion: criteria) {
				int columnIndex = criterion.getColumnIndex();
				String value1 = o1.getValue(columnIndex);
				String value2 = o2.getValue(columnIndex);
				int result = criterion.compare(value1, value2);
				if (result != 0) return result;
			}
			return 0; // values are equal according to all criteria !
		}
    	
    }
    
    static class SortCriterion implements Comparator<String> {
    	static enum Direction {ASC,DESC};
    	// important to get new instance each time because SortCriterion column may differ if criterion is used several time...
    	static SortCriterion CRITERION_TERM_NAME_ASC = new SortCriterion(TERM_NAME,Direction.ASC);
    	static SortCriterion CRITERION_ECO_NAME_ASC = new SortCriterion(ECO_NAME,Direction.ASC);
    	static SortCriterion CRITERION_EXPRESSION_LEVEL_ASC = new SortCriterion(EXPRESSION_LEVEL,Direction.ASC);
    	static SortCriterion CRITERION_STAGE_NAME_ASC = new SortCriterion(STAGE_NAME,Direction.ASC);
    	static SortCriterion CRITERION_STAGE_AC_ASC = new SortCriterion(STAGE_ACCESSION,Direction.ASC, new StageAcColumnComparator());
    	
    	Header h; 
    	Direction d;
    	int column; // index of header column in row
    	Comparator<String> comparator;
    	private SortCriterion(Header h, Direction d) {
      	  this.h=h;
      	  this.d=d;
      	  this.comparator=new StringColumnComparator(true);
      	}
    	private SortCriterion(Header h, Direction d, Comparator<String> comparator) {
      	  this.h=h;
      	  this.d=d;
      	  this.comparator=comparator;
      	}
    	
    	public Header getHeader() { return h;}
    	public Direction getDirection() { return d;}
    	public int getColumnIndex() { return column; } 
    	public void setColumn(int col) { this.column=col;}
		@Override
		public int compare(String o1, String o2) {
			return this.comparator.compare(o1, o2);
		}
    }

    static class StringColumnComparator implements Comparator<String> {
    	private boolean caseSensitive = false;
    	public StringColumnComparator(boolean caseSensitive) {
    		this.caseSensitive=caseSensitive;
    	}
		@Override
		public int compare(String o1, String o2) {
			if (o1==null && o2==null) return 0; // equal values on this criterion, go to next criterion
			if (o1==null) return -1; // value1 comes before
			if (o2==null) return  1; // value2 comes before
			if (caseSensitive) {
				return o1.compareTo(o2);
			} else {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		}
    }
    
    static class StageAcColumnComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			if (o1==null && o2==null) return 0; // equal values on this criterion, go to next criterion
			if (o1==null) return -1; // value1 comes before
			if (o2==null) return  1; // value2 comes before
			if (! ac2order.containsKey(o1) && ! ac2order.containsKey(o2)) return 0; //o1.compareTo(o2);
			if (! ac2order.containsKey(o1)) return -1;
			if (! ac2order.containsKey(o2)) return  1;
			return Integer.compare(ac2order.get(o1), ac2order.get(o2) );
		}
		
		private static Map<String,Integer> ac2order = new HashMap<>();
		private static void addAc(String ac) {
			ac2order.put(ac, ac2order.size());
		}
		static {
			addAc("HsapDO:0000006"); // blastula
			addAc("HsapDO:0000015"); // organogenesis stage
			addAc("HsapDO:0000002"); // embryonic stage
		    addAc("HsapDO:0000037"); //	fetal stage
		    addAc("HsapDO:0000005"); //	CarnegieStage02	
		    addAc("HsapDO:0000007"); //	CarnegieStage03	
		    addAc("HsapDO:0000016"); //	CarnegieStage09		
		    addAc("HsapDO:0000017"); //	CarnegieStage10		
		    addAc("HsapDO:0000018"); //	CarnegieStage11		
		    addAc("HsapDO:0000019"); //	CarnegieStage12		
		    addAc("HsapDO:0000020"); //	CarnegieStage13		
		    addAc("HsapDO:0000021"); //	CarnegieStage14		
		    addAc("HsapDO:0000024"); //	CarnegieStage17		
		    addAc("HsapDO:0000025"); //	CarnegieStage18		
		    addAc("HsapDO:0000026"); //	CarnegieStage19		
		    addAc("HsapDO:0000029"); //	CarnegieStage22		
		    addAc("HsapDO:0000030"); //	CarnegieStage23		
		    addAc("HsapDO:0000197"); //	third LMP month		
		    addAc("HsapDO:0000198"); //	fourth LMP month		
		    addAc("HsapDO:0000199"); //	fifth LMP month		
		    addAc("HsapDO:0000200"); //	sixth LMP month		
		    addAc("HsapDO:0000201"); //	seventh LMP month		
		    addAc("HsapDO:0000082"); //	newborn stage 		
		    addAc("HsapDO:0000083"); //	1-23 months infant stage		
		    addAc("HsapDO:0000084"); //	2-5 year-old child stage		
		    addAc("HsapDO:0000085"); //	6-12 year-old child stage		
		    addAc("HsapDO:0000081"); //	child stage	(0-13y)	
		    addAc("HsapDO:0000086"); //	13-18 year-old adolescent stage	
		    addAc("HsapDO:0000080"); // immature stage (0-19y)
		    addAc("HsapDO:0000089"); //	19-24 year-old adult stage		
		    addAc("HsapDO:0000090"); //	25-44 year-old adult stage		
		    addAc("HsapDO:0000088"); //	early adulthood		
		    addAc("HsapDO:0000092"); //	45-64 year-old adult stage		
		    addAc("HsapDO:0000087"); //	adult stage		
		    addAc("HsapDO:0000091"); //	late adulthood		
		    addAc("HsapDO:0000094"); //	65-79 year-old adult stage		
		    addAc("HsapDO:0000093"); //	aged stage		
		    addAc("HsapDO:0000095"); //	80 and over year-old adult		
		    addAc("HsapDO:0000044"); // unknown
		}
    }
    
    
    
    /**
     * Generic builder with common and custom fields to export
     */
    public static class Builder {

        private final Map<Header, Integer> headers = new HashMap<>();
        private final List<SortCriterion> sortCriteria = new ArrayList<>();

        Builder() {
            headers.put(ENTRY_ACCESSION, 0);
            headers.put(CATEGORY,        1);
            headers.put(TERM_ACCESSION,  2);
            headers.put(TERM_NAME,       3);
            headers.put(QUALITY,         4);
            headers.put(ECO_ACCESSION,   5);
            headers.put(ECO_NAME,        6);
            headers.put(NEGATIVE,        7);
            headers.put(SOURCE,         -2);
            headers.put(URL,            -1);
        }

        void setSortCriteria(SortCriterion... criteria) {
        	for (SortCriterion c: criteria) {
        		int index = this.headers.get(c.getHeader());
        		c.setColumn(index); // we need to know the column index at sorting time...
        		this.sortCriteria.add(c);
        	}
        }
        
        List<SortCriterion> getSortCriteria() { return this.sortCriteria; }
        
        Builder addColumns(Header... headers) {

            return addColumns(8, headers);
        }

        Builder addColumns(int insertPoint, Header... headers) {

            for (Header header : headers) {

                if (!this.headers.containsKey(header)) {
                    this.headers.put(header, insertPoint++);
                }
            }

            return this;
        }

        private void fixRelativeIndices() {

            for (Header header : headers.keySet()) {

                if (headers.get(header) < 0) {
                    headers.put(header, headers.size()+headers.get(header));
                }
            }
        }

        EntryPartExporterImpl build() {

            fixRelativeIndices();

            return new EntryPartExporterImpl(this);
        }
    }

	@Override
	public int getColumnIndex(Header header) {
		return headerMap.get(header);
	}
}
