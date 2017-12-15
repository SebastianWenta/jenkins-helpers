/**
 * Created by sebwent on 15/12/2017.
 */

def call(String excel, int jenkinsBuildPK){
    "cmd /c groovy insertRows.groovy \"${excel}\" ${jenkinsBuildPK}".execute()
}

//this.call("C:\\Users\\sebwent\\Desktop\\dane wejsciowe.xlsx", 200)

