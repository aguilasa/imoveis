package imoveis.imobiliarias;

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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaJson;
import imoveis.base.ImovelJson;
import imoveis.base.Parametro;
import imoveis.utils.Utils;

public class Orbi extends ImobiliariaJson {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private static final Parametro[] PARAMETROS = { new Parametro("ativo", "1"), new Parametro("quantidade_itens", "15"), new Parametro("cidade", "8377"), new Parametro("tipo", "2"), new Parametro("order", "ordem"), new Parametro("order_type", "asc") };

    public Orbi(String tipo) {
        super(tipo);
    }

    @Override
    public int getPaginas() {
        try {
            JSONObject json = getJson(getURI().toString());
            return json.getInt("page_count");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUrl(int pagina) {
        try {
            return getURI(pagina).toString();
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
        return new Imovel(elemento);
    }

    private JSONObject getJson(String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(USER_AGENT).build()) {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Accept", "application/json");
            ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

                @Override
                public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
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

    private URI getURI(int pagina) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("www.orbi-imoveis.com.br").setPath("/api-imobiliaria/imovel");
        List<Parametro> parameters = getParametros(pagina);
        for (Parametro par : parameters) {
            builder.addParameter(par.getChave(), par.getValor());
        }
        URI uri = builder.build();
        return uri;
    }

    private List<Parametro> getParametros(int pagina) {
        List<Parametro> parametros = new LinkedList<>(Arrays.asList(PARAMETROS));
        if (pagina > 0) {
            parametros.add(new Parametro("page", String.valueOf(pagina)));
        }
        parametros.add(new Parametro("categoria", tipo.equalsIgnoreCase("apartamento") ? "2" : "1"));
        return parametros;
    }

    private class Imovel extends ImovelJson {

        public Imovel(JSONObject elemento) {
            super(elemento);
        }

        @Override
        public void carregarNome() {
            setNome(elemento.getString("titulo"));
        }

        @Override
        public void carregarUrl() {
            setUrl(elemento.getJSONObject("_links").getJSONObject("self").getString("href"));
        }

        @Override
        public void carregarPreco() {
            setPreco(textoParaReal(elemento.getString("valor")));
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.getJSONObject("bairro").getString("nome"));
        }

        @Override
        public void carregarQuartos() {
            String valor = buscarCaracteristica("quarto");
            if (!valor.isEmpty()) {
                setQuartos(Integer.valueOf(valor));
            }
        }

        @Override
        public void carregarVagas() {
            String valor = buscarCaracteristica("garagem");
            if (!valor.isEmpty()) {
                setVagas(Integer.valueOf(valor));
            }
        }

        @Override
        public void carregarSuites() {
            String valor = buscarCaracteristica("suíte");
            if (!valor.isEmpty()) {
                setSuites(Integer.valueOf(valor));
            }
        }

        @Override
        public void carregarArea() {
            String valor = buscarCaracteristica("área");
            if (!valor.isEmpty()) {
                setArea(textoParaReal(valor));
            }
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("Orbi");
        }

        @Override
        public void carregarCondominio() {
            String valor = buscarCaracteristica("descricao");
            if (!valor.isEmpty()) {

            }
        }

        @Override
        public void carregarEndereco() {
            setEndereco(elemento.getString("rua"));
        }

        private String buscarCaracteristica(String nome) {
            JSONArray caracteristicas = elemento.getJSONArray("caracteristica");
            for (int i = 0; i < caracteristicas.length(); i++) {
                JSONObject object = caracteristicas.getJSONObject(i);
                String nomeCaracteristica = object.getJSONObject("caracteristica").getString("nome");
                if (nome.equalsIgnoreCase(nomeCaracteristica)) {
                    return object.getString("valor");
                }
            }
            return "";
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Orbi("apartamento");
        List<IImovel> imos = imobiliaria.getImoveis();
        for (IImovel imo : imos) {
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
    }

}
