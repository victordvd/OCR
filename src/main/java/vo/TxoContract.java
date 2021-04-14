package vo;

import java.math.BigDecimal;

import vo.Position.LS;

public class TxoContract {

	public enum OptionType {
		Call, Put
	}

	public OptionType type;
	public BigDecimal strike;
	public BigDecimal bid;
	public BigDecimal ask;
//	public BigDecimal price;

	public BigDecimal maxLoss;
	public BigDecimal maxProfit;
	public BigDecimal profit;

	public BigDecimal defaultLoss = BigDecimal.ONE;

	public TxoContract() {
	}

	public TxoContract(OptionType type, double strike, double bid, double ask) {
		super();
		this.type = type;
		this.strike = new BigDecimal(strike);
		this.bid = new BigDecimal(bid);
		this.ask = new BigDecimal(ask);
	}

	public TxoContract(OptionType type, BigDecimal strike, BigDecimal bid, BigDecimal ask) {
		super();
		this.type = type;
		this.strike = strike;
		this.bid = bid;
		this.ask = ask;

	}

	public Profit getProfit(LS ls, double spotPrice) {
		BigDecimal spot = new BigDecimal(spotPrice);
		BigDecimal infi = new BigDecimal("9999");
		Profit p = new Profit(this,ls);
//		Profit p = new Profit();
		if (OptionType.Call == this.type) {
			if (LS.Long == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(ask.negate().subtract(defaultLoss));
				p.setProfit(spot.subtract(ask).subtract(strike).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(bid.subtract(spot.subtract(strike)).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			}
		} else {
			if (LS.Long == ls) {
				p.setMaxProfit(infi);
				p.setMaxLoss(ask.negate().subtract(defaultLoss));
				p.setProfit(strike.subtract(spot).subtract(ask).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
			} else {
				p.setMaxProfit(bid.subtract(defaultLoss));
				p.setMaxLoss(infi.negate());
				p.setProfit(bid.subtract(strike.subtract(spot)).subtract(defaultLoss));
				p.setProfit(p.getProfit().min(p.getMaxProfit()));
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

	public BigDecimal getMaxLoss() {
		return maxLoss;
	}

	public void setMaxLoss(BigDecimal maxLoss) {
		this.maxLoss = maxLoss;
	}

	public BigDecimal getMaxProfit() {
		return maxProfit;
	}

	public void setMaxProfit(BigDecimal maxProfit) {
		this.maxProfit = maxProfit;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getDefaultLoss() {
		return defaultLoss;
	}

	public void setDefaultLoss(BigDecimal defaultLoss) {
		this.defaultLoss = defaultLoss;
	}
	
	

}
