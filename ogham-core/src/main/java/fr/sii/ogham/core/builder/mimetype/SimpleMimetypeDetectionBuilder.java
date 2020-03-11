package fr.sii.ogham.core.builder.mimetype;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.apache.tika.Tika;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.FixedMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.OverrideMimetypeProvider;
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer;

/**
 * Builds a {@link FallbackMimeTypeProvider}:
 * <ul>
 * <li>If {@link #tika()} has been called, then registers {@link Tika} as main
 * mimetype detector</li>
 * <li>If {@link #defaultMimetype(String)} has been called, then registers a
 * fallback to provide a default mimetype if none of the previously registered
 * detectors could detect mimetype</li>
 * <li>If no detector has been registered a {@link BuildException} is thrown
 * (mimetype detection is required by many Ogham components</li>
 * <li>If only one detector is registered, the {@link FallbackMimeTypeProvider}
 * is not used and instead the alone detector is directly used</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimpleMimetypeDetectionBuilder<P> extends AbstractParent<P> implements MimetypeDetectionBuilder<P> {
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<SimpleMimetypeDetectionBuilder<P>, String> defaultMimetypeValueBuilder;
	private TikaBuilder<MimetypeDetectionBuilder<P>> tikaBuilder;
	private SimpleReplaceMimetypeBuilder<MimetypeDetectionBuilder<P>> replaceMimetypeBuilder;

	/**
	 * Initializes the builder with the parent instance (used by the
	 * {@link #and()} method) and the {@link EnvironmentBuilder}. The
	 * {@link EnvironmentBuilder} is used to evaluate property values when
	 * {@link #build()} is called.
	 * 
	 * @param parent
	 *            the parent instance
	 * @param buildContext
	 *            used to evaluate property values
	 */
	public SimpleMimetypeDetectionBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		defaultMimetypeValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
	}

	@Override
	public TikaBuilder<MimetypeDetectionBuilder<P>> tika() {
		if (tikaBuilder == null) {
			tikaBuilder = new SimpleTikaBuilder<>(this, buildContext);
		}
		return tikaBuilder;
	}

	@Override
	public MimetypeDetectionBuilder<P> defaultMimetype(String mimetype) {
		defaultMimetypeValueBuilder.setValue(mimetype);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<MimetypeDetectionBuilder<P>, String> defaultMimetype() {
		return new ConfigurationValueBuilderDelegate<>(this, defaultMimetypeValueBuilder);
	}

	@Override
	public ReplaceMimetypeBuilder<MimetypeDetectionBuilder<P>> replace() {
		if (replaceMimetypeBuilder == null) {
			replaceMimetypeBuilder = new SimpleReplaceMimetypeBuilder<>(this);
		}
		return replaceMimetypeBuilder;
	}

	@Override
	public MimeTypeProvider build() {
		try {
			List<MimeTypeProvider> providers = new ArrayList<>();
			buildTika(providers);
			buildDefault(providers);
			assertNotEmpty(providers);
			MimeTypeProvider provider = buildProvider(providers);
			return overrideProvider(provider);
		} catch (MimeTypeParseException e) {
			throw new BuildException("Failed to build mimetype provider", e);
		}
	}

	private static void assertNotEmpty(List<MimeTypeProvider> providers) {
		if (providers.isEmpty()) {
			throw new BuildException("No mimetype detector configured");
		}
	}

	private static MimeTypeProvider buildProvider(List<MimeTypeProvider> providers) {
		if (providers.size() == 1) {
			return providers.get(0);
		}
		return new FallbackMimeTypeProvider(providers);
	}

	private MimeTypeProvider overrideProvider(MimeTypeProvider provider) {
		if (replaceMimetypeBuilder == null) {
			return provider;
		}
		MimetypeReplacer replacer = replaceMimetypeBuilder.build();
		return new OverrideMimetypeProvider(provider, replacer);
	}

	private void buildTika(List<MimeTypeProvider> providers) {
		if (tikaBuilder != null) {
			providers.add(tikaBuilder.build());
		}
	}

	private void buildDefault(List<MimeTypeProvider> providers) throws MimeTypeParseException {
		String mimetype = defaultMimetypeValueBuilder.getValue();
		if (mimetype != null) {
			providers.add(buildContext.register(new FixedMimeTypeProvider(mimetype)));
		}
	}

}
