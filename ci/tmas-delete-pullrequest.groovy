properties([
        parameters([
                string(name: 'USER_PROVIDED_URL', defaultValue: 'http://devhost:8080/pr-XX-TODO/', description: 'Entrez l\'URL que vous souhaitez utiliser delete de tomcat')
        ])
])

def getArtifacts(groupId, user, password) {

    def rawOutput = bat(returnStdout: true, script: 'curl -s -u '+user+':'+password+' -H "Accept: application/json" -X GET "http://devhost:7080/service/rest/v1/search?repository=nexus-pullrequest-repo&group=' + groupId + '"').trim()
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

pipeline {
    agent any
    stages {
        stage('Delete WAR from Nexus and C:/tomcat') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {

                        def url = "${env.USER_PROVIDED_URL}"
                        url = url.replaceFirst(/\/$/, '')
                        echo "urlReplace: ${url}"
                        def name = url.split('/')[-1]

                        if (name == "master" || name == "dev") {
                            error "Operation not allowed: Deleting 'master' and 'dev' environments is prohibited."
                        }

                        def callNexus = getArtifacts('com.pullrequest', JENKINS_USER, JENKINS_PASS)
                        echo "parsedJson2: ${callNexus.parsedJson}"
                        def id
                        callNexus.parsedJson.items.each { artifact ->
                            if (artifact.name == name) {
                                id = artifact.id
                                return
                            }
                        }

                        echo "id: ${id}"
                        def deleteUrl = "http://devhost:7080/service/rest/v1/components/${id}"
                        bat "curl -u ${JENKINS_USER}:${JENKINS_PASS} -X DELETE \"${deleteUrl}\""

                        bat "del /f /q C:\\tomcat\\webapps\\${name}.war"
                        bat "rmdir /s /q C:\\tomcat\\webapps\\${name}"
                    }
                }
            }
        }
    }
}

