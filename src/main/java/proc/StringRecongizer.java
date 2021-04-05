package proc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import vo.TxoContract;

public class StringRecongizer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void parseString(String str) {
		String[] lines = str.split("\n");

		List<TxoContract> ops = new ArrayList<>();
		
		for (String line : lines) {
			String[] values = line.split("\\s+");

			TxoContract call = new TxoContract();
			call.type = TxoContract.OptionType.Call;
			call.ask = parserDouble(values, 0);
			call.bid = parserDouble(values, 1);
			call.price = parserDouble(values, 2);
			call.strike = parserInt(values, 5);
			ops.add(call);

			TxoContract put = new TxoContract();
			put.type = TxoContract.OptionType.Put;
			put.strike = parserInt(values, 5);
			put.ask = parserDouble(values, 6);
			put.bid = parserDouble(values, 7);
			put.price = parserDouble(values, 8);
			ops.add(put);
		}

	}

	public static String reprocessString(String str) {
		StringBuilder sb = new StringBuilder(str);

		if (StringUtils.isNoneBlank(str)) {
			if (str.length() >= 2 && str.charAt(0) == '0' && NumberUtils.isParsable(String.valueOf(str.charAt(1)))) {
				sb.insert(1, ".");
			}
		}
		return sb.toString();
	}

	public static Integer parserInt(String[] values, int index) {
		try {
			if (values.length > index)
				return Integer.valueOf(values[index]);
			else
				return -1;
		} catch (Exception e) {
			System.out.println("ERR I "+e.getMessage());
			return -1;
		}
	}

	public static Double parserDouble(String[] values, int index) {
		try {
			if (values.length > index)
				return Double.valueOf(values[index]);
			else
				return -1D;
		} catch (Exception e) {
			System.out.println("ERR D "+e.getMessage());
			return -1D;
		}
	}

}
