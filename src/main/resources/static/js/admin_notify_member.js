window.addEventListener('click', function(){

    let cancelBtn = document.querySelector('.cancelBtn');

    cancelBtn.addEventListener('click', function(){
        close();
    });

    memberFreeze();
});

function memberFreeze(){

    let freezeBtn = document.querySelector('.freezeBtn');

    freezeBtn.addEventListener('click', function(){

        let memberId = parseInt(document.querySelector('#memberId').value);
        let freezePeriod = parseInt(document.querySelector('#freezePeriod').value);
        let freezeReason = document.querySelector('#freezeReason').value;

        $.ajax({
            url : '/admin/notify/member/freeze',
            type : 'POST',
            contentType : 'application/json',
            data : JSON.stringify({memberId : memberId, freezePeriod : freezePeriod, freezeReason : freezeReason}),
            success : function(){
                close();
                window.opener.location.reload();
            }
        });
    });
}