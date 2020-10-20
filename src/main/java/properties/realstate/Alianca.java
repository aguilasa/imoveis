package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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

public class Alianca extends RealStateHtml {

	private static final String URLBASE = "http://www.alianca.imb.br";
	private static final String PESQUISA = URLBASE + "/pesquisa-de-imoveis";

	public Alianca(PropertyType type, ActionType action) {
		super(type, action);
		setPost(true);
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div#imoveis_grid div.imovel_bloco_voosuave");
	}

	@Override
	public String getUrl() {
		return PESQUISA;
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements elements = document.select("div#pagetionTopContent div.pagetion a");
		if (!elements.isEmpty()) {
			int p = 0;
			for (Element e : elements) {
				String valor = e.text().trim();
				if (NumberUtils.isCreatable(valor)) {
					p = Integer.valueOf(valor);
				}
			}
			return p;
		}
		return 1;
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("codTB", "1");
		payload.put("codUF", "0");
		payload.put("codCid", "11");
		payload.put("codBai", "0");
		payload.put("codTP", type.equals(PropertyType.APARTMENT) ? "2" : "1");
		payload.put("qtdQuartos", "0");
		payload.put("qtdSuites", "0");
		payload.put("qtdSalas", "0");
		payload.put("qtdGaragens", "0");
		payload.put("codVal", "Min");
		payload.put("codValBK", "0");
		payload.put("codVal2", "Max");
		payload.put("codValbk2", "0");
		payload.put("searchtype", "2");
		payload.put("codOrd", "1");
		payload.put("pageIndex", String.valueOf(page - 1));
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
			Element link = elemento.select("a.localizacao").first();
			setName(link.text().replace("Bairro:", "").trim());
			setUrl(URLBASE.concat(link.attr("href")));
			setDistrict(getName());
		}

		@Override
		public void loadUrl() {
		}

		@Override
		public void loadPrice() {
			setPriceStr(elemento.select("span.imovel_valor").first().text().replace("R$", "").trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
		}

		@Override
		public void loadRooms() {
			Element dado = elemento.select("a.quartos").first();
			if (dado != null) {
				String valor = dado.text().trim();
				if (NumberUtils.isCreatable(valor)) {
					setRooms(Integer.valueOf(valor));
				}
			}
		}

		@Override
		public void loadParkingSpaces() {
			Element dado = elemento.select("a.garagens").first();
			if (dado != null) {
				String valor = dado.text().trim();
				if (NumberUtils.isCreatable(valor)) {
					setParkingSpaces(Integer.valueOf(valor));
				}
			}
		}

		@Override
		public void loadSuites() {
			Element dado = elemento.select("a.suites").first();
			if (dado != null) {
				String valor = dado.text().trim();
				if (NumberUtils.isCreatable(valor)) {
					setSuites(Integer.valueOf(valor));
				}
			}
		}

		@Override
		public void loadArea() {
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Alian�a");
		}

		@Override
		public void loadCondominium() {
			Document documento = getDocumento();
			Elements dados = documento.select("b.preco_m2");
			for (Element dado : dados) {
				Element anterior = dado.previousElementSibling();
				if (anterior != null && anterior.text().toLowerCase().contains("condom�nio")) {
					String valor = dado.text().toLowerCase().trim();
					valor = valor.replace(".", "").replace(",", ".").trim();
					setCondominium(Double.valueOf(valor));
				}
			}
		}

		@Override
		public void loadAddress() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.panel-body p b");
			for (Element dado : dados) {
				String valor = dado.text().toLowerCase().trim();
				if (valor.contains("localiza")) {
					StringBuilder localizacao = new StringBuilder();
					Node proximo = dado.nextSibling();
					while (proximo != null && !(proximo instanceof Comment)) {
						if (proximo instanceof TextNode) {
							TextNode text = (TextNode) proximo;
							localizacao.append(text.text().trim());
						}
						proximo = proximo.nextSibling();
					}
					setAddress(localizacao.toString().trim());
				}
			}
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Alianca(PropertyType.HOUSE, ActionType.RENT);
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
