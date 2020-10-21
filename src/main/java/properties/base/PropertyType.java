package properties.base;

public enum PropertyType {
	Apartment("Apartamento"), Area("�rea"), IndustrialArea("�rea Industrial"), RuralArea("�rea Rural"), House("Casa"),
	CommercialHouse("Casa Comercial"), CountryHouse("Ch�cara"), Roof("Cobertura"), Set("Conjunto"), Company("Empresa"),
	Office("Escrit�rio"), ParkingLot("Estacionamento"), Farm("Fazenda"), Shed("Galp�o"), Garage("Garagem"),
	SemiDetached("Geminada"), Hotel("Hotel"), CommercialProperty("Im�vel Comercial"), RuralProperty("Im�vel Rural"),
	Studio("Kitnet"), Store("Loja"), GroundFloorShop("Loja T�rrea"), Others("Outros Im�veis"), FishPay("Pesque-Pague"),
	Point("Ponto"), Commercialpoint("Ponto Comercial"), Inn("Pousada"), Building("Pr�dio"),
	OfficeBuilding("Pr�dio Comercial"), ResidentialBuilding("Pr�dio Residencial"), Restaurant("Restaurante"),
	Room("Sala"), CommercialRoom("Sala Comercial"), SmallFarm("S�tio"), SmallFarmCountryHouse("S�tio/Ch�cara"),
	TwoStoryhouse("Sobrado"), Ground("Terreno"), ParkingSpace("Vaga");

	private String description;

	private PropertyType(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

}
