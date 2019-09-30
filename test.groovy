this.vmware = evaluate ('http://192.168.112.118:8765/api.groovy'.toURL().text + "\ndef echo(String msg) { println(msg) }")
this.vmware.vm_baseURL = "http://192.168.112.118:8765"


//this.vmware.sendKeysToVM('Win10_1@RunningState', "left enter")

println this.vmware.runScriptOnVM("Win10_1%2FWin10_1.vmx",'vmuser', 'password', "", 
        "schtasks /create /tn \"Install BoseUpdater\" /tr \"C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller.exe\" /ru administrator /rp password /rl highest /sc once /st 00:00")