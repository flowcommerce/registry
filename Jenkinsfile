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
      inheritFrom 'generic'

      containerTemplates([
        containerTemplate(name: 'postgres', image: "flowcommerce/registry-postgresql:latest", alwaysPullImage: true, resourceRequestMemory: '1Gi'),
        containerTemplate(name: 'play', image: "flowdocker/play_builder:latest-java13", alwaysPullImage: true, resourceRequestMemory: '2Gi', command: 'cat', ttyEnabled: true)
      ])
    }
  }

  environment {
    ORG      = 'flowcommerce'
  }

  stages {
    stage('Checkout') {
      steps {
        checkoutWithTags scm

        script {
          VERSION = new flowSemver().calculateSemver() //requires checkout
        }
      }
    }

    stage('Commit SemVer tag') {
      when { branch 'main' }
      steps {
        script {
          new flowSemver().commitSemver(VERSION)
        }
      }
    }

    stage('Display Helm Diff') {
      when {
        allOf {
          not { branch 'main' }
          changeRequest()
          expression {
            return changesCheck.hasChangesInDir('deploy')
          }
        }
      }
      steps {
        script {
          container('helm') {
            new helmDiff().diff('registry')
          }
        }
      }
    }
    stage("All in parallel") {  
      parallel {
        stage('SBT Test') {
          steps {
            container('play') {
              script {
                      sh '''
                        echo "$(date) - waiting for database to start"
                        until pg_isready -h localhost
                        do
                          sleep 10
                        done
                        sbt clean flowLint test
                      '''
                      junit allowEmptyResults: true, testResults: '**/target/test-reports/*.xml'
                    }
            }   
          }
        }
        stage('build and deploy registry') {
          when { branch 'main' }
          stages {
            stage('Build and push docker image release') {
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
            stage('deploy registry') {
              steps {
                script {
                  container('helm') {
                    new helmCommonDeploy().deploy('registry', 'production', VERSION.printable())
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
