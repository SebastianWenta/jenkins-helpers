/**
 * Created by sebwent on 14/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*

def call (String process, String processDescription, int build){
    def processInstance = JenkinsProcess.findProcess(process, processDescription)
    int buildId = Integer.parseInt(
        JenkinsBuild.addNew(build,processInstance,"")
                .getObjectId()
                .getKey()
                .toString()
    )
    return buildId
}
