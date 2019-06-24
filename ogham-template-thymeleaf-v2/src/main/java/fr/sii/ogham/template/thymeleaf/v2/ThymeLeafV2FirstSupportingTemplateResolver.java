package fr.sii.ogham.template.thymeleaf.v2;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterNotFoundException;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;

/**
 * <p>
 * Decorator resolver that is able to manage {@link ResolvedResourcePath}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResourceResolver} the link between
 * path, {@link ResolvedResourcePath} and {@link ResourceResolver}. each lookup to a
 * dedicated {@link ResourceResolver}.
 * </p>
 * <p>
 * It delegates to a {@link FirstSupportingResolverAdapter} the link between
 * {@link ResourceResolver} and the {@link ITemplateResolver} implementation to
 * use with the given path.
 * </p>
 * 
 * @author Cyril Dejonghe
 * @see FirstSupportingResourceResolver
 * @see FirstSupportingResolverAdapter
 *
 */
public class ThymeLeafV2FirstSupportingTemplateResolver implements ITemplateResolver {

	private FirstSupportingResourceResolver resolver;
	private FirstSupportingResolverAdapter resolverAdapter;
	private boolean forceInitialize;

	public ThymeLeafV2FirstSupportingTemplateResolver(FirstSupportingResourceResolver resolver, FirstSupportingResolverAdapter resolverAdapter) {
		super();
		this.resolver = resolver;
		this.resolverAdapter = resolverAdapter;
	}

	@Override
	public String getName() {
		return "ThymeLeafFirstSupportingTemplateResolver";
	}

	@Override
	public Integer getOrder() {
		return 0;
	}

	@Override
	public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
		String unresolvedTemplateName = templateProcessingParameters.getTemplateName();
		ResourceResolver supportingResolver = resolver.getSupportingResolver(new UnresolvedPath(unresolvedTemplateName));
		ITemplateResolver templateResolver;
		try {
			templateResolver = resolverAdapter.adapt(supportingResolver);
			if (forceInitialize) {
				// we must initialize the new templateResolver
				templateResolver.initialize();
			}
			String resolvedPath = supportingResolver.resolve(new UnresolvedPath(unresolvedTemplateName)).getResolvedPath();
			TemplateProcessingParameters resolvedTemplateProcessingParameters = new TemplateProcessingParameters(templateProcessingParameters.getConfiguration(), resolvedPath,
					templateProcessingParameters.getContext());
			return templateResolver.resolveTemplate(resolvedTemplateProcessingParameters);
		} catch (NoResolverAdapterException e) {
			throw new ResolverAdapterNotFoundException("Unable to resolver template cause no adapter supporting template name '" + unresolvedTemplateName + "' was found. ", e);

		}

	}

	@Override
	public void initialize() {
		forceInitialize = true;
	}

}
