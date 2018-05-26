package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.excel.Excel;
import imoveis.utils.Utils;

public class Tropical extends ImobiliariaHtml {

    private static final String IMOVELBASE = "http://www.tropical.imb.br";
    private static final String URLBASE = "http://www.tropical.imb.br/imoveis/para-alugar/%s?pagina=%d";

    public Tropical(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.card.card-listing");
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements paginas = document.select("div.pagination-cell p");
        if (!paginas.isEmpty()) {
            String valor = paginas.first().text();
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
        return new ImovelImpl(elemento, tipo);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento, String tipo) {
            super(elemento, tipo);
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
            setNome(String.format("%s - %s", texto2, texto1));
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("h2.card-title").first().text());
        }

        @Override
        public void carregarPreco() {
            Elements dados = elemento.select("span.h-money.location");
            setPrecoStr(dados.last().text().trim());
            try {
                setPreco(textoParaReal(getPrecoStr().replace("R$", "")));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("div.values div.value");
            for (Element dado : dados) {
                String texto = dado.text().trim();
                String valor = dado.select("span.h-money").first().text();
                if (texto.contains("dorms")) {
                    setQuartos(Integer.valueOf(valor));
                } else if (texto.contains("vaga")) {
                    setVagas(Integer.valueOf(valor));
                } else if (texto.contains("suíte")) {
                    setSuites(Integer.valueOf(valor));
                } else if (texto.contains("m²")) {
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
            setAnunciante("Tropical");
        }

        @Override
        public void carregarCondominio() {
            Elements dados = elemento.select("div.info-right.text-xs-right p span.h-money");
            for (Element dado : dados) {
                String valor = dado.text().toUpperCase().trim();
                if (valor.contains("CONDOMÍNIO")) {
                    valor = valor.replace("CONDOMÍNIO", "").replace("R$", "").replace(".", "").replace(",", ".").trim();
                    if (NumberUtils.isCreatable(valor)) {
                        setCondominio(Double.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Tropical("apartamento");
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
