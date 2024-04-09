pipeline {
  agent {
    node {
      label 'maven'
    }
  }

  parameters {
      string(name:'PROJECT_VERSION',defaultValue: 'v0.0Beta',description:'')
      choice(name:'PROJECT_NAME',choices:['gulimall-product', 'gulimall-coupon'],description:'选择项目进行构建')
  }

  environment {
      DOCKER_CREDENTIAL_ID = 'dockerhub-id'
      GITEE_CREDENTIAL_ID = 'gitee-id'
      KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
      REGISTRY = 'docker.io'
      DOCKERHUB_NAMESPACE = '18199445947'
      GITEE_ACCOUNT = 'taoao0101@163.com'
      BRANCH_NAME = 'master'

  }

  stages {
    stage('拉取代码') {
      steps {
        git(credentialsId: 'gitee-id', url: 'https://gitee.com/SYEA01/gulimall.git', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在构建 $PROJECT_NAME    版本号 $PROJECT_VERSION    将会提交给  $REGISTRY  镜像仓库'
        sh 'echo 正在完整编译项目'
        container ('maven'){
          sh 'mvn clean install -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml'
        }
      }
    }


  }

}