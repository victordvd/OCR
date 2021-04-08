package vo;

import java.math.BigDecimal;

public class TxoContract {

	public enum OptionType {
		Call, Put
	}

	public enum LS {
		Long, Short
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
		Profit p = new Profit();
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

}
