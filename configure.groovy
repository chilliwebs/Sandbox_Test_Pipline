def configure() {
    lock(label:'Windows10', quantity: 1, variable:'vmid') {

        def machine = [:]

        //lock(label:'Levi', quantity: 1, variable:'devid') {
        //    def device = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.devices_json)[env.devid])
        stage('setup vm') {
            node("master") {
                def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
                machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid])
                this.vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }

                echo("setting up")
                this.vm_mgmt_common.restore_snapshot(machine.vmxurl, machine.snapshot)
                this.vm_mgmt_common.start_vm(machine.vmxurl)
                def vmIP = this.vm_mgmt_common.get_vm_ipaddr(machine.vmxurl)
                def masterIP = InetAddress.localHost.hostAddress
                def secret = jenkins.model.Jenkins.getInstance().getComputer(machine.name).getJnlpMac()
                this.vm_mgmt_common.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
                this.vm_mgmt_common.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", "java -Dhudson.util.ProcessTree.disable=true -jar \"C:\\Users\\vmuser\\Desktop\\agent.jar\" -jnlpUrl http://${masterIP}:8080/computer/${machine.name}/slave-agent.jnlp -secret ${secret} -workDir \"C:\\User\\vmuser\"", false)
            }
        }
        stage('vm execution') {
            node(machine.name) {
                echo("executing")
                echo('were in!')
                sleep(30)
                bat('shutdown /s /f /t 0')
            }
        }
        //}
    }
}

return this