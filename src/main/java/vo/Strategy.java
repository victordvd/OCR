package vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import vo.Position.LS;
import vo.TxoContract.OptionType;

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

	public Profit getProfit(double spotPrice){
		Profit profit = new Profit();
		
		Map<OptionType, List<Position>> m = positions.stream().collect(Collectors.groupingBy(p->p.getContract().getType()));
		
		
		for (Entry<OptionType, List<Position>> e: m.entrySet()) {
			OptionType type = e.getKey();
			List<Position> poss = e.getValue();
			
		
			List<Position> lPoss = poss.stream().filter(p->LS.L == p.getLs()).collect(Collectors.toCollection(LinkedList::new));
			List<Position> sPoss = poss.stream().filter(p->LS.S == p.getLs()).collect(Collectors.toCollection(LinkedList::new));
			
			if(OptionType.C == type) {
				
				for(Position lPos:lPoss) {
					
					
				}
				
				
			}else {
				
			}
		}
		
	
		
		
		
		return profit;
	}
	
}
