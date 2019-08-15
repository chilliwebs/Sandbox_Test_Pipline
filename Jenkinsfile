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
          def tests = [[os:'Windows10'],[os:'Windows10'],[os:'Windows10']]

          def tasks = [:]
          tests.eachWithIndex { test_conf, index ->
            def dowork = {
              lock(label:test_conf.os, quantity: 1, variable:'vmid') {
                def vm = env.vmid
                echo "**GOT VM ${vm}**"
                lock(label:'master_vmhost_node', quantity: 1, variable:'vmnod') {
                  def node = env.vmnod
                  echo "**GOT NODE ${node}**"
                  
                  def node_name = null
                  stage('Setup VM') {
                    node {
                      echo "**GOT VM ${vm} vs ${env.vmid}**"
                      echo "**GOT NODE ${node} vs ${env.vmnod}**"
                      unstash "setup_vm.groovy"
                      def stp = load "setup_vm.groovy"
                      stp.setup_vm(vm, node)
                    }
                  }
                  stage('VM Execution') {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                      node(node) {
                        unstash "vm_exec.groovy"
                        load "vm_exec.groovy"
                      }
                    }
                  }
                  stage('Teardown VM') {
                    node {
                      echo "**GOT VM ${vm} vs ${env.vmid}**"
                      echo "**GOT NODE ${node} vs ${env.vmnod}**"
                      unstash "teardown_vm.groovy"
                      def trd = load "teardown_vm.groovy"
                      trd.teardown_vm(vm, node)
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