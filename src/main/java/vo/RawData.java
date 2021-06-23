package vo;

import java.math.BigDecimal;
import java.util.List;

public class RawData {

	public BigDecimal spot;

	public String contract;

	public List<OptionContract> callContracts;

	public List<OptionContract> putContracts;
}
