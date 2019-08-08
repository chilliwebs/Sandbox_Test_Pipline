pipeline {
  agent none
  stages {
    stage('Configure') {
      steps {
        script {
          load 'configure.groovy'
        }
      }
    }
    stage('Delegate') {
      steps {
        script {
          load 'configure.groovy'
          lock(label:'Windows10', quantity: 1, variable:'vmid') {
            def machine = [:]
            stage('Setup VM') {
              node {
                machine = load "setup_vm.groovy"
              }
            }
            stage('VM Execution') {
              node(machine.name) {
                load "vm_exec.groovy"
              }
            }
          }
        }
      }
    }
  }
}