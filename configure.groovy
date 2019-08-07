import jenkins.model.Jenkins
import org.jenkins.plugins.lockableresources.LockableResource
import org.jenkins.plugins.lockableresources.LockableResourcesManager


//--COMMON LIBS--
this.vm_mgmt_common = evaluate dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.acro_mgmt_common = evaluate dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_master') { return readFile('common.groovy')  }
this.machines_json = dir('/var/jenkins_home/workspace/Vmware_Managment_Pipeline_masterconf')  { return readFile('machines.json') }
this.devices_json = dir('/var/jenkins_home/workspace/crohub_Managment_Pipeline_masterconf')  { return readFile('devices.json') }
this.common = evaluate readFile('common.groovy')
def test(){return this.common.test()}

//---------------

this.machine_groups = (new groovy.json.JsonSlurper()).parseText(this.machines_json)
this.devices = (new groovy.json.JsonSlurper()).parseText(this.devices_json)

echo(machine_groups.toString())
echo(devices.toString())

echo('trying to lock a Windows10 resource')
lock(label:'Windows10', quantity: 1, variable:'vmid') {
    echo('using vm: '+env.vmid)
    lock(label:'Levi', quantity: 1, variable:'devid') {
        echo('using device: '+env.devid)
        echo(this.machine_groups[env.vmid].toString())
        echo(this.devices[env.devid].toString())
    }
}
