function loadReport() {
    console.log('loading json data...')
    $.getJSON("../api/json", function (data) {
        console.log(data);
    });
}

$(function() {
    loadReport();
});