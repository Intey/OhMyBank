    <script>
    function affirm(b) {
        var id = $(b).parent().attr('id'); 
        $.get( '/affirm', {fid: id} )
            .done( function(data) { 
                alert(data); 
            }); 
    }
    </script>
