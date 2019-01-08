package world.cn.sinobest.mapreducepicture;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 下午1:34:08
 *
 * @description 图片处理工具类
 */
public class ImageUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	/**
	 * 本地图片转换成base64字符串
	 * 
	 * @param imgFile 图片本地路径
	 * @return
	 */
	public static String imageToBase64(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

		InputStream in = null;
		byte[] data = null;

		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);

			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			logger.error("【图片Base64转换】错误：" + e.getMessage());
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("【图片Base64转换】错误：" + e.getMessage());
				}
			}
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}

	/**
	 * base64字符串转换成图片
	 * 
	 * @param imgStr      base64字符串
	 * @param imgFilePath 图片存放路径
	 * @return
	 */
	public static boolean base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

		if (ImageUtil.isEmpty(imgStr)) { // 图像数据为空
			return false;
		}

		OutputStream out = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}

			out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();

			return true;
		} catch (Exception e) {
			logger.error("【Base64图片转换】错误：" + e.getMessage());
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("【Base64图片转换】错误：" + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 判断对象是否为空
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isEmpty(Object object) {
		// 对象为空，返回false
		if (object == null) {
			return true;
		}

		// 如果对象是字符串，则判断对象是否为空串
		if (object instanceof String && "".equals(object)) {
			return true;
		}
		
		// 对象是map，则判断map是否为空
		if (object instanceof Map && ((Map<?, ?>)object).isEmpty()) {
			return true;
		}
		
		// 对象是list，则判断list.size是否为0
		if (object instanceof List && ((List<?>)object).size() == 0) {
			return true;
		}
		return false;
	}
}
