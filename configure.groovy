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

def get_machine_info(vmid){return (new groovy.json.JsonSlurper()).parseText(this.machines_json)[vmid]}
def get_device_info(devid){return (new groovy.json.JsonSlurper()).parseText(this.devices_json)[devid]}
//---------------

echo('trying to lock a Windows10 resource')
lock(label:'Windows10', quantity: 1, variable:'vmid') {
    echo('using vm: '+env.vmid)
    lock(label:'Levi', quantity: 1, variable:'devid') {
        echo('using device: '+env.devid)
        echo(get_machine_info(env.vmid).toString())
        echo(get_device_info(env.devid).toString())
    }
}
