//--COMMON LIBS--
this.vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.acro_mgmt_common = evaluate dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
this.devices_json = dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_masterconf')  { return readFile('devices.json') }
def restore_snapshot(vmx, snapshot){return this.vm_mgmt_common.restore_snapshot(vmx, snapshot)}
def start_vm(vmx){return this.vm_mgmt_common.start_vm(vmx)}
def get_vm_ipaddr(vmx){return this.vm_mgmt_common.get_vm_ipaddr(vmx)}
def run_script_on_vm(username, password, vmx, interpreter, script_text, wait=true){return this.vm_mgmt_common.run_script_on_vm(username, password, vmx, interpreter, script_text, wait)}

this.common = evaluate readFile('common.groovy')
def test(){return this.common.test()}
//---------------

def configure() {
    lock(label:'Windows10', quantity: 1, variable:'vmid') {
        def machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid])
        lock(label:'Levi', quantity: 1, variable:'devid') {
            def device = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.devices_json)[env.devid])
            restore_snapshot(machine.vmxurl, machine.snapshot)
            start_vm(machine.vmxurl)
            def vmIP = get_vm_ipaddr(machine.vmxurl)
            def masterIP = InetAddress.localHost.hostAddress
            run_script_on_vm('vmuser', 'password', machine.vmxurl, "", "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
            run_script_on_vm('vmuser', 'password', machine.vmxurl, "", "java -Dhudson.util.ProcessTree.disable=true -jar \"C:\\Users\\vmuser\\Desktop\\agent.jar\" -jnlpUrl http://${masterIP}:8080/computer/W10HS_1/slave-agent.jnlp -secret 9047c64d32bfa07e89bf14245bcb6a1023ee4f6942f4a5b597d6b7a244bf9b3f -workDir \"C:\\User\\vmuser\"", false)
            node(machine.name) {
                echo('were in!')
                bat 'shutdown /s /f /t 0'
            }
        }
    }
}
configure()