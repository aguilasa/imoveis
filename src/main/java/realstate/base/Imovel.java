package realstate.base;

import lombok.Getter;
import lombok.Setter;

public abstract class Imovel implements IImovel {

	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String url;
	@Getter
	@Setter
	private double price;
	@Getter
	@Setter
	private String priceStr;
	@Getter
	@Setter
	private String district;
	@Getter
	@Setter
	private String address;
	@Getter
	@Setter
	private String advertiser;
	@Getter
	@Setter
	private int rooms;
	@Getter
	@Setter
	private int parkingSpaces;
	@Getter
	@Setter
	private double area;
	@Getter
	@Setter
	private int suites;
	@Getter
	@Setter
	private double condominium;
	@Getter
	@Setter
	private PropertyType propertyType;

	@Override
	public void load() {
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
