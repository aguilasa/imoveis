package properties.imobiliarias;

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
import properties.base.IImovel;
import properties.base.Imobiliaria;
import properties.base.ImobiliariaHtml;
import properties.base.ImovelHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class ACRC extends ImobiliariaHtml {

	private static final String IMOVELBASE = "https://www.acrcimoveis.com.br";
	private static final String URLBASE = "https://www.acrcimoveis.com.br/alugar/sc/sc/blumenau/%s/ordem-valor/resultado-crescente/quantidade-48/page-%d/";

	public ACRC(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElementos() {
		Document document = getDocument(getUrl());
		return document.select("div.resultado");
	}

	@Override
	public String getUrl() {
		return String.format(URLBASE, type, page);
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
	public IImovel newImovel(Element elemento) {
		return new ImovelImpl(elemento, type);
	}

	private class ImovelImpl extends ImovelHtml {

		public ImovelImpl(Element elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void carregarNome() {
			Element link = elemento.select("div.foto a").first().select("img").first();
			setName(link.attr("title"));
		}

		@Override
		public void carregarUrl() {
			Element link = elemento.select("div.foto a").first();
			setUrl(IMOVELBASE.concat(link.attr("href")));
		}

		@Override
		public void carregarPreco() {
			setPriceStr(elemento.select("div.valor h5").first().text().trim());
			try {
				setPrice(textoParaReal(getPriceStr().replace("R$", "")));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void carregarBairro() {
			setDistrict(elemento.select("h4.bairro").first().text().trim());
		}

		@Override
		public void carregarQuartos() {
			Document documento = getDocumento();
			Elements dados = documento.select("div[title=\"Dormit�rios\"]");
			if (!dados.isEmpty()) {
				dados = dados.first().select("span");
				if (!dados.isEmpty()) {
					String valor = dados.first().text().trim();
					if (StringUtils.isNumeric(valor)) {
						setRooms(Integer.valueOf(valor));
					}
				}
			}
		}

		@Override
		public void carregarVagas() {
			Document documento = getDocumento();
			Elements dados = documento.select("div[title=\"Vagas\"]");
			if (!dados.isEmpty()) {
				dados = dados.first().select("span");
				if (!dados.isEmpty()) {
					String valor = dados.first().text().trim();
					if (StringUtils.isNumeric(valor)) {
						setParkingSpaces(Integer.valueOf(valor));
					}
				}
			}
		}

		@Override
		public void carregarSuites() {
		}

		@Override
		public void carregarArea() {
			Document documento = getDocumento();
			Elements dados = documento.select("div[title=\"�reas\"]");
			if (!dados.isEmpty()) {
				dados = dados.first().select("span");
				if (!dados.isEmpty()) {
					String valor = dados.first().text().trim().replaceAll("[^\\.0123456789]", "");
					if (NumberUtils.isCreatable(valor)) {
						setArea(Double.valueOf(valor));
					}
				}
			}
		}

		@Override
		public void carregarAnunciante() {
			setAdvertiser("ACRC");
		}

		@Override
		public void carregarCondominio() {
			Document documento = getDocumento();
			Elements dados = documento.select("div[title=\"Valores\"]");
			if (!dados.isEmpty()) {
				dados = dados.first().select("span span");
				if (!dados.isEmpty()) {
					String valor = dados.get(1).text().replace("R$", "").replace(".", "").replace(",", ".").trim();
					if (NumberUtils.isCreatable(valor)) {
						setCondominium(Double.valueOf(valor));
					}
				}
			}
		}

		@Override
		public void carregarEndereco() {
		}

	}

	public static void main(String[] args) {
		Imobiliaria imobiliaria = new ACRC(PropertyType.HOUSE, ActionType.RENT);
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
