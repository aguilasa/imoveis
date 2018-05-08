package imoveis.base;

public interface IImovel {

    void setNome(String nome);

    String getNome();

    void setUrl(String url);

    String getUrl();

    void setPreco(double preco);

    double getPreco();

    void setPrecoStr(String precoStr);

    String getPrecoStr();

    void setBairro(String bairro);

    String getBairro();

    void setEndereco(String endereco);

    String getEndereco();

    void setAnunciante(String anunciante);

    String getAnunciante();

    void setQuartos(int quartos);

    int getQuartos();

    void setVagas(int vagas);

    int getVagas();

    void setArea(double area);

    double getArea();

    void setSuites(int suites);

    int getSuites();

    void setCondominio(double condominio);

    double getCondominio();

    void carregarNome();

    void carregarUrl();

    void carregarPreco();

    void carregarBairro();

    void carregarQuartos();

    void carregarVagas();

    void carregarSuites();

    void carregarArea();

    void carregarAnunciante();

    void carregarCondominio();

    void carregarEndereco();

    void carregar();

}
