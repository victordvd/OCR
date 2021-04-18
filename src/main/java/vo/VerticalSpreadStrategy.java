package vo;

import java.math.BigDecimal;

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

		if (isLongPrimary) {
			Profit lProfit = lPos.getProfit(spot, defaultLoss);
			Profit sProfit = sPos.getProfit(spot, defaultLoss);

			BigDecimal strikeDiff = sPos.getContract().getStrike().subtract(lPos.getContract().getStrike()).abs();

			BigDecimal premium = lPos.getPrice().subtract(sPos.getPrice());

			profit.setProfit(sProfit.getProfit().add(lProfit.getProfit()));
			profit.setMaxProfit(strikeDiff.subtract(premium).subtract(defaultLoss.multiply(BigDecimal.valueOf(2))));
			profit.setMaxLoss(premium.add(defaultLoss.multiply(BigDecimal.valueOf(2))));
		} else {
			// bear spread to-do

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
		return String.format("L %s%s/S %s%s", lPos.getContract().getStrike().intValue(), sPos.getContract().getType(),
				sPos.getContract().getStrike().intValue(), sPos.getContract().getType());
	}

}
