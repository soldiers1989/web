package com.cfo.stock.web.action.qrcode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jrj.common.velocity.cache.CacheManager;
 
 
 public final class MatrixToImageWriter {
 
   private static final int BLACK = 0xFF000000;
   private static final int WHITE = 0xFFFFFFFF;
 
   private MatrixToImageWriter() {}
 
   
   public static BufferedImage toBufferedImage(BitMatrix matrix,File logoPic) {
     int width = matrix.getWidth();
     int height = matrix.getHeight();
     BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
     for (int x = 0; x < width; x++) {
       for (int y = 0; y < height; y++) {
         image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
       }
     }
     if(logoPic!=null&&logoPic.exists()){
    	 addLogo_QRCode(image, logoPic, new LogoConfig());
     }
     return image;
   }
 
   
   public static void writeToFile(BitMatrix matrix, String format, File file)
       throws IOException {
     BufferedImage image = toBufferedImage(matrix,null);
     if (!ImageIO.write(image, format, file)) {
       throw new IOException("Could not write an image of format " + format + " to " + file);
     }
   }
 
   
   public static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
       throws IOException {
     BufferedImage image = toBufferedImage(matrix,null);
     if (!ImageIO.write(image, format, stream)) {
       throw new IOException("Could not write an image of format " + format);
     }
   }
   
   public static void writeToStream(BitMatrix matrix, String format,File logoPic, OutputStream stream)
	       throws IOException {
	     BufferedImage image = toBufferedImage(matrix,logoPic);
	     if (!ImageIO.write(image, format, stream)) {
	       throw new IOException("Could not write an image of format " + format);
	     }
	   }

	/**
	 * 给二维码图片添加Logo
	 * 
	 * @param qrPic
	 * @param logoPic
	 */
	public static void addLogo_QRCode(BufferedImage image, File logoPic,
			LogoConfig logoConfig) {
		try {
			/**
			 * 读取二维码图片，并构建绘图对象
			 */
			Graphics2D g = image.createGraphics();
			/**
			 * 读取Logo图片
			 */
			BufferedImage logo = ImageIO.read(logoPic);

			int widthLogo = logo.getWidth(), heightLogo = logo.getHeight();

			// 计算图片放置位置
			int x = (image.getWidth() - widthLogo) / 2;
			int y = (image.getHeight() - logo.getHeight()) / 2;

			// 开始绘制图片
			g.drawImage(logo, x, y, widthLogo, heightLogo, null);
			g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
			g.setStroke(new BasicStroke(logoConfig.getBorder()));
			g.setColor(logoConfig.getBorderColor());
			g.drawRect(x, y, widthLogo, heightLogo);

			g.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置二维码的格式参数
	 * 
	 * @return
	 */
	public static Map<EncodeHintType, Object> getDecodeHintType() {
		// 用于设置QR二维码参数
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置编码方式
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		return hints;
	}
	/**
	 * 设置二维码缓存
	 * @param image
	 * @param key
	 * @throws IOException 
	 */
	public static void setQrImageCache(BufferedImage image,String format,String key) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, format, out);
		CacheManager.set(EHCACHE_NAME, key, out.toByteArray());
	}
	
	private final static String EHCACHE_NAME="QrImage";
	/**
	 * 获取二维码缓存
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	public static BufferedImage getQrImageCache(String key) throws IOException{
		byte[] obj = (byte[]) CacheManager.get(EHCACHE_NAME, key);
		if (obj != null) {
			ByteArrayInputStream bin = new ByteArrayInputStream(obj);
			return ImageIO.read(bin);
		}
		return null;
	}
	
	static 
	class LogoConfig {
		// logo默认边框颜色
		public static final Color DEFAULT_BORDERCOLOR = Color.WHITE;
		// logo默认边框宽度
		public static final int DEFAULT_BORDER = 2;
		// logo大小默认为照片的1/5
		public static final int DEFAULT_LOGOPART = 5;

		private final int border = DEFAULT_BORDER;
		private final Color borderColor;
		private final int logoPart;

		/**
		 * Creates a default config with on color {@link #BLACK} and off color
		 * {@link #WHITE}, generating normal black-on-white barcodes.
		 */
		public LogoConfig() {
			this(DEFAULT_BORDERCOLOR, DEFAULT_LOGOPART);
		}

		public LogoConfig(Color borderColor, int logoPart) {
			this.borderColor = borderColor;
			this.logoPart = logoPart;
		}

		public Color getBorderColor() {
			return borderColor;
		}

		public int getBorder() {
			return border;
		}

		public int getLogoPart() {
			return logoPart;
		}
	}
 }