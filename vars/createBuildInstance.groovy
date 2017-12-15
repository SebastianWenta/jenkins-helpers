/**
 * Created by sebwent on 14/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*

def call (String process, String processDescription, int build){
    def processInstance = JenkinsProcess.findProcess(process, processDescription)
    int buildId = Integer.parseInt(
        JenkinsBuild.addNew(build,processInstance,"")
                .readSimpleProperty(JenkinsBuild.PK_PK_COLUMN)
                .toString()
    )
    return buildId
}
