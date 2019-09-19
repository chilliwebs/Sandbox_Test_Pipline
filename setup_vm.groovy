import groovy.json.JsonOutput

//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

def setup_vm() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT Device ${env.dev}**"
    echo "**GOT NODE ${env.vmnod}**"

    echo JsonOutput.toJson(this.vmware.getMachinesJSON())
    echo JsonOutput.toJson(this.vmware.getMachinesJSON().get(env.vmid))
    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    def dev = this.acro.getDevicesJSON().get(env.dev)

    echo("setting up ${machine.vmxurl}")
    this.vmware.revertVM(machine.vmxurl, machine.snapshot)
    dev.path.split(',').eachWithIndex({ path, idx ->
        echo("usb.autoConnect.device${idx} = path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
        this.vmware.setVMProperty(machine.vmxurl, "usb.autoConnect.device${idx}", "path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
    })
    this.vmware.startVM(machine.vmxurl)
    //this.acro.setPortInfo(env.dev.split('-')[0], env.dev.split('-')[1], 'ON')

    // this enssure the vm is ready
    def vmIP = this.vmware.getIpAddr(machine.vmxurl)
    echo("vm ip addr: ${vmIP}")

    def masterIP = InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(env.vmnod).getJnlpMac()

    this.vmware.sendKeysToVM(machine.vmxurl,'vmuser', 'password', "", 
        "schtasks /create /tn \"shutdown timeout\" /tr \"shutdown.exe /s /f /t 0\" /sc onidle /i 30")
    this.vmware.sendKeysToVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")
    this.vmware.sendKeysToVM(machine.vmxurl,'vmuser', 'password', "",
        "powershell -Command \"Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)
    sleep(15)
    this.vmware.sendKeysToVM(machine.vmxurl, "left enter")
    this.vmware.sendKeysToVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
    this.vmware.sendKeysToVM(machine.vmxurl,'vmuser', 'password', "", 
        "start cmd /k java -Dhudson.util.ProcessTree.disable=true -jar C:\\Users\\vmuser\\Desktop\\agent.jar -jnlpUrl http://${masterIP}:8080/computer/${env.vmnod}/slave-agent.jnlp -secret ${secret} -workDir C:\\Users\\vmuser", false, true)
    
    return machine
}

setup_vm()