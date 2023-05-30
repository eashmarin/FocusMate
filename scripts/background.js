const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}
const limitsKey = "limits";

let isTelegramLinked = false;
let wasTelegramMsgSent = false;

let totalSeconds = 0;
let inactiveTime = 0;

let browserFocused = true;
setInterval(checkBrowserFocus, 3000);
function checkBrowserFocus() {
    chrome.windows.getCurrent(function (browser) {
        if (browserFocused === false) {
            if (browser.focused) {
                chrome.storage.local.get([tabTimeObjectKey, lastActiveTabKey]).then((storageData) => {
                    let lastActiveTab = getLastActiveTab(storageData);
                    let newLastTab = {};
                    console.log(lastActiveTab);
                    newLastTab[lastActiveTabKey] = JSON.stringify({
                        "url": lastActiveTab.url,
                        "lastDateVal": Date.now()
                    });
                    chrome.storage.local.set(newLastTab);
                });
            } else {
                inactiveTime += 3;
            }
        }

        if (browserFocused) {
            totalSeconds += 3;
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

chrome.runtime.onStartup.addListener(() => {
    chrome.storage.local.get(["token"]).then((storageData) => {
        if (storageData["token"]) {
            let token = storageData["token"];
            //TODO: GET request localhost:8080/user/{token}/telegram
            // if response OK { set isTelegramLinked variable }
            const ulrStr = 'http://localhost:8080/user/' + token + '/telegram';
            console.log('startup')
            fetch(ulrStr.toString(), {
                method: 'GET'
            })
                .then(response => response.text())
                .then(data => {
                    if (data === null){
                        console.log(data);
                    }else{
                        console.log(data);
                        isTelegramLinked = true;
                    }
                })
                .catch(error => {
                    console.error('Произошла ошибка:', error);
                });
        }
    });
});

setInterval(() => {
    checkLimits();
    saveTotalTime();
}, 10000);

chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    if (request.event === 'pageLoading') {
        checkLimits();
    }
});

function saveTotalTime() {
    chrome.storage.local.set({"totalTime": totalSeconds.toString()});
}

function checkLimits() {
    checkTotalLimit();
    checkWebsiteLimit();
}

function checkTotalLimit() {
    if (secondsToMinutes(inactiveTime) >= 5) {
        inactiveTime = 0;
        totalSeconds = 0;
        if (isTelegramLinked) {
            wasTelegramMsgSent = false;
        } else {
            chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
                const activeTabId = tabs[0].id;
                chrome.tabs.sendMessage(activeTabId, {event: 'notification hide'});
            });
        }
    }

    if (secondsToMinutes(totalSeconds) >= 25) {
        if (isTelegramLinked && !wasTelegramMsgSent) {
            // TODO: no API for message send yet
            wasTelegramMsgSent = true;
        } else {
            chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
                const activeTabId = tabs[0].id;
                chrome.tabs.sendMessage(activeTabId, {event: 'notification popup'});
            });
        }
    }
}

function checkWebsiteLimit() {
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
            console.log('limit is out')
            if (storageData["token"]) {
                let token = storageData["token"];
                const urlStr = 'http://localhost:8080/user/' + token + '/telegram';
                fetch(urlStr.toString(), {
                    method: 'POST'
                })
                    .then((response) => {
                        if (response.ok) {
                            console.log('Запрос успешно выполнен.');
                        } else {
                            console.error('Ошибка выполнения запроса.');
                        }
                    })
                    .catch((error) => {
                        console.error('Ошибка:', error);
                    });
            }
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
    });
}

function updateTabTime(tabTime, lastActiveTab) {
    let lastUrl = lastActiveTab["url"];
    let passedSeconds = (Date.now() - lastActiveTab["lastDateVal"]) * 0.001;
    if (!isToday(new Date(lastActiveTab["lastDateVal"]))) {
        tabTime[lastUrl].trackedSeconds = 0;
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