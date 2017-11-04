package org.simmi.distann;

import java.util.Collections;
import java.util.Comparator;

public class NullComparators {
	static <T> Comparator<T> atEnd(final Comparator<T> comparator) {
		return new Comparator<T>() {

			public int compare(T o1, T o2) {
				if (o1 == null && o2 == null) {
					return 0;
				}

				if (o1 == null) {
					return 1;
				}

				if (o2 == null) {
					return -1;
				}

				return comparator.compare(o1, o2);
			}
		};
	}

	static <T> Comparator<T> atBeginning(final Comparator<T> comparator) {
		return Collections.reverseOrder(atEnd(comparator));
	}
}