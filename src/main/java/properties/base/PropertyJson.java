package properties.base;

import org.json.JSONObject;

public abstract class PropertyJson extends Property {

	protected JSONObject elemento;

	public PropertyJson(JSONObject elemento, PropertyType type) {
		this.elemento = elemento;
		this.setPropertyType(type);
	}

}
