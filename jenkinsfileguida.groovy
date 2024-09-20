@Library("PrimaLibrary") _
pipeline {
	agent any
	stages {
		stage('Pulizia') {
			steps {
				cleanWs()
			}
		}
		stage('Clonazione') {
			steps {
				checkout changelog: false, poll: false, scm: scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'JenkinsToGitHubIntegration', url: 'https://github.com/CriMaste8/CartellaEsercizio.git']])
			}
		}
		stage('Controllo duplicati files') {
			steps {
				script {
					def filesJason = findFiles glob: 'file_guida/*.json'
					def filesEnv = findFiles glob: 'env_dependent_files/**'
					filesJason.each { files ->
						def json = readJSON file: files.path
						def mapCount = [:]
						json.each { jobject ->
							def paths = jobject.files
							paths.each { file ->
								if (mapCount.containsKey(file)) {
									mapCount[file] = mapCount[file] + 1
								} else {
									mapCount[file] = 1
								}
							}
						}
						def duplici = mapCount.findAll { it.getValue() > 1 }.collect { return it.key }
						if (duplici != []) {
							error "Ci sono file duplicati! ${duplici}"
						}
					}
				}
			}
		}
		stage('Copiare path') {
			steps {
				script {
					copiarePath()
				}
			}
		}
	}
}
