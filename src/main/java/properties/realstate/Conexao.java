package properties.realstate;

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
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Conexao extends RealStateHtml {

	private static final String URLBASE = "http://www.imobiliariaconexao.com.br/imoveis/";

	public Conexao(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.imovel-item");
	}

	@Override
	public String getUrl() {
		return URLBASE;
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements dados = document.select("a.pagecao-lista-num");
		if (!dados.isEmpty()) {
			String valor = dados.last().text().trim();
			return Integer.valueOf(valor);
		}
		return 1;
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("operacao", "2");
		payload.put("cidade", "4202404");
		payload.put("type", type.equals(PropertyType.Apartment) ? "2" : "1");
		payload.put("qtd", "20");
		payload.put("page", String.valueOf(page));
		return payload;
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
			setName(elemento.select("a.imovel-item-link").first().text().replace("BLUMENAU / SC", "")
					.replace("BAIRRO", "").trim());
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("a.imovel-item-link").first();
			setUrl(link.attr("href"));
		}

		@Override
		public void loadPrice() {
			setPriceStr(elemento.select("div.imovel-item-preco").first().text().replace("Loca��o:", "")
					.replace("R$", "").trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.select("div.imovel-item-endereco").first().text().replace("BLUMENAU / SC", "")
					.replace("BAIRRO", "").trim());
		}

		@Override
		public void loadRooms() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.imovel-detalhe-conteudo-texto p");
			if (dados.size() == 2) {
				Element last = dados.last();
				String[] linhas = last.text().toUpperCase().split("�");
				for (String linha : linhas) {
					linha = linha.trim();
					if (linha.length() > 1) {
						String[] quebrado = linha.split(" ");
						if (linha.contains("DORMIT�RIO")) {
							setRooms(Integer.valueOf(quebrado[0].trim()));
							if (linha.contains("SU�TE")) {
								int i = 0;
								for (String valor : quebrado) {
									if (valor.contains("SU�TE")) {
										setSuites(Integer.valueOf(quebrado[i - 1].trim()));
									}
									i++;
								}
							}
						}
						if (linha.contains("GARAGE")) {
							setParkingSpaces(Integer.valueOf(quebrado[0].trim()));
						}
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
		public void loadAdvertiser() {
			setAdvertiser("Conex�o");
		}

		@Override
		public void loadCondominium() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.imovel-detalhe-preco");
			for (Element dado : dados) {
				String valor = dado.text().toLowerCase().trim();
				if (valor.contains("condom�nio")) {
					valor = valor.replace("condom�nio:", "").replace("r$", "").replace(".", "").replace(",", ".")
							.trim();
					setCondominium(Double.valueOf(valor));
				}
			}
		}

		@Override
		public void loadAddress() {
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Conexao(PropertyType.Apartment, ActionType.RENT);
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
