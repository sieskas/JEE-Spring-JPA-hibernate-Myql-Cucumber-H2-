import groovy.json.JsonOutput

def notifyCommitStatus(String state, String message) {
    script {
        withCredentials([string(credentialsId: 'gitea', variable: 'GIT_TOKEN')]) {
            def payload = JsonOutput.toJson([
                    state: state,
                    context: 'jenkins/pipeline',
                    description: message,
                    target_url: env.BUILD_URL
            ])

            def url = "${env.CHANGE_URL}/status"
            powershell(script: "Remove-item alias:curl")
            def result = bat(returnStdout: true, script: "curl -X POST " + url + " -H \"accept: application/json\" -H \"authorization: token " + GIT_TOKEN + "\" -H \"Content-Type: application/json\" -d '{\"state\":\"" + state + "\",\"context\":\"jenkins/pipeline\",\"description\":\"Build and test succeeded\",\"target_url\":\"" + env.BUILD_URL + "\"}'")

        }
    }
}

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
}