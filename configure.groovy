//--COMMON LIBS--
this.vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.acro_mgmt_common = evaluate dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
this.devices_json = dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_masterconf')  { return readFile('devices.json') }
def restore_snapshot(vmx, snapshot){return this.vm_mgmt_common.restore_snapshot(vmx, snapshot)}
def start_vm(vmx){return this.vm_mgmt_common.start_vm(vmx)}

this.common = evaluate readFile('common.groovy')
def test(){return this.common.test()}
//---------------

def configure() {
    echo('trying to lock a Windows10 resource')
    lock(label:'Windows10', quantity: 1, variable:'vmid') {
        echo('using vm: '+env.vmid)
        def machine = (new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid]
        lock(label:'Levi', quantity: 1, variable:'devid') {
            echo('using device: '+env.devid)
            def device = (new groovy.json.JsonSlurper()).parseText(this.devices_json)[env.devid]
            echo(jenkins.model.Jenkins.getInstance().getComputer(machine.name).getJnlpMac())
            //restore_snapshot(machine.vmxurl, machine.snapshot)
            //start_vm(machine.vmxurl)
            //sudo vmrun getGuestIPAddress /home/vmuser/vmware/W10HS_1/W10HS_1.vmx -wait
            //sudo vmrun -gu vmuser -gp password runScriptInGuest /home/vmuser/vmware/W10HS_1/W10HS_1.vmx "" "powershell -Command \"Invoke-WebRequest http://172.17.0.1:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\""
        }
    }
}
configure()