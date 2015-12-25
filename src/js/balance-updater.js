
"use strict";

function update(opt) {
    var balance = parseInt($(opt).getAttribute('data-balance'));
    $('.balance').value = balance;
}
