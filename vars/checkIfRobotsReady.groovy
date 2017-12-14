/**
 * Created by sebwent on 14/12/2017.
 */

def call (){

    String result = "INIT MESSAGE"

    def jobs = Jenkins.instance.getAllItems()
    jobs.findAll{j ->
        j.fullName.contains("Child")
    }.each { j ->
        if (j instanceof com.cloudbees.hudson.plugins.folder.Folder) { return }
        println 'JOB: ' + j.fullName
        result+='\nJOB: ' + j.fullName
        numbuilds = j.builds.size()
        if (numbuilds == 0) {
            println '  -> no build'
            result+='\n  -> no build'
            return
        }
        lastbuild = j.builds[numbuilds - 1]
        buildNumber = numbuilds - 1
        println '  -> lastbuild: ' + buildNumber + " " + lastbuild.description + ' = ' + lastbuild.result + ', time: ' + lastbuild.timestampString2
        result+='\n  -> lastbuild: ' + buildNumber + " " + lastbuild.description + ' = ' + lastbuild.result + ', time: ' + lastbuild.timestampString2
    }

    return result

}
