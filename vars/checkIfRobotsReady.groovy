/**
 * Created by sebwent on 14/12/2017.
 */

def call (){

    String robotVerificationProcessPrefix = "ROBOT"
    String verificationResult = "SUCCESS"

    def jobs = Jenkins.instance.getAllItems()
    jobs.findAll{j ->
        j.fullName.contains(robotVerificationProcessPrefix)
    }.each { j ->
        if (j instanceof com.cloudbees.hudson.plugins.folder.Folder) { return }
        println 'JOB: ' + j.fullName
        def numbuilds = j.builds.size()
        if (numbuilds == 0) {
            println '  -> no build'
            return
        }
        def lastbuild = j.builds[numbuilds - 1]
        def buildNumber = numbuilds - 1
        println '  -> lastbuild: ' + buildNumber + " " + lastbuild.description + ' = ' + lastbuild.result + ', time: ' + lastbuild.timestampString2
        if (!lastbuild.result.toString().contains("SUCCESS")){
            verificationResult = "FAILURE"
        }
    }

    return verificationResult

}
