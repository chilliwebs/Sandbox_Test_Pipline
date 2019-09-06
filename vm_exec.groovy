//--COMMON LIBS--
// def vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
// def machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
//---------------

def vm_exec() {
    bat 'schtasks /create /tn "shutdown timeout" /tr "shutdown.exe /s /f /t 0" /sc onidle /i 15'
    echo "**GOT VM ${env.vmid}**"
    echo "**GOT NODE ${env.vmnod}**"
    echo("Testing")
    echo('were in!')
    unstash "apache-maven-3.6.2-bin.zip"
    unzip zipFile: "apache-maven-3.6.2-bin.zip"
    bat "dir"
    sleep(30)
    echo('done!')
    bat 'shutdown /s /f /t 0'
}

vm_exec()