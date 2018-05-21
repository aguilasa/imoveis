/*
 * Created on 10 de mai de 2018.
 *
 * Copyright 2018 Senior Ltda. All rights reserved.
 */
package imoveis.base;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import imoveis.utils.HttpClientHelper;

public class Teste {

    public static void main(String[] args) throws Exception {
        new Teste().run();
    }

    public void run() throws Exception {
        try (HttpClientHelper helper = new HttpClientHelper()) {
            HttpGet httpget = helper.httpGet("http://www.lfernando.com.br/");
            helper.executeGet(httpget);
            httpget = helper.httpGet("http://www.lfernando.com.br/filial?id=1");
            helper.executeGet(httpget);
            
            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost("www.lfernando.com.br").setPath("/pesquisa");
            builder.addParameter("opcao", "Locação");
            builder.addParameter("cidade", "Blumenau/SC");
            builder.addParameter("tipo", "apartamento");
            builder.addParameter("init", "0");
            
            httpget = helper.httpGet(builder.build().toString());
            System.out.println(helper.executeGet(httpget));
        }
    }

}
