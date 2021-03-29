package vo;

public class TxoContract {
	
	public enum OptionType{
		Call,Put
	}
	
	public OptionType type;
	public Integer strike;
	public Double bid;
	public Double ask;
	public Double price;
	
	public TxoContract() {}
	
	public TxoContract(OptionType type, Integer strike, Double bid, Double ask, Double price) {
		super();
		this.type = type;
		this.strike = strike;
		this.bid = bid;
		this.ask = ask;
		this.price = price;
	}
	
	
	
}
