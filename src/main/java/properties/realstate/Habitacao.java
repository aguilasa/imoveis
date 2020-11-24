package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
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

public class Habitacao extends RealStateHtml {

	private static final String URLBASE = "http://www.habitacaoimob.com.br/";
	private static final String PESQUISA = URLBASE + "action-busca.php";

	public Habitacao(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public Document getDocument() {
		return getDocument(getUrl());
	}

	@Override
	public Document getDocument(String url) {
		try (HttpClientHelper helper = new HttpClientHelper()) {

			Map<String, String> payload = getPayload();
			Iterator<String> iterator = payload.keySet().iterator();
			List<NameValuePair> params = new ArrayList<>();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = payload.get(key);
				params.add(new BasicNameValuePair(key, value));
			}

			HttpPost httpPost = helper.httpPost(url, params);
			String html = helper.execute(httpPost);
			return Jsoup.parse(html);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Elements getElements() {
		Document document = getDocument(getUrl());
		return document.select("div.content-resultado-busca ul a");
	}

	@Override
	public String getUrl() {
		return PESQUISA;
	}

	@Override
	public int getPages() {
		Document document = getDocument();
		Elements elements = document.select("div#pagecao a");
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
		payload.put("negocio_", action.equals(ActionType.RENT) ? "2" : "1");
		payload.put("tipo_", (String) getTypeValues().get(type));
		payload.put("cidade_", "1");
		payload.put("bairro_", "");
		payload.put("quartos_", "");
		payload.put("quartos_", String.valueOf(page));
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
			Element dados = elemento.select("div.txt-resultado-busca").first();
			setName(dados.text().trim());
		}

		@Override
		public void loadUrl() {
			setUrl(URLBASE.concat(elemento.attr("href")));
		}

		@Override
		public void loadPrice() {
			setPriceStr(elemento.select("div.preco-busca").first().text().replace("R$", "").trim());
			try {
				setPrice(textoParaReal(getPriceStr()));
			} catch (Exception e) {
				setPrice(0);
			}
		}

		@Override
		public void loadDistrict() {
			Elements dados = elemento.select("div.caixas-busca");
			if (dados.size() >= 4) {
				setDistrict(dados.get(2).text().trim());
				String valor = dados.get(3).text().split(":")[1].trim();
				setRooms(Integer.valueOf(valor));
			}
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
			setAdvertiser("Habitação");
		}

		@Override
		public void loadCondominium() {
			Document documento = getDocumento();
			Elements dados = documento.select("div.content-imoveis-prontos-det ul li");
			for (Element dado : dados) {
				String valor = dado.text().toLowerCase().trim();
				String[] separado = valor.split(":");
				if (separado.length == 2) {
					if (valor.contains("condomínio")) {
						valor = separado[1].trim();
						setCondominium(textoParaReal(valor));
					} else if (valor.contains("garagem")) {
						setParkingSpaces(Integer.valueOf(separado[1].trim()));
					}

				}
			}
		}

		@Override
		public void loadAddress() {
		}

	}

	private class TypeValues extends PropertyTypeValues<String> {

		public TypeValues() {
			add(PropertyType.Apartment, "5");
			add(PropertyType.House, "4");
			add(PropertyType.Ground, "6");
			add(PropertyType.Room, "7");
			add(PropertyType.Shed, "19");
			add(PropertyType.CommercialProperty, "20");
			add(PropertyType.SmallFarmCountryHouse, "21");
			add(PropertyType.FishPay, "22");
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Habitacao(PropertyType.Apartment, ActionType.RENT);
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
