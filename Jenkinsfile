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
      steps {
        checkoutWithTags scm

        flowVersion sh, APP_NAME
      }
    }

    stage('Build and push docker image release') {
      when { branch 'master' }
      steps {
        container('docker') {
          script {
            
            docker.withRegistry('', 'docker-hub-credentials') {
              registry = docker.build("$ORG/registry:$APP_TAG", '--network=host -f Dockerfile .')
              registry.push()
            }
            
          }
        }
      }
    }

    stage('Deploy Helm chart') {
      when { branch 'master' }
      steps {
        container('helm') {
          helmDeploy sh, APP_NAME, APP_TAG
        }
      }
    }
  }
}