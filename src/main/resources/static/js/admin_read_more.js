window.addEventListener('load', function(){

    let cancelBtn = document.querySelector('.cancelBtn');

    cancelBtn.addEventListener('click', function(){
        close();
    });

    processComplete();
});

function processComplete(){
    
    let completeBtn = document.querySelector('.completeBtn');

    completeBtn.addEventListener('click', function(){
        let notifyId = parseInt(document.querySelector('.notify-number').value);

        $.ajax({
            url : '/admin/notify/status/change',
            type : 'POST',
            contentType : 'application/json',
            data : JSON.stringify({notifyId : notifyId}),
            success : function(){
                close();
                window.opener.location.reload();

            }
        });
    });
};


