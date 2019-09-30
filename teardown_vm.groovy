//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

@NonCPS
def teardown_vm() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT Device ${env.dev}**"
    echo "**GOT NODE ${env.vmnod}**"
    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    echo("tearing down")
    Jenkins.instance.getNode(env.vmnod).getComputer().disconnect(hudson.slaves.OfflineCause.create(hudson.slaves.Messages._RetentionStrategy_Demand_OfflineIdle()))
    this.vmware.stopVM(machine.vmxurl)
    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'OFF')
}

teardown_vm()