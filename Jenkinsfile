properties([pipelineTriggers([githubPush()])])

pipeline {
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
    timeout(time: 30, unit: 'MINUTES')
  }

  agent {
    kubernetes {
      inheritFrom 'kaniko-slim'

      containerTemplates([
        containerTemplate(name: 'postgres', image: "flowcommerce/registry-postgresql:latest-pg15", alwaysPullImage: true, resourceRequestMemory: '1Gi'),
        containerTemplate(name: 'play', image: "flowdocker/play_builder:latest-java17", alwaysPullImage: true, resourceRequestMemory: '2Gi', command: 'cat', ttyEnabled: true)
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
                try {
                  sh '''
                    echo "$(date) - waiting for database to start"
                    until pg_isready -h localhost
                    do
                      sleep 10
                    done
                    sbt clean coverage flowLint test scalafmtSbtCheck scalafmtCheck
                  '''
                  sh 'sbt coverageAggregate'
                }
                finally {
                  postSbtReport()
                }
              }
            }
          }
        }
        stage('build and deploy registry') {
          when { branch 'main' }
          stages {
            stage('Build and push docker image release') {
              steps {
                container('kaniko') {
                  script {
                    semver = VERSION.printable()
                    sh """
                       /kaniko/executor -f `pwd`/Dockerfile -c `pwd` \
                       --snapshot-mode=redo --use-new-run  \
                       --destination ${env.ORG}/registry:$semver
                    """
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
