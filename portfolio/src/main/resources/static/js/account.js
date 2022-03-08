
function settingsMenuToggle(){
    var settingmenu = document.querySelector(".setting-menu");
    settingmenu.classList.toggle('setting-menu-height');
    settingmenu.addEventListener('click',function(){event.stopPropagation()})
}


