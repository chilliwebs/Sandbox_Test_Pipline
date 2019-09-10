//--COMMON LIBS--
this.vmgmt = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
this.acgmt = evaluate dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.devices_json = dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_masterconf')  { return readFile('devices.json') }
//---------------

def setup_vm() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT Device ${env.dev}**"
    echo "**GOT NODE ${env.vmnod}**"
    def machine = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.machines_json)[env.vmid])
    def dev = new HashMap<>((new groovy.json.JsonSlurper()).parseText(this.devices_json)[env.dev])

    

    echo("setting up")
    this.vmgmt.restore_snapshot(machine.vmxurl, machine.snapshot)
    dev.path.split(',').each({p -> echo(p.replaceAll(/[-\.]/,"/"))})
    this.vmgmt.start_vm(machine.vmxurl)

    echo(acgmt.get_ports(env.dev.split('-')[0]))
    //acgmt.set_ports()

    // this enssure the vm is ready
    def vmIP = this.vmgmt.get_vm_ipaddr(machine.vmxurl)
    echo("vm ip addr: ${vmIP}")

    def masterIP = InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(env.vmnod).getJnlpMac()

    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "schtasks /create /tn \"shutdown timeout\" /tr \"shutdown.exe /s /f /t 0\" /sc onidle /i 15")
    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")
    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "",
        "powershell -Command \" Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)
    sleep(5)
    this.vmgmt.vncdo(machine.vmxurl, "key left key enter")
    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
    this.vmgmt.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
        "start cmd /k java -Dhudson.util.ProcessTree.disable=true -jar C:\\Users\\vmuser\\Desktop\\agent.jar -jnlpUrl http://${masterIP}:8080/computer/${env.vmnod}/slave-agent.jnlp -secret ${secret} -workDir C:\\Users\\vmuser", false, true)
    
    return machine
}

setup_vm()