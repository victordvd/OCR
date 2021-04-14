package proc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// w w w  . jav  a 2s  . com
import javax.imageio.ImageIO;

public class ColorChange {

	public static void main(String[] args) throws Exception {
		int width = 512;
		int height = 512;
		BufferedImage image = ImageIO.read(new File(Configuration.INPUT_PATH + "op.png"));
		replaceChar(image);


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

	public static List<BufferedImage> replaceChar(BufferedImage image) throws IOException {
		int width = image.getWidth();
		int height = image.getHeight();
		System.out.printf("W: %d, H: %d%n", width, height);

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

		for (int yy = 0; yy < height; yy++) {
			for (int xx = 0; xx < width; xx++) {
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);
				// RGB 255~0
				if (compareColor(pixels, grey, range) || compareColor(pixels, grey2, range)
						|| compareColor(pixels, black, range) || compareColor(pixels, purple, range)
						|| compareColor(pixels, purple2, range)) {
					pixels[0] = 255;
					pixels[1] = 255;
					pixels[2] = 255;
				} else {
					pixels[0] = 0;
					pixels[1] = 0;
					pixels[2] = 0;
				}

				/*
				 * else if ((pixels[0] > lightWhite && pixels[1] > lightWhite && pixels[2] >
				 * lightWhite) || (pixels[0] > majorColor && pixels[1] < minorColor && pixels[2]
				 * < minorColor) || (pixels[0] < minorColor && pixels[1] > majorColor &&
				 * pixels[2] < minorColor)) { pixels[0] = 0; pixels[1] = 0; pixels[2] = 0; }
				 * else { pixels[0] = 255; pixels[1] = 255; pixels[2] = 255; }
				 */

				raster.setPixel(xx, yy, pixels);
			}
		}
		
		image.flush();
		raster = image.getRaster();
		
		// Split
		int startX = 0;
		int continuousYCol = 0;
		List<Color> prevYColors = new ArrayList<>();
		
		List<int[]> whiteXranges = new ArrayList<int[]>();
		for (int xx = 0; xx < width; xx++) {
			List<Color> yColors = new ArrayList<>();
			for (int yy = 0; yy < height; yy++) {
//			System.out.println("<<<<< y:" + yy + ">>>>>");
		

			
				int[] pixels = raster.getPixel(xx, yy, (int[]) null);

				Color color = new Color(pixels[0], pixels[1], pixels[2]);
				yColors.add(color);

			}

			
			
			// compare Y - color
			if (almostWhite(yColors)) {
//				System.out.println("Continuous Y color: " + yy);
				continuousYCol++;

			} else {
				
//				StringBuilder sb = new StringBuilder(xx+": ");
//				for(int y=0;y<yColors.size();y++) {
//					Color c = yColors.get(y);
//					sb.append(String.format("%d(%d,%d,%d)\t",y,c.getRed(),c.getGreen(),c.getBlue()));
//				}
//				System.out.println(sb.toString());
				
				if (continuousYCol > 10) {
					System.out.println("Continue changed: " + startX + ":" + xx);
					whiteXranges.add(new int[]{startX,xx});
				}

				continuousYCol = 0;
				startX = xx;
			}
			
//			StringBuilder sb = new StringBuilder();
//			for(int x=0;x<yColors.size();x++) {
//				Color c = yColors.get(x);
//				sb.append(String.format("%d(%d,%d,%d)\t",x,c.getRed(),c.getGreen(),c.getBlue()));
//			}
//			System.out.println(sb.toString());
			
			prevYColors = yColors;
		}
		
		if (continuousYCol > 10) {
			System.out.println("Continue changed: " + startX + ":" + width);
			whiteXranges.add(new int[]{startX,width});
		}
		
		
		List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		
		int valIdx = 0;
		for(int i = 1;i<whiteXranges.size();i++) {
			int sx = whiteXranges.get(i-1)[1];
			int ex = whiteXranges.get(i)[0];
			
			BufferedImage subImg = image.getSubimage(sx, 0, ex-sx+1, height);
			subImgs.add(subImg);
//			ImageIO.write(subImg,"png",new File(Configuration.PROCESSING_PATH+"sub-"+(valIdx++)+".png"));
		}
		
		ImageIO.write(image, "png", new File(Configuration.PROCESSING_PATH + "tmp.png"));
		
		return subImgs;

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
	
	static boolean almostWhite(List<Color> colors) {
		double cnt = 0;
		
		for(Color c : colors) {
			if(Color.WHITE.equals(c))cnt++;
		}
		
		return cnt/colors.size()>0.99;
	}
}