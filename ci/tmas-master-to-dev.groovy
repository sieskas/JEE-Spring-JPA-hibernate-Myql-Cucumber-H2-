pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [[$class: 'LocalBranch', localBranch: 'master']], userRemoteConfigs: [[credentialsId: 'gitea', url: 'http://localhost:3000/root/test.git']]])
            }
        }

        stage('Update dev branch') {
            steps {
                script {
                    // Fetch all branches
                    bat 'git fetch --all'

                    // Checkout dev branch
                    bat 'git checkout dev'

                    // Check for conflicts between master and dev
                    def rebaseOutput = ""
                    try {
                        rebaseOutput = bat(script: "git rebase master", returnStdout: true)
                    } catch (Exception e) {
                        rebaseOutput = e.getMessage()
                    }

                    echo "rebaseOutput: ${rebaseOutput}"

                    // If conflicts are found, abort rebase and create a new branch to resolve them
                    if (rebaseOutput.contains("error: could not apply")) {
                        bat 'git rebase --abort'
                        def newBranchName = "jenkins-dev-to-master-conflict"
                        def branchExists = bat(script: "git show-ref --quiet refs/heads/${newBranchName}", returnStatus: true) == 0
                        if (!branchExists) {
                            bat "git checkout -b ${newBranchName}"
                            echo "Created new branch '${newBranchName}' for conflict resolution"
                            bat 'curl -X POST "http://localhost:3000/api/v1/repos/root/test/pulls" -H "accept: application/json" -H "authorization: token d5e71e6b807957878b0e4c263500dc6ceba1736c" -H "Content-Type: application/json" -d "{\"assignee\": \"root\",\"assignees\": [],\"base\": \"' + newBranchName + '\",\"body\": \"\",\"due_date\": null,\"head\": \"dev\",\\"labels\": [],\"milestone\": null,\"title\": \"' + newBranchName '\"}"\n'
                        } else {
                            bat 'git config --global user.email "root@jenkins.com"'
                            bat 'git config --global user.name "tmas-master-to-dev-bot"'
                            // If no conflicts are found, push changes
                            bat 'git push'
                        }
                    }
                }
            }
        }
    }
}