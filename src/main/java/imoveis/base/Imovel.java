package imoveis.base;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.Getter;

public abstract class Imovel {

    @Getter
    protected String nome;
    @Getter
    protected String url;
    @Getter
    protected double preco;
    @Getter
    protected String precoStr;
    @Getter
    protected String bairro;
    @Getter
    protected String endereco;
    @Getter
    protected String anunciante;
    @Getter
    protected int quartos;
    @Getter
    protected int vagas;
    @Getter
    protected double area;
    @Getter
    protected int suites;
    protected Element elemento;
    @Getter
    protected double condominio;
    private Document documento = null;

    public Imovel(Element elemento) {
        this.elemento = elemento;
    }

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

    public abstract void carregarNome();

    public abstract void carregarUrl();

    public abstract void carregarPreco();

    public abstract void carregarBairro();

    public abstract void carregarQuartos();

    public abstract void carregarVagas();

    public abstract void carregarSuites();

    public abstract void carregarArea();

    public abstract void carregarAnunciante();

    public abstract void carregarCondominio();

    public abstract void carregarEndereco();
}
