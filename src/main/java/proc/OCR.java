package proc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import vo.OptionContract;

public class OCR {

	private static Tesseract it = new Tesseract();

	public static void main(String args[]) throws Exception {

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

	public static List<OptionContract> process(List<BufferedImage> colImgs) throws Exception {
		it.setDatapath(Configuration.TESSDATA_PATH);

		int strikeIdx = 5;
		int callBidIdx = 0;
		int callAskIdx = 1;
		int callLastIdx = 3;
		int putBidIdx = 6;
		int putAskIdx = 7;
		int putLastIdx = 8;

		List<String> strikeStrs = splitColumnString(colImgs.get(strikeIdx));
		List<String> callBidStrs = splitColumnString(colImgs.get(callBidIdx));
		List<String> callAskStrs = splitColumnString(colImgs.get(callAskIdx));
		List<String> callLastStrs = splitColumnString(colImgs.get(callLastIdx));
		List<String> putBidStrs = splitColumnString(colImgs.get(putBidIdx));
		List<String> putAskStrs = splitColumnString(colImgs.get(putAskIdx));
		List<String> putLastStrs = splitColumnString(colImgs.get(putLastIdx));

		List<Double> strikes = parseStrike(strikeStrs);
		System.out.println("Parsing call bid");
		List<Double> callBids = parsePrices(callBidStrs);
		System.out.println("Parsing call ask");
		List<Double> callAsks = parsePrices(callAskStrs);
//		System.out.println("Parsing call last");
//		List<Double> callLasts = parsePrices(callLastStrs);
		System.out.println("Parsing put bid");
		List<Double> putBids = parsePrices(putBidStrs);
		System.out.println("Parsing put ask");
		List<Double> putAsks = parsePrices(putAskStrs);
//		System.out.println("Parsing put last");
//		List<Double> putLasts = parsePrices(putLastStrs);

		// Correct
		correctPrice(callBids, callAsks, true);
		correctPrice(putBids, putAsks, false);

		//
		LinkedHashMap<Double, OptionContract[]> m = new LinkedHashMap<>();
		List<OptionContract> contracts = new ArrayList<>();

		System.out.printf("C bid\tC ask\tStrike\tP bid\tP ask%n");
		for (int i = 0; i < strikes.size(); i++) {
			System.out.printf("%.1f\t%.1f\t[%.0f]\t%.1f\t%.1f%n", callBids.get(i), callAsks.get(i), strikes.get(i),
					putBids.get(i), putAsks.get(i));

			Double strike = strikes.get(i);

			OptionContract call = new OptionContract(OptionContract.OptionType.C, strike, callBids.get(i),
					callAsks.get(i));
			OptionContract put = new OptionContract(OptionContract.OptionType.P, strike, putBids.get(i),
					putAsks.get(i));

			m.put(strike, new OptionContract[] { call, put });

			contracts.add(call);
			contracts.add(put);
		}

		return contracts;
	}

	static List<String> splitColumnString(BufferedImage img) throws TesseractException {
		String str = it.doOCR(img).replace(" ", "");

//		System.out.print(str);-

		return Arrays.asList(str.split("\n"));
	}

	static List<Double> parseStrike(List<String> strikes) {
		List<Double> ps = new ArrayList<>();
		for (int i = 0; i < strikes.size(); i++) {
			String s = strikes.get(i);
			Double strike = Double.valueOf(s);
			ps.add(strike);
		}

		List<Double> degrees = new ArrayList<>();
		degrees.add(50D);
		degrees.add(100D);

		for (int i = 1; i < ps.size() - 1; i++) {
			Double a = ps.get(i - 1);
			Double b = ps.get(i);
			Double c = ps.get(i + 1);

			if (c > b && b > a) {// normal

			} else {// abnormal
				System.out.printf("abnormal strike:  %f, [%d]%f, %f", a, i, b, c);
				double degree = b - a;
				if (!degrees.contains(degree) && degrees.contains((c - a) / 2)) {
					ps.set(i, (c + a) / 2);
					System.out.print(", (fixed)");
				} else {
					System.out.print(", (unfixed)");
				}
				System.out.println();
			}
		}
		return ps;
	}

	static List<Double> parsePrices(List<String> prices) {
		List<Double> ps = new ArrayList<Double>();
		for (int i = 0; i < prices.size(); i++) {
			String p = prices.get(i);

			p = p.replace(",", ".").replace("§", "5").replace("£", "6");

			if (!p.contains(".") && p.length() > 1) {
				p = p.substring(0, p.length() - 1) + "." + p.substring(p.length() - 1, p.length());
			}

			if (NumberUtils.isParsable(p)) {
				Double pd = Double.valueOf(p);
				ps.add(pd);
//				System.out.printf("[%d] %s%n", i, p);
			} else if (StringUtils.isBlank(p)) {
				System.out.printf("[%d] %s(blank)%n", i, p);
			} else {
				System.out.printf("[%d] %s(fail)%n", i, p);
				ps.add(0D);
			}
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

	private static void correctPrice(List<Double> bids, List<Double> asks, boolean isCall) throws Exception {

		int stI = isCall ? 0 : bids.size() - 1;
		int edI = isCall ? bids.size() - 1 : 0;
		int increase = isCall ? 1 : -1;

		for (int i = stI; i < edI; i += increase) {
			Double bid = bids.get(i);
			Double ask = asks.get(i);

			Double nextBid = bids.get(i + 1);
			Double nextAsk = asks.get(i + 1);
//			System.out.println(bid+" "+ask);

			if (bid < nextBid && ask < nextAsk) {
				throw new Exception("Invalid price");
			} else if (bid < nextBid) {
				bids.set(i, ask - 1);
			} else if (ask < nextAsk) {
				asks.set(i, bid + 1);
			} else if (bid > ask) {
				bids.set(i, ask - 1);
			}
		}

	}
}
