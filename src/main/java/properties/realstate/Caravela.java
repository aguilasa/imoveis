package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Caravela extends RealStateHtml {

	private static final String BASEIMOVEL = "http://caravela.imb.br/site/";
	private static final String URLBASE = "http://caravela.imb.br/site/busca.php?negocio=2&typeimovel=%d&cidade=1039";

	public Caravela(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public int getPages() {
		return 1;
	}

	@Override
	public String getUrl() {
		return String.format(URLBASE, type.equals(PropertyType.Apartment) ? 2 : 3);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument();
		return document.select("div.quadro_prod");
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
		public void loadUrl() {
			Element link = elemento.select("a").first();
			setUrl(BASEIMOVEL.concat(link.attr("href")));
		}

		@Override
		public void loadName() {
			Element dados = elemento.select("span.dados_prod").first();
			if (dados != null) {
				List<TextNode> nodes = dados.childNodes().stream().filter(c -> c instanceof TextNode)
						.map(c -> (TextNode) c).collect(Collectors.toList());
				setName(String.format("%s - %s", nodes.get(0).text().trim(), nodes.get(1).text().trim()));
				setDistrict(nodes.get(1).text().replace("Blumenau - ", "").trim());
				setPriceStr(nodes.get(2).text().replace("R$", "").trim());
				try {
					setPrice(textoParaReal(getPriceStr()));
				} catch (Exception e) {
					setPrice(0);
				}
			}
		}

		@Override
		public void loadDistrict() {
		}

		@Override
		public void loadPrice() {
		}

		@Override
		public void loadRooms() {
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
