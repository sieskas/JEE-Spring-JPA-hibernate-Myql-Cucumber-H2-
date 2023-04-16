pipeline {
    agent any
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'master', credentialsId: 'gitea', url: 'http://devhost:3000/SMS-Storetraffic/demo.git'
            }
        }
        stage('Build & Test') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn clean install -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
        stage('Publish to Nexus') {
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

                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
                        powershell(script: "Remove-item alias:curl")
                        def resultWar = bat returnStdout: true, script: "curl -v -u ${JENKINS_USER}:${JENKINS_PASS} --upload-file web\\target\\${artifactWeb}-${pom.version}.war http://devhost:7080/repository/nexus-snapshot-repo/${groupId.replace('.', '/')}/${artifactId}/${version}/${currentTime}-${commit}/${name}.war"
                        powershell(script: "Compress-Archive ${folderToZip} web/target/${name}.zip")
                        def resultZip = bat returnStdout: true, script: "curl -v -u ${JENKINS_USER}:${JENKINS_PASS} --upload-file web\\target\\${name}.zip http://devhost:7080/repository/nexus-snapshot-repo/${groupId.replace('.', '/')}/${artifactId}/${version}/${currentTime}-${commit}/${name}.zip"
                    }


                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                bat 'mvn dependency-check:check'
                withSonarQubeEnv(installationName: 'sonar') {
                    bat 'mvn sonar:sonar -Dsonar.projectKey=Demo:master -Dsonar.projectName=demo-master'
                }
            }
        }
        stage('Download and Deploy WAR') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
                        def isRunning = powershell(returnStdout: true, script: "Get-NetTCPConnection -LocalPort 8080 -State Listen").trim()
                        if (isRunning == "") {
                            bat "net start Tomcat8"
                        } else {
                            echo "Tomcat is already running"
                        }

                        def pomWeb = readMavenPom file: 'web/pom.xml'
                        def pom = readMavenPom file: 'pom.xml'
                        def artifactWeb = pomWeb.artifactId

                        def path = "web\\target\\${artifactWeb}-${pom.version}.war"

                        bat "copy /Y ${path} C:\\tomcat\\webapps\\master.war"
                        echo "url manager tomcat: http://devhost:8080/host-manager/html"
                        echo "webapp : http://devhost:8080/master"
                    }
                }
            }
        }
    }
    post {
        always {
            junit '**/surefire-reports/*.xml'
        }
    }
}