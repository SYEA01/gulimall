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
        container ('maven') {
          sh 'mvn -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean install'
        }
      }
    }
    stage ('构建镜像 & 推送最新镜像') {
      steps {
        container ('maven') {
          sh 'mvn -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
          sh 'cd $PROJECT_NAME && docker build -f Dockerfile -t $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
          withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
            sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:latest '
          }
        }
      }
    }
    stage('部署到k8s') {
      steps {
        input(id: "deploy-to-dev-$PROJECT_NAME", message: "是否将$PROJECT_NAME 部署到k8s集群中?")
        kubernetesDeploy(configs: "$PROJECT_NAME/deploy/**", enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
      }
    }
    stage('发布版本'){
      when{
        expression{
          return params.PROJECT_VERSION =~ /v*.*/
        }
      }
      steps {
        container ('maven') {
          input(id: 'release-image-with-tag', message: '发布当前版本镜像吗?')
          withCredentials([usernamePassword(credentialsId: "$GITEE_CREDENTIAL_ID", passwordVariable: "GIT_PASSWORD", usernameVariable: "GIT_USERNAME")]) {
            sh 'git config --global user.email "taoao0101@163.com" '
            sh 'git config --global user.name "taoao" '
            sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
            sh "echo https://$GIT_USERNAME:$GIT_PASSWORD@gitee.com/$GITEE_ACCOUNT/gulimall.git"
            sh "git push https://$GIT_USERNAME:$GIT_PASSWORD@gitee.com/$GITEE_ACCOUNT/gulimall.git --tags --ipv4"
          }
          sh "docker tag  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION "
          sh "docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION "
        }
      }
    }
  }

  parameters {
      string(name:'PROJECT_VERSION',defaultValue: 'v0.0Beta',description:'')
      choice(name:'PROJECT_NAME',choices:['gulimall-auth-server', 'gulimall-cart', 'gulimall-coupon', 'gulimall-gateway', 'gulimall-member', 'gulimall-order', 'gulimall-product', 'gulimall-search', 'gulimall-seckill', 'gulimall-third-party', 'gulimall-ware', 'renren-fast'],description:'选择项目进行构建')
  }
}