package vo;

import java.math.BigDecimal;

import vo.OptionContract.OptionType;

public class Position {
	public static enum LS {
		L, S
	}

	private LS ls;
	private OptionContract contract;
	private BigDecimal price;
	private BigDecimal premium;
	private BigDecimal margin;

	public Position(LS ls, OptionContract contract) {
		this.ls = ls;
		this.contract = contract;

		switch (ls) {
		case L:
			this.price = contract.ask;
			break;
		case S:
			this.price = contract.bid;
		}
	}

	public Position(LS ls, OptionContract contract, BigDecimal price) {
		this.ls = ls;
		this.contract = contract;
		this.price = price;
	}

	public Profit getProfit(BigDecimal spot, BigDecimal defaultLoss) {
		BigDecimal infi = new BigDecimal("9999");
		Profit p = new Profit();
		if (OptionType.C == contract.type) {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(price.add(defaultLoss));
				p.setUnrealizedGain(spot.subtract(contract.ask).subtract(contract.strike).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()).max(p.getMaxLoss().negate()));
			} else {
				p.setMaxProfit(contract.bid.subtract(defaultLoss));
				p.setMaxLoss(infi);
				p.setUnrealizedGain(price.subtract(spot.subtract(contract.strike)).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			}
		} else {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(price.add(defaultLoss));
				p.setUnrealizedGain(contract.strike.subtract(spot).subtract(contract.ask).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()).max(p.getMaxLoss().negate()));
			} else {
				p.setMaxProfit(contract.bid.subtract(defaultLoss));
				p.setMaxLoss(infi);
				p.setUnrealizedGain(price.subtract(contract.strike.subtract(spot)).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			}
		}
		return p;
	}

	public LS getLs() {
		return ls;
	}

	public OptionContract getContract() {
		return contract;
	}

	public void setLs(LS ls) {
		this.ls = ls;
	}

	public void setContract(OptionContract contract) {
		this.contract = contract;
	}

	public BigDecimal getPremium() {
		return premium;
	}

	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}

	public BigDecimal getMargin() {
		return margin;
	}

	public void setMargin(BigDecimal margin) {
		this.margin = margin;
	}

	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return ls + " " + contract.getStrike() + contract.getType();
	}

}
