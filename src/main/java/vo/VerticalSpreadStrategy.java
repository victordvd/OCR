package vo;

import java.math.BigDecimal;

import proc.StrategyAnalyzer;

public class VerticalSpreadStrategy {

	private OptionContract.OptionType cp;
	private Position sPos;

	private Position lPos;

	public VerticalSpreadStrategy(Position lPos, Position sPos) {
		this.lPos = lPos;
		this.sPos = sPos;
		cp = lPos.getContract().getType();
	}

	public VerticalSpreadStrategy(Position.LS ls, OptionContract lContract, OptionContract sContract) {
		this.lPos = new Position(Position.LS.L, lContract);
		this.sPos = new Position(Position.LS.S, sContract);
		cp = lContract.getType();
	}

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
		if (isLongPrimary) {
		
			profit.setProfit(sProfit.getProfit().add(lProfit.getProfit()));
			profit.setMaxProfit(strikeDiff.subtract(priceDiff).subtract(defaultLoss.multiply(BigDecimal.valueOf(2))));
			profit.setMaxLoss(priceDiff.add(defaultLoss.multiply(BigDecimal.valueOf(2))));
		} else {// bear spread
			profit.setMargin(strikeDiff.multiply(StrategyAnalyzer.TICK_PRICE));
			
			profit.setProfit(sProfit.getProfit().add(lProfit.getProfit()));
			profit.setMaxProfit(priceDiff.add(defaultLoss.multiply(BigDecimal.valueOf(2))));
			profit.setMaxLoss(strikeDiff.subtract(priceDiff).subtract(defaultLoss.multiply(BigDecimal.valueOf(2))));
		}

		return profit;
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
		return String.format("[L]%s/[S]%s %s", lPos.getContract().getStrike().intValue(),
				sPos.getContract().getStrike().intValue(), cp);
	}

}
