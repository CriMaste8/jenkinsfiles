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
                    def filesJason = findFiles glob: 'file_guida/*.json'
                    def filesEnv = findFiles glob: 'env_dependent_files/**'
                    println(filesEnv)
                    filesJason.each { files ->
                        String ambiente = files.getName().split("\\.")[0]
                        bat "mkdir ${ambiente}1"
                        def json = readJSON file: files.path
                        def destination = "C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\EsercizioComplesso\\" + "${ambiente}1"
                        json.each { jobject ->
                            List<String> paths = jobject.files.findAll { it != "stop" }
                            paths.each { path ->
                                String finale = 'env_dependent_files\\' + ambiente + path
                                def envDepFile = [] //filesEnv.findAll(){it != finale}
                                for (int i = 0; i < filesEnv.size(); i++) {
                                    if (filesEnv[i].getPath() == finale) {
                                        envDepFile.add(filesEnv[i])
                                    }
                                }
                                println(envDepFile)
                                if (envDepFile != []) {
                                    bat """cd ${finale}
                                    copy ${path.substring(13)} ${destination}                                   
                                    """
                                } else {
                                    bat """cd ${path.substring(0, 12)}
                                    copy ${path.substring(13)} ${destination}                                    
                                    """
                                }
                            }
                        }
                        bat """tar.exe -a -c -f ${ambiente}01.zip ${ambiente}1"""
                    }
                }
            }
        }
    }
}
