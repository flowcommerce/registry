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
      }
    }

    stage('Commit SemVer tag if necessary') {
      when {
        expression {
          return branch 'master' &&
            !(new flowVersionDev().calculateSemver(APP_NAME).isTagged())
        }
      }
      steps {
        script {
          new flowVersionDev().calculateSemver(APP_NAME).commitSemver()
        }
      }
    }


    stage('Build and push docker image release') {
      when {
        expression {
        return branch 'master' &&
          new flowVersionDev().calculateSemver(APP_NAME).isTagged()
        }
      }
      steps {
        container('docker') {
          script {
            semver = new flowVersionDev().calculateSemver(APP_NAME).printable()
            docker.withRegistry('https://index.docker.io/v1/', 'jenkins-dockerhub') {
              db = docker.build("$ORG/registry:$semver", '--network=host -f Dockerfile .')
              db.push()
            }
            
          }
        }
      }
    }

    stage('Deploy Helm chart') {
      when { branch 'master' && tagged() }
      steps {
        container('helm') {
          script {
            semver = new flowVersionDev().calculateSemver(APP_NAME)
            new helmDeploy().deploy('registry', semver.printable()
          }
        }
      }
    }
  }
}