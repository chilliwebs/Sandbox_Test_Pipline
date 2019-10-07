//--COMMON LIBS--
this.vmware = evaluate 'http://172.17.0.1:8765/api.groovy'.toURL().text
this.acro = evaluate 'http://172.17.0.1:9876/api.groovy'.toURL().text
//---------------

def configure() {
    echo('configure')
    def machines_json = this.vmware.getMachinesJSON()
    def devices_json = this.acro.getDevicesJSON()

    def os_choices = machines_json.values().collect({val -> val.group}).unique().findAll { it != '(No Group)' }
    def chs_os_es = input(
        id: 'chs_os_es', message: 'What operating systems do you want to test?',
        parameters: os_choices.collect {
            [$class: 'BooleanParameterDefinition', defaultValue: false, name: it]
        }
    )
    if(!chs_os_es instanceof Map) chs_os_es = [chs_os_es:true]
    def os_es = chs_os_es.findAll{ it.value }.collect {
        it.key.toString()
    }

    def browser_choices = ["chrome", "firefox", "internet explorer", "MicrosoftEdge"]
    def chs_browsers = input(
        id: 'chs_browsers', message: 'What browsers do you want to test?',
        parameters: browser_choices.collect {
            [$class: 'BooleanParameterDefinition', defaultValue: false, name: it]
        }
    )
    def browsers = chs_browsers.findAll{ it.value }.collect {
        it.key.toString()
    }

    def device_choices = devices_json.values().collect({val -> 'any:'+val.group}).unique().findAll { it != '(No Group)' } +
        devices_json.values().collect({val -> val.alias}).unique().findAll { !it.trim().isEmpty() } +
        devices_json.keySet().collect({val -> 'port:'+val})
    def chs_devices = input(
        id: 'chs_devices', message: 'What devices do you want to test?',
        parameters: device_choices.collect {
            [$class: 'BooleanParameterDefinition', defaultValue: false, name: it]
        }
    )
    def devices = chs_devices.findAll{ it.value }.collect {
        it.key.toString()
    }

    def test_matrix = []
    os_es.each({ os -> 
        browsers.each({ browser -> 
            devices.each({ device -> 
                test_matrix.add([os:os, browser:browser, device:device, setup: false])
            })
        })
    })
    Collections.shuffle(test_matrix);

    env.test_matrix = test_matrix
}

configure()
