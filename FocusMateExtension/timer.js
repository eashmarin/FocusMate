const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}
let totalSeconds;

chrome.storage.local.get(tabTimeObjectKey, (result) => {
    totalSeconds = Math.round(result[tabTimeObjectKey].trackedSeconds);
    console.log(result[tabTimeObjectKey]);
});

setInterval(() => {
    setTime(totalSeconds);
    totalSeconds++;
}, 1000);

// chrome.tabs.onUpdated.addListener(() => {
//     launchTimer()
// });

// chrome.alarms.create("myAlarm", {delayInMinutes: 0.1, periodInMinutes: 0.1}, () => {
//     let counter = 0;
//     counter++;
//     console.log(counter);
//     setTime(counter);
// });

function align(value) {
    if (value.length < 2) {
        return "0" + value;
    } else {
        return value;
    }
}

function setTime(totalSeconds) {
    let seconds = align(totalSeconds % 60);
    let minutes = align(parseInt(totalSeconds / 60));
    let hours = align(parseInt(totalSeconds / 3600));
    document.getElementById("timer-text").innerHTML = hours + ":" + minutes + ":" + seconds;
}

function launchTimer() {
    // setInterval(() => {
    //     counter++;
    //     console.log(counter);
    //     setTime(counter);
    // }, 1000);
}