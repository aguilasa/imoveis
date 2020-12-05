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

public class Conceito extends RealStateHtml {

	private static final String URLBASE = "https://www.conceitoimobiliaria.com.br/imoveis?pretensao=%s&tipo=%d&cidade=4202404&bairro=&pagina=%d";

	public Conceito(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument();
		return document.select("div.lista div.imovel-list div.item");
	}

	@Override
	public String getUrl() {
		String actionString = action.equals(ActionType.RENT) ? "alugar" : "comprar";
		int propertyTypeValue = (int) getTypeValues().get(type);
		return String.format(URLBASE, actionString, propertyTypeValue, page);
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements pages = document.select("div.pagination a");
		if (pages.size() > 1) {
			String valor = pages.get(pages.size() - 2).text();
			return Integer.valueOf(valor);
		}
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
			String link = elemento.select("a").first().attr("href");
			link = link.replace("//", "https://");
			setUrl(link);
		}

		@Override
		public void loadName() {
			String value = xpath().text("//h1[@class=\"titulo-page\"]");
			setName(value);
		}

		@Override
		public void loadPrice() {
			String value = elemento.select("span.prince").first().text().replace("R$", "").trim();
			setPriceStr(value);
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			String value = xpath().text("//div[@class=\"bairro\"]").replace("Bairro:", "").trim();
			setDistrict(value);
		}

		@Override
		public void loadRooms() {
			String value = xpath().text("//li/i[@class=\"fa fa-bed \"]/following-sibling::span");
			if (StringUtils.isNotEmpty(value)) {
				String[] quebrado = value.split(" ");
				setRooms(Integer.valueOf(quebrado[0]));

				if (quebrado.length == 4 && quebrado[3].trim().toLowerCase().contains("suíte")) {
					setSuites(Integer.valueOf(quebrado[2]));
				}
			}
		}

		@Override
		public void loadParkingSpaces() {
			String value = xpath().text("//li/i[@class=\"fa fa-car \"]/following-sibling::span");
			if (StringUtils.isNotEmpty(value)) {
				String[] quebrado = value.split(" ");
				setParkingSpaces(Integer.valueOf(quebrado[0]));
			}
		}

		@Override
		public void loadSuites() {
		}

		@Override
		public void loadArea() {
			String value = xpath().text("//li/i[@class=\"fa fa-object-ungroup\"]/following-sibling::span")
					.replace(" m²", "").trim();
			if (StringUtils.isNotEmpty(value)) {
				String[] quebrado = value.split(" ");
				if (NumberUtils.isCreatable(quebrado[0])) {
					setArea(Double.valueOf(quebrado[0]));
				}
			}
		}

		@Override
		public void loadCondominium() {
			String value = xpath().text("//div[@class=\"valor\"]/p").replace("+ Condomínio: ", "").replace("R$", "")
					.replace(",", ".").trim();
			if (NumberUtils.isCreatable(value)) {
				setCondominium(Double.valueOf(value));
			}
		}

		@Override
		public void loadAddress() {
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Conceito Imobiliária");
		}

	}

	private class TypeValues extends PropertyTypeValues<Integer> {

		public TypeValues() {
			add(PropertyType.Apartment, 1);
			add(PropertyType.RuralArea, 29);
			add(PropertyType.House, 23);
			add(PropertyType.Shed, 7);
			add(PropertyType.CommercialPoint, 25);
			add(PropertyType.OfficeBuilding, 11);
			add(PropertyType.CommercialRoom, 13);
			add(PropertyType.SmallFarm, 5);
			add(PropertyType.Ground, 14);
			add(PropertyType.Studio, 9);
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Conceito(PropertyType.Apartment, ActionType.RENT);
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
