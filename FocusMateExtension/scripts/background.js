const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}

chrome.windows.onFocusChanged.addListener(function (windowId) {
    if (windowId === chrome.windows.WINDOW_ID_NONE) {
        processTabChange();
    }
});

chrome.tabs.onActivated.addListener(function (activeInfo) {
    processTabChange();
});

// chrome.windows.onRemoved.addListener(function (windowId) {
//     chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then( (storageData) => {
//         let lastActiveTab = getLastActiveTab(storageData);
//         let lastActiveTabUrl = lastActiveTab["url"];

//     });
// });

function processTabChange(tabId) {
    //chrome.tabs.get(tabId, function(tab) {
    chrome.tabs.query({ "active": true }, function (tabs) {
        if (tabs.length === 0 || tabs[0] == null) {
            return;
        }

        let currentTab = tabs[0];
        let hostname = getHostname(currentTab);

        console.log(hostname)

        chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then((storageData) => {
            updateLocalStorageData(hostname, storageData);
        });
    });

    function updateLocalStorageData(hostname, storageData) {
        let tabTime = getTabTime(storageData);
        let lastActiveTab = getLastActiveTab(storageData);

        let newLastTab = {};
        newLastTab[lastActiveTabKey] = JSON.stringify({ "url": hostname, "lastDateVal": Date.now() });
        chrome.storage.local.set(newLastTab, function () {
            let newTabTime = {};
            newTabTime[tabTimeObjectKey] = JSON.stringify(updateTabTime(tabTime, lastActiveTab));
            chrome.storage.local.set(newTabTime);
        });
    }

    function updateTabTime(tabTime, lastActiveTab) {
        let lastUrl = lastActiveTab["url"];
        let passedSeconds = (Date.now() - lastActiveTab["lastDateVal"]) * 0.001;

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
        tabTime[url] = { trackedSeconds: value["trackedSeconds"], lastDateVal: value["lastDateVal"] };
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
}