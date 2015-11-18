function affirm(elem) {
    var id = $(elem).parent().parent().attr('id');
    $.get( '/affirm', {fid: id} )
        .done( function(data) {
            $(elem).remove();
        });
}
function start(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/start', {uid: uid, eid: eid} )
        .done( function(data) {
            $(elem).remove();
        });
}
function pay(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/pay', {uid: uid, eid: eid} )
        .done( function(data) {
            $(elem).remove();
        });
}

function participate(elem) {
    var eid = $(elem).parent().parent().attr('id');
    $.get( '/participate', {uid: uid, eid: eid} )
        .done( function(data) {
            $(elem).remove();
        });

}
