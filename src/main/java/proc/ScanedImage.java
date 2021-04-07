package proc;

import java.awt.Graphics2D;

import net.sourceforge.tess4j.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class ScanedImage {

	static Tesseract it = new Tesseract();

	public static void main(String args[]) throws Exception {
		process();
		/*
		 * File f = new File(Configuration.PROCESSING_PATH + "tmp.png");
		 * 
		 * BufferedImage ipimage = ImageIO.read(f);
		 * 
		 * // getting RGB content of the whole image file double d =
		 * ipimage.getRGB(ipimage.getTileWidth() / 2, ipimage.getTileHeight() / 2);
		 * 
		 * // comparing the values // and setting new scaling values // that are later
		 * on used by RescaleOP processImg(ipimage, 3f, -10f);
		 */
		/*
		 * if (d >= -1.4211511E7 && d < -7254228) { processImg(ipimage, 3f, -10f); }
		 * else if (d >= -7254228 && d < -2171170) { processImg(ipimage, 1.455f, -47f);
		 * } else if (d >= -2171170 && d < -1907998) { processImg(ipimage, 1.35f, -10f);
		 * } else if (d >= -1907998 && d < -257) { processImg(ipimage, 1.19f, 0.5f); }
		 * else if (d >= -257 && d < -1) { processImg(ipimage, 1f, 0.5f); } else if (d
		 * >= -1 && d < 2) { processImg(ipimage, 1f, 0.35f); }
		 */
	}

	private static void process() throws IOException, TesseractException {
		BufferedImage image = ImageIO.read(new File(Configuration.INPUT_PATH + "op.png"));
		List<BufferedImage> colImgs = ColorChange.replaceChar(image);

		it.setDatapath(Configuration.TESSDATA_PATH);

		int strikeIdx = 5;
		int callBidIdx = 0;
		int callAskIdx = 1;
		int putBidIdx = 6;
		int putAskIdx = 7;

		List<String> strikeStrs = splitColumnString(colImgs.get(strikeIdx));
		List<String> callBidStrs = splitColumnString(colImgs.get(callBidIdx));
		List<String> callAskStrs = splitColumnString(colImgs.get(callAskIdx));

		List<Integer> strikes = parseStrike(strikeStrs);
		List<Double> callBids = parsePrices(callBidStrs);
		List<Double> callAsks = parsePrices(callAskStrs);
		
		for(int i = 0;i<strikes.size();i++) {
			
			System.out.printf("%.1f\t%.1f\t%d\t%n",callBids.get(i),callAsks.get(i),strikes.get(i));
			
		}
		
	}

	static List<String> splitColumnString(BufferedImage img) throws TesseractException {
		String str = it.doOCR(img).replace(" ", "");

		System.out.print(str);

		return Arrays.asList(str.split("\n"));
	}

	static List<Integer> parseStrike(List<String> strikes) {
		List<Integer> ps = new ArrayList<Integer>();
		for (int i = 0; i < strikes.size(); i++) {
			String s = strikes.get(i);

			Integer strike = Integer.valueOf(s);
			ps.add(strike);
		}
		return ps;
	}
	
	static List<Double> parsePrices(List<String> prices) {
		List<Double> ps = new ArrayList<Double>();
		for (int i = 0; i < prices.size(); i++) {
			String p = prices.get(i);
			
			p =p.replace(",",".");
			
			if(!p.contains(".")) {
				p = p.substring(0,p.length()-1)+"."+p.substring(p.length()-1,p.length());
			}

			Double pd = Double.valueOf(p);
			ps.add(pd);
		}
		return ps;
	}



	private static void processImg(BufferedImage ipimage, float scaleFactor, float offset)
			throws IOException, TesseractException {
		// Making an empty image buffer
		// to store image later
		// ipimage is an image buffer
		// of input image
		BufferedImage opimage = new BufferedImage(1050, 1024, ipimage.getType());
//		BufferedImage opimage = new BufferedImage( ipimage);

		// creating a 2D platform
		// on the buffer image
		// for drawing the new image
		Graphics2D graphic = opimage.createGraphics();

		// drawing new image starting from 0 0
		// of size 1050 x 1024 (zoomed images)
		// null is the ImageObserver class object
//		graphic.drawImage(ipimage, 0, 0, 
//						1050, 1024, null); 
		graphic.dispose();
		// rescale OP object
		// for gray scaling images
		float[] factors = new float[] {

				// RGB each value for 1 color
				1.45f, 1.45f, 1.45f };

		float[] offsets = new float[] { 0.0f, 150.0f, 0.0f };

//		RescaleOp rescale = new RescaleOp(factors, offsets, null);

//		RescaleOp rescale = new RescaleOp(1.5f, 10f, null); 
//		RescaleOp rescale = new RescaleOp(1.5f,0.2f, null); 

//		RescaleOp rescale 
//			= new RescaleOp(scaleFactor, offset, null); 

		// performing scaling
		// and writing on a .png file
//		BufferedImage fopimage
////			= rescale.filter(opimage, null); 
//				= rescale.filter(ipimage, null);
//		ImageIO.write(fopimage, "jpg", new File("C:\\ocr\\img\\tmp.png"));

		// Instantiating the Tesseract class
		// which is used to perform OCR
		Tesseract it = new Tesseract();

		it.setDatapath(Configuration.TESSDATA_PATH);

		// doing OCR on the image
		// and storing result in string str
//		String str = it.doOCR(fopimage);
		String str = it.doOCR(ipimage);

		System.out.println(str);

		StringRecongizer.parseString(str);

//		Files.write(Paths.get(Configuration.OUTPUT_PATH+"tmp.csv"), str.getBytes());

	}
}
