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
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.base.PropertyTypeValues;
import properties.base.RealState;
import properties.base.RealStateHtml;
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
		Elements dados = document.select("a.paginacao-lista-num");
		if (!dados.isEmpty()) {
			String valor = dados.last().text().trim();
			return Integer.valueOf(valor);
		}
		return 1;
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("operacao", ActionType.RENT.equals(action) ? "2" : "1");
		payload.put("cidade", "4202404");
		payload.put("tipo", (String) getTypeValues().get(type));
		payload.put("qtd", "20");
		payload.put("page", String.valueOf(page));
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
			setName(elemento.select("h2.imovel-item-titulo").first().text().trim());
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("div.imovel-item-conteudo a").first();
			setUrl(link.attr("href"));
		}

		@Override
		public void loadPrice() {
			String value = xpath().text("//div[strong=\"Locação\"]/strong/following-sibling::text()").trim();
			setPriceStr(value.replace(":", "").replace("R$", "").trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			String value = xpath().text("//div[contains(@class, \"imovel-item-endereco\")]/span[text()]").trim();
			setDistrict(value.replace("BLUMENAU / SC", "").replace("Bairro", "").replace("-", "").trim());
		}

		@Override
		public void loadRooms() {
			Document documento = getDocumento();
			Elements dados = documento.select("ul.imovel-detalhe-lista li");
			for (Element e : dados) {
				String linha = e.text().toUpperCase().trim();
				if (linha.length() > 1) {
					String[] quebrado = linha.split(" ");
					if (linha.contains("DORMITÓRIO")) {
						setRooms(Integer.valueOf(quebrado[0].trim()));
					}
					if (linha.contains("SUÍTE")) {
						int i = 0;
						for (String valor : quebrado) {
							if (valor.contains("SUÍTE")) {
								setSuites(Integer.valueOf(quebrado[i - 1].trim()));
							}
							i++;
						}
					}
					if (linha.contains("GARAGE")) {
						setParkingSpaces(Integer.valueOf(quebrado[0].trim()));
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
			setAdvertiser("Conexão");
		}

		@Override
		public void loadCondominium() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.imovel-detalhe-preco");
			for (Element dado : dados) {
				String valor = dado.text().toLowerCase().trim();
				if (valor.contains("condomínio")) {
					valor = valor.replace("condomínio:", "").replace("r$", "").replace(".", "").replace(",", ".")
							.trim();
					setCondominium(Double.valueOf(valor));
				}
			}
		}

		@Override
		public void loadAddress() {
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.House, "1");
			add(PropertyType.Apartment, "2");
			add(PropertyType.Ground, "3");
			add(PropertyType.CommercialRoom, "4");
			add(PropertyType.Roof, "5");
			add(PropertyType.GroundFloorShop, "6");
			add(PropertyType.OfficeBuilding, "7");
			add(PropertyType.CountryHouse, "8");
			add(PropertyType.Shed, "9");
			add(PropertyType.TwoStoryhouse, "10");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Conexao(PropertyType.House, ActionType.RENT);
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
