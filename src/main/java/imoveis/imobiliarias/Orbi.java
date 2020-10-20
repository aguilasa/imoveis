package imoveis.imobiliarias;

import static imoveis.utils.Utils.buscarCondominio;
import static imoveis.utils.Utils.getHttpClient;
import static imoveis.utils.Utils.slug;
import static imoveis.utils.Utils.textoParaReal;

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

import imoveis.base.ActionType;
import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaJson;
import imoveis.base.ImovelJson;
import imoveis.base.Parametro;
import imoveis.base.PropertyType;
import imoveis.utils.Utils;

public class Orbi extends ImobiliariaJson {

	private static final String URL = "http://www.orbi-imoveis.com.br/imovel/detalhes/%s/%s";
	private static final Parametro[] PARAMETROS = { new Parametro("ativo", "1"),
			new Parametro("quantidade_itens", "15"), new Parametro("cidade", "8377"), new Parametro("type", "2"),
			new Parametro("order", "ordem"), new Parametro("order_type", "asc") };

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
	public IImovel newImovel(JSONObject elemento) {
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
		List<Parametro> parameters = getParametros(page);
		for (Parametro par : parameters) {
			builder.addParameter(par.getChave(), par.getValor());
		}
		URI uri = builder.build();
		return uri;
	}

	private List<Parametro> getParametros(int page) {
		List<Parametro> parametros = new LinkedList<>(Arrays.asList(PARAMETROS));
		if (page > 0) {
			parametros.add(new Parametro("page", String.valueOf(page)));
		}
		parametros.add(new Parametro("categoria", type.equals(PropertyType.APARTMENT) ? "2" : "1"));
		return parametros;
	}

	private class Imovel extends ImovelJson {

		public Imovel(JSONObject elemento, PropertyType type) {
			super(elemento, type);
		}

		@Override
		public void carregarNome() {
			setName(elemento.getString("titulo"));
		}

		@Override
		public void carregarUrl() {
			setUrl(String.format(URL, elemento.getString("referencia"), slug(elemento.getString("titulo"))));
		}

		@Override
		public void carregarPreco() {
			setPrice(textoParaReal(elemento.getString("valor")));
		}

		@Override
		public void carregarBairro() {
			setDistrict(elemento.getJSONObject("bairro").getString("nome"));
		}

		@Override
		public void carregarQuartos() {
			String valor = buscarCaracteristica("quarto");
			if (!valor.isEmpty()) {
				setRooms(Integer.valueOf(valor));
			}
		}

		@Override
		public void carregarVagas() {
			String valor = buscarCaracteristica("garagem");
			if (!valor.isEmpty()) {
				setParkingSpaces(Integer.valueOf(valor));
			}
		}

		@Override
		public void carregarSuites() {
			String valor = buscarCaracteristica("su�te");
			if (!valor.isEmpty()) {
				setSuites(Integer.valueOf(valor));
			}
		}

		@Override
		public void carregarArea() {
			String valor = buscarCaracteristica("�rea");
			if (!valor.isEmpty()) {
				setArea(textoParaReal(valor));
			}
		}

		@Override
		public void carregarAnunciante() {
			setAdvertiser("Orbi");
		}

		@Override
		public void carregarCondominio() {
			String valor = elemento.getString("descricao");
			if (!valor.isEmpty()) {
				setCondominium(buscarCondominio(valor));
			}
		}

		@Override
		public void carregarEndereco() {
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
		Imobiliaria imobiliaria = new Orbi(PropertyType.APARTMENT, ActionType.RENT);
		List<IImovel> imos = imobiliaria.getProperties();
		for (IImovel imo : imos) {
			JSONObject json = Utils.imovelToJson(imo);
			System.out.println(json.toString());
		}
	}

}
