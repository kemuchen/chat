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
 * @author ����
 * 
 * @time 2018��12��26�� ����1:34:08
 *
 * @description ͼƬ��������
 */
public class ImageUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	/**
	 * ����ͼƬת����base64�ַ���
	 * 
	 * @param imgFile ͼƬ����·��
	 * @return
	 */
	public static String imageToBase64(String imgFile) {// ��ͼƬ�ļ�ת��Ϊ�ֽ������ַ��������������Base64���봦��

		InputStream in = null;
		byte[] data = null;

		// ��ȡͼƬ�ֽ�����
		try {
			in = new FileInputStream(imgFile);

			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			logger.error("��ͼƬBase64ת��������" + e.getMessage());
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("��ͼƬBase64ת��������" + e.getMessage());
				}
			}
		}
		// ���ֽ�����Base64����
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// ����Base64��������ֽ������ַ���
	}

	/**
	 * base64�ַ���ת����ͼƬ
	 * 
	 * @param imgStr      base64�ַ���
	 * @param imgFilePath ͼƬ���·��
	 * @return
	 */
	public static boolean base64ToImage(String imgStr, String imgFilePath) { // ���ֽ������ַ�������Base64���벢����ͼƬ

		if (ImageUtil.isEmpty(imgStr)) { // ͼ������Ϊ��
			return false;
		}

		OutputStream out = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64����
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// �����쳣����
					b[i] += 256;
				}
			}

			out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();

			return true;
		} catch (Exception e) {
			logger.error("��Base64ͼƬת��������" + e.getMessage());
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("��Base64ͼƬת��������" + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * �ж϶����Ƿ�Ϊ��
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isEmpty(Object object) {
		// ����Ϊ�գ�����false
		if (object == null) {
			return true;
		}

		// ����������ַ��������ж϶����Ƿ�Ϊ�մ�
		if (object instanceof String && "".equals(object)) {
			return true;
		}
		
		// ������map�����ж�map�Ƿ�Ϊ��
		if (object instanceof Map && ((Map<?, ?>)object).isEmpty()) {
			return true;
		}
		
		// ������list�����ж�list.size�Ƿ�Ϊ0
		if (object instanceof List && ((List<?>)object).size() == 0) {
			return true;
		}
		return false;
	}
}
