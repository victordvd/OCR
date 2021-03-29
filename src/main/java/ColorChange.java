import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
// w w w  . jav  a 2s  . com
import javax.imageio.ImageIO;

public class ColorChange {

	public static void main(String[] args) throws Exception {
		int width = 512;
		int height = 512;
		File f = new File("C:\\ocr\\img\\op2.png");

		BufferedImage image = ImageIO.read(f);
		BufferedImage image2 = replaceChar(image);

		ImageIO.write(image2, "png", new File("C:\\ocr\\img\\tmp.png"));
		/*
		 * // BufferedImage image = new BufferedImage(width, height,
		 * BufferedImage.TYPE_4BYTE_ABGR); Graphics2D g2d = image.createGraphics();
		 * Color darkOliveGreen = new Color(85, 107, 47); g2d.setColor(darkOliveGreen);
		 * g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		 * 
		 * // BufferedImage img = colorImage(ImageIO.read(new
		 * File("C:\\ocr\\img\\op.png"))); // ImageIO.write(img, "png", new
		 * File("Test.png"));
		 * 
		 */
	}

	private static BufferedImage replaceChar(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();

		int lightWhite = 150;
		int majorColor = 180;
		int minorColor = 50;
		int range = 40;

		Color black = Color.BLACK;
		Color grey = new Color(36, 36, 36);
		Color grey2 = new Color(61, 61, 61);
		Color purple = new Color(91, 0, 91);
		Color purple2 = new Color(78, 50, 78);

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);

				// RGB 255~0
				if (compareColor(pixels, grey,range) || compareColor(pixels, grey2,range) || compareColor(pixels, black,range)
						|| compareColor(pixels, purple,range) || compareColor(pixels, purple2,range)) {
					pixels[0] = 255;
					pixels[1] = 255;
					pixels[2] = 255;
				}  else {
					pixels[0] = 0;
					pixels[1] = 0;
					pixels[2] = 0;
				}

				/*
				else if ((pixels[0] > lightWhite && pixels[1] > lightWhite && pixels[2] > lightWhite)
						|| (pixels[0] > majorColor && pixels[1] < minorColor && pixels[2] < minorColor)
						|| (pixels[0] < minorColor && pixels[1] > majorColor && pixels[2] < minorColor)) {
					pixels[0] = 0;
					pixels[1] = 0;
					pixels[2] = 0;
				} else {
					pixels[0] = 255;
					pixels[1] = 255;
					pixels[2] = 255;
				}*/

				raster.setPixel(xx, yy, pixels);
			}
		}

		return image;
	}

	private static BufferedImage replaceBackground(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();

		int lightWhite = 243;
		int grey = 80;

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);

				if ((pixels[0] <= grey && pixels[1] <= grey && pixels[2] <= grey)/* grey */
						|| (pixels[0] >= 70 && pixels[1] <= 54 && pixels[2] >= 70)/* purple */) {
					pixels[0] = 255;
					pixels[1] = 255;
					pixels[2] = 255;
				}

				raster.setPixel(xx, yy, pixels);
			}
		}
		return image;
	}

	private static BufferedImage colorImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();

		int lightWhite = 243;
		int grey = 80;

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);

				// RGB 255~0
				if ((pixels[0] > lightWhite || pixels[1] > lightWhite || pixels[2] > lightWhite)) {
					pixels[0] = 0;
					pixels[1] = 0;
					pixels[2] = 0;
				} else if ((pixels[0] <= grey && pixels[1] <= grey && pixels[2] <= grey)/* grey */
						|| (pixels[0] >= 70 && pixels[1] <= 54 && pixels[2] >= 70)/* purple */) {
					pixels[0] = 255;
					pixels[1] = 255;
					pixels[2] = 255;
				} else {

				}

				raster.setPixel(xx, yy, pixels);
			}
		}

		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);

				// RGB 255~0
				if (pixels[0] < 250 && pixels[1] < 250 && pixels[2] < 250) {
					pixels[0] = 0;
					pixels[1] = 0;
					pixels[2] = 0;
				}

				raster.setPixel(xx, yy, pixels);
			}
		}
		return image;
	}

	static boolean compareColor(int[] pixels, Color color) {
		return (color.getRed() == pixels[0] && color.getGreen() == pixels[1] && color.getBlue() == pixels[2]);

	}

	static boolean compareColor(int[] pixels, Color color, int range) {
		return ((color.getRed() + range >= pixels[0] && color.getGreen() + range >= pixels[1]
				&& color.getBlue() + range >= pixels[2])
				&& (color.getRed() - range <= pixels[0] && color.getGreen() - range <= pixels[1]
						&& color.getBlue() - range <= pixels[2]));

	}
}