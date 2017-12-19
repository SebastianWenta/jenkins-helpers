@Library('jenkins-helper') _

int jenkinsBuildId
String excelFile

boolean isDBReady = false
boolean isFileReady = false
boolean isFileVerified = false
boolean areRobotsReady = false
boolean areRecordsImportedToDB = false

pipeline {
    agent any
    parameters {
        string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
    }
    stages {
        stage('Get Source Code from Repository'){
                steps{
                    git credentialsId: '3e55bd01-346f-40c4-8c41-095e438689c2', url: 'https://github.com/SebastianWenta/jenkins-helpers'
                }
        }
        stage('Initilize DB'){
            steps{
                script{
                    def buildNumber = "${env.BUILD_NUMBER}".toInteger()
                    def procesName = env.JOB_NAME
                    echo "buildNumber: " + buildNumber
                    echo "procesName: " + procesName

                    jenkinsBuildId = createBuildInstance(procesName, "description", buildNumber).toInteger()
                    echo "jenkinsBuildId: " + jenkinsBuildId

                    if (jenkinsBuildId>0){
                        isDBReady = true
                    } else {
                        currentBuild.result = 'FAILURE'
                        currentBuild.setDescription("Problem with DB")
                    }
                }
            }
        }
        stage('Check if Robots are ready'){
            when {
                expression { isDBReady == true }
            }
            steps{
                script{
                    def isWindowsNodeReady = Jenkins.instance.getNode('Windows').toComputer().isOnline()
                    echo "Is Windows node online: " + isWindowsNodeReady
                    if (isWindowsNodeReady){
                        def robotsResult = checkIfRobotsReady()
                        if (robotsResult.contains("SUCCESS")){
                            areRobotsReady = true
                        } else {
                            currentBuild.result = 'FAILURE'
                            currentBuild.setDescription("Robots are not ready")
                        }
                    } else {
                        currentBuild.result = 'FAILURE'
                        currentBuild.setDescription("Robots are not online")
                    }

                }
            }
        }
        stage('Get Excel') {
            when {
                expression { areRobotsReady == true }
            }
            steps {
                script {
                    def inputFile = input message: 'Wybierz plik excel z danymi wejściowymi', parameters: [file (name:'data', description:'Plik powinien zawiera wpisy, które będa danymi wejściowymi do robotów.')]
                    def workspace = env.WORKSPACE
                    excelFile = copyFile(inputFile, workspace)
                }
            }
            post {
                success {
                    echo "Uploading to DB"
                    script{
                        echo "excelFile: " + excelFile
                        echo "jenkinsBuildId: " + jenkinsBuildId
                        def att = insertAttachment(excelFile, jenkinsBuildId)
                        if (att.contains("SUCCESS")){
                            isFileReady = true
                        } else {
                            currentBuild.result = 'FAILURE'
                            currentBuild.setDescription("Attachment not inserted to DB")
                        }

                    }
                }
                failure {
                    echo "There is a problem with FILE"
                }
            }
        }
        stage('Verify Excel'){
            when {
                expression { isFileReady == true }
            }
            steps{
                script{
                    String excelHandlerResult = bat(script: 'java -jar excelHandler-all.JAR verify target.xlsx Umowy.config', returnStdout: true)
                    echo "RESULT: " + excelHandlerResult
                    if (excelHandlerResult.contains("SUCCESS")){
                        isFileVerified = true
                    } else {
                        currentBuild.result = 'FAILURE'
                        currentBuild.setDescription("Excel verification failed")
                    }
                }
            }
            post {
                always{
                    echo "POST Function ater excel Verification"
                }
            }
        }
        stage('Importing data to DB'){
            when {
                expression { isFileVerified == true }
            }
            steps {
                script {
                    String excelHandlerResult = bat(script: "java -jar excelHandler-all.JAR insert \"${excelFile}\" ${jenkinsBuildId}", returnStdout: true)
                    echo "RESULT: " + excelHandlerResult
                    if (excelHandlerResult.contains("SUCCESS")){
                        areRecordsImportedToDB = true
                    } else {
                        currentBuild.result = 'FAILURE'
                        currentBuild.setDescription("Records not imported to DB")
                    }
                }
            }
            post {
                always {
                    echo "POST Function ater Importing"
                }
            }
        }
        stage('Robot'){
            when {
                expression { areRecordsImportedToDB == true }
            }
            steps {
                node('Windows') {
                    echo "Get Robot source code"
                    git credentialsId: '3e55bd01-346f-40c4-8c41-095e438689c2', url: 'https://github.com/SebastianWenta/jenkins-helpers'
                    script{
                        for (int i = 0; i <10; i++){
                            echo "current trial: ${i}"
                        }
                    }
                }
            }
            post {
                always {
                    echo "POST Function ater robot"
                }
            }
        }
    }
}