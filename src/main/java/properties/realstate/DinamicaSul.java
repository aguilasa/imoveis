package properties.realstate;

import static properties.utils.Utils.buscarCondominio;
import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class DinamicaSul extends RealStateHtml {

	private static final String URLBASE = "https://www.dinamicasul.com.br/buscar?tipoNegocio=%s&tipoImovel=%d&cidade=4202404&dormitorios=&vagas=&valor_min=&valor_max=&bairro=&pagina=24x1&suites=&banheiros=";

	public DinamicaSul(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.os_property-main");
	}

	@Override
	public String getUrl() {
		String actionString = action.equals(ActionType.RENT) ? "alugar" : "comprar";
		int propertyTypeValue = (int) getTypeValues().get(type);
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
			link = link.replaceAll("\r*\n", "");
			setUrl(link);
		}

		@Override
		public void loadName() {
			String value = xpath().text("//span[@class=\"title-product\"]");
			setName(value);
		}

		@Override
		public void loadPrice() {
			String value = xpath().text("//p[@class=\"uk-h3 status-price\"]").trim();
			setPriceStr(value.replace("R$", ""));
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			String value = getName().split("Bairro ")[1].trim();
			setDistrict(value);
		}

		@Override
		public void loadRooms() {
			Document documento = getDocumento();
			Elements dados = documento.select("li.uk-active div.uk-grid div.caracteristicas p");
			for (Element e : dados) {
				String linha = e.text().toUpperCase().trim();
				if (linha.length() > 1) {
					String[] quebrado = linha.split(" ");
					if (linha.contains("DORMITÓRIO")) {
						setRooms(Integer.valueOf(quebrado[1].trim()));
					} else if (linha.contains("SUÍTE")) {
						setSuites(Integer.valueOf(quebrado[1].trim()));
					} else if (linha.contains("VAGA")) {
						setParkingSpaces(Integer.valueOf(quebrado[1].trim()));
					} else if (linha.contains("ÁREA ÚTIL")) {
						setArea(textoParaReal(quebrado[2].trim()));
					}
				}
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
		public void loadCondominium() {
			Elements dados = getDocument().select("div.detail-body div");
			if (!dados.isEmpty()) {
				String value = dados.first().text();
				setCondominium(buscarCondominio(value));
			}
		}

		@Override
		public void loadAddress() {
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("DinamicaSul");
		}

	}

	private class TypeValues extends PropertyTypeValues<Integer> {

		public TypeValues() {
			add(PropertyType.Apartment, 1);
			add(PropertyType.RuralArea, 29);
			add(PropertyType.House, 23);
			add(PropertyType.CountryHouse, 17);
			add(PropertyType.Roof, 15);
			add(PropertyType.Shed, 7);
			add(PropertyType.Hotel, 28);
			add(PropertyType.Studio, 9);
			add(PropertyType.GroundFloorShop, 10);
			add(PropertyType.CommercialPoint, 25);
			add(PropertyType.Inn, 18);
			add(PropertyType.OfficeBuilding, 11);
			add(PropertyType.CommercialRoom, 13);
			add(PropertyType.SmallFarm, 5);
			add(PropertyType.TwoStoryhouse, 16);
			add(PropertyType.Ground, 14);
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new DinamicaSul(PropertyType.House, ActionType.RENT);
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
