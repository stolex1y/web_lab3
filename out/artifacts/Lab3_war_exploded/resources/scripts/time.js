window.addEventListener("load", function () {
    let element = document.getElementById("now");
    updateTime(element);
    setInterval(updateTime, 10000, element);
    
}, false);

function updateTime(element) {
    let now = new Date();
    element.innerHTML = now.toLocaleDateString() + " " + now.toLocaleTimeString();
}