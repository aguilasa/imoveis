package imoveis.imobiliarias;

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

public class Conexao extends ImobiliariaHtml {

    private static final String URLBASE = "http://www.imobiliariaconexao.com.br/imoveis/";

    public Conexao(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.imovel-item");
    }

    @Override
    public String getUrl() {
        return URLBASE;
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements dados = document.select("a.paginacao-lista-num");
        if (!dados.isEmpty()) {
            String valor = dados.last().text().trim();
            return Integer.valueOf(valor);
        }
        return 1;
    }

    @Override
    public Map<String, String> getPayload() {
        LinkedHashMap<String, String> payload = new LinkedHashMap<>();
        payload.put("operacao", "2");
        payload.put("cidade", "4202404");
        payload.put("tipo", tipo.equals("apartamento") ? "2" : "1");
        payload.put("qtd", "20");
        payload.put("page", String.valueOf(pagina));
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
        Imobiliaria imobiliaria = new Conexao("apartamento");
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
