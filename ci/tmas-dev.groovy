pipeline {
    agent any
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'dev', credentialsId: 'gitea', url: 'http://localhost:3000/root/test.git'
            }
        }
        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn clean install -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
        stage('Test') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn test -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
        stage('Publish') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def groupId = pom.groupId
                    def artifactId = pom.artifactId
                    def version = pom.version + '-SNAPSHOT'
                    def commit = powershell(script: "git rev-parse --short=20 HEAD", returnStdout: true).trim()
                    def currentTime = new Date().format("yyyyMMddHHmmss")
                    def name = "${artifactId}-${commit.substring(0, 8)}-${version}"
                    def folderToZip = "target/${artifactId}-" + pom.version
                    powershell(script: "Remove-item alias:curl")
                    def resultWar = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file target\\${artifactId}-${pom.version}.war http://localhost:8081/repository/repo-snapshot/${groupId.replace('.', '/')}/${artifactId}/${version}/${artifactId}-${currentTime}-${commit}/${name}.war"
                    powershell(script: "Compress-Archive ${folderToZip} target/${name}.zip")
                    def resultZip = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file target\\${name}.zip http://localhost:8081/repository/repo-snapshot/${groupId.replace('.', '/')}/${artifactId}/${version}/${artifactId}-${currentTime}-${commit}/${name}.zip"

//                    println "Result: ${result}"
//                    if (result.contains("HTTP/1.1 201 Created") || result.contains("HTTP/1.1 200 OK")
//                            || result.contains("HTTP/1.1 400 Repository does not allow updating assets: repo-snapshot")) {
//                        echo "Upload réussi"
//                    } else {
//                        echo "Erreur lors de l'upload"
//                        currentBuild.result = 'FAILURE'
//                        error("Echec de l'upload")
//                    }
                }
            }
        }
    }
}