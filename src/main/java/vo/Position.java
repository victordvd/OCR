package vo;

public class Position {
	public static enum LS {
		Long, Short
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
	
	

}
