this.vmware = evaluate 'http://192.168.112.118:8765/api.groovy'.toURL().text
this.vmware.baseURL = "http://192.168.112.118:8765"

println this.vmware.getMachinesJSON()
println this.vmware.getMachinesJSON().get('Win10_2@RunningState').vmxurl