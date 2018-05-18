package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import imoveis.utils.Utils;

public class ImoveisSc extends ImobiliariaHtml {

    private static final String URLBASE = "https://www.imoveis-sc.com.br/blumenau/alugar/%s?page=%d";

    public ImoveisSc(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("article.imovel");
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        String paginacao = document.select("div.navigation").first().text();
        paginacao = paginacao.replaceAll("[^-?0-9]+", " ");
        LinkedList<String> lista = new LinkedList<>(Arrays.asList(paginacao.trim().split(" ")));
        return Integer.valueOf(lista.getLast());
    }

    @Override
    public Map<String, String> getPayload() {
        return new LinkedHashMap<>();
    }

    @Override
    public IImovel newImovel(Element elemento) {
        return new ImovelImpl(elemento);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento) {
            super(elemento);
        }

        @Override
        public void carregarNome() {
            Element link = elemento.select("h2.imovel-titulo a").first();
            setNome(link.text().trim());
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("h2.imovel-titulo a").first();
            setUrl(link.attr("href"));
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("span.imovel-preco small").first().text().trim());
            try {
                setPreco(textoParaReal(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("div.imovel-extra strong").first().text().replace("Blumenau, ", "").trim());
        }

        @Override
        public void carregarQuartos() {
            Elements dados = elemento.select("ul.imovel-info li");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.toUpperCase().contains("QUARTO")) {
                    setQuartos(Integer.valueOf(valor.split(" ")[0].trim()));
                    break;
                }
            }
        }

        @Override
        public void carregarVagas() {
            Elements dados = elemento.select("ul.imovel-info li");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.toUpperCase().contains("VAGA")) {
                    setVagas(Integer.valueOf(valor.split(" ")[0].trim()));
                    break;
                }
            }
        }

        @Override
        public void carregarSuites() {
            Elements dados = elemento.select("ul.imovel-info li");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.toUpperCase().contains("SUITE")) {
                    setSuites(Integer.valueOf(valor.split(" ")[0].trim()));
                    break;
                }
            }
        }

        @Override
        public void carregarArea() {
            Elements dados = elemento.select("ul.imovel-info li");
            for (Element dado : dados) {
                String valor = dado.text().trim();
                if (valor.toUpperCase().contains("M²")) {
                    setArea(textoParaReal(valor.split(" ")[0].trim()));
                    break;
                }
            }
        }

        @Override
        public void carregarAnunciante() {
            String anunciante = elemento.select("a.imovel-anunciante").first().attr("title").trim();
            String[] quebra = anunciante.split(" - ");
            if (quebra.length == 2) {
                anunciante = quebra[0].trim();
            }
            setAnunciante(anunciante);
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("li.visualizacao-caracteristica-item");
            if (!dados.isEmpty()) {
                Element dado = dados.first();
                dados = dado.select("ul li");
                for (Element li : dados) {
                    String valor = li.text().toUpperCase().trim();
                    if (valor.contains("CONDOMÍNIO")) {
                        valor = valor.replace("+", "").replace("R$", "").replace("CONDOMÍNIO", "").replace(".", "").replace(",", ".").trim();
                        String[] quebrado = valor.split("\\.");
                        if (quebrado.length == 2) {
                            setCondominio(Double.valueOf(valor));
                        } else {
                            valor = "";
                            for (int i = 0; i < quebrado.length - 1; i++) {
                                valor = valor.concat(quebrado[i]);
                            }
                            valor = valor.concat(".").concat(quebrado[quebrado.length - 1]);
                            setCondominio(Double.valueOf(valor));
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void carregarEndereco() {
            Document documento = getDocumento();
            Elements selecao = documento.select("address.visualizar-endereco-texto");
            if (!selecao.isEmpty()) {
                setEndereco(selecao.first().text().trim());
            }
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new ImoveisSc("apartamento");
        List<IImovel> imos = imobiliaria.getImoveis();
        for (IImovel imo : imos) {
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
    }

}
