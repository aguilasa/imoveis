package properties.base;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RealStateJson extends RealState {

	public RealStateJson(PropertyType type, ActionType action) {
		super(type, action);
	}

	public void load() {
		int pages = getPages();

		for (int i = 1; i <= pages; i++) {
			setPage(i);
			String url = getUrl();
			JSONArray elementos = getElementos(url);
			int total = elementos.length();
			for (int j = 0; j < total; j++) {
				JSONObject elemento = elementos.getJSONObject(j);
				IProperty imovel = newImovel(elemento);
				imovel.load();
				properties.add(imovel);
			}
		}
	}

	public abstract JSONArray getElementos(String url);

	public abstract IProperty newImovel(JSONObject elemento);

}
