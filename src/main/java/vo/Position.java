package vo;

import java.math.BigDecimal;

public class Position {
	public static enum LS {
		L, S
	}
	
	private LS ls;
	private TxoContract contract;
	private BigDecimal premium;

	private BigDecimal margin;
	
	public Position(LS ls, TxoContract contract) {
		this.ls = ls;
		this.contract = contract;
	}

	public LS getLs() {
		return ls;
	}

	public TxoContract getContract() {
		return contract;
	}

	public void setLs(LS ls) {
		this.ls = ls;
	}

	public void setContract(TxoContract contract) {
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

	@Override
	public String toString() {
		return ls + " " + contract.getStrike()+contract.getType();
	}
	
	

}
