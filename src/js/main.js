function affirm(b) {
    var id = $(b).parent().parent().attr('id');
    $.get( '/affirm', {fid: id} )
        .done( function(data) {
            alert(data);
        });
}
