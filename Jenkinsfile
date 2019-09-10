pipeline {
  agent none
  stages {
    stage('Configure') {
      agent any
      steps {
        script {
          load 'configure.groovy'
          stash "setup_vm.groovy"
          stash "vm_exec.groovy"
          stash "teardown_vm.groovy"
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

            stash "chromedriver.exe"
            stash "geckodriver.exe"
            stash "IEDriverServer.exe"
            stash "MicrosoftWebDriver.exe"

            dir('target') {
              stash "Sandbox_Test_Pipline-1.0-SNAPSHOT-tests.jar"
              dir('dependency') {
                stash "guava-25.0-jre.jar"
                stash "hamcrest-core-1.3.jar"
                stash "junit-4.11.jar"
                stash "okhttp-3.11.0.jar"
                stash "okio-1.14.0.jar"
                stash "selenium-api-3.141.59.jar"
                stash "selenium-remote-driver-3.141.59.jar"
                stash "selenium-support-3.141.59.jar"

                stash "selenium-chrome-driver-3.141.59.jar"
                stash "selenium-edge-driver-3.141.59.jar"
                stash "selenium-firefox-driver-3.141.59.jar"
                stash "selenium-ie-driver-3.141.59.jar"
                stash "selenium-opera-driver-3.141.59.jar"
                stash "selenium-safari-driver-3.141.59.jar"
              }
            }
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
            [os:'Windows10', browser: 'internet explorer', device: 'Baywolf'],
            [os:'Windows10', browser: 'MicrosoftEdge', device: 'Levi'],
            [os:'Windows10', browser: 'chrome', device: 'Frames'],
            [os:'Windows10', browser: 'firefox', device: 'Frames'],
          ]

          def tasks = [:]
          tests.eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                lock(label:test_conf.device, quantity: 1, variable:'dev') {
                  lock(label:'master_vmhost_node', quantity: 1, variable:'vmnod') {
                    stage('Setup VM') {
                      node {
                        unstash "setup_vm.groovy"
                        load "setup_vm.groovy"
                      }
                    }
                    stage('VM Execution') {
                      catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        timeout(45) {
                          node(env.vmnod) {
                            withEnv(["browser=${test_conf.browser}"]) {
                              unstash "vm_exec.groovy"
                              load "vm_exec.groovy"
                            }
                          }
                        }
                      }
                    }
                    stage('Teardown VM') {
                      node {
                        unstash "teardown_vm.groovy"
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