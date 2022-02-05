import utilities.*

def call(stages, compileTool){
    def listStagesOrder = [
        'gitDiff': 'sGitDiff',
        'nexusDownload': 'sNexusDownload',
        'run': 'sRun',
        'test': 'sTest',
        'gitMergeMaster': 'sGitMergeMaster',
        'gitMergeDevelop': 'sGitMergeDevelop',
        'gitTagMaster': 'sGitTagMaster'
    ]
    def arrayUtils = new array.arrayExtentions();
    def stagesArray = []
        stagesArray = arrayUtils.searchKeyInArray(stages, ";", listStagesOrder)

    if (stagesArray.isEmpty()) {
        echo 'El pipeline se ejecutará completo'
        allStages()
    } else {
        echo 'Stages a ejecutar :' + stages
        stagesArray.each{ stageFunction ->//variable as param
            echo 'Ejecutando ' + stageFunction
            "${stageFunction}"()
        }
    }
}

def allStages(){
    sGitDiff()
    sNexusDownload()
    sRun()
    sTest() 
    sGitMergeMaster()
    sGitMergeDevelop()
    sGitTagMaster()
}

def sGitDiff() {
stage("Paso 7: Git Diff"){
    def branch = env.GIT_BRANCH
      env.STAGE = env.STAGE_NAME
      sh "git checkout main && git pull origin main"
      sh "git checkout ${branch}"
      sh "echo 'DIFERENCIAS ${branch} VS MAIN:'"
      sh "git diff ${branch}..main"
  }

}

def sNexusDownload() {
stage("Paso 8: Descargar Nexus"){
      env.STAGE = env.STAGE_NAME
      sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar" -O'
  }

}

def sRun() {

  stage("Paso 9: Levantar Artefacto Jar"){
      env.STAGE = env.STAGE_NAME
       sh 'java -jar DevOpsUsach2020-0.0.1.jar &'
  }

}

def sTest() {

  stage("Paso 10: Testear Artefacto - Dormir(Esperar 20sg) "){
      env.STAGE = env.STAGE_NAME
      sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
  }

}

def sGitMergeMaster() {

stage("Paso 11: Git Merge Master"){
    env.STAGE = env.STAGE_NAME
    branch = env.GIT_BRANCH
      sh "git checkout main && git pull origin main"
      sh "git merge --no-ff --strategy=ours ${branch}"
      sh "git push origin main"
  }
}

def sGitMergeDevelop() {
stage("Paso 12: Git Merge Develop"){
    env.STAGE = env.STAGE_NAME
    branch = env.GIT_BRANCH
      sh "git checkout develop && git pull origin develop"
      sh "git merge --no-ff --strategy=ours ${branch}"
      sh "git push origin develop"
  }

}

def sGitTagMaster() {

stage("Paso 13: Git Tag Master"){
    env.STAGE = env.STAGE_NAME
    branch = env.GIT_BRANCH
      sh "git checkout main && git pull origin main"
      sh "git tag ${branch.substring(9)}"
      sh "git push origin ${branch.substring(9)}"
  }
}

return this;