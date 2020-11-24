package properties.base;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class RealState implements IRealState {

	@Getter
	@Setter
	protected PropertyType type;
	@Getter
	@Setter
	protected ActionType action;
	@Getter
	@Setter
	protected int page;

	protected PropertyTypeValues<?> typeValues = null;

	private boolean carregou = false;
	protected List<IProperty> properties = new LinkedList<>();

	public RealState(PropertyType type, ActionType action) {
		this.type = type;
		this.action = action;
		page = 1;
	}

	public List<IProperty> getProperties() {
		if (!ignore() && !carregou) {
			load();
		}
		return properties;
	}

	public boolean ignore() {
		if (getTypeValues() != null) {
			return getTypeValues().get(type) == null;
		}
		return true;
	}

}
