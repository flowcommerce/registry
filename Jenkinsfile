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
        }
      }
      steps {
        script {
          container('helm') {
            helmCommonDiff(['registry'])
          }
        }
      }
    }

    stage("Build, Deploy, SBT test") {
      stages {
        stage('Build and deploy') {
          // when { branch 'main' }
          stages {
            stage('Build and push docker image release') {
              stages {
                stage("parallel image builds") {
                  parallel {
                    stage("Build x86_64/amd64 registry image") {
                      steps {
                        container('kaniko') {
                          script {
                            String semversion = VERSION.printable()
                            imageBuild(
                              orgName: 'flowcommerce',
                              serviceName: 'registry',
                              platform: 'amd64',
                              dockerfilePath: '/Dockerfile',
                              // semver: semversion
                              semver: 'test'
                            )
                          }
                        }
                      }
                    }
                    stage("Build arm64 registry image") {
                      agent {
                        kubernetes {
                          label 'registry-arm64'
                          inheritFrom 'kaniko-slim-arm64'
                        }
                      }
                      steps {
                        container('kaniko') {
                          script {
                            String semversion = VERSION.printable()
                            imageBuild(
                              orgName: 'flowcommerce',
                              serviceName: 'registry',
                              platform: 'arm64',
                              dockerfilePath: '/Dockerfile',
                              // semver: semversion
                              semver: 'test'
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
            stage('manifest tool step for registry docker images') {
              steps {
                container('kaniko') {
                  script {
                    // semver = VERSION.printable()
                    semver = 'test'
                    String templateName = "registry-ARCH:${semver}"
                    String targetName = "registry:${semver}"
                    String orgName = "flowcommerce"
                    String jenkinsAgentArch = "amd64"
                    manifestTool(templateName, targetName, orgName, jenkinsAgentArch)
                  }
                }
              }
            }
            stage('Deploy service') {
              when { branch 'main' }
              stages {
                stage("parallel service Deploy") {
                   parallel {
                      stage('Deploy registry') {
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
                  '''
                  sh 'sbt clean flowLint coverage test scalafmtSbtCheck scalafmtCheck'
                  sh 'sbt coverageAggregate'
                }
                finally {
                  postSbtReport()
                }
              }
            }
          }
        }
      }
    }
  }
}
