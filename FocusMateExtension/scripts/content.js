// `document.querySelector` may return null if the selector doesn't match anything.

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', afterDOMLoaded);
} else {
    afterDOMLoaded();
}

function afterDOMLoaded() {
    setInterval(() => {
        chrome.storage.local.get(["block"]).then((storageData) => {
            console.log(JSON.parse(storageData["block"]));
            if (JSON.parse(storageData["block"]).some(site => site.hostname === window.location.hostname)) {
                document.getElementsByTagName('body')[0].innerHTML = "";
            }
        });
    }, 10000);
}