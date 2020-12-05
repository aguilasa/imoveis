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

public class Alameda extends RealStateHtml {

	private static final String IMOVELBASE = "https://www.alamedablumenau.com.br/";
	private static final String URLBASE = "https://www.alamedablumenau.com.br/imoveis/%s/%s/blumenau";

	public Alameda(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		int p = 1;
		Elements pages = document.select("div.pagination-buttons a");
		for (Element page : pages) {
			String valor = page.text().trim();
			if (NumberUtils.isCreatable(valor)) {
				p = Integer.valueOf(valor);
			}
		}
		return p;
	}

	@Override
	public String getUrl() {
		String actionString = action.equals(ActionType.RENT) ? "para-alugar" : "a-venda";
		String propertyTypeValue = (String) getTypeValues().get(type);
		return String.format(URLBASE, actionString, propertyTypeValue, page);

	}

	@Override
	public Elements getElements() {
		Document document = getDocument();
		return document.select("div.card.card-listing");
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("pagina", String.valueOf(page));
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
			setUrl(IMOVELBASE.concat(link.attr("href")));
		}

		@Override
		public void loadName() {
			String texto1 = elemento.select("h2.card-title").first().text().trim();
			String texto2 = elemento.select("h3.card-text").first().text().trim();
			setName(String.format("%s - %s", texto1, texto2));
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.select("h2.card-title").first().text().trim());
		}

		@Override
		public void loadPrice() {
			Elements dados = elemento.select("span.h-money.location");
			setPriceStr(dados.last().text().trim());
			try {
				setPrice(textoParaReal(getPriceStr().replace("R$", "")));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadRooms() {
			Elements dados = elemento.select("div.values div.value p");
			for (Element dado : dados) {
				String texto = dado.text().trim();
				String[] quebrado = texto.split(" ");
				if (texto.contains("quarto")) {
					setRooms(Integer.valueOf(quebrado[0].trim()));
				} else if (texto.contains("suíte")) {
					setSuites(Integer.valueOf(quebrado[0].trim()));
				} else if (texto.contains("vaga")) {
					setParkingSpaces(Integer.valueOf(quebrado[0].trim()));
				} else if (texto.contains("m²")) {
					setArea(Double.valueOf(quebrado[0].trim().replace(".", "").replace(",", ".")));
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
		public void loadAdvertiser() {
			setAdvertiser("Alameda Imóveis");
		}

		@Override
		public void loadCondominium() {
			Elements dados = elemento.select("div.info-right.text-xs-right p span.h-money");
			for (Element dado : dados) {
				String texto = dado.text().trim();
				if (texto.toLowerCase().contains("condom")) {
					String[] quebrado = texto.split("R\\$");
					texto = quebrado[1].trim();
					setCondominium(textoParaReal(texto));
					break;
				}
			}
		}

		@Override
		public void loadAddress() {
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.House, "casa");
			add(PropertyType.Apartment, "apartamento");
			add(PropertyType.Shed, "galpao");
			add(PropertyType.Store, "loja");
			add(PropertyType.Building, "predio");
			add(PropertyType.Ground, "terreno");
			add(PropertyType.Room, "sala");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Alameda(PropertyType.House, ActionType.RENT);
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
