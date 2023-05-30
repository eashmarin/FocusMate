// `document.querySelector` may return null if the selector doesn't match anything.
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', afterDOMLoaded);
} else {
    afterDOMLoaded();
}

function afterDOMLoaded() {
    chrome.runtime.sendMessage({event: "pageLoading"});
}

chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    if (request.event === 'block') {
        document.getElementsByTagName('body')[0].innerHTML = "<div class=\"container-fluid h-100 d-flex justify-content-center align-items-center\">\n" +
            "  <div class=\"row\">\n" +
            "   <div class=\"col-12 text-center\">\n" +
            "    <h1 class=\"mt-3 mb-5\">Лимит превышен</h1>\n" +
            "    <p class=\"lead\">Вы достигли лимита времени на данном сайте. Посетите страницу на следующий день.</p>\n" +
            "   </div>\n" +
            "  </div>\n" +
            " </div>";
    }
    console.log("received");

    if (request.event === 'notification popup' && !document.getElementById("focusmate-notification")) {
        showNotification();
        console.log("show notification")
    }

    if (request.event === 'notification hide') {
        const notification = document.getElementById("focusmate-notification");
        if (notification) {
            notification.remove();
        }
    }
});

function loadCSS(url) {
    var link = document.createElement('link');
    link.rel = 'stylesheet';
    link.type = 'text/css';
    link.href = url;

    console.log(link.href);
    document.head.appendChild(link);
}

loadCSS("notification.css");

function showNotification() {
    var container = document.createElement('div');
    container.id = 'focusmate-notification';
    container.className = 'alert alert-light w-25';
    container.setAttribute('role', 'alert');

    container.innerHTML = `    
    <button type="button" class="btn-close" data-dismiss="alert" aria-label="Close"></button>
    <h4 class="alert-heading">Время сделать перерыв!</h4>
    <p>Вы уже провели достаточно времени за компьютером. Рекомендуется сделать небольшой перерыв.</p>
  `;

    document.body.appendChild(container);

    container.querySelector('.btn-close').addEventListener('click', function() {
        container.style.display = 'none';
    });
}