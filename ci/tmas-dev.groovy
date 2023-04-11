pipeline {
    agent any
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'dev', credentialsId: 'gitea', url: 'http://devhost:3000/SMS-Storetraffic/demo.git'
            }
        }
        stage('Build & Test') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn clean install -T 8 -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
        stage('Publish') {
            steps {
                script {
                    def pomWeb= readMavenPom file: 'web/pom.xml'
                    def pom = readMavenPom file: 'pom.xml'
                    def groupId = pom.groupId
                    def artifactId = pom.artifactId
                    def artifactWeb = pomWeb.artifactId
                    def version = pom.version + '-SNAPSHOT'
                    def commit = powershell(script: "git rev-parse --short=20 HEAD", returnStdout: true).trim()
                    def currentTime = new Date().format("yyyyMMddHHmmss")
                    def name = "${artifactWeb}-${commit.substring(0, 8)}-${version}"
                    def folderToZip = "web/target/${artifactWeb}-" + pom.version
                    powershell(script: "Remove-item alias:curl")
                    def resultWar = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file web\\target\\${artifactWeb}-${pom.version}.war http://192.168.250.88:7080/repository/nexus-snapshot-repo/${groupId.replace('.', '/')}/${artifactId}/${version}/${currentTime}-${commit}/${name}.war"
                    powershell(script: "Compress-Archive ${folderToZip} web/target/${name}.zip")
                    def resultZip = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file web\\target\\${name}.zip http://192.168.250.88:7080/repository/nexus-snapshot-repo/${groupId.replace('.', '/')}/${artifactId}/${version}/${currentTime}-${commit}/${name}.zip"
                }
            }
        }
    }
}