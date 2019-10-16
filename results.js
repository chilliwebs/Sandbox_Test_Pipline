function loadReport() {
    console.log('loading json data...')
    $.getJSON("../api/json", function (data) {
        console.log(data);
    });
}

console.log('loading JQuery...');

//Load jQuery library using plain JavaScript
(function () {
    var newscript = document.createElement('script');
    newscript.type = 'text/javascript';
    newscript.async = true;
    newscript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js';
    newscript.onload = function () {
        loadReport();
    };
    (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(newscript);
})();
