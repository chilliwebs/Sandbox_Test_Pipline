this.vmware = evaluate 'http://192.168.112.118:8765/api.groovy'.toURL().text
this.vmware.baseURL = "http://192.168.112.118:8765"

this.vmware.sendKeysToVM('Win10_1@RunningState', "left enter")