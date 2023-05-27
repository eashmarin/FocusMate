const tabTimeObjectKey = "tabTimeObject"; // {key: url, value: {trackedSeconds: number, lastDateVal: number}}
const lastActiveTabKey = "lastActiveTab"; // {url: string, lastDateVal: number}
let totalSeconds = 0;

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("add-row-button").addEventListener("click", (e) => addRow());
    document.getElementById("edit-table-button").addEventListener("click", (e) => madeTableEditable());
    document.getElementById("save-table-button").addEventListener("click", (e) => saveTable());

    chrome.storage.local.get(tabTimeObjectKey, (result) => {
        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
            let activeTab = tabs[0];
            let currentHostName = new URL(activeTab.url).hostname;
            totalSeconds = Math.round(JSON.parse(result[tabTimeObjectKey])[currentHostName].trackedSeconds);
        });
    });

    if (!document.getElementById("table-body").hasChildNodes()) {
        chrome.storage.local.get("limits").then((limits) => {
            if (limits["limits"] === undefined) {
                return;
            }

            const jsonLimits = JSON.parse(limits["limits"]);
            let limitsLength = jsonLimits.length;
            
            let tableBody = document.getElementById("table-body");
    
            for (let i = 0; i < limitsLength; i++) {
                const tr = document.createElement("tr");
                tableBody.append(tr);
    
                const td1 = document.createElement("td");
                td1.innerHTML = jsonLimits[i].hostname;
                tr.appendChild(td1);
    
                const td2 = document.createElement("td");
                td2.innerHTML = jsonLimits[i].time;
                tr.appendChild(td2);
            }
        });
    }
});

setInterval(() => {
    setTime(totalSeconds);
    totalSeconds++;
}, 1000);


function align(value) {
    if (value.toString().length < 2) {
        return "0" + value;
    } else {
        return value;
    }
}

function setTime(totalSeconds) {
    let seconds = align(totalSeconds % 60);
    let minutes = align(parseInt(totalSeconds / 60) % 60);
    let hours = align(parseInt(totalSeconds / 3600));
    document.getElementById("timer-text").innerHTML = hours + ":" + minutes + ":" + seconds;
}

function openTab(event, tabName) {
    let i, tabcontent, tablinks;

    tabcontent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    tablinks = document.getElementsByClassName("tab-links");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    document.getElementById(tabName).style.display = "flex";
    event.currentTarget.className += " active";
}

function addRow() {
    var table = document.getElementById("table-body");
    var row = table.insertRow(-1);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var input1 = document.createElement("input");
    var input2 = document.createElement("input");

    input1.type = "text";
    input2.type = "number";

    cell1.appendChild(input1);
    cell2.appendChild(input2);

    input1.addEventListener("focusout", function () {
        if (input1.value !== '') {
            cell1.innerHTML = input1.value;
        }
    });
    input2.addEventListener("focusout", function () {
        if (input2.value !== '') {
            cell2.innerHTML = input2.value;
        }
    });

    document.getElementById("add-row-button").style.display = "none";
    document.getElementById("edit-table-button").style.display = "none";
    document.getElementById("save-table-button").style.display = "block";
}

function madeTableEditable() {
    let table = document.getElementById("table-body");
    const rows = table.getElementsByTagName('tr'); // получаем все строки таблицы

    for (let i = 0; i < rows.length; i++) {
        const cells = rows[i].getElementsByTagName('td'); // получаем все ячейки строки

        for (let j = 0; j < cells.length; j++) {
            const cell = cells[j];
            const text = cell.innerHTML; // получаем содержимое ячейки

            // создаем новый input и заменяем содержимое ячейки
            const input = document.createElement('input');
            input.type = 'text';
            input.value = text;
            cell.innerHTML = '';
            cell.appendChild(input);
        }
    }

    document.getElementById("add-row-button").style.display = "none";
    document.getElementById("edit-table-button").style.display = "none";
    document.getElementById("save-table-button").style.display = "block";
}

function saveTable() {
    document.getElementById("add-row-button").style.display = "";
    document.getElementById("edit-table-button").style.display = "";
    document.getElementById("save-table-button").style.display = "none";

    let table = document.getElementById("table-body");
    let rows = table.getElementsByTagName('tr'); // получаем все строки таблицы

    let hasEmptyRow = false;

    for (var i = 0; i < rows.length; i++) {
        var cells = rows[i].getElementsByTagName('td'); // получаем все ячейки строки

        for (var j = 0; j < cells.length; j++) {
            var cell = cells[j];
            var input = cell.getElementsByTagName('input')[0]; // получаем первый input в ячейке

            if (input) { // если есть input в ячейке
                var value = input.value;
                cell.removeChild(input);
                cell.innerHTML = value;
                if (input.value == "") {
                    hasEmptyRow = true;
                }
            }
        }
    }

    if (hasEmptyRow) {
        table.deleteRow(-1);
    }

    saveTableData();
}

function saveTableData() {
    let data = [];

    let table = document.getElementById("table-body");
    let rows = table.getElementsByTagName('tr'); // получаем все строки таблицы
    for (var i = 0; i < rows.length; i++) {
        let hostname = rows[i].getElementsByTagName('td')[0].innerHTML;
        let time = rows[i].getElementsByTagName('td')[1].innerHTML;

        let dict = {};
        dict['hostname'] = hostname;
        dict['time'] = time;

        data[i] = dict;
    }

    console.log(JSON.stringify({ "limits": data }));

    chrome.storage.local.get(["limits"]).then((limits) => {
        let newLimits = {};
        newLimits["limits"] = JSON.stringify(data);
        chrome.storage.local.set(newLimits);

        if (rows.length === JSON.parse(limits["limits"]).length) {
            //TODO: PUT requests here
        } else {
            //TODO: POST request here
        }
    });
}