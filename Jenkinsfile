pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'gitee-id', url: 'https://gitee.com/SYEA01/gulimall.git', branch: 'master', changelog: true, poll: false)
      }
    }
  }
}