pipeline {
    agent any
    stages {
        stage("Pulizia Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Clone") {
            steps {
                script {
                    checkout changelog: false, poll: false, scm: scmGit(branches: [[name: '*/main']], userRemoteConfigs: [[credentialsId: 'JenkinsToGitHubIntegration', url: 'https://github.com/CriMaste8/jenkinsfiles.git']])
                }
            }
        }
        stage("Ordine alfabetico") {
            steps {
                script {
                    def files = findFiles glob: 'EsInventati/*.txt'
                    println(files)
                }
            }
        }
    }
}