// core
"use strict";

function replace(data) {
	var error_holder = $('#error');
	if (data) {
		var result = JSON.parse(data);
		var err_msg = result.error;
		if (err_msg) {
			$(error_holder).text(err_msg);
			return;
		}
		var action = $(this).parent();
		$(this).hide('slow', function() { $(this).remove(); });
		$(action).append(data);
	}
	else {
		$(error_holder).text("Internal: Response is empty. Look handlers, Luke.");
	}
}

// actions
function affirm(elem) {
    var id = $(elem).parent().parent().attr('id');
    $.get( '/affirm',       {fid: id} ).done( replace.bind(elem) );
}

function start(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/start',        {eid: eid} ).done( replace.bind(elem) );
}

function pay(elem) {
    var eid = $(elem).parent().parent().attr('id');
    var parts
    $.get( '/pay',          {eid: 100500, parts: 0} ).done( replace.bind(elem) );
}

function participate(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/participate',  {eid: eid} ).done( replace.bind(elem) );
}
