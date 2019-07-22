package hutool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.cell.CellEditor;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import cn.hutool.poi.word.TableUtil;
import cn.hutool.poi.word.Word07Writer;
import cn.hutool.poi.word.WordUtil;

public class POIDemo {
	String path07 = "F:\\中信\\项目资料\\中国民航总局\\文书迁移\\文书文件档案 - 副本.xlsx";
	String path03 = "F:\\中信\\项目资料\\中国民航总局\\文书迁移\\文书文件档案 - 副本.xlsx";
	
	String wordpath07 = "F:\\企业级搜索引擎solr4.7.docx";

	// 不推荐使用 对自定义格式的处理不友好 如日期格式（2012年10月1）
	// @Test
	public void readExcelDemo() {
		ExcelUtil.readBySax(path07, 0, new RowHandler() {
			@Override
			public void handle(int sheetIndex, int rowIndex, List<Object> rowList) {
				System.out.println(rowIndex);
				for (Object cellVal : rowList) {
					System.out.print(cellVal + " ");
				}
				System.out.println();
			}
		});

	}

	// @Test
	public void readExcelDemo2() {
		ExcelReader reader = ExcelUtil.getReader(path07);
		int rowCount = reader.getRowCount();
		int physicalRowCount = reader.getPhysicalRowCount();
		System.out.println("总行数：" + rowCount);
		System.out.println("有记录的行数：" + physicalRowCount);

		List<Map<String, Object>> list = reader.read(0, 1, rowCount - 1);
		int rowNo = 0;
		for (Map<String, Object> map : list) {
			System.out.println("行号：" + rowNo++);
			for (Entry<String, Object> entry : map.entrySet()) {
				System.out.println(entry.getKey() + "：" + entry.getValue());
			}
		}
		 reader.close();
	}

	// @Test
	public void readExcelDemo3() {

		ExcelReader reader = ExcelUtil.getReader(path07);
		reader.setCellEditor(new CellEditor() {
			@Override
			public Object edit(Cell cell, Object value) {
				// int dataFormat = cell.getCellStyle().getDataFormat();
				// String dataFormatStr =
				// cell.getCellStyle().getDataFormatString();
				// CellType cellType = cell.getCellTypeEnum();
				return value;
			}
		});
		List<List<Object>> list = reader.read(0);
		for (List<Object> row : list) {
			for (Object cell : row) {
				System.out.print(cell + " ");
			}
			System.out.println();
		}
		 reader.close();
	}

	//@Test
	public void readExcelDemo4() {
		ExcelReader reader = ExcelUtil.getReader(path07);
		
		Map<String,String> map = new HashMap<>();
		map.put("年度", "col1");
		map.put("保管期限", "col2");
		map.put("数字1", "col3");
		map.put("数字2", "col4");
		map.put("日期1", "col5");
		map.put("日期2", "col6");
		reader.setHeaderAlias(map);
		
		 List<SheetEntity> list = reader.readAll(SheetEntity.class);
		 for(SheetEntity sheet : list){
			 System.out.println(sheet);
		 }
		 reader.close();
	}

	
	// @Test
	public void writeExcel() {
		ExcelReader reader = ExcelUtil.getReader(path07);
		List<Map<String, Object>> list = reader.read(0, 1, 5);
		reader.close();
		ExcelWriter writer = ExcelUtil.getBigWriter();
		try {
			writer.setSheet(0);
			writer.write(list);
			writer.setDestFile(FileUtil.file("F:\\文书文件档案 - 副本1.xlsx"));
			writer.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Test
	public void wordDemo1(){
		ExcelReader reader = ExcelUtil.getReader(path07);
		List<Map<String, Object>> list = reader.read(0, 1, 5);
		reader.close();
		
		Word07Writer writer = WordUtil.getWriter();
		writer.setDestFile(FileUtil.file(wordpath07));
		XWPFDocument doc = writer.getDoc();
		XWPFTable table = TableUtil.createTable(doc);
		int rowNo = 0;
		for(Map<String, Object> map : list){
			XWPFTableRow row = TableUtil.getOrCreateRow(table, rowNo);
			Iterable<?> it = null;
			if(rowNo == 0){
				it = map.keySet();
				TableUtil.writeRow(row, it);
				rowNo++;
				row = TableUtil.getOrCreateRow(table, rowNo);
			}
			it = map.values();
			TableUtil.writeRow(row, it);
			System.out.println(rowNo);
			rowNo++;
		}
		writer.flush();
		writer.close();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
