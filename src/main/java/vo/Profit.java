package vo;

import java.math.BigDecimal;

public class Profit {

	private BigDecimal unrealizedGain = BigDecimal.ZERO;
	private BigDecimal maxProfit = BigDecimal.ZERO;
	private BigDecimal maxLoss = BigDecimal.ZERO;
	private BigDecimal margin;
	private BigDecimal basis = BigDecimal.ZERO;

	public Profit() {
	}

	public Profit merge(Profit o) {

		this.unrealizedGain.add(o.unrealizedGain);
		this.maxProfit.add(o.maxProfit);
		this.maxLoss.add(o.maxLoss);

		return this;
	}

	public BigDecimal getUnrealizedGain() {
		return unrealizedGain;
	}

	public void setUnrealizedGain(BigDecimal profit) {
		this.unrealizedGain = profit;
	}

	public BigDecimal getMaxProfit() {
		return maxProfit;
	}

	public void setMaxProfit(BigDecimal maxProfit) {
		this.maxProfit = maxProfit;
	}

	public BigDecimal getMaxLoss() {
		return maxLoss;
	}

	public void setMaxLoss(BigDecimal maxLoss) {
		this.maxLoss = maxLoss;
	}

	public BigDecimal getSpread() {
		return unrealizedGain;
	}

	public void setSpread(BigDecimal spread) {
		this.unrealizedGain = spread;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	@Override
	public String toString() {
		String marginStr = (margin == null) ? "NA" : margin.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
		return String.format("[Unreal-gain: % 7.1f\tmax-profit: % 7.1f\tmax-loss: % 7.1f\tmargin: %s]", unrealizedGain,
				maxProfit, maxLoss, marginStr);
	}

}
