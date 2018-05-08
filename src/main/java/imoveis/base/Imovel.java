package imoveis.base;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.Getter;
import lombok.Setter;

public abstract class Imovel implements IImovel {

    @Getter @Setter
    private String nome;
    @Getter @Setter
    private String url;
    @Getter @Setter
    private double preco;
    @Getter @Setter
    private String precoStr;
    @Getter @Setter
    private String bairro;
    @Getter @Setter
    private String endereco;
    @Getter @Setter
    private String anunciante;
    @Getter @Setter
    private int quartos;
    @Getter @Setter
    private int vagas;
    @Getter @Setter
    private double area;
    @Getter @Setter
    private int suites;
    @Getter @Setter
    private double condominio;
    
    protected Element elemento;

    private Document documento = null;

    public Imovel(Element elemento) {
        this.elemento = elemento;
    }

    @Override
    public void carregar() {
        carregarUrl();
        carregarNome();
        carregarBairro();
        carregarPreco();
        carregarQuartos();
        carregarVagas();
        carregarArea();
        carregarSuites();
        carregarAnunciante();
        carregarCondominio();
        carregarEndereco();
        documento = null;
    }

    protected double textoParaReal(String texto) {
        return Double.valueOf(texto.replace(".", "").replace(",", "."));
    }

    protected Document getDocumento() throws IOException {
        if (documento == null) {
            documento = Jsoup.connect(url).get();
        }
        return documento;
    }


}
