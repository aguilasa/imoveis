package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

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

import imoveis.base.ActionType;
import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.base.PropertyType;
import imoveis.excel.Excel;
import imoveis.utils.HttpClientHelper;
import imoveis.utils.Utils;

public class Habitacao extends ImobiliariaHtml {

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
            while(iterator.hasNext()) {
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
    public Elements getElementos() {
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
        payload.put("negocio_", "2");
        payload.put("type_", type.equals(PropertyType.APARTMENT) ? "5" : "4");
        payload.put("cidade_", "1");
        payload.put("bairro_", "");
        payload.put("quartos_", "");
        payload.put("page", String.valueOf(page));
        return payload;
    }

    @Override
    public IImovel newImovel(Element elemento) {
        return new ImovelImpl(elemento, type);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento, PropertyType type) {
            super(elemento, type);
        }

        @Override
        public void carregarNome() {
            Element dados = elemento.select("div.txt-resultado-busca").first();
            setName(dados.text().trim());
        }

        @Override
        public void carregarUrl() {
            setUrl(URLBASE.concat(elemento.attr("href")));
        }

        @Override
        public void carregarPreco() {
            setPriceStr(elemento.select("div.preco-busca").first().text().replace("R$", "").trim());
            try {
                setPrice(textoParaReal(getPriceStr()));
            } catch (Exception e) {
                setPrice(0);
            }
        }

        @Override
        public void carregarBairro() {
            Elements dados = elemento.select("div.caixas-busca");
            if (dados.size() >= 4) {
                setDistrict(dados.get(2).text().trim());
                String valor = dados.get(3).text().split(":")[1].trim();
                setRooms(Integer.valueOf(valor));
            }
        }

        @Override
        public void carregarQuartos() {
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
            setAdvertiser("Habita��o");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.content-imoveis-prontos-det ul li");
            for (Element dado : dados) {
                String valor = dado.text().toLowerCase().trim();
                String[] separado = valor.split(":");
                if (separado.length == 2) {
                    if (valor.contains("condom�nio")) {
                        valor = separado[1].trim();
                        setCondominium(textoParaReal(valor));
                    } else if (valor.contains("garagem")) {
                        setParkingSpaces(Integer.valueOf(separado[1].trim()));
                    }

                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Habitacao(PropertyType.APARTMENT, ActionType.RENT);
        List<IImovel> imos = imobiliaria.getProperties();
        Excel.getInstance().clear();
        for (IImovel imo : imos) {
            Excel.getInstance().addImovel(imo);
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
        Excel.getInstance().gerar();
    }

}
