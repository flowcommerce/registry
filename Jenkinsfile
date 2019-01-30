properties([pipelineTriggers([githubPush()])])

pipeline {
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
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

    stage('Build and push docker image release') {
      when { branch 'master' }
      steps {
        container('docker') {
          docker.withRegistry('https://hub.docker.com', 'docker-hub-credentials') {
            script {
              IMAGE_TAG = "${env.BRANCH_NAME.toLowerCase()}-${env.BUILD_NUMBER}"
              withCaching(cacheDirectories: ['/root/.sbt/boot', '/root/.ivy2']) {
                image = docker.build("$ORG/$APP_NAME:$IMAGE_TAG", '-f Dockerfile .')
              }
            }
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
