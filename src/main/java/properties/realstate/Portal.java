package properties.realstate;

import static properties.utils.Utils.extrairValor;
import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class Portal extends RealStateHtml {

    private static final String URLBASE = "http://vale.imoveisportal.com";

    public Portal(PropertyType type, ActionType action) {
        super(type, action);
    }

    @Override
    public Elements getElements() {
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
    public IProperty newProperty(Element elemento) {
        return new ImovelImpl(elemento, type);
    }

    private class ImovelImpl extends PropertyHtml {

        public ImovelImpl(Element elemento, PropertyType type) {
            super(elemento, type);
        }

        @Override
        public void loadName() {
            setName(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
        }

        @Override
        public void loadUrl() {
            Element link = elemento.select("a").first();
            setUrl(URLBASE.concat(link.attr("href")));
        }

        @Override
        public void loadPrice() {
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
        public void loadDistrict() {
            setDistrict(elemento.select("div.truncate").first().text().replace(", Blumenau", "").trim());
        }

        @Override
        public void loadRooms() {
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
        public void loadParkingSpaces() {
        }

        @Override
        public void loadSuites() {
        }

        @Override
        public void loadArea() {
        }

        @Override
        public void loadAdvertiser() {
            setAdvertiser("Portal");
        }

        @Override
        public void loadCondominium() {
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
        public void loadAddress() {
        }

    }

    public static void main(String[] args) {
        RealState imobiliaria = new Portal(PropertyType.APARTMENT, ActionType.RENT);
        List<IProperty> imos = imobiliaria.getProperties();
        Excel.getInstance().clear();
        for (IProperty imo : imos) {
            Excel.getInstance().addImovel(imo);
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
        Excel.getInstance().gerar();
    }

}
