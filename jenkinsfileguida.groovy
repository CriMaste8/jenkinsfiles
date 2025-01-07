@Library("PrimaLibrary") _
pipeline {
	agent any
	/*stages {
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
	}*/


	environment {
	        DOCKER_RUN_PARAMS = "-u root"
		PROJECT_URL = "www.ciao.it"
	}

	parameters {
	    string(name: 'TARGET_BRANCH_NAME', defaultValue: '', description: 'The name of the target branch of the pull request', trim: true)
	    string(name: 'SOURCE_BRANCH_NAME', defaultValue: env.SOURCE_BRANCH, description: '(Optional) The name of the source branch of the pull request', trim: true)
	    string(name: 'RELEASE_VERSION', defaultValue: '', description: 'The name of the release in the format (i.e. 2020.12.16)', trim: true)
	    choice(name: 'TARGET_ENVIRONMENT', choices: [env.TARGET_ENV_1, env.TARGET_ENV_2], description: env.TARGET_ENVS_DESCRIPTION)
	}

	stages {
	        stage('Print') {
	            steps {
	                script {
	                    echo PROJECT_URL
	                    echo env.PROJECT_URL
	                    echo SOURCE_BRANCH_NAME
	                    echo env.SOURCE_BRANCH
	                    echo TARGET_ENVIRONMENT
	                }
	            }
	        }
    	} 
}
