// `document.querySelector` may return null if the selector doesn't match anything.
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', afterDOMLoaded);
} else {
    afterDOMLoaded();
}

function afterDOMLoaded() {
    chrome.runtime.sendMessage({event: "pageLoading"});
}

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    if (request.event === 'block') {
        document.getElementsByTagName('body')[0].innerHTML = "";
    }
});