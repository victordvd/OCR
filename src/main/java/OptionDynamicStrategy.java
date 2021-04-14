import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.TesseractException;
import proc.ColorChange;
import proc.Configuration;
import proc.ScanedImage;
import proc.StrategyAnalyzer;
import vo.TxoContract;

public class OptionDynamicStrategy {

	
	public static void main(String[] args) throws IOException, TesseractException {
		
		// 1. Scan image of option informations from some trading APP
	

		// 2. Recognize option informations to java data type
		System.out.println("\nRecognize image");
		BufferedImage image = ImageIO.read(new File(Configuration.INPUT_PATH + "op.png"));
		List<BufferedImage> colImgs = ColorChange.replaceChar(image);
		List<TxoContract> contracts = ScanedImage.process(colImgs) ;

		// 3. Analyze option informations for the most profitable option strategy
		System.out.println("\nAnalyze strategies");
		StrategyAnalyzer.calculateProfit(contracts);
	}

}
