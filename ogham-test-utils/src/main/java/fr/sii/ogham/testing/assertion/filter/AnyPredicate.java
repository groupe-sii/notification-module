package fr.sii.ogham.testing.assertion.filter;

import com.google.common.base.Predicate;

public class AnyPredicate<T> implements Predicate<T> {
	@Override
	public boolean apply(T input) {
		return true;
	}
}
