properties([pipelineTriggers([githubPush()])])

pipeline {
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '3'))
    timeout(time: 30, unit: 'MINUTES')
  }

  agent {
    kubernetes {
      label 'worker-registry'
      inheritFrom 'default'

      containerTemplates([
        containerTemplate(name: 'helm', image: "lachlanevenson/k8s-helm:v2.12.0", command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'scala', image: "jenkinsxio/builder-scala", command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)
      ])
    }
  }

  environment {
    ORG      = 'flowcommerce'
    APP_NAME = 'registry'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Tests') {
      steps {
        container('scala') {
          withCredentials([usernamePassword(credentialsId: 'jenkins-x-jfrog', usernameVariable: 'ARTIFACTORY_USERNAME', passwordVariable: 'ARTIFACTORY_PASSWORD')]) {
            withCaching(cacheDirectories: ['/root/.sbt/boot', '/root/.ivy2']) {
              withEnv(['JWT_SALT=123456test']) {
                sh('sbt clean test')
              }
            }
          }
        }
      }
    }

    stage('Build and push docker image snapshot') {
      when { branch 'PR-*' }
      steps {
        container('docker') {
          withAWS(role: 'arn:aws:iam::479720515435:role/cicd20181011095611663000000001', roleAccount: '479720515435') {
            script {
              scmVars = git branch: env.BRANCH_NAME
              IMAGE_TAG = "${env.CHANGE_BRANCH.toLowerCase()}-pr-${scmVars.GIT_COMMIT.substring(0,8)}"
              withCaching(cacheDirectories: ['/root/.sbt/boot', '/root/.ivy2']) {
                image = docker.build("$ORG/$APP_NAME:$IMAGE_TAG", '-f Dockerfile .')
              }
            }

            sh(script: ecrLogin(email:false))
            script { image.push() }
          }
        }
      }
    }

    stage('Build and push docker image release') {
      when { branch 'master' }
      steps {
        container('docker') {
          withAWS(role: 'arn:aws:iam::479720515435:role/cicd20181011095611663000000001', roleAccount: '479720515435') {
            script {
              IMAGE_TAG = "${env.BRANCH_NAME.toLowerCase()}-${env.BUILD_NUMBER}"
              withCaching(cacheDirectories: ['/root/.sbt/boot', '/root/.ivy2']) {
                image = docker.build("$ORG/$APP_NAME:$IMAGE_TAG", '-f Dockerfile .')
              }
            }

            sh(script: ecrLogin(email:false))
            script { image.push() }
          }
        }
      }
    }

    stage('Deploy Helm chart') {
      when { branch 'master' }
      steps {
        container('helm') {
          sh('helm init --client-only')
          sh("helm upgrade --wait --namespace production --set deployments.live.version=$IMAGE_TAG -i $APP_NAME ./deploy/$APP_NAME")
        }
      }
    }
  }
}
