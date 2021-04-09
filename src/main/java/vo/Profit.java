package vo;

import java.math.BigDecimal;

public class Profit {
	
	private TxoContract contract;
	private TxoContract.LS ls;
	private BigDecimal profit;
	private BigDecimal maxProfit;
	private BigDecimal maxLoss;
	
	
	
	public Profit(TxoContract contract, TxoContract.LS ls ) {
		super();
		this.contract = contract;
		this.ls = ls;
	}
	public TxoContract getContract() {
		return contract;
	}

	
	
	public TxoContract.LS getLs() {
		return ls;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
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
	@Override
	public String toString() {
		return String.format("[profit: % 7.1f\tmax: % 7.1f\tmin: % 7.1f",  profit,maxProfit,maxLoss);
	}
	
	
}
