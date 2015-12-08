// core
"use strict";
var errorHideTime = 4000;

function hideParent(self) {
	$(self).parent().hide("slow");
}
function setErrorMessage(msg) {
    $('#errorbox > .message').text(msg);
    $('#errorbox').show("slow");
    setTimeout(function() { $('#errorbox').hide("slow") },
               errorHideTime );
}

/* If error in response, return true; else - false. */
function fillErrorOrNull(result) {
		var err_msg = result.error;
		if (err_msg) { setErrorMessage(err_msg); return true; }
        return false;
}

// should be bind to event element
function replaceAction(action, new_action) {
    var action_container = $(action).parent();
    $(action).hide('slow', function() { $(action).remove(); });
    $(action_container).append(new_action);
}

function handleActionResponse(data) {
	if (data) {
		var result = JSON.parse(data);

        if (! fillErrorOrNull(data)) {
            replaceAction(this, data);
        }

	} else {
        setErrorMessage("Internal: Response is empty. Look handlers, Luke.");
	}
}


// NOTE: should be bind to fee.
// Element Response contains data about event, that need be updated with
// actions, in response.
function handleFeeResponse(data) {
    if (data) {
        var result = JSON.parse(data);
        if (! fillErrorOrNull(result)) {
            hideParent(this);
        }
    } else {
        setErrorMessage("Internal: Response is empty. Look handlers, Luke.");
    }
}

// actions
function affirm(elem) {
    var id = $(elem).parent().parent().attr('id');
    $.get( '/affirm', {fid: id} ).done( handleFeeResponse.bind(elem) );
}

function start(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/start', {eid: eid} ).done( handleActionResponse.bind(elem) );
}

function pay(elem) {
	var event_dom = $(elem).parent().parent();
    var eid = $(event_dom).attr('id');
	var parts = parseInt($(event_dom).children('.parts').text());
    $.get( '/pay', {eid: eid, parts: parts} ).done( handleActionResponse.bind(elem) );
}

function participate(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get('/participate', {eid: eid}).done( handleActionResponse.bind(elem) );
}
