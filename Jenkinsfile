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
                stage('Setup VM') {
                  node {
                    machine = load "setup_vm.groovy"
                  }
                }
                stage('VM Execution') {
                  node(machine.name) {
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
            tasks.put(test_conf.os+index, dowork)
          }
          parallel tasks
        }
      }
    }
  }
}