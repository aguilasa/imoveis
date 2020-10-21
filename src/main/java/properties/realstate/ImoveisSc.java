package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import properties.utils.Utils;

public class ImoveisSc extends RealStateHtml {

	private static final String URLBASE = "https://www.imoveis-sc.com.br/blumenau/alugar/%s?page=%d";

	public ImoveisSc(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("article.imovel");
	}

	@Override
	public String getUrl() {
		return String.format(URLBASE, type, page);
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		String pagecao = document.select("div.navigation").first().text();
		pagecao = pagecao.replaceAll("[^-?0-9]+", " ");
		LinkedList<String> lista = new LinkedList<>(Arrays.asList(pagecao.trim().split(" ")));
		return Integer.valueOf(lista.getLast());
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
			Element link = elemento.select("h2.imovel-titulo a").first();
			setName(link.text().trim());
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("h2.imovel-titulo a").first();
			setUrl(link.attr("href"));
		}

		@Override
		public void loadPrice() {
			setPriceStr(elemento.select("span.imovel-preco small").first().text().trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.select("div.imovel-extra strong").first().text().replace("Blumenau, ", "").trim());
		}

		@Override
		public void loadRooms() {
			Elements dados = elemento.select("ul.imovel-info li");
			for (Element dado : dados) {
				String valor = dado.text().trim();
				if (valor.toUpperCase().contains("QUARTO")) {
					setRooms(Integer.valueOf(valor.split(" ")[0].trim()));
					break;
				}
			}
		}

		@Override
		public void loadParkingSpaces() {
			Elements dados = elemento.select("ul.imovel-info li");
			for (Element dado : dados) {
				String valor = dado.text().trim();
				if (valor.toUpperCase().contains("VAGA")) {
					setParkingSpaces(Integer.valueOf(valor.split(" ")[0].trim()));
					break;
				}
			}
		}

		@Override
		public void loadSuites() {
			Elements dados = elemento.select("ul.imovel-info li");
			for (Element dado : dados) {
				String valor = dado.text().trim();
				if (valor.toUpperCase().contains("SUITE")) {
					setSuites(Integer.valueOf(valor.split(" ")[0].trim()));
					break;
				}
			}
		}

		@Override
		public void loadArea() {
			Elements dados = elemento.select("ul.imovel-info li");
			for (Element dado : dados) {
				String valor = dado.text().trim();
				if (valor.toUpperCase().contains("M�")) {
					setArea(textoParaReal(valor.split(" ")[0].trim()));
					break;
				}
			}
		}

		@Override
		public void loadAdvertiser() {
			String advertiser = elemento.select("a.imovel-anunciante").first().attr("title").trim();
			String[] quebra = advertiser.split(" - ");
			if (quebra.length == 2) {
				advertiser = quebra[0].trim();
			}
			setAdvertiser(advertiser);
		}

		@Override
		public void loadCondominium() {
			Document documento = getDocumento();
			Elements dados = documento.select("li.visualizacao-caracteristica-item");
			if (!dados.isEmpty()) {
				Element dado = dados.first();
				dados = dado.select("ul li");
				for (Element li : dados) {
					String valor = li.text().toUpperCase().trim();
					if (valor.contains("CONDOM�NIO")) {
						valor = valor.replace("+", "").replace("R$", "").replace("CONDOM�NIO", "").replace(".", "")
								.replace(",", ".").trim();
						String[] quebrado = valor.split("\\.");
						if (quebrado.length == 2) {
							setCondominium(Double.valueOf(valor));
						} else {
							valor = "";
							for (int i = 0; i < quebrado.length - 1; i++) {
								valor = valor.concat(quebrado[i]);
							}
							valor = valor.concat(".").concat(quebrado[quebrado.length - 1]);
							setCondominium(Double.valueOf(valor));
						}
						break;
					}
				}
			}
		}

		@Override
		public void loadAddress() {
			Document documento = getDocumento();
			Elements selecao = documento.select("address.visualizar-endereco-texto");
			if (!selecao.isEmpty()) {
				setAddress(selecao.first().text().trim());
			}
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new ImoveisSc(PropertyType.Apartment, ActionType.RENT);
		List<IProperty> imos = imobiliaria.getProperties();
		for (IProperty imo : imos) {
			JSONObject json = Utils.imovelToJson(imo);
			System.out.println(json.toString());
		}
	}

}
