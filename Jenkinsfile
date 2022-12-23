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
        containerTemplate(name: 'helm', image: "flowcommerce/k8s-build-helm2:0.0.50", command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'himanshu', image: 'himanshu1018/sbt-docker:latest', resourceRequestCpu: '1', resourceRequestMemory: '2Gi', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'docker', image: 'docker:18', resourceRequestCpu: '1', resourceRequestMemory: '2Gi', command: 'cat', ttyEnabled: true)
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

    stage('Build and push docker image release') {
      when { branch 'main' }
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


    stage('Test') {
      when {
        allOf {
          not { branch 'main' }
          changeRequest()
        }
      }
      steps {
        container('himanshu') {
          script {
            docker.withRegistry('https://index.docker.io/v1/', 'jenkins-dockerhub') {
                docker.image('flowcommerce/registry-postgresql:latest').withRun('--network=host -p 127.0.0.1:6019:5432') { c ->
                    docker.image('himanshu1018/sbt-docker:latest').inside("--network=host") {
                       sh 'sbt ++2.13.10 clean flowLint test doc'
                    }
                }
            }
          }
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

    stage('Deploy Helm chart') {
      when { branch 'main' }
      parallel {
        
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
