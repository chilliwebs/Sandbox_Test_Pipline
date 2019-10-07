//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

def setup_vm() {
    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    def dev = this.acro.getDevicesJSON().get(env.dev)

    echo "**VM:${machine.name} Device:${dev.alias} NODE:${env.vmnod}**"

    echo("setting up ${machine.vmxurl}")
    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'ON')
    this.vmware.revertVM(machine.vmxurl, machine.snapshot)
    dev.path.split(',').eachWithIndex({ path, idx ->
        echo("usb.autoConnect.device${idx} = path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
        this.vmware.setVMProperty(machine.vmxurl, "usb.autoConnect.device${idx}", "path:${path.replaceAll(/[-\.]/,"/")} autoclean:1")
    })
    this.vmware.startVM(machine.vmxurl)

    def vmIP = this.vmware.getIpAddr(machine.vmxurl)
    echo("vm ip addr: ${vmIP}")

    def installerURL = "https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe"
    def masterIP = "172.17.0.1" //InetAddress.localHost.hostAddress
    def secret = jenkins.model.Jenkins.getInstance().getComputer(env.vmnod).getJnlpMac()

    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"IEX(New-Object Net.WebClient).downloadString('https://raw.githubusercontent.com/chilliwebs/Sandbox_Test_Pipline/${env.BRANCH_NAME}/setup_vm.ps1');" +
        "Invoke-SetupVM -masterIP '${masterIP}' -vmnod '${env.vmnod}' -secret '${secret}' -installerURL '${installerURL}'\"", false, true)

    return machine
}

setup_vm()