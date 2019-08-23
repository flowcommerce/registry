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
          currentTag  = sh(returnStdout: true, script: 'git describe --tags --dirty').trim()

          def semverMatch = /^(\d+)\.(\d+)\.(\d+)\-?(.*)$/
          def semverParsed = (currentTag =~ semverMatch)[0]
          def major = semverParsed[1].toInteger()
          def minor = semverParsed[2].toInteger()
          def micro = semverParsed[3].toInteger()
          def extra = semverParsed[4]

          if (extra) {
              if (micro >= 99) {
                  minor++
                  micro = 0
              } else {
                  micro++
              }
              sh(script: """git tag -m "Jenkins automated tag $major.$minor.$micro" $major.$minor.$micro""")
          }
          APP_TAG = "$major.$minor.$micro"
        }
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
          sh('helm init --client-only')
          
          sh("helm upgrade --wait --install --debug --namespace production --set deployments.live.version=$APP_TAG registry ./deploy/registry")
          
        }
      }
    }
  }
}