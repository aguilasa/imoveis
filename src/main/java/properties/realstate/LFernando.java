package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.base.PropertyTypeValues;
import properties.excel.Excel;
import properties.utils.HttpClientHelper;
import properties.utils.Utils;

public class LFernando extends RealStateHtml {

	private static final String BASE = "www.lfernando.com.br";
	private static final String URLBASE = "http://" + BASE + "/";

	public LFernando(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Document getDocument() {
		return getDocument(getUrl());
	}

	@Override
	public Document getDocument(String url) {
		try (HttpClientHelper helper = new HttpClientHelper()) {
			HttpGet httpget = helper.httpGet("http://www.lfernando.com.br/cidade/");
			helper.execute(httpget);
			httpget = helper.httpGet("http://www.lfernando.com.br/c_filial.php?id=1");
			helper.execute(httpget);

			httpget = helper.httpGet(url);
			String html = helper.execute(httpget);
			return Jsoup.parse(html);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Elements getElements() {
		Document document = getDocument();
		return document.select("div.imovel");
	}

	@Override
	public String getUrl() {
		try {
			URIBuilder builder = new URIBuilder();
			builder.setScheme("http").setHost(BASE).setPath("/pesquisa");
			Map<String, String> payload = getPayload();
			Iterator<String> iterator = payload.keySet().iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				String value = payload.get(name);
				builder.addParameter(name, value);
			}
			return builder.build().toString();
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Element elemento = document.select("a.paginacao_pagina").last();
		if (elemento != null) {
			String valor = elemento.text().trim();
			return Integer.valueOf(valor);
		}
		return 1;
	}

	@Override
	public Map<String, String> getPayload() {
		LinkedHashMap<String, String> payload = new LinkedHashMap<>();
		payload.put("opcao", action.equals(ActionType.RENT) ? "Locação" : "Venda");
		payload.put("cidade", "Blumenau/SC");
		payload.put("tipo", (String) getTypeValues().get(type));
		int init = (page - 1) * 15;
		payload.put("init", String.valueOf(init));
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
		}

		@Override
		public void loadUrl() {
			Element link = elemento.select("div.imovel_imagem_container a").first();
			setUrl(URLBASE.concat(link.attr("href")));
		}

		@Override
		public void loadPrice() {
			setPriceStr(elemento.select("div.imovel_preco span").first().text().replace("R$", "").trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.select("span.imovel_info_destaques_texto").first().text().replace("| Blumenau/SC", "")
					.trim());
			setName(getDistrict());
		}

		@Override
		public void loadRooms() {
			Elements dados = elemento.select("div.imovel_info_destaques span");
			for (Element dado : dados) {
				String valor = dado.text().toLowerCase().trim();
				if (valor.contains("quarto")) {
					setRooms(Integer.valueOf(valor.split(" ")[0].trim()));
				} else if (valor.contains("vaga")) {
					setParkingSpaces(Integer.valueOf(valor.split(" ")[0].trim()));
				} else if (valor.contains("m²")) {
					setArea(Double.valueOf(valor.split("m²")[0].trim().replace(",", ".")));
				} else if (valor.contains("suite")) {
					setSuites(Integer.valueOf(valor.split(" ")[0].trim()));
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
			setAdvertiser("LFernando");
		}

		@Override
		public void loadCondominium() {
		}

		@Override
		public void loadAddress() {
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.Apartment, "Apartamento");
			add(PropertyType.House, "Casa");
			add(PropertyType.CommercialHouse, "Casa Comercial");
			add(PropertyType.ResidentialHouse, "Casa Residencial");
			add(PropertyType.Roof, "Cobertura");
			add(PropertyType.Company, "Empresa");
			add(PropertyType.Shed, "Galpão");
			add(PropertyType.Garage, "Garagem");
			add(PropertyType.CommercialProperty, "Imóvel Comercial");
			add(PropertyType.CommercialPoint, "Ponto Comercial");
			add(PropertyType.Building, "Prédio");
			add(PropertyType.Restaurant, "Restaurante");
			add(PropertyType.CommercialRoom, "Sala Comercial");
			add(PropertyType.SmallFarmCountryHouse, "Sítio/Chácara");
			add(PropertyType.TwoStoryhouse, "Sobrado");
			add(PropertyType.Ground, "Terreno");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new LFernando(PropertyType.Apartment, ActionType.RENT);
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
