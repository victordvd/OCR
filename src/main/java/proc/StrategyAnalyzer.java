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
	static double spot = 16926D;
	static BigDecimal g_minCurrentProfitLimit = new BigDecimal(-200);
	static BigDecimal g_minProfitLimit = new BigDecimal(20);
	static BigDecimal g_maxLossLimit = new BigDecimal(200);

	static Integer g_maxMargin = 50000;
	static int tickPrise = 50;

	static LS lsLimit = LS.Long;

	static int defaultPositionLoss = 2;

	public static void calculateProfit(List<TxoContract> contracts) {
		// single position
		contracts.stream().map(c -> c.getProfit(lsLimit, spot))
				.filter(p -> p.getMaxLoss().compareTo(g_maxLossLimit) < 0)
				.filter(p -> p.getMaxProfit().compareTo(g_minProfitLimit) > 0)
				.filter(p -> p.getProfit().compareTo(g_minCurrentProfitLimit) > 0)
				.sorted((p1, p2) -> p1.getContract().strike.compareTo(p2.getContract().strike)).forEach(p -> {
					TxoContract c = p.getContract();
					System.out.printf("%.0f%s %s %s%n", c.strike, c.type, p.getLs(), p.toString());
				});

		// Contract for difference
		List<Strategy> strats = new ArrayList<>();

		List<TxoContract> callContracts = contracts.stream().filter(c -> c.getType() == OptionType.Call)
				.sorted((c1, c2) -> c1.getStrike().compareTo(c2.getStrike())).collect(Collectors.toList());

		BigDecimal spotVd = BigDecimal.valueOf(spot);

		TxoContract longPosition;
		TxoContract shortPosition;

		BigDecimal finalPremium;
		BigDecimal finalMaxLoss;
		BigDecimal finalMaxProfit;
		BigDecimal finalProfit;

		for (int i = 0; i < callContracts.size() - 1; i++) {
			TxoContract c1 = callContracts.get(i);

			for (int j = i + 1; j < callContracts.size(); j++) {
				TxoContract c2 = callContracts.get(j);

				BigDecimal premium = c1.getAsk().subtract(c2.getBid());
				BigDecimal maxLoss = premium.subtract(new BigDecimal(defaultPositionLoss * 2));
				BigDecimal profit = spotVd.subtract(c1.strike).subtract(premium).max(maxLoss);
				BigDecimal maxProfit = c2.strike.subtract(c1.strike).subtract(premium);

//				static BigDecimal g_maxDiff = new BigDecimal(-50);
//				static BigDecimal g_minProfit = new BigDecimal(20); 
//				static BigDecimal g_maxLoss = new BigDecimal(200); 
//				static Integer g_maxMargin = 50000;

				if ((g_minProfitLimit == null || g_minProfitLimit.compareTo(maxProfit) < 0)
						&& (g_maxLossLimit == null || g_maxLossLimit.compareTo(maxLoss) < 0)
						&& (g_minCurrentProfitLimit == null || g_minCurrentProfitLimit.compareTo(profit) < 0)) {
					strats.add(new Strategy(new Position(LS.Long,c1),new Position(LS.Long,c2)));
				}
			}
		}
		
		// Print CFD strategies
		strats.forEach(s->System.out.println(s.getPositions().get(0)));

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
	
	public Profit getProfit(Position position, double spotPrice) {
		LS ls = position.getLs();
		TxoContract contract = position.getContract();
		BigDecimal defaultLoss = new BigDecimal(defaultPositionLoss);
		BigDecimal spot = new BigDecimal(spotPrice);
		BigDecimal infi = new BigDecimal("9999");
		Profit p = new Profit(contract,ls);
		if (OptionType.Call == contract.type) {
			if (LS.Long == ls) {
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
			if (LS.Long == ls) {
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
}
