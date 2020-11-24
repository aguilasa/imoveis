package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

public class Portal extends RealStateHtml {

	private static final String URLBASE = "https://imoveisportal.com";

	public Portal(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.row.box.box-imovel");
	}

	@Override
	public String getUrl() {
		return URLBASE.concat("/imoveis/busca");
	}

	@Override
	public int getPages() {
		int result = 1;
		Document document = getDocument();
		Elements dados = document.select("li.page-item a");
		if (!dados.isEmpty()) {
			for (Element e : dados) {
				String valor = e.text();
				if (NumberUtils.isCreatable(valor)) {
					result = Integer.valueOf(valor);
				}
			}
		}
		return result;
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("operacao", action.equals(ActionType.RENT) ? "locacao" : "venda");
		payload.put("cidade", "4202404");
		payload.put("tipo", (String) getTypeValues().get(type));
		if (page > 1) {
			int valor = (page - 1) * 16;
			payload.put("page", String.valueOf(valor));
		}
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
		public void loadName() {
			setName(elemento.select("div.box-img.left-list").first().text().trim());
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("a").first();
			setUrl(link.attr("href"));
		}

		@Override
		public void loadPrice() {
			Element valor = elemento.select("span.title.title-2.price").first();
			if (valor != null) {
				String[] texts = valor.text().split("R\\$");
				setPriceStr(texts[texts.length - 1].trim());
				try {
					setPrice(textoParaReal(getPriceStr()));
				} catch (Exception e) {
					setPrice(0);
				}
			}
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.select("div.text p").first().text().replace("Blumenau - SC", "").trim());
		}

		@Override
		public void loadRooms() {
			String value = xpath().text("//li/i[@class=\"fas fa-bed\"]/following-sibling::text()");
			if (StringUtils.isNotEmpty(value)) {
				setRooms(Integer.valueOf(value.trim()));
			}
		}

		@Override
		public void loadParkingSpaces() {
			String value = xpath().text("//li/i[@class=\"fas fa-warehouse\"]/following-sibling::text()");
			if (StringUtils.isNotEmpty(value)) {
				setParkingSpaces(Integer.valueOf(value.trim()));
			}
		}

		@Override
		public void loadSuites() {
		}

		@Override
		public void loadArea() {
			String value = xpath().text("//li/i[@class=\"fas fa-ruler\"]/following-sibling::text()");
			if (StringUtils.isNotEmpty(value)) {
				setArea(textoParaReal(value.replace("m²", "").trim()));
			}
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Portal");
		}

		@Override
		public void loadCondominium() {
		}

		@Override
		public void loadAddress() {
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.Apartment, "1");
			add(PropertyType.House, "23");
			add(PropertyType.Ground, "14");
			add(PropertyType.Roof, "15");
			add(PropertyType.Shed, "7");
			add(PropertyType.TwoStoryhouse, "16");
			add(PropertyType.OfficeBuilding, "11");
			add(PropertyType.CommercialRoom, "13");
			add(PropertyType.CountryHouse, "5");
			add(PropertyType.GroundFloorShop, "10");
			add(PropertyType.ResidentialBuilding, "12");
			add(PropertyType.IndustrialArea, "2");
			add(PropertyType.CountryHouse, "17");
			add(PropertyType.SemiDetached, "26");
			add(PropertyType.Studio, "9");
			add(PropertyType.CommercialPoint, "25");
			add(PropertyType.Inn, "18");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Portal(PropertyType.Apartment, ActionType.RENT);
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
