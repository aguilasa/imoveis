/*
 * Created on 10 de mai de 2018.
 *
 * Copyright 2018 Senior Ltda. All rights reserved.
 */
package imoveis.base;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Teste {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private static final Parametro[] parameters = { new Parametro("ativo", "1"), new Parametro("quantidade_itens", "15"), new Parametro("cidade", "8377"), new Parametro("tipo", "2"), new Parametro("categoria", "2"), new Parametro("order", "ordem"), new Parametro("order_type", "asc") };

    public static void main(String[] args) throws Exception {

        URI uri = getURI();
        String url = uri.toString();

        JSONObject json = getJson(url);
        System.out.println(json.toString(1));
    }

    private static JSONObject getJson(String url) throws Exception {
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

    private static URI getURI() throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("www.orbi-imoveis.com.br").setPath("/api-imobiliaria/imovel");
        for (Parametro par : parameters) {
            builder.addParameter(par.getChave(), par.getValor());
        }
        URI uri = builder.build();
        return uri;
    }
}
