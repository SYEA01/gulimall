pipeline {
  agent {
    node {
      label 'maven'
    }
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
      }
    }
    stage ('构建镜像 & 推送镜像') {
      steps {
        container ('maven') {
          sh 'mvn -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
          sh 'docker build -f $PROJECT_NAME/Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
          withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER'
          }
        }
      }
    }

  }

  parameters {
      string(name:'PROJECT_VERSION',defaultValue: 'v0.0Beta',description:'')
      choice(name:'PROJECT_NAME',choices:['gulimall-auth-server', 'gulimall-cart', 'gulimall-coupon', 'gulimall-gateway', 'gulimall-member', 'gulimall-order', 'gulimall-product', 'gulimall-search', 'gulimall-seckill', 'gulimall-third-party', 'gulimall-ware', 'renren-fast'],description:'选择项目进行构建')
  }
}