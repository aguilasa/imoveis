package properties.base;

import java.util.LinkedHashMap;
import java.util.Map;

public class PropertyTypeValues<T> {

	private Map<PropertyType, T> values = new LinkedHashMap<>();

	public PropertyTypeValues<T> add(PropertyType type, T value) {
		values.put(type, value);
		return this;
	}

	public T get(PropertyType type) {
		return values.getOrDefault(type, null);
	}
}
