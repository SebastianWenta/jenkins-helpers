/**
 * Created by sebwent on 14/12/2017.
 */

def call (){

    jobs = Jenkins.instance.getAllItems()
    jobs.findAll{j ->
        j.fullName.contains("ROBOT")
    }.each { j ->
        if (j instanceof com.cloudbees.hudson.plugins.folder.Folder) { return }
        println 'JOB: ' + j.fullName
        numbuilds = j.builds.size()
        if (numbuilds == 0) {
            println '  -> no build'
            return
        }
        lastbuild = j.builds[numbuilds - 1]
        buildNumber = numbuilds - 1
        println '  -> lastbuild: ' + buildNumber + " " + lastbuild.description + ' = ' + lastbuild.result + ', time: ' + lastbuild.timestampString2
    }

}
