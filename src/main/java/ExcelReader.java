import com.google.api.client.util.ArrayMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelReader {

    public static ArrayMap<String,String> loadMails(String path) throws IOException {
        ArrayMap<String,String> result = new ArrayMap();

        FileInputStream fis=new FileInputStream(new File(path));
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

        //FirstRow
        Iterator<Row> rowIterator = mySheet.iterator();
        Row rowFirst = rowIterator.next();
        Iterator<Cell> cellIteratorFirst = rowFirst.cellIterator();
        App.setSubject(cellIteratorFirst.next().getStringCellValue());
        String unformattedMssg = cellIteratorFirst.next().getStringCellValue();
        String[] percentTab = unformattedMssg.split("%");
        int percentCount = percentTab.length-1;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();
            String email = cellIterator.next().getStringCellValue();
            Object[] arguments = new Object[percentCount];
            int i = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                arguments[i] = cell.getStringCellValue();
                i++;
            }
            for(;i<percentCount;i++)arguments[i] = 0;
            result.add(email,String.format(unformattedMssg,arguments));
        }
        return result;
    }

}
