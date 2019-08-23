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
          def flowVersion = new flowVersion()
          APP_TAG = flowVersion.make(APP_NAME)
        }
      }
    }

    stage('Build and push docker image release') {
      when { branch 'master' }
      steps {
        container('docker') {
          script {
            
            docker.withRegistry('', 'docker-hub-credentials') {
              build = docker.build("$ORG/registry:$APP_TAG", '--network=host -f Dockerfile .')
              build.push()
            }
            
          }
        }
      }
    }

    stage('Deploy Helm chart') {
      when { branch 'master' }
      steps {
        container('helm') {
          script {
            def helm = new helmDeploy()
            helm.deploy(APP_NAME, APP_TAG)
          }
        }
      }
    }
  }
}
