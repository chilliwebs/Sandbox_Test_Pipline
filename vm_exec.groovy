
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

    unstash "Sandbox_Test_Pipline-1.0-SNAPSHOT-tests.jar"
    unstash "guava-25.0-jre.jar"
    unstash "hamcrest-core-1.3.jar"
    unstash "junit-4.11.jar"
    unstash "okhttp-3.11.0.jar"
    unstash "okio-1.14.0.jar"
    unstash "selenium-api-3.141.59.jar"
    unstash "selenium-remote-driver-3.141.59.jar"
    unstash "selenium-support-3.141.59.jar"

    if(env.browser == "chrome") {
        unstash "chromedriver.exe"
        unstash "selenium-chrome-driver-3.141.59.jar"
    }
    if(env.browser == "firefox") {
        unstash "geckodriver.exe"
        unstash "selenium-firefox-driver-3.141.59.jar"
    }
    if(env.browser == "internet explorer") {
        unstash "IEDriverServer.exe"
        unstash "selenium-ie-driver-3.141.59.jar"
    }
    if(env.browser == "MicrosoftEdge") {
        unstash "MicrosoftWebDriver.exe"
        unstash "selenium-edge-driver-3.141.59.jar"
    }

    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'ON')
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "", 
        "powershell -Command \"Invoke-WebRequest https://downloads.bose.com/ced/boseupdater/windows/BoseUpdaterInstaller_6.0.0.4388.exe -OutFile C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\"")
    this.vmware.runScriptOnVM(machine.vmxurl,'vmuser', 'password', "",
        "powershell -Command \"Start-Process C:\\Users\\vmuser\\Desktop\\BoseUpdaterInstaller_6.0.0.4388.exe\" -verb RunAs", false, true)
    sleep(5)
    this.vmware.sendKeysToVM(machine.vmxurl, "left enter")

    bat "java -cp * -Dbrowser=\"${env.browser}\" org.junit.runner.JUnitCore com.chilliwebs.Sandbox_Test_Pipline.SimpleFWUpdateTest"

    echo('done!')
    //bat 'shutdown /s /f /t 0'
}

vm_exec()