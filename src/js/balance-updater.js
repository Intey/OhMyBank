
"use strict";
(function(){
    var b = $('.users').children()[0].getAttribute('data-balance');
    $('.balance').attr({'max': b, 'value': b});
})()

function update(selector) {
    var balance = selector.options[selector.selectedIndex].getAttribute('data-balance');
    var b_e = $('.balance');
    b_e.attr({'max': balance,
              'value': balance});

}
