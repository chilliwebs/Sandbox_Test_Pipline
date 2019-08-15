//--COMMON LIBS--
this.vmgmt = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def teardown_vm() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT NODE ${env.vmnod}**"
    machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid])
    echo("tearing down")
    Jenkins.instance.getNode(env.vmnod).getComputer().disconnect(hudson.slaves.OfflineCause.create(hudson.slaves.Messages._RetentionStrategy_Demand_OfflineIdle()))
    this.vmgmt.stop_vm(machine.vmxurl)
}

teardown_vm()