package imoveis.base;

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
    @Getter @Setter
    private TipoImovel tipoImovel;
    
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
    }
}
