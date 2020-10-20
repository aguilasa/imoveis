package properties.realstate;

import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IImovel;
import properties.base.Imobiliaria;
import properties.base.ImobiliariaHtml;
import properties.base.ImovelHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Tropical extends ImobiliariaHtml {

    private static final String IMOVELBASE = "http://www.tropical.imb.br";
    private static final String URLBASE = "http://www.tropical.imb.br/imoveis/para-alugar/%s?page=%d";

    public Tropical(PropertyType type, ActionType action) {
        super(type, action);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.card.card-listing");
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, type, page);
    }

    @Override
    public int getPages() {
        Document document = getDocument();
        Elements pages = document.select("div.pagetion-cell p");
        if (!pages.isEmpty()) {
            String valor = pages.first().text();
            String[] separado = valor.split("de");
            return Integer.valueOf(separado[1].trim());
        }
        return 1;
    }

    @Override
    public Map<String, String> getPayload() {
        return new LinkedHashMap<>();
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
        public void carregarUrl() {
            Element link = elemento.select("a").first();
            setUrl(IMOVELBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarNome() {
            String texto1 = elemento.select("h2.card-title").first().text();
            String texto2 = elemento.select("h3.card-text").first().text();
            setName(String.format("%s - %s", texto2, texto1));
        }

        @Override
        public void carregarBairro() {
            setDistrict(elemento.select("h2.card-title").first().text());
        }

        @Override
        public void carregarPreco() {
            Elements dados = elemento.select("span.h-money.location");
            setPriceStr(dados.last().text().trim());
            try {
                setPrice(textoParaReal(getPriceStr().replace("R$", "")));
            } catch (Exception e) {
                setPrice(0);
            }
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("div.values div.value");
            for (Element dado : dados) {
                String texto = dado.text().trim();
                String valor = dado.select("span.h-money").first().text();
                if (texto.contains("dorms")) {
                    setRooms(Integer.valueOf(valor));
                } else if (texto.contains("vaga")) {
                    setParkingSpaces(Integer.valueOf(valor));
                } else if (texto.contains("su�te")) {
                    setSuites(Integer.valueOf(valor));
                } else if (texto.contains("m�")) {
                    setArea(Double.valueOf(valor.replace(".", "").replace(",", ".")));
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
            setAdvertiser("Tropical");
        }

        @Override
        public void carregarCondominio() {
            Elements dados = elemento.select("div.info-right.text-xs-right p span.h-money");
            for (Element dado : dados) {
                String valor = dado.text().toUpperCase().trim();
                if (valor.contains("CONDOM�NIO")) {
                    valor = valor.replace("CONDOM�NIO", "").replace("R$", "").replace(".", "").replace(",", ".").trim();
                    if (NumberUtils.isCreatable(valor)) {
                        setCondominium(Double.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Tropical(PropertyType.APARTMENT, ActionType.RENT);
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
