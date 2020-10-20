package properties.base;

public interface IImovel {

	void setName(String name);

	String getName();

	void setUrl(String url);

	String getUrl();

	void setPrice(double price);

	double getPrice();

	void setPriceStr(String priceStr);

	String getPriceStr();

	void setDistrict(String district);

	String getDistrict();

	void setAddress(String address);

	String getAddress();

	void setAdvertiser(String advertiser);

	String getAdvertiser();

	void setRooms(int rooms);

	int getRooms();

	void setParkingSpaces(int parkingSpaces);

	int getParkingSpaces();

	void setArea(double area);

	double getArea();

	void setSuites(int suites);

	int getSuites();

	void setCondominium(double condominium);

	double getCondominium();

	void setPropertyType(PropertyType type);

	PropertyType getPropertyType();

	void carregarUrl();

	void carregarNome();

	void carregarBairro();

	void carregarPreco();

	void carregarQuartos();

	void carregarVagas();

	void carregarSuites();

	void carregarArea();

	void carregarAnunciante();

	void carregarCondominio();

	void carregarEndereco();

	void load();

}
