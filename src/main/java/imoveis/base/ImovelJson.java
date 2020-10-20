package imoveis.base;

import org.json.JSONObject;

public abstract class ImovelJson extends Imovel {

	protected JSONObject elemento;

	public ImovelJson(JSONObject elemento, PropertyType type) {
		this.elemento = elemento;
		this.setPropertyType(type);
	}

}
