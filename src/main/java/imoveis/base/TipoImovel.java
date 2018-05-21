package imoveis.base;

public enum TipoImovel {
        apartamento("Apartamento"), casa("Casa");

    private String tipoImovel;

    private TipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getBucketName() {
        return this.tipoImovel;
    }

    public static TipoImovel getEnum(String tipo) {
        if (tipo.equals("") || tipo.equalsIgnoreCase("apartamento")) {
            return apartamento;
        }
        return casa;
    }

    @Override
    public String toString() {
        return tipoImovel;
    }
}
