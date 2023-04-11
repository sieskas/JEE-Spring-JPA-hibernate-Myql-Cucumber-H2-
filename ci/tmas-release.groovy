properties([
        parameters([
                choice(name: 'UPDATE_TYPE', choices: ['Majeur', 'Mineur', 'Patch'])
        ])
])

pipeline {
    agent any
    stages {
        stage('Git Clone') {
            steps {
                git branch: 'master', credentialsId: 'gitea', url: 'http://localhost:3000/root/test.git'
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
        stage('Update POM version') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def updateType = "${params.UPDATE_TYPE}"
                    echo "updateType: ${updateType}"
                    def parts = version.tokenize('.')
                    switch (updateType) {
                        case 'Majeur':
                            parts[0] = (parts[0].toInteger() + 1).toString()
                            parts[1] = '0'
                            parts[2] = '0'
                            break
                        case 'Mineur':
                            parts[1] = (parts[1].toInteger() + 1).toString()
                            parts[2] = '0'
                            break
                        case 'Patch':
                            parts[2] = (parts[2].toInteger() + 1).toString()
                            break
                        default:
                            error("Invalid update type.")
                    }
                    def newVersion = parts.join('.')

                    echo "todo: ${params.UPDATE_TYPE}"
                    echo "newVersion: ${newVersion}"
                    bat 'git config --global user.email "root@jenkins.com"'
                    bat 'git config --global user.name "tmas-master-to-dev-bot"'
                    bat "mvn versions:set -DnewVersion=${newVersion}"
                    bat 'git add pom.xml'
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
                    bat "mvn clean install -DskipTests -s ${env.MAVEN_SETTINGS}"
                }
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def groupId = pom.groupId
                    def artifactId = pom.artifactId
                    def version = pom.version
                    def name = "${artifactId}-${version}"
                    def folderToZip = "target/${artifactId}-" + pom.version
                    powershell(script: "Remove-item alias:curl")
                    def resultWar = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file target\\${artifactId}-${pom.version}.war http://localhost:8081/repository/repo-release/${groupId.replace('.', '/')}/${artifactId}/${version}/${name}.war"
                    powershell(script: "Compress-Archive ${folderToZip} target/${name}.zip")
                    def resultZip = bat returnStdout: true, script: "curl -v -u admin:Passw0rd! --upload-file target\\${name}.zip http://localhost:8081/repository/repo-release/${groupId.replace('.', '/')}/${artifactId}/${version}/${name}.zip"
                }
            }
        }

        stage('Create Gitea Release') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def tagName = "v${version}"
                    def groupId = pom.groupId.replace('.', '/')
                    def artifactId = pom.artifactId
                    def name = "${artifactId}-${version}"
                    def warURL = "http://localhost:8081/repository/repo-release/${groupId}/${artifactId}/${version}/${name}.war"
                    def zipURL = "http://localhost:8081/repository/repo-release/${groupId}/${artifactId}/${version}/${name}.zip"

                    def releaseBody = """
                Download the artifacts:
                - [WAR file](${warURL})
                - [ZIP file](${zipURL})
                """
                    def result = bat returnStdout: true, script: "curl -X POST \"http://localhost:3000/api/v1/repos/root/test/releases\" -H \"accept: application/json\" -H \"authorization: token d5e71e6b807957878b0e4c263500dc6ceba1736c\" -H \"Content-Type: application/json\" -d \"{\\\"body\\\": \\\"Download the artifacts:\\n- [WAR file](" + warURL + ")\\n- [ZIP file](" + zipURL + ")\\\", \\\"name\\\": \\\"Release " + tagName + "\\\", \\\"tag_name\\\": \\\"" + tagName + "\\\", \\\"target_commitish\\\": \\\"master\\\"}\""

                }

            }
        }
    }
}

