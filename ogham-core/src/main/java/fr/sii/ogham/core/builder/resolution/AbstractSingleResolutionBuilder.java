package fr.sii.ogham.core.builder.resolution;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.RelativisableResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;

/**
 * Base implementation to handle lookup prefix configuration. Path prefix/suffix
 * configuration is also partially managed. As path prefix/suffix for string
 * resolution has no sense, this base class doesn't implement
 * {@link PrefixSuffixBuilder}.
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
@SuppressWarnings("squid:S00119")
public abstract class AbstractSingleResolutionBuilder<MYSELF extends AbstractSingleResolutionBuilder<MYSELF, P>, P> extends AbstractParent<P> implements Builder<ResourceResolver> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSingleResolutionBuilder.class);

	protected final List<String> lookups;
	protected final ConfigurationValueBuilderHelper<MYSELF, String> pathPrefixValueBuilder;
	protected final ConfigurationValueBuilderHelper<MYSELF, String> pathSuffixValueBuilder;
	protected final EnvironmentBuilder<?> environmentBuilder;
	protected final MYSELF myself;

	@SuppressWarnings("unchecked")
	protected AbstractSingleResolutionBuilder(Class<?> selfType, P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		this.environmentBuilder = environmentBuilder;
		lookups = new ArrayList<>();
		pathPrefixValueBuilder = new ConfigurationValueBuilderHelper<>(myself, String.class);
		pathSuffixValueBuilder = new ConfigurationValueBuilderHelper<>(myself, String.class);
	}

	/**
	 * Configure lookup prefix. For example:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:");
	 * 
	 * // path prefixed by classpath: matches 
	 * // then classpath resolver is used
	 * resourceResolver.getResource("classpath:foo/bar.html");
	 * // path is not prefixed (or using another prefix) doesn't match 
	 * // then classpath resolver is not used
	 * resourceResolver.getResource("foo/bar.html");
	 * </pre>
	 * 
	 * <p>
	 * Several lookups can be provided:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:", "cp:");
	 * </pre>
	 * 
	 * If a path starts with one of the prefix ("classpath:" or "cp:"), the
	 * corresponding resolver (classpath resolver in this example) is used to
	 * resolve the file.
	 * 
	 * <p>
	 * Lookup may be empty meaning that if the path has no prefix, the
	 * corresponding resolver is use as default. For example:
	 * 
	 * <pre>
	 * .classpath().lookup("");
	 * </pre>
	 * 
	 * If the path is "foo/bar.html" is provided then the classpath resolver is
	 * used.
	 * 
	 * @param prefix
	 *            one or several prefixes that indicates which resolver to use
	 *            according to path
	 * @return this instance for fluent chaining
	 */
	public MYSELF lookup(String... prefix) {
		this.lookups.addAll(asList(prefix));
		return myself;
	}

	/**
	 * Configure lookup prefix. For example:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:");
	 * 
	 * // path prefixed by classpath: matches 
	 * // then classpath resolver is used
	 * resourceResolver.getResource("classpath:foo/bar.html");
	 * // path is not prefixed (or using another prefix) doesn't match 
	 * // then classpath resolver is not used
	 * resourceResolver.getResource("foo/bar.html");
	 * </pre>
	 * 
	 * <p>
	 * Several lookups can be provided:
	 * 
	 * <pre>
	 * .classpath().lookup("classpath:", "cp:");
	 * </pre>
	 * 
	 * If a path starts with one of the prefix ("classpath:" or "cp:"), the
	 * corresponding resolver (classpath resolver in this example) is used to
	 * resolve the file.
	 * 
	 * <p>
	 * Lookup may be empty meaning that if the path has no prefix, the
	 * corresponding resolver is use as default. For example:
	 * 
	 * <pre>
	 * .classpath().lookup("");
	 * </pre>
	 * 
	 * If the path is "foo/bar.html" is provided then the classpath resolver is
	 * used.
	 * 
	 * @param prefix
	 *            one or several prefixes that indicates which resolver to use
	 *            according to path
	 * @return this instance for fluent chaining
	 */
	public MYSELF lookup(List<String> prefix) {
		this.lookups.addAll(prefix);
		return myself;
	}
	
	@Override
	public ResourceResolver build() {
		ResourceResolver resolver = createResolver();
		if (!(resolver instanceof RelativisableResourceResolver) || environmentBuilder == null) {
			return resolver;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		String resolvedPathPrefix = pathPrefixValueBuilder.getValue(propertyResolver, "");
		String resolvedPathSuffix = pathSuffixValueBuilder.getValue(propertyResolver, "");
		if (!resolvedPathPrefix.isEmpty() || !resolvedPathSuffix.isEmpty()) {
			LOG.debug("Using parentPath {} and extension {} for resource resolution", resolvedPathPrefix, resolvedPathSuffix);
			resolver = new RelativeResolver((RelativisableResourceResolver) resolver, resolvedPathPrefix, resolvedPathSuffix);
		}
		return resolver;
	}

	protected abstract ResourceResolver createResolver();

	/**
	 * Provide the list of registered lookups
	 * 
	 * @return the list of lookups
	 */
	public List<String> getLookups() {
		return lookups;
	}
}
