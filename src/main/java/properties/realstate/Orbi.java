package properties.realstate;

import static properties.utils.Utils.buscarCondominio;
import static properties.utils.Utils.getHttpClient;
import static properties.utils.Utils.slug;
import static properties.utils.Utils.textoParaReal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateJson;
import properties.base.PropertyJson;
import properties.base.Parameter;
import properties.base.PropertyType;
import properties.utils.Utils;

public class Orbi extends RealStateJson {

	private static final String URL = "http://www.orbi-imoveis.com.br/imovel/detalhes/%s/%s";
	private static final Parameter[] PARAMETROS = { new Parameter("ativo", "1"),
			new Parameter("quantidade_itens", "15"), new Parameter("cidade", "8377"), new Parameter("type", "2"),
			new Parameter("order", "ordem"), new Parameter("order_type", "asc") };

	public Orbi(PropertyType type, ActionType action) {
		super(type, action);
	}

	@Override
	public int getPages() {
		try {
			JSONObject json = getJson(getURI().toString());
			return json.getInt("page_count");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getUrl() {
		try {
			return getURI(page).toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public JSONArray getElementos(String url) {
		try {
			JSONObject json = getJson(url);
			return json.getJSONObject("_embedded").getJSONArray("imovel");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IProperty newImovel(JSONObject elemento) {
		return new Imovel(elemento, type);
	}

	private JSONObject getJson(String url) throws Exception {
		try (CloseableHttpClient httpclient = getHttpClient()) {
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Accept", "application/json");
			ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

				@Override
				public JSONObject handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String responseValue = entity != null ? EntityUtils.toString(entity) : "{}";
						return new JSONObject(responseValue);
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}

			};
			return httpclient.execute(httpget, responseHandler);
		}
	}

	private URI getURI() throws URISyntaxException {
		return getURI(0);
	}

	private URI getURI(int page) throws URISyntaxException {
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("www.orbi-imoveis.com.br").setPath("/api-imobiliaria/imovel");
		List<Parameter> parameters = getParametros(page);
		for (Parameter par : parameters) {
			builder.addParameter(par.getChave(), par.getValor());
		}
		URI uri = builder.build();
		return uri;
	}

	private List<Parameter> getParametros(int page) {
		List<Parameter> parametros = new LinkedList<>(Arrays.asList(PARAMETROS));
		if (page > 0) {
			parametros.add(new Parameter("page", String.valueOf(page)));
		}
		parametros.add(new Parameter("categoria", type.equals(PropertyType.Apartment) ? "2" : "1"));
		return parametros;
	}

	private class Imovel extends PropertyJson {

		public Imovel(JSONObject elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void loadName() {
			setName(elemento.getString("titulo"));
		}

		@Override
		public void loadUrl() {
			setUrl(String.format(URL, elemento.getString("referencia"), slug(elemento.getString("titulo"))));
		}

		@Override
		public void loadPrice() {
			setPrice(textoParaReal(elemento.getString("valor")));
		}

		@Override
		public void loadDistrict() {
			setDistrict(elemento.getJSONObject("bairro").getString("nome"));
		}

		@Override
		public void loadRooms() {
			String valor = buscarCaracteristica("quarto");
			if (!valor.isEmpty()) {
				setRooms(Integer.valueOf(valor));
			}
		}

		@Override
		public void loadParkingSpaces() {
			String valor = buscarCaracteristica("garagem");
			if (!valor.isEmpty()) {
				setParkingSpaces(Integer.valueOf(valor));
			}
		}

		@Override
		public void loadSuites() {
			String valor = buscarCaracteristica("su�te");
			if (!valor.isEmpty()) {
				setSuites(Integer.valueOf(valor));
			}
		}

		@Override
		public void loadArea() {
			String valor = buscarCaracteristica("�rea");
			if (!valor.isEmpty()) {
				setArea(textoParaReal(valor));
			}
		}

		@Override
		public void loadAdvertiser() {
			setAdvertiser("Orbi");
		}

		@Override
		public void loadCondominium() {
			String valor = elemento.getString("descricao");
			if (!valor.isEmpty()) {
				setCondominium(buscarCondominio(valor));
			}
		}

		@Override
		public void loadAddress() {
			setAddress(elemento.getString("rua"));
		}

		private String buscarCaracteristica(String name) {
			JSONArray caracteristicas = elemento.getJSONArray("caracteristica");
			for (int i = 0; i < caracteristicas.length(); i++) {
				JSONObject object = caracteristicas.getJSONObject(i);
				String nameCaracteristica = object.getJSONObject("caracteristica").getString("nome");
				if (name.equalsIgnoreCase(nameCaracteristica)) {
					return object.getString("valor");
				}
			}
			return "";
		}

	}

	public static void main(String[] args) {
		RealState imobiliaria = new Orbi(PropertyType.Apartment, ActionType.RENT);
		List<IProperty> imos = imobiliaria.getProperties();
		for (IProperty imo : imos) {
			JSONObject json = Utils.imovelToJson(imo);
			System.out.println(json.toString());
		}
	}

}
