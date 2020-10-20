package properties.imobiliarias;

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
import properties.base.IImovel;
import properties.base.Imobiliaria;
import properties.base.ImobiliariaHtml;
import properties.base.ImovelHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Abelardo extends ImobiliariaHtml {

	private static final String IMOVELBASE = "http://www.abelardoimoveis.com.br";
	private static final String URLBASE = "http://www.abelardoimoveis.com.br/imoveis-type-%s-para-locacao-em-blumenau-pg-%s";

	public Abelardo(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Elements getElementos() {
		Document document = getDocument(getUrl());
		return document.select("div.imovel");
	}

	@Override
	public String getUrl() {
		return String.format(URLBASE, type, page);
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements pages = document.select("ul.nav-pages li a");
		if (!pages.isEmpty()) {
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
			Element link = elemento.select("a.visualizar-imovel").first();
			setName(link.attr("title"));
		}

		@Override
		public void carregarUrl() {
			Element link = elemento.select("a.visualizar-imovel").first();
			setUrl(IMOVELBASE.concat(link.attr("href")));
		}

		@Override
		public void carregarPreco() {
			setPriceStr(elemento.select("strong.preco-imovel").first().text().trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void carregarBairro() {
			Elements valores = elemento.select("div.endereco-imovel");
			if (!valores.isEmpty()) {
				Element valor = valores.first();
				String texto = valor.text().trim();
				Elements span = valor.select("span");
				if (!span.isEmpty()) {
					texto = texto.replace(span.first().text().trim(), "");
				}
				texto = texto.replaceAll("Blumenau -", "").trim();
				setDistrict(texto);
			}
		}

		@Override
		public void carregarQuartos() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.dormitorios div.quantidade");
			if (!dados.isEmpty()) {
				setRooms(Integer.valueOf(dados.first().text().trim()));
			}
		}

		@Override
		public void carregarVagas() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.garagens div.quantidade");
			if (!dados.isEmpty()) {
				setParkingSpaces(Integer.valueOf(dados.first().text().trim()));
			}
		}

		@Override
		public void carregarSuites() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.suites div.quantidade");
			if (!dados.isEmpty()) {
				setSuites(Integer.valueOf(dados.first().text().trim()));
			}
		}

		@Override
		public void carregarArea() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.areaprivada div.quantidade");
			if (!dados.isEmpty()) {
				setArea(textoParaReal(dados.first().text().trim()));
			}
		}

		@Override
		public void carregarAnunciante() {
			setAdvertiser("Abelardo");
		}

		@Override
		public void carregarCondominio() {
			Document documento = getDocumento();
			Elements resumo = documento.select("div.resumo-imovel");
			if (!resumo.isEmpty()) {
				String texto = resumo.first().text();
				setCondominium(buscarCondominio(texto));
			}
		}

		@Override
		public void carregarEndereco() {
		}

	}

	public static void main(String[] args) {
		Imobiliaria imobiliaria = new Abelardo(PropertyType.HOUSE, ActionType.RENT);
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
