/**
 * Created by sebwent on 14/12/2017.
 */
import pl.uniteam.rpa_dbconnect.*
def call (String process, int build){
    int buildId = Integer.parseInt(
        JenkinsBuild.addNew(build,JenkinsProcess.findProcess(process),"")
                .getObjectId()
                .get("PK")
                .toString()
    )
    return buildId
}
