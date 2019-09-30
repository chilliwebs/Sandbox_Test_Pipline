//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

@NonCPS
def setup_vm() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT Device ${env.dev}**"
    echo "**GOT NODE ${env.vmnod}**"

    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    def dev = this.acro.getDevicesJSON().get(env.dev)

    echo("setting up ${machine.vmxurl}")
    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'ON')
    this.vmware.revertVM(machine.vmxurl, machine.snapshot)
    dev.path.split(',').eachWithIndex({ path, idx ->
        echo("usb_xhci.autoConnect.device${idx} = path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
        this.vmware.setVMProperty(machine.vmxurl, "usb_xhci.autoConnect.device${idx}", "path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
    })
    this.vmware.startVM(machine.vmxurl)

    def masterIP = "172.17.0.1" //InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(env.vmnod).getJnlpMac()

    //IEX(New-Object Net.WebClient).downloadString('http://<host/ip>:<port>/<file>'); Invoke-SetupVM('${masterIP}','${env.vmnod}','${secret}')
    //env.BRANCH_NAME

    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "schtasks /create /tn \"shutdown timeout\" /tr \"shutdown.exe /s /f /t 0\" /sc onidle /i 30")
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "start cmd /k java -Dhudson.util.ProcessTree.disable=true -Dhudson.slaves.ChannelPinger.pingIntervalSeconds=60 -jar C:\\Users\\vmuser\\Desktop\\agent.jar -jnlpUrl http://${masterIP}:8080/computer/${env.vmnod}/slave-agent.jnlp -secret ${secret} -workDir C:\\Users\\vmuser", false, true)
    
    return machine
}

setup_vm()