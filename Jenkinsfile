pipeline {
  agent none
  stages {
    stage('Configure') {
      agent any
      steps {
        script {
          load 'configure.groovy'
          stash name: "scripts", includes: "*.groovy"
        }
      }
    }
    stage('Prepare') {
      agent {
        docker {
            image 'maven:3-alpine'
            args '-u root -v maven-repo:/root/.m2'
        }
      }
      stages {
        stage('Build') {
          steps {
            sh "mvn install"
            sh "mvn dependency:copy-dependencies"

            sh "chown 1000:1000 -R target"

            stash name: "binaries", includes: "**/*.jar,*.exe"
          }
        }
      }
    }
    stage('Delegate') {
      steps {
        script {
          def tests = [
            [os:'Windows10', browser: 'chrome', device: 'Baywolf'],
            [os:'Windows10', browser: 'firefox', device: 'Levi'],
            //[os:'Windows10', browser: 'internet explorer', device: 'Baywolf'],
            //[os:'Windows10', browser: 'MicrosoftEdge', device: 'Levi'],
            [os:'Windows10', browser: 'chrome', device: 'Baywolf'],
            [os:'Windows10', browser: 'firefox', device: 'Levi'],
            [os:'Windows10', browser: 'chrome', device: 'Celine'],
            [os:'Windows10', browser: 'firefox', device: 'Celine'],
          ]

          def tasks = [:]
          tests.eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                lock(label:test_conf.device, quantity: 1, variable:'dev') {
                  lock(label:'master_vmhost_node', quantity: 1, variable:'vmnod') {
                    stage('Setup VM') {
                      node {
                        unstash "scripts"
                        load "setup_vm.groovy"
                      }
                    }
                    stage('VM Execution') {
                      catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        timeout(45) {
                          node(env.vmnod) {
                            withEnv(["browser=${test_conf.browser}"]) {
                              unstash "scripts"
                              load "vm_exec.groovy"
                            }
                          }
                        }
                      }
                    }
                    stage('Teardown VM') {
                      node {
                        unstash "scripts"
                        load "teardown_vm.groovy"
                      }
                    }
                  }
                }
              }
            }
            tasks.put(test_conf.os+"_"+index, dowork)
          }

          parallel tasks
        }
      }
    }
  }
}