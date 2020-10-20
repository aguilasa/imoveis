package properties.base;

import java.util.List;

public interface IImobiliaria {

	PropertyType getType();

	void setType(PropertyType type);

	ActionType getAction();

	void setAction(ActionType action);

	int getPage();

	void setPage(int page);

	int getPages();

	List<IImovel> getProperties();

	void load();

	String getUrl();
}
