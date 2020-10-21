package properties.realstate;

import static properties.utils.Utils.buscarCondominio;
import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Abelardo extends RealStateHtml {

	private static final String URLBASE = "https://www.abelardoimoveis.com.br/buscar?tipoNegocio=%s&tipoImovel=%d&cidade=4202404&dormitorios=&suites=&banheiros=&vagas=&valor_min=&valor_max=&bairro=";
	private static Map<PropertyType, Integer> PropertyTypeValues = new LinkedHashMap<>();

	public Abelardo(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.listing-box");
	}

	@Override
	public String getUrl() {
		String actionString = action.equals(ActionType.RENT) ? "alugar" : "comprar";
		int propertyTypeValue = PropertyTypeValues.getOrDefault(type, 1);
		return String.format(URLBASE, actionString, propertyTypeValue);
	}

	@Override
	public int getPages() {
		return 1;
	}

	@Override
	public Map<String, String> getPayload() {
		return new LinkedHashMap<>();
	}

	@Override
	public IProperty newProperty(Element elemento) {
		return new ImovelImpl(elemento, type);
	}

	private class ImovelImpl extends PropertyHtml {

		public ImovelImpl(Element elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void loadName() {
			String value = xpath().text("//div[@class=\"content-title-inner\"]/div/h1[text()]");
			setName(value);
		}

		@Override
		public void loadUrl() {
			String link = elemento.select("a").first().attr("href");
			link = link.replaceAll("\r*\n", "");
			setUrl(link);
		}

		@Override
		public void loadPrice() {
			String value = xpath().text("//li[strong=\"Valor Locação:\"]/span[text()]").replace("R$", "").trim();
			setPriceStr(value);
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			String value = xpath().text("//li[strong=\"Bairro:\"]/span[text()]");
			setDistrict(value);
		}

		@Override
		public void loadRooms() {
			String value = xpath().text("//li[strong=\"Dormitórios:\"]/span[text()]");
			if (StringUtils.isNotEmpty(value)) {
				setRooms(Integer.valueOf(value));
			}
		}

		@Override
		public void loadParkingSpaces() {
			String value = xpath().text("//div[div=\"Vagas de Garagem\"]/div[2][text()]");
			if (StringUtils.isNotEmpty(value)) {
				setParkingSpaces(Integer.valueOf(value.trim()));
			}
		}

		@Override
		public void loadSuites() {
			String value = xpath().text("//li[strong=\"Suítes:\"]/span[text()]");
			if (StringUtils.isNotEmpty(value)) {
				setSuites(Integer.valueOf(value));
			}
		}

		@Override
		public void loadArea() {
			String value = xpath().text("//li[strong=\"Área Útil:\"]/span[text()]").replace("M2", "").trim();
			if (StringUtils.isNotEmpty(value)) {
				setArea(textoParaReal(value));
			}
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Abelardo");
		}

		@Override
		public void loadCondominium() {
			String value = xpath().text("//li[strong=\"Valor Condomínio:\"]/span[text()]");
			setCondominium(buscarCondominio(value));
		}

		@Override
		public void loadAddress() {
		}

	}

	static {
		PropertyTypeValues.put(PropertyType.Apartment, 1);
		PropertyTypeValues.put(PropertyType.RuralArea, 29);
		PropertyTypeValues.put(PropertyType.House, 23);
		PropertyTypeValues.put(PropertyType.CountryHouse, 17);
		PropertyTypeValues.put(PropertyType.Roof, 15);
		PropertyTypeValues.put(PropertyType.Shed, 7);
		PropertyTypeValues.put(PropertyType.Hotel, 28);
		PropertyTypeValues.put(PropertyType.Studio, 9);
		PropertyTypeValues.put(PropertyType.GroundFloorShop, 10);
		PropertyTypeValues.put(PropertyType.CommercialPoint, 25);
		PropertyTypeValues.put(PropertyType.Inn, 18);
		PropertyTypeValues.put(PropertyType.OfficeBuilding, 11);
		PropertyTypeValues.put(PropertyType.CommercialRoom, 13);
		PropertyTypeValues.put(PropertyType.SmallFarm, 5);
		PropertyTypeValues.put(PropertyType.TwoStoryhouse, 16);
		PropertyTypeValues.put(PropertyType.Ground, 14);
	}

	public static void main(String[] args) {
		RealState imobiliaria = new Abelardo(PropertyType.House, ActionType.RENT);
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
