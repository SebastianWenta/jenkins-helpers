/**
 * Created by sebwent on 15/12/2017.
 */

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import org.apache.poi.openxml4j.exceptions.InvalidFormatException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

def call(String excel){
    Sheet sheet = getFirstSheetFromExcelWorkbook(new File (excel))
}


static Sheet getFirstSheetFromExcelWorkbook(File excel){
    FileInputStream inputStreamFromExcel = new FileInputStream( excel )
    Workbook workbook = WorkbookFactory.create( inputStreamFromExcel )
    return workbook.getSheetAt(0)
}

static JsonBuilder getJsonRepresentation(Sheet sheet){

    if (logout){
        System.out.println("SHEET NAME: " + sheet);
    }


    int rowIndex = 0;
    for (Iterator<Row> rowsIT = sheet.rowIterator(); (rowsIT.hasNext() && (rowIndex<3));) {
        Row row = rowsIT.next();
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
        secondRowItem.parent = excelItems.findAll {it.row==0 && it.column <=secondRowItem.column}.sort {-it.column}.get(0)

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

        if (excelItems.findAll {it.row==1 && it.column <=thirdRowItem.column}?.sort {-it.column}.size()>0){
            nearestSecondRow = excelItems?.findAll {it.row==1 && it.column <=thirdRowItem.column}?.sort {-it.column}?.get(0)
        }

        if (excelItems.findAll {it.row==0 && it.column <= thirdRowItem.column}.sort {-it.column}.size()>0){
            nearestFirstRow = excelItems.findAll {it.row==0 && it.column <= thirdRowItem.column}.sort {-it.column}.get(0)
        }

        if (logout){
            println "nearestSecondRow: " + (nearestSecondRow?.value?:"null") + " " + (nearestSecondRow?.column?:"0")
            println "nearestFirstRow: " + (nearestFirstRow?.value?:"null") + " " + (nearestFirstRow?.column?:"0")
        }

        if ((nearestSecondRow?.column?:0) >= (nearestFirstRow?.column?:0)){
            thirdRowItem.parent = nearestSecondRow
        } else {
            thirdRowItem.parent = nearestFirstRow
        }

        if (logout){
            println "thirdRowItem.row: " + thirdRowItem.row
            println "thirdRowItem.column: " + thirdRowItem.column
            println "thirdRowItem.value: " + thirdRowItem.value
            println "thirdRowItem.parent: " + thirdRowItem.parent.row + " : " + thirdRowItem.parent.column + " : " + thirdRowItem.parent.value
        }
    }

    def json = JsonOutput.toJson(excelItems)

    if (logout){
        println json
    }

    def jsonBuilder = new JsonBuilder()

    jsonBuilder {
        data excelItems.findAll{it.row==0}.collect {excelItem ->
            [
                    row : excelItem.row,
                    column : excelItem.column,
                    value : excelItem.value,
                    children : excelItems.findAll{(it.row==1 || it.row==2)  && it.parent == excelItem}.collect { secondRow ->
                        [
                                row : secondRow.row,
                                column : secondRow.column,
                                value : secondRow.value,
                                children : excelItems.findAll{it.row==2 && it.parent == secondRow}.collect { thirdRow ->
                                    [
                                            row : thirdRow.row,
                                            column : thirdRow.column,
                                            value : thirdRow.value
                                    ]
                                }
                        ]
                    }

            ]
        }
    }
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
}
