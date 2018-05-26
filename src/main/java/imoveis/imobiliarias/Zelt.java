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

public class Zelt extends ImobiliariaHtml {

    private static final String IMOVELBASE = "http://www.zelt.com.br";
    private static final String URLBASE = "http://www.zelt.com.br/public/search";

    public Zelt(String tipo) {
        super(tipo);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements dados = document.select("nav.swt-pagination");
        if (!dados.isEmpty()) {
            int p = 0;
            Elements paginas = dados.first().select("li.swt-pagination__item a");
            for (Element pagina : paginas) {
                String valor = pagina.text().trim();
                if (NumberUtils.isCreatable(valor)) {
                    p = Integer.valueOf(valor);
                }
            }
            return p;
        }
        return 1;
    }

    @Override
    public String getUrl() {
        return URLBASE;
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument();
        return document.select("div.swt-realty-preview.swt-realty-preview--search-list");
    }

    @Override
    public Map<String, String> getPayload() {
        LinkedHashMap<String, String> payload = new LinkedHashMap<>();
        payload.put("tipo", tipo.equals("apartamento") ? "1" : "2");
        payload.put("cidade", "8377");
        payload.put("goalId", "1");
        payload.put("viewMap", "");
        payload.put("max", "12");
        payload.put("bairro", "");
        payload.put("preco_ate", "0");
        payload.put("uf", "SC");
        payload.put("ref", "");
        payload.put("area_de", "0");
        payload.put("area_ate", "0");
        payload.put("sortOrder", "desc");
        payload.put("tag", "");
        payload.put("preco_de", "0");
        payload.put("fullSearch", "true");
        payload.put("openSearch", "");
        payload.put("offset", String.valueOf((pagina - 1) * 12));
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
        public void carregarUrl() {
            Element link = elemento.select("a").first();
            setUrl(IMOVELBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarNome() {
            String texto1 = elemento.select("p.swt-realty-preview__sub-heading.swt-color-text--1.swt-size-text--5").first().text().trim();
            String texto2 = elemento.select("h3.swt-realty-preview__heading.swt-size-text--3").first().text().trim();
            setNome(String.format("%s - %s", texto1, texto2));
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("h3.swt-realty-preview__heading.swt-size-text--3").first().text().replace("(Blumenau - SC)", "").trim());
        }

        @Override
        public void carregarPreco() {
            Elements dados = elemento.select("dd.swt-price__value");
            setPrecoStr(dados.last().text().trim());
            try {
                setPreco(textoParaReal(getPrecoStr().replace("R$", "")));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("li.swt-realty-features__item.swt-realty-preview__feature");
            for (Element dado : dados) {
                String texto = dado.text().trim();
                String[] quebrado = texto.split(" ");
                if (texto.contains("dormi")) {
                    setQuartos(Integer.valueOf(quebrado[0].trim()));
                    if (texto.contains("suíte")) {
                        for (int i = 1; i < quebrado.length; i++) {
                            if (NumberUtils.isCreatable(quebrado[i].trim())) {
                                setSuites(Integer.valueOf(quebrado[i].trim()));
                            }
                        }
                    }
                } else if (texto.contains("vaga")) {
                    setVagas(Integer.valueOf(quebrado[0].trim()));
                } else if (texto.contains("útil")) {
                    setArea(Double.valueOf(quebrado[0].trim().replace(".", "").replace(",", ".")));
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
            setAnunciante("Zelt");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("li.swt-realty-features__item.swt-realty-details__feature");
            for (Element dado : dados) {
                String valor = dado.text().toUpperCase().trim();
                if (valor.contains("CONDOM")) {
                    String[] quebrado = valor.split("R\\$");
                    valor = quebrado[1].trim();
                    setCondominio(textoParaReal(valor));
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Zelt("casa");
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
