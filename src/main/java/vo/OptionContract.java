package vo;

import java.math.BigDecimal;

import vo.Position.LS;

public class OptionContract {

	public static enum OptionType {
		C, P
	}

	public OptionType type;
	public BigDecimal strike;
	public BigDecimal bid;
	public BigDecimal ask;

	public OptionContract() {
	}

	public OptionContract(OptionType type) {
		super();
		this.type = type;
	}

	public OptionContract(OptionType type, double strike, double bid, double ask) {
		super();
		this.type = type;
		this.strike = new BigDecimal(strike);
		this.bid = new BigDecimal(bid);
		this.ask = new BigDecimal(ask);
	}

	public OptionContract(OptionType type, BigDecimal strike, BigDecimal bid, BigDecimal ask) {
		super();
		this.type = type;
		this.strike = strike;
		this.bid = bid;
		this.ask = ask;

	}

	public Profit getProfit(LS ls, double spotPrice) {
		BigDecimal defaultLoss = BigDecimal.ONE;
		BigDecimal spot = new BigDecimal(spotPrice);
		BigDecimal infi = new BigDecimal("9999");
//		Profit p = new Profit(this,ls);
		Profit p = new Profit();
		if (OptionType.C == this.type) {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(ask.negate().subtract(defaultLoss));
				p.setUnrealizedGain(spot.subtract(ask).subtract(strike).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setUnrealizedGain(bid.subtract(spot.subtract(strike)).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			}
		} else {
			if (LS.L == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(ask.negate().subtract(defaultLoss));
				p.setUnrealizedGain(strike.subtract(spot).subtract(ask).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setUnrealizedGain(bid.subtract(strike.subtract(spot)).subtract(defaultLoss));
				p.setUnrealizedGain(p.getUnrealizedGain().min(p.getMaxProfit()));
			}
		}
		return p;
	}

	public OptionType getType() {
		return type;
	}

	public void setType(OptionType type) {
		this.type = type;
	}

	public BigDecimal getStrike() {
		return strike;
	}

	public void setStrike(BigDecimal strike) {
		this.strike = strike;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

}
