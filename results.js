function loadReport() {
    console.log('loading json data...')
    $.getJSON("../api/json", function (data) {
        console.log(data);
    });
}


//Load jQuery library using plain JavaScript
console.log('loading JQuery...');
var newscript = document.createElement('script');
newscript.type = 'text/javascript';
newscript.async = true;
newscript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js';
newscript.onload = function () {
    loadReport();
};
(document.getElementsByTagName('body')[0]).appendChild(newscript);
