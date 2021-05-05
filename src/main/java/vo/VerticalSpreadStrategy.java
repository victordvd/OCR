package vo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import proc.StrategyAnalyzer;

public class VerticalSpreadStrategy {
	public static enum Type {
		BullCall, BearPut, // premium
		BearCall, BullPut // margin
	}

	private OptionContract.OptionType cp;
	private Position sPos;

	private Position lPos;
	private Type type;

	public VerticalSpreadStrategy(Position lPos, Position sPos) {
		this.lPos = lPos;
		this.sPos = sPos;
		cp = lPos.getContract().getType();

		BigDecimal lStrike = lPos.getContract().getStrike();
		BigDecimal sStrike = sPos.getContract().getStrike();

		if (cp == OptionContract.OptionType.C) {
			if (lStrike.compareTo(sStrike) < 0) {
				type = Type.BullCall;
			} else {
				type = Type.BearCall;
			}
		} else {
			if (lStrike.compareTo(sStrike) > 0) {
				type = Type.BearPut;
			} else {
				type = Type.BullPut;
			}
		}
	}

//	public VerticalSpreadStrategy(Position.LS ls, OptionContract lContract, OptionContract sContract) {
//		this.lPos = new Position(Position.LS.L, lContract);
//		this.sPos = new Position(Position.LS.S, sContract);
//		cp = lContract.getType();
//	}

	public Profit getProfit(BigDecimal spot, BigDecimal defaultLoss) {
		Profit profit = new Profit();

		BigDecimal lStrike = lPos.getContract().getStrike();
		BigDecimal sStrike = sPos.getContract().getStrike();

		boolean isLongPrimary = (cp == OptionContract.OptionType.C) ? lStrike.compareTo(sStrike) < 0
				: lStrike.compareTo(sStrike) > 0;

		BigDecimal strikeDiff = sPos.getContract().getStrike().subtract(lPos.getContract().getStrike()).abs();

		Profit lProfit = lPos.getProfit(spot, defaultLoss);
		Profit sProfit = sPos.getProfit(spot, defaultLoss);
		BigDecimal priceDiff = lPos.getPrice().subtract(sPos.getPrice()).abs();

		switch (type) {
		case BullCall:
		case BearPut:
			profit.setUnrealizedGain(sProfit.getUnrealizedGain().add(lProfit.getUnrealizedGain()));
			profit.setMaxProfit(strikeDiff.subtract(priceDiff).subtract(defaultLoss.multiply(BigDecimal.valueOf(2))));
			profit.setMaxLoss(priceDiff.add(defaultLoss.multiply(BigDecimal.valueOf(2))));
			break;
		case BearCall:
		case BullPut:
			profit.setMargin(strikeDiff.multiply(StrategyAnalyzer.OPTION_TICK_PRICE));

//			System.out.println("S " + sProfit.getUnrealizedGain() + ", L " + lProfit.getUnrealizedGain());

			profit.setUnrealizedGain(sProfit.getUnrealizedGain().add(lProfit.getUnrealizedGain()));
			profit.setMaxProfit(priceDiff.subtract(defaultLoss.multiply(BigDecimal.valueOf(2))));
			profit.setMaxLoss(strikeDiff.subtract(priceDiff).add(defaultLoss.multiply(BigDecimal.valueOf(2))));
			break;
		}

		switch (type) {
		case BullCall:
			profit.setGainSpread(lPos.getContract().getStrike().subtract(spot).add(profit.getMaxLoss()));
			break;
		case BearPut:
			profit.setGainSpread(spot.subtract(lPos.getContract().getStrike()).add(profit.getMaxLoss()));
			break;
		case BearCall:
			profit.setGainSpread(
					spot.subtract(sPos.getContract().getStrike()).add(defaultLoss.multiply(BigDecimal.valueOf(2))));
			break;
		case BullPut:
			profit.setGainSpread(
					sPos.getContract().getStrike().subtract(spot).add(defaultLoss.multiply(BigDecimal.valueOf(2))));
			break;
		}

		return profit;
	}

	public BigDecimal getImStrikePriceSpread(BigDecimal spot) {
		BigDecimal lSpread = lPos.getContract().getStrike().subtract(spot).abs();
		BigDecimal sSpread = sPos.getContract().getStrike().subtract(spot).abs();

		if (lSpread.compareTo(sSpread) < 0) {
			return lSpread;
		} else {
			return sSpread;
		}
	}

	public Position getBearPos() {
		return sPos;
	}

	public void setBearPos(Position bearPos) {
		this.sPos = bearPos;
	}

	public Position getBullPos() {
		return lPos;
	}

	public void setBullPos(Position bullPos) {
		this.lPos = bullPos;
	}

	@Override
	public String toString() {
		BigDecimal lStrike = lPos.getContract().getStrike();
		BigDecimal sStrike = sPos.getContract().getStrike();
		boolean isLongPrimary = (cp == OptionContract.OptionType.C) ? lStrike.compareTo(sStrike) < 0
				: lStrike.compareTo(sStrike) > 0;
		if (isLongPrimary) {
			return String.format("%s [L]%s/[S]%s", cp, lPos.getContract().getStrike().intValue(),
					sPos.getContract().getStrike().intValue());
		} else {
			return String.format("%s [S]%s/[L]%s", cp, sPos.getContract().getStrike().intValue(),
					lPos.getContract().getStrike().intValue());
		}
	}

	public String toJson() {

		Map<String, Object> m = new HashMap<>();

		return String.format("[%s,%s]", new Gson().toJson(lPos), new Gson().toJson(sPos));
	}

}
