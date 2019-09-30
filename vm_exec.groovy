
//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

def vm_exec() {
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT Device ${env.dev}**"
    echo "**GOT NODE ${env.vmnod}**"
    echo("Testing")
    echo('were in!')

    unstash name: "binaries"

    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "",
        "powershell -Command \"Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)
    sleep(10)
    this.vmware.sendKeysToVM(machine.vmxurl, "left enter")

    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'ON')
    bat "java -cp * -Dbrowser=\"${env.browser}\" org.junit.runner.JUnitCore com.chilliwebs.Sandbox_Test_Pipline.SimpleFWUpdateTest"

    echo('done!')
    //bat 'shutdown /s /f /t 0'
}

vm_exec()