package imoveis.imobiliarias;

import static imoveis.utils.Utils.*;
import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class Portal extends ImobiliariaHtml {

    private static final String URLBASE = "http://vale.imoveisportal.com";

    public Portal(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.row.row-list div.col-xs-12.col-sm-6.col-md-4");
    }

    @Override
    public String getUrl() {
        return URLBASE.concat("/imoveis");
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        String paginacao = document.select("h4.text-muted").first().text();
        paginacao = paginacao.replaceAll("\\D+", "").trim();
        double valor = Double.valueOf(paginacao);
        return (int) Math.ceil(valor / 24.0);
    }

    @Override
    public Map<String, String> getPayload() {
        LinkedHashMap<String, String> payload = new LinkedHashMap<>();
        payload.put("opcao", "alugar");
        payload.put("cidades", "blumenau");
        payload.put("tipos", tipo.equals("apartamento") ? "apartamento" : "casa");
        if (pagina > 1) {
            int valor = (pagina - 1) * 24;
            payload.put("pagina", String.valueOf(valor));
        }
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
            setNome(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("a").first();
            setUrl(URLBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarPreco() {
            Element valor = elemento.select("div.panel-footer strong").first();
            if (valor != null) {
                setPrecoStr(valor.text().replace("/ mês", "").replace("R$", "").trim());
                try {
                    setPreco(textoParaReal(getPrecoStr()));
                } catch (Exception e) {
                    setPreco(0);
                }
            }
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("div.tags div.label");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.contains("dormitório")) {
                    setQuartos(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("garage")) {
                    setVagas(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("m²")) {
                    setArea(textoParaReal(valor.split("m²")[0].trim()));
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
            setAnunciante("Portal");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("ul.list-group li");
            for (Element dado : dados) {
                String valor = dado.text().toUpperCase().trim();
                if (valor.contains("CONDOM")) {
                    setCondominio(extrairValor(valor));
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Portal("apartamento");
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
