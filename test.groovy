this.vmware = evaluate ('http://192.168.112.118:8765/api.groovy'.toURL().text + "\ndef echo(String msg) { println(msg) }")
this.vmware.vm_baseURL = "http://192.168.112.118:8765"

this.acro = evaluate ('http://192.168.112.118:9876/api.groovy'.toURL().text + "\ndef echo(String msg) { println(msg) }")
this.acro.acro_baseURL = "http://192.168.112.118:9876"

def browsers = ["chrome", "firefox", "internet explorer", "MicrosoftEdge"]
def machines = this.vmware.getMachinesJSON()
def devices = this.acro.getDevicesJSON()


println(machines.values().collect({val -> val.group}).unique().findAll { it != '(No Group)' })
println(
    devices.values().collect({val -> 'any:'+val.group}).unique().findAll { it != '(No Group)' } +
    devices.values().collect({val -> val.alias}).unique().findAll { !it.trim().isEmpty() } +
    devices.keySet().collect({val -> 'port:'+val})
)