this.vmware = evaluate 'http://192.168.112.118:8765/api.groovy'.toURL().text
this.vmware.baseURL = "http://192.168.112.118:8765"

masterIP = "192.168.112.118"
vmnod = ""

// println this.vmware.runScriptOnVM("Win10_1%2FWin10_1.vmx", 'vmuser', 'password', "", 
//         "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")

// println this.vmware.runScriptOnVM("Win10_1%2FWin10_1.vmx", 'vmuser', 'password', "", 
//         "powershell -Command \"Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)

// sleep(10)

this.vmware.sendKeysToVM("Win10_1%2FWin10_1.vmx", "left enter")

// println this.vmware.runScriptOnVM("Win10_1%2FWin10_1.vmx", 'vmuser', 'password', "", 
//         "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")

// println this.vmware.runScriptOnVM("Win10_1%2FWin10_1.vmx", 'vmuser', 'password', "", 
//         "start cmd /k java -Dhudson.util.ProcessTree.disable=true -jar C:\\Users\\vmuser\\Desktop\\agent.jar -jnlpUrl http://${masterIP}:8080/computer/${vmnod}/slave-agent.jnlp -secret ${secret} -workDir C:\\Users\\vmuser", false, true)

// this.vmware.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
//         "schtasks /create /tn \"shutdown timeout\" /tr \"shutdown.exe /s /f /t 0\" /sc onidle /i 30")
//     this.vmware.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
//         "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")
//     this.vmware.run_script_on_vm('vmuser', 'password', machine.vmxurl, "",
//         "powershell -Command \" Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)
//     sleep(10)
//     this.vmware.sendKeysToVM(machine.vmxurl, "left enter")
//     this.vmware.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
//         "powershell -Command \"Invoke-WebRequest http://${masterIP}:8080/jnlpJars/agent.jar -OutFile C:\\Users\\vmuser\\Desktop\\agent.jar\"")
//     this.vmware.run_script_on_vm('vmuser', 'password', machine.vmxurl, "", 
//         "start cmd /k java -Dhudson.util.ProcessTree.disable=true -jar C:\\Users\\vmuser\\Desktop\\agent.jar -jnlpUrl http://${masterIP}:8080/computer/${env.vmnod}/slave-agent.jnlp -secret ${secret} -workDir C:\\Users\\vmuser", false, true)
    