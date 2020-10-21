package properties.base;

public enum PropertyType {
	Apartment("Apartamento"), Area("Área"), IndustrialArea("Área Industrial"), RuralArea("Área Rural"), House("Casa"),
	CommercialHouse("Casa Comercial"), CountryHouse("Chácara"), Roof("Cobertura"), Set("Conjunto"), Company("Empresa"),
	Office("Escritório"), ParkingLot("Estacionamento"), Farm("Fazenda"), Shed("Galpão"), Garage("Garagem"),
	SemiDetached("Geminada"), Hotel("Hotel"), CommercialProperty("Imóvel Comercial"), RuralProperty("Imóvel Rural"),
	Studio("Kitnet"), Store("Loja"), GroundFloorShop("Loja Térrea"), Others("Outros Imóveis"), FishPay("Pesque-Pague"),
	Point("Ponto"), Commercialpoint("Ponto Comercial"), Inn("Pousada"), Building("Prédio"),
	OfficeBuilding("Prédio Comercial"), ResidentialBuilding("Prédio Residencial"), Restaurant("Restaurante"),
	Room("Sala"), CommercialRoom("Sala Comercial"), SmallFarm("Sítio"), SmallFarmCountryHouse("Sítio/Chácara"),
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
