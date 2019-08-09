pipeline {
  agent none
  stages {
    stage('Configure') {
      agent any
      steps {
        script {
          load 'configure.groovy'
          stash "vm_exec.groovy"
        }
      }
    }
    stage('Delegate') {
      steps {
        script {
          def tests = [[os:'Windows10'],[os:'Windows10'],[os:'Windows10']]

          def tasks = [:]
          tests.eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                lock(label:'vmnode', quantity: 1, variable:'vmnod') {
                  def node_name = null
                  stage('Setup VM') {
                    node {
                      load "setup_vm.groovy"
                    }
                  }
                  stage('VM Execution') {
                    node(env.vmnod) {
                      unstash "vm_exec.groovy"
                      load "vm_exec.groovy"
                    }
                  }
                  stage('Teardown VM') {
                    node {
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