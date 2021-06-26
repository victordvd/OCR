package exec;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import proc.JsonBuilder;
import proc.StrategyAnalyzer;
import proc.TxoDataFetch;
import vo.RawData;

public class OptionDynamicStrategy {

	public static Path rawDataPath;

	public static void main(String[] args) throws Exception {

		rawDataPath = Paths.get(System.getProperty("user.dir") + File.separator + "src/resource/rawdata.json");

		System.out.println(rawDataPath);

		// 1. Scan image of option informations from some trading APP

		// 2. Recognize option informations to java data type
//		System.out.println("\nRecognize image");
//		BufferedImage image = ImageIO.read(new File(Configuration.INPUT_PATH + "op.png"));
//		List<BufferedImage> colImgs = ColorChange.replaceChar(image);
//		List<OptionContract> contracts = OCR.process(colImgs);

		RawData rawdata = TxoDataFetch.fetchTxoRawData();

		// 3. Analyze option informations for the most profitable option strategy
		System.out.println("\nAnalyze strategies");
		StrategyAnalyzer.calculateProfit(rawdata);

		System.out.println("\nCreate json files");
		JsonBuilder.writePositionJson(rawdata);

		System.out.println("Data generated");
	}

}
