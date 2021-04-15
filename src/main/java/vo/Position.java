package vo;

public class Position {
	public static enum LS {
		L, S
	}
	
	private LS ls;
	private TxoContract contract;
	
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

	@Override
	public String toString() {
		return ls + " " + contract.getStrike()+contract.getType();
	}
	
	

}
