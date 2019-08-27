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
    stage('Delegate') {
      steps {
        script {
          def tests = [[os:'Windows10'],[os:'Windows10'],[os:'Windows10'],[os:'Windows10'],[os:'Windows10']]

          def tasks = [:]
          tests.eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                lock(label:'master_vmhost_node', quantity: 1, variable:'vmnod') {
                  stage('Setup VM') {
                    node {
                      unstash "setup_vm.groovy"
                      load "setup_vm.groovy"
                    }
                  }
                  stage('VM Execution') {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                      timeout(30) {
                          node(env.vmnod) {
                          unstash "vm_exec.groovy"
                          load "vm_exec.groovy"
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
            tasks.put(test_conf.os+"_"+index, dowork)
          }

          parallel tasks
        }
      }
    }
  }
}