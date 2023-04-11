pipeline {
    agent any
    stages {
        stage('Build & test') {
            steps {
                configFileProvider([configFile(fileId: '6877d451-0725-4fa5-921d-166084db20a2', variable: 'MAVEN_SETTINGS')]) {
                    bat "mvn clean install -T 8 -s ${env.MAVEN_SETTINGS}"
                }
            }
        }
    }
}