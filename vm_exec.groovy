
//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

def vm_exec() {
    def machine = this.vmware.getMachinesJSON().get(env.vmid)
    def dev = this.acro.getDevicesJSON().get(env.dev)

    echo "**VM:${machine.name} Device:${dev.alias} NODE:${env.vmnod}**"
    echo("Testing")
    echo('were in!')

    unstash name: "binaries"

    this.acro.setPortInfo(env.dev.split('-')[0], (env.dev.split('-')[1]).toInteger(), 'ON')
    bat "java -cp target/*;target/dependency/* -Dbrowser=\"${env.browser}\" -Dmachine=\"${machine.name}\" -Ddev=\"${dev.alias}\" org.junit.runner.JUnitCore com.chilliwebs.Sandbox_Test_Pipline.SimpleFWUpdateTest"

    echo('done!')
}

vm_exec()