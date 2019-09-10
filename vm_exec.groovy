//--COMMON LIBS--
// def vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
// def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def vm_exec() {
    echo "**GOT VM ${env.vmid}**"
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

    bat "java -cp * -Dbrowser=\"${env.browser}\" org.junit.runner.JUnitCore com.chilliwebs.Sandbox_Test_Pipline.SimpleFWUpdateTest"

    echo('done!')
    //bat 'shutdown /s /f /t 0'
}

vm_exec()