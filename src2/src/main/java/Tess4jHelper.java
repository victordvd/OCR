import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;

public class Tess4jHelper {

	/**
	 * 圖片識別(一張)
	 * 
	 * @param imgPath
	 * @param ZH_CN
	 *            是否使用中文訓練庫,true-是
	 * @return 識別結果
	 */
	public String recognizing(String imgPath, boolean ZH_CN) {
		try {
			File imageFile = new File(imgPath); // 建立一個圖片檔案
			if (!imageFile.exists()) { // 如果圖片不存在，給出提示並返回
				return "圖片不存在";
			}
			BufferedImage textImage = ImageIO.read(imageFile); // 將圖片載入到記憶體
			Tesseract instance = new Tesseract(); // 建立Tesseract物件
			
			instance.setDatapath(System.getProperty("user.dir") + File.separator+"tessdata");// 設定訓練庫路徑
			if (ZH_CN) //
				instance.setLanguage("chi_sim");// 匯入中文識別字庫
			String recognizeResult = null; // 定義變數，接收識別結果
			recognizeResult = instance.doOCR(textImage);// 呼叫識別方法，得到識別結果
			return recognizeResult; // 返回識別結果
		} catch (Exception e) {
			e.printStackTrace();
			return "tess4j識別圖片時出錯！該圖片路徑為" + imgPath;
		}
	}

	public static void main(String[] args) {
		Tess4jHelper tess4jHelper = new Tess4jHelper();
		String imgPath = "D:\\workplaces\\Spring-WS\\OpenCV\\OCR image\\op.PNG";
		String result = tess4jHelper.recognizing(imgPath, false);//中文識別用true 英文識別用false
		System.out.println(result);

	}
}