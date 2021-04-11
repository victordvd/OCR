package vo;

import java.util.ArrayList;
import java.util.List;

public class Strategy {
	
	private List<TxoContract> positions = new ArrayList<>();

	public List<TxoContract> getPositions() {
		return positions;
	}

	public void setPositions(List<TxoContract> positions) {
		this.positions = positions;
	}

	
}
