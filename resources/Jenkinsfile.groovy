@Library('jenkins-helper') _

int jenkinsBuildId
String excelFile

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
                }
            }
        }
        stage('Check if Robots are ready'){
            steps{
                script{
                    echo checkIfRobotsReady()
                }
            }
        }
        stage('Get Excel') {
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
                        insertAttachment(excelFile, jenkinsBuildId)
                        isFileReady = true
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
                    String excelHandlerResult = bat(script: 'java -jar excelHandler-all.JAR target.xlsx Umowy.config', returnStdout: true)
                    echo "RESULT: " + excelHandlerResult
                    if (excelHandlerResult.contains("SUCCESS")){
                        isFileVerified = true
                    }
                }
            }
            post {
                always{
                    echo "Verification update to DB"
                }
            }
        }
        stage('Importing data to DB'){
            when {
                expression { isFileVerified == true }
            }
            steps {
                echo "Doing Some Job"
            }
            post {
                always {
                    echo "POST Function ater Importing"
                }
            }
        }
    }
}