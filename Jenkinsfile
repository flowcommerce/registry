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
          VERSION = new flowVersionDev().calculateSemver() //requires checkout
        }
      }
    }

    stage('Commit SemVer tag if necessary') {
      when {
        expression {
          return branch('master') &&
            !(VERSION.isSameAsCurrentRepoTag)
        }
      }
      steps {
        script {
          commitSemver VERSION
        }
      }
    }


    stage('Build and push docker image release') {
      when {
        expression {
          return branch('master')
        }
      }
      steps {
        container('docker') {
          script {
            semver = VERSION.printable()
            docker.withRegistry('https://index.docker.io/v1/', 'jenkins-dockerhub') {
              db = docker.build("$ORG/registry:$semver", '--network=host -f Dockerfile .')
              db.push()
            }
          }
        }
      }
    }

    stage('Deploy Helm chart') {
      when {
        expression {
          return branch('master')
        }
      }
      steps {
        container('helm') {
          script {
            semver = VERSION.printable()
            new helmDeploy().deploy('registry', semver
          }
        }
      }
    }
  }
}