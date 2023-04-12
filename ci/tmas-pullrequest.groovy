import groovy.json.JsonOutput
//import java.util.regex.Matcher
//import java.util.regex.Pattern

def notifyCommitStatus(String state, String message) {
    script {
        withCredentials([string(credentialsId: 'gitea', variable: 'GIT_TOKEN')]) {
            def payload = JsonOutput.toJson([
                    state      : state,
                    context    : 'jenkins/pipeline',
                    description: message,
                    target_url : env.BUILD_URL
            ])

            def url = "${env.CHANGE_URL}/status"
            powershell(script: "Remove-item alias:curl")
            def result = bat(returnStdout: true, script: "curl -X POST " + url + " -H \"accept: application/json\" -H \"authorization: token " + GIT_TOKEN + "\" -H \"Content-Type: application/json\" -d '{\"state\":\"" + state + "\",\"context\":\"jenkins/pipeline\",\"description\":\"Build and test succeeded\",\"target_url\":\"" + env.BUILD_URL + "\"}'")

        }
    }
}

//def getJson(String reponse) {
//    echo "Réponse : ${reponse}"
//    def pattern = Pattern.compile("(\\{.*\\})")
//    def matcher = pattern.matcher(reponse)
//    if (matcher.find()) {
//        reponse = matcher.group(1)
//    }
//    echo "Réponse JSON : ${reponse}"
//    def jsonSlurper = new groovy.json.JsonSlurper()
//    return jsonSlurper.parseText(reponse)
//}
//
//def getSonarQubeMetrics(String projectKey, String metricKeys, String token) {
//    def url = "http://devhost:7090/api/measures/component?component=${projectKey}&metricKeys=${metricKeys}"
//    def reponse = bat(returnStdout: true, script: "curl -s -u ${token}: \"${url}\"").trim()
//    return getJson(reponse).component.measures
//}

//def deleteProjetSonar(String projectKey, String token) {
//    def url = "http://devhost:7090/api/projects/delete?project=${projectKey}"
//    def reponse = bat(returnStdout: true, script: "curl -s -u ${token}: -X POST \"${url}\"").trim()
//    echo "Réponse JSON : ${reponse}"
//}
//
//def createCopyProjet(String from, String to, String name, String token) {
//    def url = "http://devhost:7090/api/projects/create_copy?from=${from}&to=${to}&name=${name}"
//    def reponse = bat(returnStdout: true, script: "curl -s -u ${token}: -X POST \"${url}\"").trim()
//    echo "Réponse JSON : ${reponse}"
//}

pipeline {
    agent any
    stages {
        stage('Build & test') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    script {
                        try {
                            notifyCommitStatus("PENDING", 'Build and test in progress')
                            bat "mvn clean install -s ${env.MAVEN_SETTINGS}"
                        } catch (Exception e) {
                            notifyCommitStatus("FAILURE", 'Build and test failed')
                            throw e
                        }
                    }
                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                script {
                    try {
                        notifyCommitStatus("PENDING", 'SonarQube analysis in progress')
                        withSonarQubeEnv(installationName: 'sonar') {
                            bat "mvn sonar:sonar -Dsonar.projectKey=Demo:pullrequest -Dsonar.projectName=demo-pullrequest -Dsonar.qualitygate.wait=true"
                        }

                        notifyCommitStatus("SUCCESS", 'Build and test succeeded')
                    } catch (Exception e) {
                        notifyCommitStatus("FAILURE", 'Build and test failed')
                        throw e
                    }
                }
            }
        }
    }
}