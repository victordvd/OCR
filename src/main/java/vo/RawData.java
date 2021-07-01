package vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeSet;

public class RawData {

	public BigDecimal spot;

	public String targetContract;

	public List<String> contracts;

	public TreeSet<Integer> strikes;

	public List<OptionContract> callContracts;

	public List<OptionContract> putContracts;
}
