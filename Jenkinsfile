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
        script {
          APP_TAG = sh(returnStdout: true, script: 'git describe --tags --dirty --always').trim()
        }
      }
    }

    stage('Build and push docker image release') {
      when { branch 'master' }
      steps {
        container('docker') {
          script {
            
            docker.withRegistry('', 'docker-hub-credentials') {
              registry = docker.build("$ORG/registry:$APP_TAG", '-f Dockerfile .')
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
          sh('helm init --client-only')
          
          sh("helm upgrade --wait --install --debug --namespace production --set deployments.live.version=$APP_TAG registry ./deploy/registry")
          
        }
      }
    }
  }
}