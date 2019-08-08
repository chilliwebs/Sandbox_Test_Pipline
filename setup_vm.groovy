//--COMMON LIBS--
def vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def setup_vm() {
    machine = (new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid]
    echo("setting up")
    vm_mgmt_common.restore_snapshot(machine.vmxurl, machine.snapshot)
    vm_mgmt_common.start_vm(machine.vmxurl)

    def masterIP = InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(machine.name).getJnlpMac()

    vm_mgmt_common.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
    vm_mgmt_common.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "java -Dhudson.util.ProcessTree.disable=true -jar \"C:\\Users\\vmuser\\Desktop\\agent.jar\" -jnlpUrl http://${masterIP}:8080/computer/${machine.name}/slave-agent.jnlp -secret ${secret} -workDir \"C:\\User\\vmuser\"", false)
    
    return machine
}

return setup_vm()