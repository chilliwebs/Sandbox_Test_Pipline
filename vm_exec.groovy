//--COMMON LIBS--
// def vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
// def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def vm_exec() {
    echo("Testing")
    echo('were in!')
    sleep(30)
    echo('done!')
}

vm_exec()