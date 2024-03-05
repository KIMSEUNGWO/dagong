window.addEventListener('load', function(){

    roomOptionOpen();
    roomOptionClose();
    deleteRoom();

});

function roomOptionOpen(){
    let optionMores = document.querySelectorAll('.option-more');
    let optionMenus = document.querySelectorAll('.option-menu');

    optionMores.forEach(function(optionMore){
        optionMore.addEventListener('click', function(e){
            if (e.target.classList.contains('option-more')) {
                initMoreList();
                let menu = optionMore.children.namedItem('option-menu');
                menu.classList.remove('disabled');

            }
        });
    });

};

function initMoreList() {
    let list = document.querySelectorAll('.option-menu');
    list.forEach(li => li.classList.add('disabled'));
}

function roomOptionClose(){
    let optionMenus = document.querySelectorAll('.option-menu');
    let container = document.querySelector(':not(.option-more):not(.option-more *):first-of-type');

    optionMenus.forEach(function(optionMenu){
        container.addEventListener('click', function(event){
            let s = event.target;
    
            if(!s.classList.contains('option-more') && !s.classList.contains('option-exit')
                && !optionMenu.classList.contains('disabled')){
                optionMenu.classList.add('disabled');
            }
        });
    })
    
};

function deleteRoom(){
    let optionMenus = document.querySelectorAll('.option-menu');

    optionMenus.forEach(function(optionMenu){
            let exit = optionMenu.children.namedItem('option-exit');
            exit.addEventListener('click', function(){
                optionMenu.classList.add('disabled');
                var roomId = exit.getAttribute('id');
               
                $.ajax({
                    url : '/admin/room/delete',
                    type : 'POST',
                    contentType : 'application/json',
                    data : JSON.stringify({roomId : roomId}),
                    success : function(){
                    window.location.reload();
                    }
                });
            });
        });     
}