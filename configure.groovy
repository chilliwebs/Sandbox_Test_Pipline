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
    if(chs_os_es instanceof Boolean) chs_os_es = os_choices.collectEntries { [(it): chs_os_es] }
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
    if(chs_browsers instanceof Boolean) chs_browsers = browser_choices.collectEntries { [(it): chs_browsers] }
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
    if(chs_devices instanceof Boolean) chs_devices = device_choices.collectEntries { [(it): chs_devices] }
    def devices = chs_devices.findAll{ it.value }.collect {
        it.key.toString().replaceAll('any:','').replaceAll('port:','')
    }

    def test_matrix = []
    os_es.each({ os -> 
        browsers.each({ browser -> 
            devices.each({ device -> 
                test_matrix.add([os:os, browser:browser, device:device, setup:false])
            })
        })
    })
    Collections.shuffle(test_matrix);

    echo('Storing Test Matrix: ')
    param.test_matrix = groovy.json.JsonOutput.toJson(test_matrix)
    echo(param.test_matrix)
}

configure()
