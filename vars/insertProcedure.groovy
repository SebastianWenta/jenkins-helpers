import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import pl.uniteam.rpa_dbconnect.Attachments
import pl.uniteam.rpa_dbconnect.Data
import pl.uniteam.rpa_dbconnect.Fields
import pl.uniteam.rpa_dbconnect.JenkinsBuild

/**
 * Created by sebwent on 15/12/2017.
 */

String excel = args[0]
String jenkinsBuildPK = args[1]


