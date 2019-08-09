//--COMMON LIBS--
this.vmgmt = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def teardown_vm() {
    machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid])
    echo("tearing down")

    this.vmgmt.stop_vm(machine.vmxurl)
    Jenkins.instance.getNode(machine.name).getComputer().disconnect(hudson.slaves.OfflineCause.UserCause)
}

teardown_vm()