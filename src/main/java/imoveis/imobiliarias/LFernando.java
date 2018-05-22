package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

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

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.excel.Excel;
import imoveis.utils.HttpClientHelper;
import imoveis.utils.Utils;

public class LFernando extends ImobiliariaHtml {

    private static final String BASE = "www.lfernando.com.br";
    private static final String URLBASE = "http://" + BASE + "/";

    public LFernando(String tipo) {
        super(tipo);
    }

    @Override
    public Document getDocument() {
        return getDocument(getUrl());
    }

    @Override
    public Document getDocument(String url) {
        try (HttpClientHelper helper = new HttpClientHelper()) {
            HttpGet httpget = helper.httpGet("http://www.lfernando.com.br/");
            helper.execute(httpget);
            httpget = helper.httpGet("http://www.lfernando.com.br/filial?id=1");
            helper.execute(httpget);

            httpget = helper.httpGet(url);
            String html = helper.execute(httpget);
            return Jsoup.parse(html);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Elements getElementos() {
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
        } catch (Exception e) {}
        return "";
    }

    @Override
    public int getPaginas() {
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
        payload.put("opcao", "Locação");
        payload.put("cidade", "Blumenau/SC");
        payload.put("tipo", tipo.equals("apartamento") ? "Apartamento" : "Casa Residencial");
        int init = (pagina - 1) * 15;
        payload.put("init", String.valueOf(init));
        return payload;
    }

    @Override
    public IImovel newImovel(Element elemento) {
        return new ImovelImpl(elemento, tipo);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento, String tipo) {
            super(elemento, tipo);
        }

        @Override
        public void carregarNome() {
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("div.imovel_imagem_container a").first();
            setUrl(URLBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("div.imovel_preco span").first().text().replace("R$", "").trim());
            try {
                setPreco(textoParaReal(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("span.imovel_info_destaques_texto").first().text().replace("| Blumenau/SC", "").trim());
            setNome(getBairro());
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("div.imovel_info_destaques span");
            for (Element dado : dados) {
                String valor = dado.text().toLowerCase().trim();
                if (valor.contains("quarto")) {
                    setQuartos(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("vaga")) {
                    setVagas(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("m²")) {
                    setArea(Double.valueOf(valor.split("m²")[0].trim().replace(",", ".")));
                } else if (valor.contains("suite")) {
                    setSuites(Integer.valueOf(valor.split(" ")[0].trim()));
                }
            }
        }

        @Override
        public void carregarVagas() {
        }

        @Override
        public void carregarSuites() {
        }

        @Override
        public void carregarArea() {
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("LFernando");
        }

        @Override
        public void carregarCondominio() {
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new LFernando("apartamento");
        List<IImovel> imos = imobiliaria.getImoveis();
        Excel.getInstance().clear();
        for (IImovel imo : imos) {
            Excel.getInstance().addImovel(imo);
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
        Excel.getInstance().gerar();
    }

}
