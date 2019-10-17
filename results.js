function msToTime(duration) {
    var milliseconds = parseInt((duration%1000)/100)
        , seconds = parseInt((duration/1000)%60)
        , minutes = parseInt((duration/(1000*60))%60)
        , hours = parseInt((duration/(1000*60*60))%24);
    return (hours > 0 ? hours + " hr " : "") + (minutes > 0 ? minutes + " min " : "") + seconds + " sec";
}

function loadReport() {
    console.log('loading json data...');
    $.getJSON("../api/json", function (data) {
        $('#buildInfo').addClass(data.result);
        $('.fullDisplayName').html(data.fullDisplayName);
        $('.result').html(data.result);
        $('.datetime').html(new Date(data.timestamp).toString());
        $('.duration').html(msToTime(data.duration));

        var parametersAction = data.actions.find(function(e) { return e._class == "hudson.model.ParametersAction"; });
        if(!parametersAction) {
            $(document.body).append($('<div class="parsing_error">ERROR! Failed to find parameters "hudson.model.ParametersAction"</div>'));
            return;
        }

        var test_matrix_parameter = parametersAction.parameters.find(function(e) { return e.name == "test_matrix"; });
        if(!test_matrix_parameter) {
            $(document.body).append($('<div class="parsing_error">ERROR! Failed to find build parameter "test_matrix"</div>'));
            return;
        }

        var lookup = {"Windows10" : "Win10", 
            "Wolfcastle" : ["6fb7c6d8-0"],
            "Baywolf" : ["6fb7c6d8-1","6fb7c6d8-6"],
            "Levi" : ["6fb7c6d8-2","6fb7c6d8-3"],
            "Celine" : ["6fb7c6d8-4","6fb7c6d8-5"],
            "Powder" : ["6fb7c6d8-7"]};
        
        var test_matrix = JSON.parse(test_matrix_parameter.value);
        var tests_tbl = $('<table class="tests_tbl" cellpadding="0px"></table>');
        var tests_tbl_header = $('<tr><th>Operating System</th><th>Device</th><th>Browser</th><th>Test Suite</th><th>Status</th><th>Passes</th><th>Failures</th><th>Skips</th></tr>');
        tests_tbl.append(tests_tbl_header);
        test_matrix.forEach(function(test) {
            var status = "ERROR - missing results xml";
            var passes = 0;
            var failures = 0;
            var skips = 0;
            var test_methods = [];
            var results = data.artifacts.find(function(e) { return lookup[test.device].some(function(port) { return e.fileName.match(new RegExp(lookup[test.os]+'.*'+port+'.*'+test.browser+'.*testng-results\.xml')); }); });
            
            if(results) {
                $.ajax({
                    type: 'GET',
                    async: false,
                    url: results.fileName,
                    dataType: "xml",
                    success: function(xml) {
                        var attrs = $(xml).find('testng-results')[0].attributes;
                        test_methods = $(xml).find('testng-results').find('suite').find('class').find('test-method');
                        passes = parseInt(attrs.passed.value);
                        failures = parseInt(attrs.failed.value);
                        skips = parseInt(attrs.skipped.value);
                    }
                });
                
                if(failures > 0) {
                    status = "FAILED";
                } else if(skips > 0) {
                    status = "SKIPPED";
                } else {
                    status = "PASSED";
                }
            }

            var results_row = $('<tr class="results_row '+status+'"><td>'+test.os+'</td><td>'+test.device+'</td><td>'+test.browser+'</td><td>'+test.suite+'</td><td class="'+status+'">'+status+'</td><td>'+passes+'</td><td>'+failures+'</td><td>'+skips+'</td></tr>')
            var detail_row = $('<tr class="detail_row '+status+'"></tr>');
            var detail_contents = $('<td colspan="8"></td>');
            var detail_content_wrapper = $('<div id="detail"></div>');

            if(results) {
                var detail_tbl = $('<table class="detail_tbl" cellpadding="0px"></table>');
                var detail_tbl_header = $('<tr><th>Test</th><th>Start</th><th>Finish</th><th>Duration</th><th>Status</th></tr>');
                detail_tbl.append(detail_tbl_header);
                for(var i = 0; i < test_methods.length; i++) {
                    var method = test_methods[i];
                    var attrs = method.attributes;
                    var detail_results_row = $('<tr class="detail_results_row '+attrs['status'].value+' '+(attrs['is-config'] && attrs['is-config'].value == "true" ? 'config' : '')+'"><td>'+attrs['name'].value+'</td><td>'+attrs['started-at'].value+'</td><td>'+attrs['finished-at'].value+'</td><td>'+msToTime(attrs['duration-ms'].value)+'</td><td>'+attrs['status'].value+'</td></tr>');
                    var sub_detail_row = $('<tr class="sub_detail_row"></tr>');
                    var sub_detail_contents = $('<td colspan="5"></td>');
                    var sub_detail_content_wrapper = $('<div id="sub_detail"></div>');

                    if($(method).find('reporter-output').length > 0) {
                        var reporter_output = $(method).find('reporter-output').find('line').text();
                        sub_detail_content_wrapper.append($('<div class="reporter-output"></div>').append(reporter_output.replace(/^\s+/gm, '\n').trim()));
                    }

                    if($(method).find('exception').length > 0) {
                        var exception = $(method).find('exception').find('full-stacktrace').text();
                        sub_detail_content_wrapper.append($('<div class="exception"></div>').append(exception.replace(/^\s+/gm, '\n').trim()));
                    }

                    sub_detail_contents.append(sub_detail_content_wrapper);
                    sub_detail_row.append(sub_detail_contents);

                    (function(drr,sdr) {
                        drr.click(function() {
                            if(!$(this).hasClass('selected')) {
                                $('.detail_results_row.selected').removeClass('selected');
                                $('.sub_detail_row.selected').removeClass('selected');
                                drr.addClass('selected');
                                sdr.addClass('selected');
                                $('.sub_detail_row #sub_detail').slideUp("slow");
                                sdr.find('#sub_detail').slideDown("slow");
                            } else {
                                $('.detail_results_row.selected').removeClass('selected');
                                $('.sub_detail_row.selected').removeClass('selected');
                                $('.sub_detail_row #sub_detail').slideUp("slow");
                            }
                        });
                    })(detail_results_row, sub_detail_row);

                    detail_tbl.append(detail_results_row);
                    detail_tbl.append(sub_detail_row);
                }
                detail_content_wrapper.append(detail_tbl);
            }

            var zip = data.artifacts.find(function(e) { return e.fileName.match(new RegExp('test-output_.*'+test.device.toLowerCase()+'.*'+lookup[test.os].toLowerCase()+'.*'+test.browser.toLowerCase()+'.*\.zip')); });
            if(zip) {
                var screenshots = $('<div id="screenshots"></div>');
                var screenshot_wrapper = $('<div id="screenshot_wrapper"></div>');
                screenshots.append(screenshot_wrapper);
                detail_content_wrapper.append(screenshots);
                new JSZip.external.Promise(function (resolve, reject) {
                    JSZipUtils.getBinaryContent(zip.fileName, function(err, data) {
                        if (err) {
                            reject(err);
                        } else {
                            resolve(data);
                        }
                    });
                }).then(function (data) {
                    return JSZip.loadAsync(data);
                })
                .then(function (data) {
                    data.filter(function(p, f) { return f.name.endsWith('.png'); }).forEach(function (file) {
                        file.async("uint8array").then(function (u8) {
                            screenshot_wrapper.append($('<div class="screenshot"><div class="screenshot_name">'+/[^/\\]*$/.exec(file.name)[0]+'</div><img height="300px" src="data:image/png;base64, '+btoa(String.fromCharCode.apply(null, u8))+'" /></div>'));
                        });
                    });
                });
            }

            var logs = [];
            var test_log = data.artifacts.find(function(e) { return lookup[test.device].some(function(port) { return e.fileName.match(new RegExp(lookup[test.os]+'.*'+port+'.*'+test.browser+'.*bosewebautomationtest\.log')); }); });
            if(test_log) {
                $.ajax({
                    type: 'GET',
                    async: false,
                    url: test_log.fileName,
                    success: function(log) {
                        log.split(/\n(?=\d)/).forEach(function(line) {
                            //var arr = line.split(/(?:<=^[^ ]+) /);
                            var arr = [line.substr(0,line.indexOf(' ')), line.substr(line.indexOf(' ')+1)];
                            var dte = arr[0].split('-');
                            logs.push(dte[1]+'/'+dte[2]+'/'+dte[0]+ " " + arr[1]);
                        });
                    }
                });
            }
            var bose_log = data.artifacts.find(function(e) { return lookup[test.device].some(function(port) { return e.fileName.match(new RegExp(lookup[test.os]+'.*'+port+'.*'+test.browser+'.*BoseUpdater\.log')); }); });
            if(bose_log) {
                $.ajax({
                    type: 'GET',
                    async: false,
                    url: bose_log.fileName,
                    success: function(log) {
                        log.split(/\n(?=\d)/).forEach(function(line){
                            //var arr = line.split(/(?:<=^[^ ]+) /);
                            var arr = [line.substr(0,line.indexOf(' ')), line.substr(line.indexOf(' ')+1)];
                            logs.push(arr[0] + " " + arr[1]);
                        });
                    }
                });
            }

            if(logs.length > 0) {
                detail_content_wrapper.append($('<div id="logs"></div>').append(logs.sort().join('\n')));
            }

            detail_contents.append(detail_content_wrapper);
            detail_row.append(detail_contents);
            results_row.click(function() {
                if(!$(this).hasClass('selected')) {
                    $('.results_row.selected').removeClass('selected');
                    $('.detail_row.selected').removeClass('selected');
                    results_row.addClass('selected');
                    detail_row.addClass('selected');
                    $('.detail_row #detail').slideUp("slow");
                    detail_row.find('#detail').slideDown("slow");
                } else {
                    $('.results_row.selected').removeClass('selected');
                    $('.detail_row.selected').removeClass('selected');
                    $('.detail_row #detail').slideUp("slow");
                }
            });
            tests_tbl.append(results_row);
            tests_tbl.append(detail_row);
        });
        
        $(document.body).append($('<div id="results"></div>').append(tests_tbl));
    });
}

$(function() {
    loadReport();
});