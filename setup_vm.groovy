//--COMMON LIBS--
this.vmgmt = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def setup_vm(vm, node) {
    machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[vm])
    echo("setting up")
    this.vmgmt.restore_snapshot(machine.vmxurl, machine.snapshot)
    this.vmgmt.start_vm(machine.vmxurl)

    // this enssure the vm is ready
    def vmIP = this.vmgmt.get_vm_ipaddr(machine.vmxurl)

    def masterIP = InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(node).getJnlpMac()

    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "java -Dhudson.util.ProcessTree.disable=true -jar \"C:\\Users\\vmuser\\Desktop\\agent.jar\" -jnlpUrl http://${masterIP}:8080/computer/${node}/slave-agent.jnlp -secret ${secret} -workDir \"C:\\Users\\vmuser\"", false)
    
    return machine
}

return this