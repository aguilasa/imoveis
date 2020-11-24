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
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.base.PropertyTypeValues;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.excel.Excel;
import properties.utils.Utils;

public class ACRC extends RealStateHtml {

	private static final String IMOVELBASE = "https://www.acrcimoveis.com.br";
	private static final String URLBASE = "https://www.acrcimoveis.com.br/%s/sc/sc/blumenau/%s/ordem-valor/resultado-crescente/quantidade-48/page-%d/";

	public ACRC(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.resultado");
	}

	@Override
	public String getUrl() {
		String actionString = action.equals(ActionType.RENT) ? "alugar" : "comprar";
		String propertyTypeValue = (String) getTypeValues().get(type);
		return String.format(URLBASE, actionString, propertyTypeValue, page);
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements pages = document.select("ul.pagetion li a");
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
			Element link = elemento.select("div.foto a").first();
			setUrl(IMOVELBASE.concat(link.attr("href")));
		}

		@Override
		public void loadName() {
			String value = xpath().text("//h1[@class=\"titulo\"][text()]");
			setName(value);
		}

		@Override
		public void loadPrice() {
			String value = xpath().text("//div[h2=\"Locação \"]/h3[text()]").replace("R$", "").trim();
			setPriceStr(value);
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			String value = xpath().text("//h2[@class=\"cidade_bairro\"][text()]");
			value = value.replace("BLUMENAU, SC - ", "");
			value = value.replace("Blumenau, SC - ", "");
			setDistrict(value);
		}

		@Override
		public void loadRooms() {
			String value = xpath().text("//div[@title=\"Dormitórios\"]/span[1][text()]").replace("-", "").trim();
			if (StringUtils.isNotEmpty(value)) {
				setRooms(Integer.valueOf(value));
			}
		}

		@Override
		public void loadParkingSpaces() {
			String value = xpath().text("//div[@title=\"Vagas\"]/span[1][text()]").replace("-", "").trim();
			if (StringUtils.isNotEmpty(value)) {
				setParkingSpaces(Integer.valueOf(value.trim()));
			}
		}

		@Override
		public void loadSuites() {
			String value = xpath().text("//div[@title=\"Dormitórios\"]/span[3][text()]");
			if (StringUtils.isNotEmpty(value)) {
				value = value.replaceAll("\\D+", "").trim();
				setSuites(Integer.valueOf(value));
			}
		}

		@Override
		public void loadArea() {
			String value = xpath().text("//div[@title=\"Áreas\"]/span[1][text()]").trim();
			if (StringUtils.isNotEmpty(value)) {
				setArea(textoParaReal(value.split(" ")[0]));
			}
		}

		@Override
		public void loadCondominium() {
			String value = xpath().text("//div[@title=\"Valores\"]/span[2]/span[text()]").replace("-", "").trim();
			if (StringUtils.isNotEmpty(value)) {
				setCondominium(buscarCondominio(value));
			}
		}

		@Override
		public void loadAddress() {
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("ACRC");
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.Apartment, "APARTAMENTO");
			add(PropertyType.House, "CASA");
			add(PropertyType.Room, "SALA");
			add(PropertyType.Shed, "GALPAO");
			add(PropertyType.Hotel, "HOTEL");
			add(PropertyType.Studio, "KITNET");
			add(PropertyType.SmallFarm, "SITIO");
			add(PropertyType.ParkingSpace, "VAGA");
			add(PropertyType.Ground, "TERRENO");
			add(PropertyType.Building, "PRÉDIO");
			add(PropertyType.Point, "PONTO");
			add(PropertyType.Store, "LOJA");
			add(PropertyType.Farm, "FAZENDA");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new ACRC(PropertyType.House, ActionType.RENT);
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
