package proc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import vo.OptionContract;
import vo.RawData;

public class JsonBuilder {

	public static final String JSON_PATH = "frontend/json/";

	public static void writePositionJson(RawData rawdata) throws IOException {

		String time = DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now());
		try (BufferedWriter bw = Files
				.newBufferedWriter(Paths.get(JSON_PATH + rawdata.contract + "_" + time + ".json"))) {

			for (OptionContract oc : rawdata.callContracts) {
//				bw.write(oc.getProfit(ls, spotPrice));
			}
		}

	}
}
