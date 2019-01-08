package world.cn.sinobest.mapreduceexcel;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author ����
 * 
 * @time 2018��12��26�� ����8:59:55
 *
 * @description Excel������
 */
public class ExcelParser {

	private static Logger logger = LoggerFactory.getLogger(ExcelParser.class);

	/**
	 * ����is
	 * 
	 * @param is ����Դ
	 * @return String[]
	 */
	public static List<String> parseExcelData(InputStream is) {
		// �����
		List<String> resultList = new ArrayList<String>();

		try {
			// ��ȡWorkbook
			Workbook workbook = create(is);
			// ��ȡsheet
			Sheet sheet = workbook.getSheetAt(0);
			
			//������
			int rowNum = sheet.getLastRowNum();
			for (int i = 0; i < rowNum; i++) {
				Row  row = sheet.getRow(i);
				// �ַ���
				StringBuilder rowString = new StringBuilder();
				//������
				int cellNum = row.getLastCellNum();
				for (int j = 0; j < cellNum; j++) {
					rowString.append(",");
					Cell cell = row.getCell(j);
					// �յ�Ԫ��
					if (cell == null) {
						continue;
					}
					
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						rowString.append(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						rowString.append(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						rowString.append(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BLANK:
						break;
					}
				}
				resultList.add(rowString.toString().substring(1));
			}
		} catch (Exception e) {
			logger.error("Exception : " + e);
		}
		return resultList;
	}

	/**
	 * Creates the appropriate HSSFWorkbook / XSSFWorkbook from the given
	 * InputStream. Your input stream MUST either support mark/reset, or be wrapped
	 * as a {@link PushbackInputStream}!
	 */
	public static Workbook create(InputStream inp) throws Exception {
		// If clearly doesn't do mark/reset, wrap up
		if (!inp.markSupported()) {
			inp = new PushbackInputStream(inp, 8);
		}

		if (POIFSFileSystem.hasPOIFSHeader(inp)) {
			return new HSSFWorkbook(inp);
		}
		if (POIXMLDocument.hasOOXMLHeader(inp)) {
			return new XSSFWorkbook(OPCPackage.open(inp));
		}
		throw new IllegalArgumentException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
	}
}
