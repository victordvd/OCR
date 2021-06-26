package proc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

import vo.RawData;

public class JsonBuilder {

	public static final String JSON_PATH = System.getProperty("user.dir") + File.separator + "frontend/json/";

	public static void writePositionJson(RawData rawdata) throws IOException {

		String time = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
//		String filename = JSON_PATH + rawdata.contract + "_" + time + ".json";
		String filename = JSON_PATH + "data.js";

		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filename))) {
			bw.write("var data = ");
			bw.write(new Gson().toJson(rawdata));
//			for (OptionContract oc : rawdata.callContracts) {
//				bw.write(new Gson().toJson(oc));
//				bw.newLine();
//			}
			bw.write(";");
			bw.flush();
		}

	}
}
