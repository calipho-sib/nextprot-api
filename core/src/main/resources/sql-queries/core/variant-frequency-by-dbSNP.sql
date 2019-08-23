select chr, gnomad_ac, chrpos, dbsnp_id, ori_nuc, var_nuc, allele_count, allele_number, allele_freq, hom_count, var_type, gene_name, ensg, enst, ensp, iso_pos, ori_aa, var_aa, uniprot_ac
from nxflat.gnomad_variants
where dbsnp_id=(:dbsnp_id) and ori_aa=(:ori_aa) and var_aa=(:var_aa)