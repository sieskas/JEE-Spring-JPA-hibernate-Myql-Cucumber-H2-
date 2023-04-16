def getArtifacts(groupId) {

    def rawOutput = bat(returnStdout: true, script: 'curl -s -u admin:Passw0rd! -H "Accept: application/json" -X GET "http://localhost:7080/service/rest/v1/search?repository=nexus-pullrequest-repo&group=' + groupId + '"').trim()
    echo "Raw output: ${rawOutput}"

    def jsonStartIndex = rawOutput.indexOf('{')
    def jsonResponse = rawOutput.substring(jsonStartIndex)

    def parsedJson = readJSON text: jsonResponse
    echo "parsedJson: ${parsedJson}"

    def artifactList = []

    parsedJson.items.each { artifact ->
        echo "jsonResponse: ${artifact.name}"
        artifactList.add(artifact.name)
    }

    return [parsedJson: parsedJson, artifactList: artifactList]
}

def callNexus
def choice

pipeline {
    agent any
    stages {
        stage('Select') {
            steps {
                script {
                    callNexus = getArtifacts('com.pullrequest')
                    timeout(time: 1, unit: 'MINUTES') {
                        choice = input(id: 'userInput', message: 'Select your choice', parameters: [[$class: 'ChoiceParameterDefinition', choices: callNexus.artifactList, description: '', name: '']])
                    }
                }
            }
        }

        stage('Start Tomcat') {
            steps {
                script {
                    def isRunning = powershell(returnStdout: true, script: "Get-NetTCPConnection -LocalPort 8080 -State Listen").trim()
                    if (isRunning == "") {
                        bat "net start Tomcat8"
                    } else {
                        echo "Tomcat is already running"
                    }
                }
            }
        }
        stage('Download and Deploy WAR') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
                        echo "parsedJson2: ${callNexus.parsedJson}"
                        def downloadUrl
                        callNexus.parsedJson.items.each { artifact ->
                            if (artifact.name == choice) {
                                downloadUrl = artifact.assets[0].downloadUrl
                            }
                        }

                        bat "curl -u ${JENKINS_USER}:${JENKINS_PASS} -O ${downloadUrl}"
                        def warFilename = downloadUrl.split('/')[-1]
                        bat "copy /Y ${warFilename} C:\\tomcat\\webapps\\${choice}.war"
                        echo "url manager tomcat: http://devhost:8080/host-manager/html"
                        echo "webapp : http://devhost:8080/${choice}"
                    }
                }
            }
        }
    }
}
