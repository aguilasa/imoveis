package properties.base;

import lombok.Getter;
import lombok.Setter;

public abstract class Property implements IProperty {

	@Getter
	@Setter
	private String name;
	@Getter
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
		loadUrl();
		loadName();
		loadDistrict();
		loadPrice();
		loadRooms();
		loadParkingSpaces();
		loadArea();
		loadSuites();
		loadAdvertiser();
		loadCondominium();
		loadAddress();
	}
	
	public void setUrl(String url) {
		this.url = url.replaceAll("\\r*\\n", "");
	}
}
