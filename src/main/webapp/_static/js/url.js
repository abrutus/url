$(document).ready(function() {
    $('#url').focus();
    $('#showLinks').click(function() {
    	$('#links').toggle('slow');
    });
    $('#shortener').submit(function(e){
    	$('#url').hide();
    	e.preventDefault();
    	$.get('/', $('#shortener').serialize(), function(retVal) {
    		$('#url').val(retVal);
    		$('#url').toggle( "highlight" );
    		$('#url').select();
    	}, 'text');
    	return false;
    });
});