package utilities;

import java.util.Collection;

import org.fest.assertions.Description;

public class CollectionDescription implements Description {

	private final Collection<?> describee;

	public CollectionDescription(Collection<?> describee) {
		this.describee = describee;
	}

	@Override
	public String value() {
		return collectionPrettyPrint(describee);
	}

	private static String collectionPrettyPrint(Collection<?> c) {
		StringBuilder stringBuilder = new StringBuilder("\nElements in collection:\n");
		for (Object element : c) {
			stringBuilder.append(element.toString()).append("\n");
		}
		return stringBuilder.toString();
	}

}
