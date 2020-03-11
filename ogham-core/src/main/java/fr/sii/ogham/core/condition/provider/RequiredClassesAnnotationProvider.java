package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.condition.RequiredClass;
import fr.sii.ogham.core.builder.condition.RequiredClasses;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.fluent.Conditions;

/**
 * Provider that handle {@link RequiredClasses} annotation to provide a
 * condition.
 * 
 * It delegates handling of {@link RequiredClass} to
 * {@link RequiredClassAnnotationProvider}.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the kind of the object under conditions
 */
public class RequiredClassesAnnotationProvider<T> implements ConditionProvider<RequiredClasses, T> {
	private final RequiredClassAnnotationProvider<T> delegate;

	public RequiredClassesAnnotationProvider() {
		super();
		this.delegate = new RequiredClassAnnotationProvider<>();
	}

	@Override
	public Condition<T> provide(RequiredClasses annotation) {
		if (annotation == null) {
			return new FixedCondition<>(true);
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			for (String requiredClassName : annotation.value()) {
				mainCondition = mainCondition.and(Conditions.<T> requiredClass(requiredClassName));
			}
			for (RequiredClass subAnnotation : annotation.classes()) {
				mainCondition = mainCondition.and(delegate.provide(subAnnotation));
			}
			return mainCondition;
		}
	}

}
