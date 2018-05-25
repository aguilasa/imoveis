/*
 * Created on 10 de mai de 2018.
 *
 * Copyright 2018 Senior Ltda. All rights reserved.
 */
package imoveis.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import imoveis.utils.HttpClientHelper;

public class Teste {

    public static void main(String[] args) throws Exception {
        new Teste().run();
    }

    public void run() throws Exception {

        try (HttpClientHelper helper = new HttpClientHelper()) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("negocio_", "2"));
            params.add(new BasicNameValuePair("tipo_", "5"));
            params.add(new BasicNameValuePair("cidade_", "1"));
            params.add(new BasicNameValuePair("bairro_", ""));
            params.add(new BasicNameValuePair("quartos_", ""));
            params.add(new BasicNameValuePair("pagina", "2"));
            HttpPost httpPost = helper.httpPost("http://www.habitacaoimob.com.br/action-busca.php", params);
            System.out.println(helper.execute(httpPost));
        }
    }

}
