/**
 * Created by sebwent on 15/12/2017.
 */

def call(String excel, int jenkinsBuildPK){
    String runInsertProcedure = "cmd /c groovy insertProcedure.groovy \"${excel}\" ${jenkinsBuildPK}"

    def process = runInsertProcedure.execute();
    process.in.eachLine { line ->
        println line
    }

}

//this.call("C:\\Users\\sebwent\\Desktop\\dane wejsciowe.xlsx", 200)

