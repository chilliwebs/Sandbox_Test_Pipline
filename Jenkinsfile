pipeline {
  parameters {
    string(defaultValue: '[]', description: 'dont change if you wish to run configure step', name: 'test_matrix')
  }
  options {
    buildDiscarder(logRotator(daysToKeepStr: '30', numToKeepStr: '20', artifactDaysToKeepStr: '30', artifactNumToKeepStr: '20'))
  }
  agent none
  stages {
    stage('Configure') {
      agent any
      steps {
        script {
          if(params.test_matrix == '[]') {
            load 'configure.groovy'
          }
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

            stash name: "binaries", includes: "**/*.jar"
          }
        }
      }
    }
    stage('Delegate') {
      steps {
        script {
          echo('Using Test Matrix: ')
          echo(env.test_matrix)
          def tasks = [:]
          (new groovy.json.JsonSlurper()).parseText(env.test_matrix).collect({tst -> new HashMap<String,Object>(tst)}).eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.device, quantity: 1, variable:'dev') {
                lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                  lock(label:'master_vmhost_node', quantity: 1, variable:'vmnod') {
                    stage('Setup VM') {
                      catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        timeout(15) {
                          node {
                            unstash "scripts"
                            load "setup_vm.groovy"
                            test_conf.setup = true
                          }
                        }
                      }
                    }
                    stage('VM Execution') {
                      if (test_conf.setup == true) {
                        node(env.vmnod) {
                          catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                            timeout(45) {
                              withEnv(["browser=${test_conf.browser}"]) {
                                unstash "scripts"
                                load "vm_exec.groovy"
                              }
                            }
                          }
                          archiveArtifacts artifacts: '**/*.png'
                        }
                      } else {
                        echo "Skipping execution setup failed"
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
            tasks.put(index+"_"+test_conf.os+"_"+test_conf.browser+"_"+test_conf.device, dowork)
          }

          parallel tasks
        }
      }
    }
  }
}