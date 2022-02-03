/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USER         = credentials('user-nexus')
            NEXUS_PASSWORD     = credentials('password-nexus')
            
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                        if(env.GIT_BRANCH.contains("feature"))
                        {
                            //case 'Maven':
                                //def ejecucion = load 'maven.groovy'
                                //ejecucion.call()
                                echo "GRADLE********"
                                gradle.call()
                            //break;
                        }

                        if(env.GIT_BRANCH.contains("develop"))
                        {
                            echo "MAVEN********"
                            maven.call()

                        }
                    }
                    sh "echo 'Hola mundo. Job:${JOB_NAME} - Branch:${env.GIT_BRANCH} - Commit:${GIT_COMMIT}'"
                }
                post{
                    success{
                        sh "echo 'Hola mundo success...'"
                        //slackSend color: 'good', message: "[Wladimir Román Tapia] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                    }
                    failure{
                        sh "echo 'Hola mundo failure...'"
                        //slackSend color: 'danger', message: "[Wladimir Román Tapia] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'token-slack'
                    }
                }
            }
        }
    }
}
