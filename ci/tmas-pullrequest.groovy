import groovy.json.JsonOutput

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

pipeline {
    agent any
    stages {
        stage('Build & test') {
            steps {
                echo "envID: pr-${env.CHANGE_ID}_${env.GIT_BRANCH}"
                echo "envID: pr-${env.CHANGE_ID}_${env.CHANGE_FORK}"
                echo "envID: pr-${env.CHANGE_ID}_${env.CHANGE_TARGET}"
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

                    } catch (Exception e) {
                        notifyCommitStatus("FAILURE", 'Build and test failed')
                        throw e
                    }
                }
            }
        }
        stage('Publish to Nexus') {
            steps {
                script {
                    try {
                        notifyCommitStatus("PENDING", 'Publish to Nexus')
                        def pomWeb = readMavenPom file: 'web/pom.xml'
                        def pom = readMavenPom file: 'pom.xml'
                        def artifactWeb = pomWeb.artifactId

                        def nameNexus = "pr-${env.CHANGE_ID}_${env.GIT_BRANCH}"

                        withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
                            def resultWar = bat returnStdout: true, script: "curl -v -u ${JENKINS_USER}:${JENKINS_PASS} --upload-file web\\target\\${artifactWeb}-${pom.version}.war http://devhost:7080/repository/nexus-pullrequest-repo/com/pullrequest/${nameNexus}/SNAPSHOT/${nameNexus}-SNAPSHOT.war"
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