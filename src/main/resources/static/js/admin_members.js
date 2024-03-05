 window.addEventListener('load', function(){
     
     let checkbox = document.querySelector('#onlyFreezeMembers');
     checkbox.addEventListener('change', () => {
        let form = document.querySelector('form.table-option-list');
        form.submit();
     
    });
 });