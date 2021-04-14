package vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Strategy {
	
	private List<Position> positions = new ArrayList<>();

	public Strategy(Position... positions) {
		this.positions= new ArrayList<>(Arrays.asList(positions));
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}

	
}
