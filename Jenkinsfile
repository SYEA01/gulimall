pipeline {
  agent {
    node {
      label 'maven'
    }
  }

  parameters {
      string(name:'PROJECT_VERSION',defaultValue: 'v0.0Beta',description:'')
      string(name:'PROJECT_NAME',defaultValue: '',description:'')
  }

  environment {
      DOCKER_CREDENTIAL_ID = 'dockerhub-id'
      GITEE_CREDENTIAL_ID = 'gitee-id'
      KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
      REGISTRY = 'docker.io'
      DOCKERHUB_NAMESPACE = '18199445947'
      GITEE_ACCOUNT = 'taoao0101@163.com'
  }

  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'gitee-id', url: 'https://gitee.com/SYEA01/gulimall.git', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME    版本号 $PROJECT_VERSION    将会提交给  $REGISTRY  镜像仓库'
      }
    }
  }

}