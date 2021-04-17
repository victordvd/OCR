package proc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import vo.Strategy;
import vo.TxoContract;
import vo.Position;
import vo.Profit;
import vo.Position.LS;
import vo.TxoContract.OptionType;

public class StrategyAnalyzer {
	
	// Strategy Conditions
	static double spot = 17150D;
	static BigDecimal g_minSpreadLimit = new BigDecimal(-100);
	static BigDecimal g_minProfitLimit = new BigDecimal(150);
	static BigDecimal g_maxLossLimit = new BigDecimal(100);

	static Integer g_maxMargin = 50000;
	static int tickPrise = 50;

	static LS lsLimit = LS.L;

	static int defaultPositionLoss = 2;

	public static void calculateProfit(List<TxoContract> contracts) {
		// single position
//		contracts.stream().map(c -> c.getProfit(lsLimit, spot))
//				.filter(p -> p.getMaxLoss().compareTo(g_maxLossLimit) < 0)
//				.filter(p -> p.getMaxProfit().compareTo(g_minProfitLimit) > 0)
//				.filter(p -> p.getProfit().compareTo(g_minCurrentProfitLimit) > 0)
//				.sorted((p1, p2) -> p1.getContract().strike.compareTo(p2.getContract().strike)).forEach(p -> {
//					TxoContract c = p.getContract();
//					System.out.printf("%.0f%s %s %s%n", c.strike, c.type, p.getLs(), p.toString());
//				});

		// Contract for difference
		List<Strategy> strats = new ArrayList<>();

		List<TxoContract> callContracts = contracts.stream().filter(c -> c.getType() == OptionType.C)
				.sorted((c1, c2) -> c1.getStrike().compareTo(c2.getStrike())).collect(Collectors.toList());

		BigDecimal spotVd = BigDecimal.valueOf(spot);

		TxoContract longPosition;
		TxoContract shortPosition;

		Profit lastProfit = null;

		for (int i = 0; i < callContracts.size() - 1; i++) {
			TxoContract c1 = callContracts.get(i);
			Position pos1 =  new Position(LS.L,c1);
			pos1.setPremium(c1.ask);
			Profit p1 = getProfit(pos1,spot);
			
//			System.out.println(pos1+" "+p1);
			
			for (int j = i + 1; j < callContracts.size(); j++) {
				TxoContract c2 = callContracts.get(j);	
				Position pos2 = new Position(LS.S,c2);
				Profit p2 = getProfit(pos2 ,spot);
//				System.out.print(pos2+" "+p2+" ");
				
				Profit p = mergeProfit_spread(pos1, pos2);
//				System.out.println(p);
				if (matchProfitCondition(p)) {
					strats.add(new Strategy(new Position(LS.L,c1),new Position(LS.S,c2)));
//					System.out.println("bingo");
					System.out.printf("L/S %dC/%dC  %s%n",pos1.getContract().getStrike().intValue(),pos2.getContract().getStrike().intValue(),p);
				}
			}
		}
		
		// Print CFD strategies
		strats.forEach(s->{
				Profit total = new Profit();
				for(Position pos :s.getPositions()) {
					total.merge(getProfit(pos, spot));
				}
		});

		/*
		 * all for (Entry<Double, TxoContract[]> e : m.entrySet()) { Double strike =
		 * e.getKey(); TxoContract call = e.getValue()[0]; TxoContract put =
		 * e.getValue()[1];
		 * 
		 * Profit lcp = call.getProfit(TxoContract.LS.Long, spot); Profit scp =
		 * call.getProfit(TxoContract.LS.Short, spot); Profit lpp =
		 * put.getProfit(TxoContract.LS.Long, spot); Profit spp =
		 * put.getProfit(TxoContract.LS.Short, spot);
		 * 
		 * if(TxoContract.LS.Long==lsLimit) { System.out.printf("%.0fC L %s%n", strike,
		 * lcp.toString()); System.out.printf("%.0fP L %s%n", strike, lpp.toString());
		 * }else if(TxoContract.LS.Short==lsLimit) { System.out.printf("%.0fC S %s%n",
		 * strike, scp.toString()); System.out.printf("%.0fP S %s%n", strike,
		 * spp.toString()); }else { System.out.printf("%.0fC L %s%n", strike,
		 * lcp.toString()); System.out.printf("%.0fC S %s%n", strike, scp.toString());
		 * System.out.printf("%.0fP L %s%n", strike, lpp.toString());
		 * System.out.printf("%.0fP S %s%n", strike, spp.toString()); } }
		 */

	}
	
	public static Profit getProfit(Position position, double spotPrice) {
		LS ls = position.getLs();
		TxoContract contract = position.getContract();
		BigDecimal defaultLoss = new BigDecimal(defaultPositionLoss);
		BigDecimal spot = new BigDecimal(spotPrice);
		BigDecimal infi = new BigDecimal("9999");
		Profit p = new Profit();
//		Profit p = new Profit(contract,ls);
		if (OptionType.C == contract.type) {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(contract.ask.negate().subtract(defaultLoss));
				p.setProfit(spot.subtract(contract.ask).subtract(contract.strike).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(contract.bid.subtract(spot.subtract(contract.strike)).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			}
		} else {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(contract.ask.negate().subtract(defaultLoss));
				p.setProfit(contract.strike.subtract(spot).subtract(contract.ask).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(contract.bid.subtract(contract.strike.subtract(spot)).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			}
		}
		return p;
	}
	
	public static Profit mergeProfit_spread(Position pos1 ,Position pos2) {
		Profit p = new Profit();
		Profit p1 = getProfit(pos1,spot);
		Profit p2 = getProfit(pos2,spot);
		
		BigDecimal strikeDiff =  pos2.getContract().getStrike().subtract(pos1.getContract().getStrike());
		BigDecimal premium = pos1.getContract().getAsk().subtract( pos2.getContract().getBid());
		
		p.setProfit(p1.getProfit().add(p2.getProfit()));
		p.setMaxProfit( strikeDiff.subtract(premium));
		p.setMaxLoss( premium);
		
		return p;
	}
	
	private static boolean matchProfitCondition(Profit p) {
		if ((g_minProfitLimit == null || g_minProfitLimit.compareTo(p.getMaxProfit()) < 0)
				&& (g_maxLossLimit == null || g_maxLossLimit.compareTo(p.getMaxLoss()) > 0)
				&& (g_minSpreadLimit == null || g_minSpreadLimit.compareTo(p.getProfit()) < 0)) {
			return true;
		}
		return false;
	}
}
