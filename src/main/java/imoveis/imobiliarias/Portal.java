package imoveis.imobiliarias;

import static imoveis.utils.Utils.extrairValor;
import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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
import imoveis.utils.Utils;

public class Portal extends ImobiliariaHtml {

    private static final String URLBASE = "http://vale.imoveisportal.com";

    public Portal(PropertyType type, ActionType action) {
        super(type, action);
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
    public int getPages() {
        Document document = getDocument();
        String pagecao = document.select("h4.text-muted").first().text();
        pagecao = pagecao.replaceAll("\\D+", "").trim();
        double valor = Double.valueOf(pagecao);
        return (int) Math.ceil(valor / 24.0);
    }

    @Override
    public Map<String, String> getPayload() {
        LinkedHashMap<String, String> payload = new LinkedHashMap<>();
        payload.put("opcao", "alugar");
        payload.put("cidades", "blumenau");
        payload.put("types", type.equals(PropertyType.APARTMENT) ? "apartamento" : "casa");
        if (page > 1) {
            int valor = (page - 1) * 24;
            payload.put("page", String.valueOf(valor));
        }
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
            setName(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
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
                setPriceStr(valor.text().replace("/ m�s", "").replace("R$", "").trim());
                try {
                    setPrice(textoParaReal(getPriceStr()));
                } catch (Exception e) {
                    setPrice(0);
                }
            }
        }

        @Override
        public void carregarBairro() {
            setDistrict(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("div.tags div.label");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.contains("dormit�rio")) {
                    setRooms(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("garage")) {
                    setParkingSpaces(Integer.valueOf(valor.split(" ")[0].trim()));
                } else if (valor.contains("m�")) {
                    setArea(textoParaReal(valor.split("m�")[0].trim()));
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
            setAdvertiser("Portal");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("ul.list-group li");
            for (Element dado : dados) {
                String valor = dado.text().toUpperCase().trim();
                if (valor.contains("CONDOM")) {
                    setCondominium(extrairValor(valor));
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Portal(PropertyType.APARTMENT, ActionType.RENT);
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
