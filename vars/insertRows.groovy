/**
 * Created by sebwent on 15/12/2017.
 */

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import pl.uniteam.rpa_dbconnect.Attachments
import pl.uniteam.rpa_dbconnect.Data
import pl.uniteam.rpa_dbconnect.Fields
import pl.uniteam.rpa_dbconnect.JenkinsBuild


def call(String excel, int jenkinsBuildPK){
    Sheet sheet = getFirstSheetFromExcelWorkbook(new File (excel))
    ArrayList<ValueToInsert> fieldsValues = getAllFieldsValues(sheet)
    Attachments att = JenkinsBuild.getJenkinsBuildByID(jenkinsBuildPK).getAttachmentIDRelation()

    int maxRow = fieldsValues.collect {it.row}.max()
    int minRow = fieldsValues.collect {it.row}.min()

    minRow.upto(maxRow){ currentRow ->
        Map<Fields, String> map = [:]

        fieldsValues.findAll {it.row==currentRow}.each { currentField ->
            println "CURRENT FIELD VALUES \n${currentField.column1}\n${currentField.column2}\n${currentField.column3}\n${currentField.value}"
            Fields f = Fields.findField(currentField.column1, currentField.column2, currentField.column3)
            println "CURRENT FIELD \n${f}"
            map.put(f, currentField.value)
        }

        Data.insertValue(map, att, currentRow)
    }

}

//this.call("C:\\Users\\sebwent\\Desktop\\dane wejsciowe.xlsx", 200)


static Sheet getFirstSheetFromExcelWorkbook(File excel){
    FileInputStream inputStreamFromExcel = new FileInputStream( excel )
    Workbook workbook = WorkbookFactory.create( inputStreamFromExcel )
    return workbook.getSheetAt(0)
}

static ArrayList<ValueToInsert> getAllFieldsValues(Sheet sheet){
    boolean logout = false
    ArrayList<ExcelItem> excelItems = new ArrayList<ExcelItem>()
    ArrayList<ValueToInsert> fields = new ArrayList<>()

    if (logout){
        System.out.println("SHEET NAME: " + sheet);
    }

    int rowIndex = 0;
    int columns = 0;
    for (Iterator<Row> rowsIT = sheet.rowIterator(); (rowsIT.hasNext() && (rowIndex<3));) {
        Row row = rowsIT.next();
        columns = row.getPhysicalNumberOfCells()
        int cellIndex = 0;
        for (Iterator<Cell> cellsIT = row.cellIterator(); cellsIT.hasNext(); ) {

            Cell cell = cellsIT.next();
            if (logout){
                println rowIndex + ":" + cellIndex + " - " + (String) cell.getStringCellValue()
            }
            if (cell.getStringCellValue()!=""){
                excelItems.add(new ExcelItem(rowIndex, cellIndex, cell.getStringCellValue()))
            }
            cellIndex++;
        }
        rowIndex++;
    }

    excelItems.findAll {it.row==1}.each { secondRowItem ->

        ArrayList<Integer> availableColumns = new ArrayList()
        excelItems.findAll {it.row==0 && it.column <=secondRowItem.column}.eachWithIndex{ExcelItem e, int i ->
            println "${i}: ${e.column}"
            availableColumns.add(e.column)
        }
        secondRowItem.parent = excelItems.find {it.row==0 && it.column == availableColumns.min()}

        if (logout){
            println "secondRowItem.row: " + secondRowItem.row
            println "secondRowItem.column: " + secondRowItem.column
            println "secondRowItem.value: " + secondRowItem.value
            println "secondRowItem.parent: " + secondRowItem.parent.row + " : " + secondRowItem.parent.column + " : " + secondRowItem.parent.value
        }
    }

    excelItems.findAll {it.row==2}.each { thirdRowItem ->
        ExcelItem nearestSecondRow = null
        ExcelItem nearestFirstRow = null

        if (logout){
            println "\nCURRENT THIRD ROW: " + thirdRowItem.column + " : " + thirdRowItem.column + " : " + thirdRowItem.value
        }

        if (excelItems.findAll {it.row==1 && it.column <=thirdRowItem.column}?.size()>0){

            ArrayList<Integer> availableColumns = new ArrayList()
            excelItems.findAll {it.row==1 && it.column <=thirdRowItem.column}.eachWithIndex{ExcelItem e, int i ->
                println "${i}: ${e.column}"
                availableColumns.add(e.column)
            }
            nearestSecondRow = excelItems?.findAll {it.row==1 && it.column == availableColumns.min()}
        }

        if (excelItems.findAll {it.row==0 && it.column <= thirdRowItem.column}?.size()>0){

            ArrayList<Integer> availableColumns = new ArrayList()
            excelItems.findAll {it.row==0 && it.column <= thirdRowItem.column}.eachWithIndex{ExcelItem e, int i ->
                println "${i}: ${e.column}"
                availableColumns.add(e.column)
            }

            nearestFirstRow = excelItems.findAll {it.row==0 && it.column == availableColumns.min()}
        }

//        if (logout){
//            println "nearestSecondRow: " + (nearestSecondRow?.value?:"null") + " " + (nearestSecondRow?.column?:"0")
//            println "nearestFirstRow: " + (nearestFirstRow?.value?:"null") + " " + (nearestFirstRow?.column?:"0")
//        }
//
//        if ((nearestSecondRow?.column?:0) >= (nearestFirstRow?.column?:0)){
//            thirdRowItem.parent = nearestSecondRow
//        } else {
//            thirdRowItem.parent = nearestFirstRow
//        }
//
//        if (logout){
//            println "thirdRowItem.row: " + thirdRowItem.row
//            println "thirdRowItem.column: " + thirdRowItem.column
//            println "thirdRowItem.value: " + thirdRowItem.value
//            println "thirdRowItem.parent: " + thirdRowItem.parent.row + " : " + thirdRowItem.parent.column + " : " + thirdRowItem.parent.value
//        }
    }

//    rowIndex = 0;
//    for (Iterator<Row> rowsIT = sheet.rowIterator(); rowsIT.hasNext();) {
//        Row row = rowsIT.next();
//        if (rowIndex>2){
//            int cellIndex = 0;
//            for (Iterator<Cell> cellsIT = row.cellIterator(); cellsIT.hasNext(); ) {
//                Cell cell = cellsIT.next();
//                if (logout){
//                    println rowIndex + ":" + cellIndex + " - " + cell.toString()
//                }
//
//                println "OPTIONS: "
//                excelItems.findAll{it.column==cellIndex}.each { it ->
//                    println "\n" + it.toString() + "\n"
//                }
//
//                ExcelItem columnDescription
//
//                ExcelItem row2 = excelItems.findAll{it.column==cellIndex}.find{it.row==2}
//                ExcelItem row1 = excelItems.findAll{it.column==cellIndex}.find{it.row==1}
//                ExcelItem row0 = excelItems.findAll{it.column==cellIndex}.find{it.row==0}
//
//                if (row2!=null){
//                    columnDescription = row2
//                } else if (row1!=null){
//                    columnDescription = row1
//                } else if (row0!=null){
//                    columnDescription = row0
//                }
//
//                println "CHOSEN FOR COLUMN: \n" + columnDescription.toString() + "\n"
//                println "CELL VALUE: " + cell.toString()
//
//                 if (columnDescription.parent != null){
//                     if (columnDescription.parent.row==0){
//                         if (columnDescription.row==2){
//                             fields.add(new ValueToInsert(columnDescription.parent.value, "", columnDescription.value,cell.toString(), cellIndex,rowIndex))
//                         } else {
//                             fields.add(new ValueToInsert(columnDescription.parent.value, columnDescription.value, "",cell.toString(), cellIndex,rowIndex))
//                         }
//                     } else {
//                         fields.add(new ValueToInsert(columnDescription.parent.parent.value, columnDescription.parent.value, columnDescription.value,cell.toString(), cellIndex,rowIndex))
//                     }
//                 } else {
//                     fields.add(new ValueToInsert(columnDescription.value, "", "",cell.toString(), cellIndex,rowIndex))
//                 }
//
//                cellIndex++;
//            }
//        }
//        rowIndex++;
//    }
//
//    fields.each{
//        println it.toString()
//    }

    return fields
}


class ExcelItem{
    int row
    int column
    String value
    ExcelItem parent

    ExcelItem(int r, int c, String v){
        row = r
        column = c
        value = v
    }

    public String toString(){
        "EXCELITEM \n" +
                "row: ${this.row}\n" +
                "column: ${this.column}\n" +
                "value: ${this.value}\n" +
                "parent: ${this.parent}"
    }

}

class ValueToInsert {
    String column1
    String column2
    String column3
    String value
    int column
    int row

    ValueToInsert(String c1, String c2, String c3, String v, int c, int r){
        column1 = c1
        column2 = c2
        column3 = c3
        value = v
        column = c
        row = r
    }

    public String toString(){
        "VALUE TO INSERT \n" +
                "column: ${this.column}\n" +
                "row: ${this.row}\n" +
                "column1: ${this.column1}\n" +
                "column2: ${this.column2}\n" +
                "column3: ${this.column3}\n" +
                "value: ${this.value}\n"
    }
}
