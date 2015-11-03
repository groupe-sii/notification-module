package fr.sii.ogham.core.builder.template;

public interface PrefixSuffixBuilder<MYSELF> {
	MYSELF pathPrefix(String... prefixes);
	
	MYSELF pathSuffix(String... suffixes);
}
