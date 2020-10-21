package properties.realstate;

import static properties.utils.Utils.extrairValor;
import static properties.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import properties.base.ActionType;
import properties.base.IProperty;
import properties.base.RealState;
import properties.base.RealStateHtml;
import properties.base.PropertyHtml;
import properties.base.PropertyType;
import properties.excel.Excel;
import properties.utils.Utils;

public class DinamicaSul extends RealStateHtml {

    private static final String URLBASE = "http://www.dinamicasul.com.br/imoveis/blumenau/quero-alugar/%s/page%d";

    public DinamicaSul(PropertyType type, ActionType action) {
        super(type, action);
    }

    @Override
    public int getPages() {
        Document document = getDocument();
        int p = 1;
        Elements pages = document.select("ul.pagetion.pagetion-sm li");
        for (Element page : pages) {
            String valor = page.text().trim();
            if (NumberUtils.isCreatable(valor)) {
                p = Integer.valueOf(valor);
            }
        }
        return p;
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, type, page);
    }

    @Override
    public Elements getElements() {
        Document document = getDocument();
        return document.select("article.imovel");
    }

    @Override
    public Map<String, String> getPayload() {
        return new LinkedHashMap<>();
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
        public void loadUrl() {
            Element link = elemento.select("a").first();
            setUrl(link.attr("href"));
            setName(link.attr("title"));
        }

        @Override
        public void loadName() {
        }

        @Override
        public void loadDistrict() {
            setDistrict(elemento.select("p.bairro").first().text().replace("Bairro", "").trim());
        }

        @Override
        public void loadPrice() {
            setPriceStr(elemento.select("p.valor").last().text().trim());
            try {
                setPrice(extrairValor(getPriceStr()));
            } catch (Exception e) {
                setPrice(0);
            }
        }

        @Override
        public void loadRooms() {
            Elements dados = getDocumento().select("div.extras.dormitorios span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setRooms(Integer.valueOf(texto));
            }
        }

        @Override
        public void loadParkingSpaces() {
            Elements dados = getDocumento().select("div.extras.garagens span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setParkingSpaces(Integer.valueOf(texto));
            }
        }

        @Override
        public void loadSuites() {
            Elements dados = getDocumento().select("div.extras.suites span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setSuites(Integer.valueOf(texto));
            }
        }

        @Override
        public void loadArea() {
            Elements dados = getDocumento().select("div.extras.areaprivativa span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().replace("m�", "").trim();
                setArea(textoParaReal(texto));
            }
        }

        @Override
        public void loadAdvertiser() {
            setAdvertiser("Din�mica Sul");
        }

        @Override
        public void loadCondominium() {
            Element dado = getDocumento().select("div#conteudoImovel").first();
            if (dado != null) {
                List<Node> nodes = dado.childNodes();
                for (Node node : nodes) {
                    if (node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        String texto = textNode.text();
                        if (texto.toUpperCase().contains("CONDOM")) {
                            setCondominium(extrairValor(texto));
                            if (getCondominium() > 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void loadAddress() {
        }

    }

    public static void main(String[] args) {
        RealState imobiliaria = new DinamicaSul(PropertyType.House, ActionType.RENT);
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
