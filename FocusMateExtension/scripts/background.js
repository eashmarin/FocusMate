const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}

chrome.windows.onFocusChanged.addListener(function (windowId) {
    if (windowId === chrome.windows.WINDOW_ID_NONE) {
        processTabChange(false);
    } else {
        processTabChange(true);
    }
});

function processTabChange(isWindowActive) {
    chrome.tabs.query({"active": true}, function (tabs) {
        if (tabs.length === 0 || tabs[0] == null) {
            return;
        }

        let currentTab = tabs[0];
        let hostname = getHostname(currentTab);

        console.log(hostname)
        console.log("isWindowActive: " + isWindowActive);

        chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey], function (result) {
            let tabTimeObjectString = result[tabTimeObjectKey];
            let lastActiveTabString = result[lastActiveTabKey];

            console.log(tabTimeObjectString);
            console.log(lastActiveTabString);

            let tabTime = {};
            let lastActiveTab = {};
            if (tabTimeObjectString != null) {
                tabTime = JSON.parse(tabTimeObjectString);
            }
            if (lastActiveTabString != null) {
                lastActiveTab = JSON.parse(lastActiveTabString);
            }

            let newLastTab = {};
            if (isWindowActive) {
                newLastTab[lastActiveTabKey] = JSON.stringify({"url": hostname, "lastDateVal": Date.now()});
                chrome.storage.local.set(newLastTab, function () {
                    let newTabTime = {};
                    newTabTime[tabTimeObjectKey] = JSON.stringify(updateTabTime(tabTime, lastActiveTab));
                    chrome.storage.local.set(newTabTime);
                });
            }
        });

    });

    function getHostname(tab) {
        let hostname;
        try {
            hostname = new URL(tab.url).hostname;
        } catch (e) {
            console.log(e);
        }
        return hostname;
    }

    function updateTabTime(tabTime, lastActiveTab) {
        if (lastActiveTab.hasOwnProperty("url") && lastActiveTab.hasOwnProperty("lastDateVal")) {
            let lastUrl = lastActiveTab["url"];
            let passedSeconds = (Date.now() - lastActiveTab["lastDateVal"]) * 0.001;

            if (tabTime.hasOwnProperty(lastUrl)) {
                let lastUrlInfo = tabTime[lastUrl];

                if (lastUrlInfo.hasOwnProperty("trackedSeconds")) {
                    tabTime[lastUrl].trackedSeconds = tabTime[lastUrl].trackedSeconds + passedSeconds;
                } else {
                    tabTime[lastUrl].trackedSeconds = passedSeconds;
                }

                tabTime[lastUrl].lastDateVal = Date.now();
            } else {
                tabTime[lastUrl] = {url: lastUrl, trackedSeconds: passedSeconds, lastDateVal: Date.now()};
            }
        }

        return tabTime;
    }
}