window.addEventListener('load',function(){

    let logoutBtn = document.querySelector('#btn-logout');

    logoutBtn.addEventListener('click', function(){

        $.ajax({
            url : '/admin/logout',
            type : 'POST',
            success : function(response){
                window.location.href = response;
            }
        });
    });
});