properties([
        parameters([
                choice(name: 'UPDATE_TYPE', choices: ['Major', 'Minor', 'Hotfix'])
        ])
])

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
                    bat "mvn clean install -T 8 -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
        stage('Update POM version') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def updateType = "${params.UPDATE_TYPE}"
                    echo "UpdateType: ${updateType}"
                    def parts = version.tokenize('.')[1..-1] // Ignore the first fixed part and tokenize the rest
                    switch (updateType) {
                        case 'Major':
                            parts[0] = (parts[0].toInteger() + 1).toString()
                            parts[1] = '0'
                            parts[2] = '0'
                            break
                        case 'Minor':
                            parts[1] = (parts[1].toInteger() + 1).toString()
                            parts[2] = '0'
                            break
                        case 'Hotfix':
                            parts[2] = (parts[2].toInteger() + 1).toString()
                            break
                        default:
                            error("Invalid update type.")
                    }
                    def newVersion = "1.${parts.join('.')}"


                    echo "newVersion: ${newVersion}"
                    bat 'git config --global user.email "root@jenkins.com"'
                    bat 'git config --global user.name "tmas-master-to-dev-bot"'
                    configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                        bat "mvn versions:set -DnewVersion=${newVersion} -s ${env.MAVEN_SETTINGS}"
                    }
                    bat 'git add pom.xml'
                    bat 'git add **/pom.xml'
                    bat "git commit -m \"Update POM version to ${newVersion}\""
                    bat 'git push --set-upstream origin master'
                }

            }
        }
        stage('Create and Push Git Tag') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def newVersion = pom.version
                    def tagName = "v${newVersion}"
                    echo "tagName: ${tagName}"
                    bat "git tag ${tagName}"
                    bat "git push origin ${tagName}"
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn clean install -T 8 -DskipTests -s ${env.MAVEN_SETTINGS}"
                }
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'JENKINS_USER', passwordVariable: 'JENKINS_PASS')]) {
                        def pomWeb = readMavenPom file: 'web/pom.xml'
                        def pom = readMavenPom file: 'pom.xml'
                        def groupId = pom.groupId
                        def artifactId = pom.artifactId
                        def artifactWeb = pomWeb.artifactId
                        def version = pom.version
                        def name = "${artifactWeb}-${version}"
                        def folderToZip = "web/target/${artifactWeb}-" + pom.version
                        powershell(script: "Remove-item alias:curl")
                        def resultWar = bat returnStdout: true, script: "curl -v -u ${JENKINS_USER}:${JENKINS_PASS} --upload-file web\\target\\${artifactWeb}-${pom.version}.war http://devhost:7080/repository/nexus-release-repo/${groupId.replace('.', '/')}/storetraffic/${artifactWeb}/${version}/${name}.war"
                        powershell(script: "Compress-Archive ${folderToZip} web/target/${name}.zip")
                        def resultZip = bat returnStdout: true, script: "curl -v -u ${JENKINS_USER}:${JENKINS_PASS} --upload-file web\\target\\${name}.zip http://devhost:7080/repository/nexus-release-repo/${groupId.replace('.', '/')}/storetraffic/${artifactWeb}/${version}/${name}.zip"
                    }
                }
            }
        }

        stage('Create Gitea Release') {
            steps {
                script
                withCredentials([string(credentialsId: 'gitea', variable: 'GIT_TOKEN')]) {

                    def pomWeb = readMavenPom file: 'web/pom.xml'
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def tagName = "v${version}"
                    def groupId = pom.groupId.replace('.', '/')
                    def artifactId = pom.artifactId
                    def artifactWeb = pomWeb.artifactId
                    def name = "${artifactWeb}-${version}"
                    def warURL = "http://devhost:7080/repository/nexus-release-repo/${groupId}/storetraffic/${artifactWeb}/${version}/${name}.war"
                    def zipURL = "http://devhost:7080/repository/nexus-release-repo/${groupId}/storetraffic/${artifactWeb}/${version}/${name}.zip"

                    echo "name=" + name
                    echo "warURL=" + warURL
                    echo "zipURL=" + zipURL
                    def releaseBody = """
                Download the artifacts:
                - [WAR file](${warURL})
                - [ZIP file](${zipURL})
                """
                    def result = bat returnStdout: true, script: "curl -X POST \"http://devhost:3000/api/v1/repos/SMS-Storetraffic/demo/releases\" -H \"accept: application/json\" -H \"authorization: token "+ GIT_TOKEN +"\" -H \"Content-Type: application/json\" -d \"{\\\"body\\\": \\\"Download the artifacts:\\n- [WAR file](" + warURL + ")\\n- [ZIP file](" + zipURL + ")\\\", \\\"name\\\": \\\"Release " + tagName + "\\\", \\\"tag_name\\\": \\\"" + tagName + "\\\", \\\"target_commitish\\\": \\\"master\\\"}\""
                }
            }

        }
    }
}