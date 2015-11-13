package org.nextprot.api.tasks.commons;

import org.junit.Assert;
import org.junit.Test;

public class TreeVisitorDotTest {

    @Test
    public void testExportDotTree() {

        String[] expectedEdges = new String[] {
                "Root -- Name",
                "Name -- FamilyName",
                "Root -- GeneralAnnotation",
                "GeneralAnnotation -- CellularComponent",
                "CellularComponent -- SubcellularLocation",
                "CellularComponent -- GoCellularComponent",
                "CellularComponent -- SubcellularLocationNote",
                "GeneralAnnotation -- Interaction",
                "Interaction -- InteractionInfo",
                "Interaction -- EnzymeRegulation",
                "Interaction -- SmallMoleculeInteraction",
                "Interaction -- Cofactor",
                "Interaction -- BinaryInteraction",
                "GeneralAnnotation -- EnzymeClassification",
                "GeneralAnnotation -- Caution",
                "GeneralAnnotation -- Miscellaneous",
                "GeneralAnnotation -- Function",
                "Function -- GoMolecularFunction",
                "Function -- CatalyticActivity",
                "Function -- FunctionInfo",
                "Function -- GoBiologicalProcess",
                "Function -- Pathway",
                "GeneralAnnotation -- Induction",
                "GeneralAnnotation -- Expression",
                "Expression -- ExpressionInfo",
                "Expression -- ExpressionProfile",
                "Expression -- DevelopmentalStageInfo",
                "GeneralAnnotation -- Medical",
                "Medical -- Allergen",
                "Medical -- Pharmaceutical",
                "Medical -- Disease",
                "GeneralAnnotation -- SequenceCaution",
                "GeneralAnnotation -- Keyword",
                "Keyword -- UniprotKeyword",
                "Root -- PositionalAnnotation",
                "PositionalAnnotation -- SequenceConflict",
                "PositionalAnnotation -- Site",
                "Site -- MiscellaneousSite",
                "Site -- ActiveSite",
                "Site -- BindingSite",
                "Site -- CleavageSite",
                "Site -- MetalBindingSite",
                "PositionalAnnotation -- NonTerminalResidue",
                "PositionalAnnotation -- Topology",
                "Topology -- IntramembraneRegion",
                "Topology -- TopologicalDomain",
                "Topology -- TransmembraneRegion",
                "PositionalAnnotation -- Ptm",
                "Ptm -- LipidationSite",
                "Ptm -- CrossLink",
                "Ptm -- Selenocysteine",
                "Ptm -- ModifiedResidue",
                "Ptm -- DisulfideBond",
                "Ptm -- GlycosylationSite",
                "Ptm -- PtmInfo",
                "PositionalAnnotation -- ProcessingProduct",
                "ProcessingProduct -- MitochondrialTransitPeptide",
                "ProcessingProduct -- MatureProtein",
                "ProcessingProduct -- SignalPeptide",
                "ProcessingProduct -- Propeptide",
                "ProcessingProduct -- InitiatorMethionine",
                "ProcessingProduct -- PeroxisomeTransitPeptide",
                "PositionalAnnotation -- Region",
                "Region -- MiscellaneousRegion",
                "Region -- CoiledCoilRegion",
                "Region -- Domain",
                "Region -- NucleotidePhosphateBindingRegion",
                "Region -- CompositionallyBiasedRegion",
                "Region -- ShortSequenceMotif",
                "Region -- DnaBindingRegion",
                "Region -- CalciumBindingRegion",
                "Region -- ZincFingerRegion",
                "Region -- InteractingRegion",
                "Region -- Repeat",
                "PositionalAnnotation -- Mapping",
                "Mapping -- PdbMapping",
                "PositionalAnnotation -- SecondaryStructure",
                "SecondaryStructure -- Turn",
                "SecondaryStructure -- BetaStrand",
                "SecondaryStructure -- Helix",
                "PositionalAnnotation -- Variant",
                "PositionalAnnotation -- DomainInfo",
                "PositionalAnnotation -- NonConsecutiveResidue",
                "PositionalAnnotation -- Mutagenesis",
                "GeneralAnnotation -- VariantInfo"
        };

        AnnotationCategoryTreeExporter app = new AnnotationCategoryTreeExporter(new TreeVisitorDot(""));

        String export = app.export();

        for (String expectedEdge : expectedEdges) {
            Assert.assertTrue(export.contains(expectedEdge));
        }
    }
}