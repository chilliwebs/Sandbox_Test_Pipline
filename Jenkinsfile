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
                def machine = [:]
                def node_name = null
                stage('Setup VM') {
                  node {
                    machine = load "setup_vm.groovy"
                  }
                }
                stage('VM Execution') {
                  node('vmnode') {
                    node_name = env.node_name
                    unstash "vm_exec.groovy"
                    load "vm_exec.groovy"
                  }
                }
                stage('Teardown VM') {
                  node {
                    def tvm = load "teardown_vm.groovy"
                    tvm.teardown_vm(node_name)
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