pipeline {
  agent any
  stages {
    stage('Configure') {
      steps {
        script {
          def vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
          def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
          //def machine_groups = (new groovy.json.JsonSlurper()).parseText(machines_json)
          def acro_mgmt_common = evaluate dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_master') { return readFile('common.groovy')  }
          def devices_json = dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_masterconf')  { return readFile('devices.json') }
          //def devices = (new groovy.json.JsonSlurper()).parseText(devices_json)

          echo(machines_json)
          echo(devices_json)

          parallel(
            a: {
              
              echo "This is branch a"
              lock('labels') {
                echo 'in locked resource'
                sleep 5
              }
            },
            b: {
              echo "This is branch b"
              lock('labels') {
                echo 'in locked resource'
                sleep 5
              }
            },
            c: {
              echo "This is branch c"
              lock('labels') {
                echo 'in locked resource'
                sleep 5
              }
            }
          )

          echo "test"
        }
      }
    }
  }
}