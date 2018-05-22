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

public class Alianca extends ImobiliariaHtml {

    private static final String URLBASE = "http://www.alianca.imb.br/";
    private static final String PESQUISA = URLBASE + "pesquisa-de-imoveis";

    public Alianca(String tipo) {
        super(tipo);
        setPost(true);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div#imoveis_grid div.imovel_bloco_voosuave");
    }

    @Override
    public String getUrl() {
        return PESQUISA;
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements elements = document.select("div#paginationTopContent div.pagination a");
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
        payload.put("codTB", "1");
        payload.put("codUF", "0");
        payload.put("codCid", "11");
        payload.put("codBai", "0");
        payload.put("codTP", tipo.equals("apartamento") ? "2" : "1");
        payload.put("qtdQuartos", "0");
        payload.put("qtdSuites", "0");
        payload.put("qtdSalas", "0");
        payload.put("qtdGaragens", "0");
        payload.put("codVal", "Min");
        payload.put("codValBK", "0");
        payload.put("codVal2", "Max");
        payload.put("codValbk2", "0");
        payload.put("searchtype", "2");
        payload.put("codOrd", "1");
        payload.put("pageIndex", String.valueOf(pagina - 1));
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
            setNome(elemento.select("a.imovel-item-link").first().text().replace("BLUMENAU / SC", "").replace("BAIRRO", "").trim());
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("a.imovel-item-link").first();
            setUrl(link.attr("href"));
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("div.imovel-item-preco").first().text().replace("Locação:", "").replace("R$", "").trim());
            try {
                setPreco(textoParaReal(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("div.imovel-item-endereco").first().text().replace("BLUMENAU / SC", "").replace("BAIRRO", "").trim());
        }

        @Override
        public void carregarQuartos() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.imovel-detalhe-conteudo-texto p");
            if (dados.size() == 2) {
                Element last = dados.last();
                String[] linhas = last.text().toUpperCase().split("»");
                for (String linha : linhas) {
                    linha = linha.trim();
                    if (linha.length() > 1) {
                        String[] quebrado = linha.split(" ");
                        if (linha.contains("DORMITÓRIO")) {
                            setQuartos(Integer.valueOf(quebrado[0].trim()));
                            if (linha.contains("SUÍTE")) {
                                int i = 0;
                                for (String valor : quebrado) {
                                    if (valor.contains("SUÍTE")) {
                                        setSuites(Integer.valueOf(quebrado[i - 1].trim()));
                                    }
                                    i++;
                                }
                            }
                        }
                        if (linha.contains("GARAGE")) {
                            setVagas(Integer.valueOf(quebrado[0].trim()));
                        }
                    }
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
            setAnunciante("Conexão");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.imovel-detalhe-preco");
            for (Element dado : dados) {
                String valor = dado.text().toLowerCase().trim();
                if (valor.contains("condomínio")) {
                    valor = valor.replace("condomínio:", "").replace("r$", "").replace(".", "").replace(",", ".").trim();
                    setCondominio(Double.valueOf(valor));
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Alianca("apartamento");
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
