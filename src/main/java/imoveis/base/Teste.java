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
            params.add(new BasicNameValuePair("codTB", "1"));
            params.add(new BasicNameValuePair("codUF", "0"));
            params.add(new BasicNameValuePair("codCid", "11"));
            params.add(new BasicNameValuePair("codBai", "0"));
            params.add(new BasicNameValuePair("codTP", "2"));
            params.add(new BasicNameValuePair("qtdQuartos", "0"));
            params.add(new BasicNameValuePair("qtdSuites", "0"));
            params.add(new BasicNameValuePair("qtdSalas", "0"));
            params.add(new BasicNameValuePair("qtdGaragens", "0"));
            params.add(new BasicNameValuePair("codVal", "Min"));
            params.add(new BasicNameValuePair("codValBK", "0"));
            params.add(new BasicNameValuePair("codVal2", "Max"));
            params.add(new BasicNameValuePair("codValbk2", "0"));
            params.add(new BasicNameValuePair("searchtype", "2"));
            params.add(new BasicNameValuePair("codOrd", "1"));
            params.add(new BasicNameValuePair("pageIndex", "0"));
            HttpPost httpPost = helper.httpPost("http://www.alianca.imb.br/pesquisa-de-imoveis", params);
            System.out.println(helper.execute(httpPost));
        }
    }

}
