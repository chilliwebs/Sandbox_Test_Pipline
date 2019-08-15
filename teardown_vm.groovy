//--COMMON LIBS--
this.vmgmt = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def teardown_vm(vm, vm_node) {
    machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[vm])
    echo("tearing down")
    Jenkins.instance.getNode(vm_node).getComputer().disconnect(hudson.slaves.OfflineCause.create(hudson.slaves.Messages._RetentionStrategy_Demand_OfflineIdle()))
    this.vmgmt.stop_vm(machine.vmxurl)
}

return this