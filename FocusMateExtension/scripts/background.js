const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}
const limitsKey = "limits";

let browserFocused = true;
setInterval(checkBrowserFocus, 3000);

function checkBrowserFocus() {
    chrome.windows.getCurrent(function (browser) {
        if (browserFocused === false && browser.focused === true) {
            chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then((storageData) => {
                let lastActiveTab = getLastActiveTab(storageData);
                let newLastTab = {};
                console.log(lastActiveTab);
                newLastTab[lastActiveTabKey] = JSON.stringify({"url": lastActiveTab.url, "lastDateVal": Date.now()});
                chrome.storage.local.set(newLastTab);
            });
        }

        if (browserFocused && browser.focused === false) {
            chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then((storageData) => {
                console.log(getLastActiveTab(storageData).url);
                updateLocalStorageData(getLastActiveTab(storageData).url, storageData);
            });
        }

        browserFocused = browser.focused;
        console.log(browserFocused);
    });
}

setInterval(checkLimit, 10000);

chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    if (request.event === 'pageLoading') {
        checkLimit();
    }
});

function checkLimit() {
    chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey, limitsKey]).then((storageData) => {
        const currentHostname = getLastActiveTab(storageData).url;

        const jsonLimits = JSON.parse(storageData[limitsKey]);
        const currentLimit = jsonLimits.filter(item => item.hostname === currentHostname)[0];
        if (currentLimit === undefined) {
            return;
        }

        const currTime = JSON.parse(storageData[tabTimeObjectKey])[currentHostname].trackedSeconds;
        if (secondsToMinutes(currTime) >= currentLimit.time) {
            chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
                const activeTabId = tabs[0].id;
                chrome.tabs.sendMessage(activeTabId, {event: 'block'});
            });
        }
    });
}

chrome.windows.onFocusChanged.addListener(function (windowId) {
    if (windowId === chrome.windows.WINDOW_ID_NONE) {
        processTabChange();
    }
});

chrome.tabs.onActivated.addListener(function (activeInfo) {
    processTabChange();
});

function processTabChange(tabId) {
    chrome.tabs.query({"active": true}, function (tabs) {
        if (tabs.length === 0 || tabs[0] == null || !browserFocused) {
            return;
        }

        let currentTab = tabs[0];
        let hostname = getHostname(currentTab);

        chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then((storageData) => {
            updateLocalStorageData(hostname, storageData);
        });
    });

}

function updateLocalStorageData(hostname, storageData) {
    let tabTime = getTabTime(storageData);
    let lastActiveTab = getLastActiveTab(storageData);

    let newLastTab = {};
    newLastTab[lastActiveTabKey] = JSON.stringify({"url": hostname, "lastDateVal": Date.now()});
    chrome.storage.local.set(newLastTab, function () {
        let newTabTime = {};
        newTabTime[tabTimeObjectKey] = JSON.stringify(updateTabTime(tabTime, lastActiveTab));
        chrome.storage.local.set(newTabTime);


        // chrome.storage.local.get(["totalTime"]).then((storageData) => {
        //     let newTotalTime = {};
        //     newTotalTime["totalTime"] = storageData["totalTime"] + 
        // });
    });
}

function updateTabTime(tabTime, lastActiveTab) {
    let lastUrl = lastActiveTab["url"];
    let passedSeconds = (Date.now() - lastActiveTab["lastDateVal"]) * 0.001;
    if (!isToday(new Date(lastActiveTab["lastDateVal"]))) {
        passedSeconds = 0;
    }

    let newTabTime = {};

    if (tabTime.hasOwnProperty(lastUrl)) {
        newTabTime["trackedSeconds"] = tabTime[lastUrl].trackedSeconds + passedSeconds;
    } else {
        newTabTime["trackedSeconds"] = passedSeconds;
    }

    newTabTime["lastDateVal"] = Date.now();

    setTabTime(tabTime, lastUrl, newTabTime);

    console.log(tabTime[lastUrl].trackedSeconds);

    return tabTime;
}

function setTabTime(tabTime, url, value) {
    tabTime[url] = {trackedSeconds: value["trackedSeconds"], lastDateVal: value["lastDateVal"]};
}

function getTabTime(storageData) {
    let tabTimeObjectString = storageData[tabTimeObjectKey];
    let tabTime = {};

    if (tabTimeObjectString != null) {
        tabTime = JSON.parse(tabTimeObjectString);
    }

    return tabTime;
}

function getLastActiveTab(storageData) {
    let lastActiveTabString = storageData[lastActiveTabKey];
    let lastActiveTab = {};

    if (lastActiveTabString != null) {
        lastActiveTab = JSON.parse(lastActiveTabString);
    }

    return lastActiveTab;
}

function getHostname(tab) {
    let hostname;
    try {
        hostname = new URL(tab.url).hostname;
    } catch (e) {
        console.log(e);
    }
    return hostname;
}

function secondsToMinutes(seconds) {
    return seconds / 60;
}

function isToday(date) {
    const now = new Date()

    return date.getDate() === now.getDate() &&
        date.getMonth() === now.getMonth() &&
        date.getFullYear() === now.getFullYear()
}