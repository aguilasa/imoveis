package properties.base;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class Imobiliaria implements IImobiliaria {

	@Getter
	@Setter
	protected PropertyType type;
	@Getter
	@Setter
	protected ActionType action;
	@Getter
	@Setter
	protected int page;
	private boolean carregou = false;
	protected List<IImovel> imoveis = new LinkedList<>();

	public Imobiliaria(PropertyType type, ActionType action) {
		this.type = type;
		this.action = action;
		page = 1;
	}

	public List<IImovel> getProperties() {
		if (!carregou) {
			load();
		}
		return imoveis;
	}

}
