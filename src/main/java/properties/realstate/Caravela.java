package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.base.PropertyTypeValues;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.excel.Excel;
import properties.utils.Utils;

public class Caravela extends RealStateHtml {

	private static final String BASEIMOVEL = "http://caravela.imb.br/site/";
	private static final String SEARCH = BASEIMOVEL + "imoveisbusca.php";

	public Caravela(PropertyType type, ActionType action) {
		super(type, action);
		setPost(true);
	}

	@Override
	public int getPages() {
		return 1;
	}

	@Override
	public String getUrl() {
		return SEARCH;
	}

	@Override
	public Elements getElements() {
		Document document = getDocument();
		return document.select("div.col-md-4.vfl_services_grid");
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("tipoimovel", (String) getTypeValues().get(type));
		payload.put("negocio", ActionType.RENT.equals(action) ? "Locacao" : "Venda");
		payload.put("cidade", "1039");
		payload.put("bairro", "undefined");
		payload.put("valor", "");
		payload.put("busca", "");
		return payload;
	}

	@Override
	public IProperty newProperty(Element elemento) {
		return new ImovelImpl(elemento, type);
	}

	@Override
	public PropertyTypeValues<?> getTypeValues() {
		if (typeValues == null) {
			typeValues = new TypeValues();
		}
		return typeValues;
	}

	private class ImovelImpl extends PropertyHtml {

		public ImovelImpl(Element elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("a").first();
			setUrl(BASEIMOVEL.concat(link.attr("href")));
		}

		@Override
		public void loadName() {
			String value = xpath().text("//div[@class=\"col-md-6 agileinfo_about_left\"]/h2[text()]");
			setName(value);
		}

		@Override
		public void loadDistrict() {
			String value = xpath().text("//p[strong=\"Bairro: \"]/strong/following-sibling::text()[1]");
			setDistrict(value);
		}

		@Override
		public void loadPrice() {
			String value = xpath().text("//div[@class=\"col-md-6 agileinfo_about_left\"]/h4/span[text()]");
			value = value.split("R\\$")[1].trim();
			setPriceStr(value);
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadRooms() {
			String valor = getMapValues().getOrDefault("Quarto(s):", "0");
			if (NumberUtils.isCreatable(valor)) {
				setRooms(Integer.valueOf(valor));
			}
		}

		@Override
		public void loadParkingSpaces() {
		}

		@Override
		public void loadSuites() {
		}

		@Override
		public void loadArea() {
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Caravela");
		}

		@Override
		public void loadCondominium() {
		}

		@Override
		public void loadAddress() {
		}

		private boolean mapLoaded = false;
		private Map<String, String> mapValues = new LinkedHashMap<>();

		private Map<String, String> getMapValues() {
			if (!mapLoaded) {
				List<String> keys = xpath().list("//p[strong=\"Observações:\"]/strong/following-sibling::text()");
				List<String> values = xpath().list("//p[strong=\"Observações:\"]/strong/following-sibling::strong[text()]");
				if (!keys.isEmpty() && keys.size() == values.size()) {
					for (int i = 0; i < keys.size(); i++) {
						String key = keys.get(i).trim();
						String value = values.get(i).trim();
						mapValues.put(key, value);
					}
				}
				mapLoaded = true;
			}
			return mapValues;
		}
	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.Apartment, "Apartamento");
			add(PropertyType.House, "Casa");
			add(PropertyType.Ground, "Terreno");
			add(PropertyType.CommercialHouse, "Sala comercial");
			add(PropertyType.Shed, "Galpão");
			add(PropertyType.RuralProperty, "Imóvel Rural");
			add(PropertyType.Store, "Loja");
			add(PropertyType.Others, "Outros Imóveis");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Caravela(PropertyType.Apartment, ActionType.RENT);
		List<IProperty> imos = imobiliaria.getProperties();
		Excel.getInstance().clear();
		for (IProperty imo : imos) {
			Excel.getInstance().addImovel(imo);
			JSONObject json = Utils.imovelToJson(imo);
			System.out.println(json.toString());
		}
		Excel.getInstance().gerar();
	}
}
