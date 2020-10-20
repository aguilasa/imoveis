package properties.imobiliarias;

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
import properties.base.IImovel;
import properties.base.Imobiliaria;
import properties.base.ImobiliariaHtml;
import properties.base.ImovelHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Caravela extends ImobiliariaHtml {

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
		return String.format(URLBASE, type.equals(PropertyType.APARTMENT) ? 2 : 3);
	}

	@Override
	public Elements getElementos() {
		Document document = getDocument();
		return document.select("div.quadro_prod");
	}

	@Override
	public Map<String, String> getPayload() {
		return new LinkedHashMap<>();
	}

	@Override
	public IImovel newImovel(Element elemento) {
		return new ImovelImpl(elemento, type);
	}

	private class ImovelImpl extends ImovelHtml {

		public ImovelImpl(Element elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void carregarUrl() {
			Element link = elemento.select("a").first();
			setUrl(BASEIMOVEL.concat(link.attr("href")));
		}

		@Override
		public void carregarNome() {
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
		public void carregarBairro() {
		}

		@Override
		public void carregarPreco() {
		}

		@Override
		public void carregarQuartos() {
		}

		@Override
		public void carregarVagas() {
		}

		@Override
		public void carregarSuites() {
		}

		@Override
		public void carregarArea() {
		}

		@Override
		public void carregarAnunciante() {
			setAdvertiser("Caravela");
		}

		@Override
		public void carregarCondominio() {
		}

		@Override
		public void carregarEndereco() {
		}

	}

	public static void main(String[] args) {
		Imobiliaria imobiliaria = new Caravela(PropertyType.APARTMENT, ActionType.RENT);
		List<IImovel> imos = imobiliaria.getProperties();
		Excel.getInstance().clear();
		for (IImovel imo : imos) {
			Excel.getInstance().addImovel(imo);
			JSONObject json = Utils.imovelToJson(imo);
			System.out.println(json.toString());
		}
		Excel.getInstance().gerar();
	}
}
