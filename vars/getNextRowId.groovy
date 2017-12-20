/**
 * Created by sebwent on 19/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*

def call (int build){
    return RowAttr.getNext(build, new Date())
}

