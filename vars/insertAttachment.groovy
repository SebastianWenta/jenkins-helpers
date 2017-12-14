/**
 * Created by sebwent on 14/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*
def call (Object sourcePath){
    def Attachments att = new Attachments()
    att.insertAttachment(sourcePath.toString(), test, 15, asd, 1)
}
