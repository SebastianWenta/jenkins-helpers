/**
 * Created by sebwent on 14/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*
import java.security.Timestamp

def call (String process, String processDescription, int build){
    def processInstance = JenkinsProcess.findProcess(process, processDescription)
    def jenkinsBuild = JenkinsBuild.addNew(build,processInstance,Timestamp.toString())
    def pk = jenkinsBuild.getObjectId().getIdSnapshot().get(JenkinsBuild.PK_PK_COLUMN).toString()
    return Integer.parseInt(pk)
}