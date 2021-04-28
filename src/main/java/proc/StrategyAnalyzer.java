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

	public static final BigDecimal OPTION_TICK_PRICE = BigDecimal.valueOf(50);

	static BigDecimal spot = BigDecimal.valueOf(17570);
	static BigDecimal g_defaultPositionLoss = BigDecimal.valueOf(1);

	static BigDecimal g_maxMargin = BigDecimal.valueOf(50000);

	// Single - Long
	static BigDecimal l_minUnrealGainLimit = new BigDecimal(-150);
	static BigDecimal l_minProfitLimit = new BigDecimal(60);
	static BigDecimal l_maxLossLimit = new BigDecimal(80);

	// Single - Short
	static BigDecimal s_minUnrealGainLimit = new BigDecimal(-150);
	static BigDecimal s_minProfitLimit = new BigDecimal(60);
	static BigDecimal s_maxMargin = BigDecimal.valueOf(15000);

	// Vertical Spread - IM
	static BigDecimal spread_im_minUnrealGainLimit = new BigDecimal(-150);
	static BigDecimal spread_im_minProfitLimit = new BigDecimal(60);
	static BigDecimal spread_im_maxLossLimit = new BigDecimal(80);
	static BigDecimal spread_im_minShortPriceLimit = BigDecimal.valueOf(8);

	// Vertical Spread - OM
	static BigDecimal spread_om_minUnrealGainLimit = new BigDecimal(5);
	static BigDecimal spread_om_minShortStrikeSpreadLimit = new BigDecimal(50);
	static BigDecimal spread_om_minProfitLimit = new BigDecimal(20);
	static BigDecimal spread_om_maxLossLimit = new BigDecimal(100);
	static BigDecimal spread_om_minLongPriceLimit = BigDecimal.valueOf(2);
	static BigDecimal spread_om_maxMargin = BigDecimal.valueOf(15000);

	public static void calculateProfit(List<OptionContract> contracts) {
		// single position
		System.out.println("Single position");

		contracts.stream()
				.filter(c -> StrategyAnalyzer.matchSingleLongCondition(
						new Position(Position.LS.L, c).getProfit(spot, g_defaultPositionLoss)))
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

		for (int i = 0; i < callContracts.size() - 1; i++) {
			OptionContract c1 = callContracts.get(i);
			Position pos1 = new Position(LS.L, c1);
			pos1.setPremium(c1.ask);
			Profit p1 = getProfit(pos1, spot);

//			System.out.println(pos1+" "+p1);

			for (int j = i + 1; j < callContracts.size(); j++) {
				OptionContract c2 = callContracts.get(j);

				if (c2.getAsk().compareTo(spread_im_minShortPriceLimit) <= 0)
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

		// Put VS
		List<OptionContract> putContracts = contracts.stream().filter(c -> c.getType() == OptionType.P)
				.sorted((c1, c2) -> c2.getStrike().compareTo(c1.getStrike())).collect(Collectors.toList());

		for (int i = 0; i < putContracts.size() - 1; i++) {
			OptionContract c1 = putContracts.get(i);
			Position pos1 = new Position(LS.L, c1);
			pos1.setPremium(c1.ask);

			for (int j = i + 1; j < putContracts.size(); j++) {
				OptionContract c2 = putContracts.get(j);
				if (c2.getAsk().compareTo(spread_im_minShortPriceLimit) <= 0)
					continue;

				VerticalSpreadStrategy vs = new VerticalSpreadStrategy(new Position(LS.L, c1), new Position(LS.S, c2));
				Profit p = vs.getProfit(spot, g_defaultPositionLoss);

				if (matchProfitCondition(p)) {
					vss.add(vs);
					System.out.println(vs + " " + p);
				}
			}
		}

		// OM spread
		List<VerticalSpreadStrategy> vsOm = new ArrayList<>();
		System.out.println("\nOut the Money Spread");
		// Bear Call Spread
		for (int i = 0; i < callContracts.size() - 1; i++) {
			OptionContract c1 = callContracts.get(i);

			for (int j = i + 1; j < callContracts.size(); j++) {
				OptionContract c2 = callContracts.get(j);

				if (c2.getAsk().compareTo(spread_om_minLongPriceLimit) <= 0)
					continue;

				VerticalSpreadStrategy vs = new VerticalSpreadStrategy(new Position(LS.L, c2), new Position(LS.S, c1));
				Profit p = vs.getProfit(spot, g_defaultPositionLoss);

//				System.out.println(vs + " " + p);
				if (matchOmProfitCondition(vs, p)) {
					vsOm.add(vs);
					System.out.println(vs + " " + p);
//					System.out.println("bingo");
				}
			}
		}
		System.out.println();
		// Bull Put Spread
		for (int i = 0; i < putContracts.size() - 1; i++) {
			OptionContract c1 = putContracts.get(i);

			for (int j = i + 1; j < putContracts.size(); j++) {
				OptionContract c2 = putContracts.get(j);

				if (c2.getAsk().compareTo(spread_om_minLongPriceLimit) <= 0)
					continue;

				VerticalSpreadStrategy vs = new VerticalSpreadStrategy(new Position(LS.L, c2), new Position(LS.S, c1));
				Profit p = vs.getProfit(spot, g_defaultPositionLoss);

//				System.out.println(vs + " " + p);
				if (matchOmProfitCondition(vs, p)) {
					vsOm.add(vs);
					System.out.println(vs + " " + p);
//					System.out.println("bingo");
				}
			}
		}

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
				p.setUnrealizedGain(
						spot.subtract(contract.ask).subtract(contract.strike).subtract(g_defaultPositionLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(g_defaultPositionLoss));
				p.setMaxLoss(infi.negate());
				p.setUnrealizedGain(
						contract.bid.subtract(spot.subtract(contract.strike)).subtract(g_defaultPositionLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			}
		} else {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(contract.ask.negate().subtract(g_defaultPositionLoss));
				p.setUnrealizedGain(
						contract.strike.subtract(spot).subtract(contract.ask).subtract(g_defaultPositionLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(contract.bid.subtract(g_defaultPositionLoss));
				p.setMaxLoss(infi.negate());
				p.setUnrealizedGain(
						contract.bid.subtract(contract.strike.subtract(spot)).subtract(g_defaultPositionLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
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

		p.setUnrealizedGain(p1.getUnrealizedGain().add(p2.getUnrealizedGain()));
		p.setMaxProfit(strikeDiff.subtract(premium));
		p.setMaxLoss(premium);

		return p;
	}

	private static boolean matchSingleLongCondition(Profit p) {
		if ((l_minProfitLimit == null || l_minProfitLimit.compareTo(p.getMaxProfit()) < 0)
				&& (l_maxLossLimit == null || l_maxLossLimit.compareTo(p.getMaxLoss()) > 0)
				&& (l_minUnrealGainLimit == null || l_minUnrealGainLimit.compareTo(p.getUnrealizedGain()) < 0)) {
			return true;
		}
		return false;
	}

	private static boolean matchProfitCondition(Profit p) {
		if ((spread_im_minProfitLimit == null || spread_im_minProfitLimit.compareTo(p.getMaxProfit()) < 0)
				&& (spread_im_maxLossLimit == null || spread_im_maxLossLimit.compareTo(p.getMaxLoss()) > 0)
				&& (spread_im_minUnrealGainLimit == null
						|| spread_im_minUnrealGainLimit.compareTo(p.getUnrealizedGain()) < 0)) {
			return true;
		}
		return false;
	}

	private static boolean matchOmProfitCondition(VerticalSpreadStrategy vs, Profit p) {
		BigDecimal imStrikePriceSpread = vs.getImStrikePriceSpread(spot);

		if ((spread_om_minProfitLimit == null || spread_om_minProfitLimit.compareTo(p.getMaxProfit()) < 0)
				&& (spread_om_maxLossLimit == null || spread_om_maxLossLimit.compareTo(p.getMaxLoss()) > 0)
				&& (spread_om_minShortStrikeSpreadLimit == null
						|| spread_om_minShortStrikeSpreadLimit.compareTo(imStrikePriceSpread) <= 0)
				&& (spread_om_maxMargin == null || spread_om_maxMargin.compareTo(p.getMargin()) >= 0)
				&& (spread_om_minUnrealGainLimit == null
						|| spread_om_minUnrealGainLimit.compareTo(p.getUnrealizedGain()) <= 0)) {
			return true;
		}
		return false;
	}
}
