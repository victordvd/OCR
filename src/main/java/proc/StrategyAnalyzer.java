package proc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vo.OptionContract;
import vo.OptionContract.OptionType;
import vo.Position;
import vo.Position.LS;
import vo.Profit;
import vo.VerticalSpreadStrategy;

public class StrategyAnalyzer {

	// Strategy Conditions
	static BigDecimal spot = BigDecimal.valueOf(17150);
	static BigDecimal g_minSpreadLimit = new BigDecimal(-100);
	static BigDecimal g_minProfitLimit = new BigDecimal(150);
	static BigDecimal g_maxLossLimit = new BigDecimal(100);
	static Integer g_maxMargin = 50000;

	static BigDecimal g_defaultPositionLoss = BigDecimal.valueOf(1);

	static BigDecimal g_spreadShortPriceLimit = BigDecimal.valueOf(8);

//	static int tickPrise = 50;
//	static LS lsLimit = LS.L;

	public static void calculateProfit(List<OptionContract> contracts) {
		// single position
		System.out.println("Single position");

		contracts.stream()
				.filter(c -> StrategyAnalyzer
						.matchProfitCondition(new Position(Position.LS.L, c).getProfit(spot, g_defaultPositionLoss)))
				.sorted((c1, c2) -> c1.strike.compareTo(c2.strike)).forEach(c -> {
					Profit p = new Position(Position.LS.L, c).getProfit(spot, g_defaultPositionLoss);
					System.out.printf("[L]%.0f%s %s%n", c.strike, c.type, p.toString());
				});

		System.out.println();
		System.out.println("Multiple position");

		// Contract for difference
		List<VerticalSpreadStrategy> vss = new ArrayList<>();

		// Call VS
		List<OptionContract> callContracts = contracts.stream().filter(c -> c.getType() == OptionType.C)
				.sorted((c1, c2) -> c1.getStrike().compareTo(c2.getStrike())).collect(Collectors.toList());

		Profit lastProfit = null;

		for (int i = 0; i < callContracts.size() - 1; i++) {
			OptionContract c1 = callContracts.get(i);
			Position pos1 = new Position(LS.L, c1);
			pos1.setPremium(c1.ask);
			Profit p1 = getProfit(pos1, spot);

//			System.out.println(pos1+" "+p1);

			for (int j = i + 1; j < callContracts.size(); j++) {
				OptionContract c2 = callContracts.get(j);

				if (c2.getAsk().compareTo(g_spreadShortPriceLimit) <= 0)
					continue;

				Position pos2 = new Position(LS.S, c2);

				Profit p2 = getProfit(pos2, spot);
//				System.out.print(pos2+" "+p2+" ");

				VerticalSpreadStrategy vs = new VerticalSpreadStrategy(new Position(LS.L, c1), new Position(LS.S, c2));
//				Profit p = mergeProfit_spread(pos1, pos2);
				Profit p = vs.getProfit(spot, g_defaultPositionLoss);

//				System.out.println(p);
				if (matchProfitCondition(p)) {
					vss.add(vs);
					System.out.println(vs + " " + p);
//					System.out.printf("L/S %dC/%dC  %s%n", pos1.getContract().getStrike().intValue(),
//							pos2.getContract().getStrike().intValue(), p);
				}
			}
		}
		System.out.println();

		// Call VS
		List<OptionContract> putContracts = contracts.stream().filter(c -> c.getType() == OptionType.P)
				.sorted((c1, c2) -> c2.getStrike().compareTo(c1.getStrike())).collect(Collectors.toList());

		for (int i = 0; i < putContracts.size() - 1; i++) {
			OptionContract c1 = putContracts.get(i);
			Position pos1 = new Position(LS.L, c1);
			pos1.setPremium(c1.ask);

			for (int j = i + 1; j < putContracts.size(); j++) {
				OptionContract c2 = putContracts.get(j);
				if (c2.getAsk().compareTo(g_spreadShortPriceLimit) <= 0)
					continue;

				VerticalSpreadStrategy vs = new VerticalSpreadStrategy(new Position(LS.L, c1), new Position(LS.S, c2));
				Profit p = vs.getProfit(spot, g_defaultPositionLoss);

				if (matchProfitCondition(p)) {
					vss.add(vs);
					System.out.println(vs + " " + p);
				}
			}
		}

		// Print VS strategies
		vss.forEach(s -> {

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

	public static Profit getProfit(Position position, BigDecimal spot) {
		LS ls = position.getLs();
		OptionContract contract = position.getContract();
		BigDecimal infi = new BigDecimal("9999");
		Profit p = new Profit();
//		Profit p = new Profit(contract,ls);
		if (OptionType.C == contract.type) {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(contract.ask.negate().subtract(g_defaultPositionLoss));
				p.setProfit(spot.subtract(contract.ask).subtract(contract.strike).subtract(g_defaultPositionLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(g_defaultPositionLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(contract.bid.subtract(spot.subtract(contract.strike)).subtract(g_defaultPositionLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			}
		} else {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(contract.ask.negate().subtract(g_defaultPositionLoss));
				p.setProfit(contract.strike.subtract(spot).subtract(contract.ask).subtract(g_defaultPositionLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(g_defaultPositionLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(contract.bid.subtract(contract.strike.subtract(spot)).subtract(g_defaultPositionLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			}
		}
		return p;
	}

	public static Profit mergeProfit_spread(Position pos1, Position pos2) {
		Profit p = new Profit();
		Profit p1 = getProfit(pos1, spot);
		Profit p2 = getProfit(pos2, spot);

		BigDecimal strikeDiff = pos2.getContract().getStrike().subtract(pos1.getContract().getStrike());
		BigDecimal premium = pos1.getContract().getAsk().subtract(pos2.getContract().getBid());

		p.setProfit(p1.getProfit().add(p2.getProfit()));
		p.setMaxProfit(strikeDiff.subtract(premium));
		p.setMaxLoss(premium);

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
