function affirm(elem) {
    var id = $(elem).parent().parent().attr('id');
    $.get( '/affirm', {fid: id} )
        .done( function(data) {
            $(elem).hide('slow', function() { $(elem).remove() });
        });
}
function start(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/start', {eid: eid} )
        .done( function(data) {
            $(elem).hide('slow', function() { $(elem).remove() });
        });
}
function pay(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/pay', {eid: eid} )
        .done( function(data) {
            $(elem).hide('slow', function() { $(elem).remove() });
        });
}

function participate(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/participate', {eid: eid} )
        .done( function(data) {
            $(elem).hide('slow', function() { $(elem).remove() });
        });

}
