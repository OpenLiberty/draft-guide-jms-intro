/*******************************************************************************
* Copyright (c) 2024 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

function addSystem(event) {
    var systemCreationForm = document.getElementById("systemCreation");

    var system = {
        hostname: systemCreationForm.elements.systemHostname.value,
        osName: systemCreationForm.elements.systemOSName.value,
        javaVersion: systemCreationForm.elements.systemJavaVersion.value,
        heapSize: systemCreationForm.elements.systemHeapSize.value
    };
    
    var request = new XMLHttpRequest();
    request.onload = function () {
        setStatus(this.responseText);
    }
    request.open("POST", "inventory/systems", true);
    request.setRequestHeader("Content-type", "application/json");
    request.send(JSON.stringify(system));
 
    event.preventDefault();
}

function toggleUpdateForm(entry) { 

    var systemUpdateForm = document.getElementById("systemUpdate");
    if (systemUpdateForm.elements.updateSystemHostname.value === entry.hostname) {
        clearUpdateForm();
        return;
    }
 
    systemUpdateForm.elements.updateSystemHostname.value = entry.hostname;
    systemUpdateForm.elements.updateSystemOSName.value = entry.osName;
    systemUpdateForm.elements.updateSystemJavaVersion.value = entry.javaVersion;
    systemUpdateForm.elements.updateSystemHeapSize.value = entry.heapSize;
    
    systemUpdateForm.classList.remove("hidden");
}

function updateSystem(event) {
    var systemUpdateForm = document.getElementById("systemUpdate");
    var system = {
        hostname: systemUpdateForm.elements.updateSystemHostname.value,
        osName: systemUpdateForm.elements.updateSystemOSName.value,
        javaVersion: systemUpdateForm.elements.updateSystemJavaVersion.value,
        heapSize: systemUpdateForm.elements.updateSystemHeapSize.value
    };

    var request = new XMLHttpRequest();
    request.onload = function () {
        setStatus(this.responseText);
    }
    request.open("PUT", "inventory/systems/" + system.hostname, true);
    request.setRequestHeader("Content-type", "application/json");
    request.send(JSON.stringify(system));

    event.preventDefault();
}

function remove(e, hostname) {
    var request = new XMLHttpRequest();

    request.onload = function () {
        setStatus(this.responseText);
    }

    request.open("DELETE", "inventory/systems/" + hostname, true);
    request.send();
    e.stopPropagation();
}

function clearUpdateForm() {
    document.getElementById("systemUpdate").classList.add("hidden");
    document.getElementById("systemUpdate").reset();
}

function setStatus(message) {
    var status = document.getElementById("status");
    status.classList.remove("hidden");
    status.innerHTML = message;
}

function refreshDisplay() {
    webSocket.send("refresh");
}

function addToDisplay(entry) {
    console.log(entry);
    var systemHtml = "<div>Hostname: " + entry.hostname + "</div>" +
        "<div>OS: " + entry.osName + "</div>" +
        "<div>Java version: " + entry.javaVersion + "</div>" +
        "<div>Heap size: " + entry.heapSize + "</div>" +
        "<button class=\"deleteButton\" onclick=\"remove(event,'" + entry.hostname + "')\">Delete</button>";

    var systemDiv = document.createElement("div");
    systemDiv.setAttribute("class", "system flexbox");
    systemDiv.setAttribute("id", entry.id);
    systemDiv.onclick = function() { toggleUpdateForm(entry) };
    systemDiv.innerHTML = systemHtml;
    document.getElementById("systemBoxes").appendChild(systemDiv);
}

function clearDisplay() {
    var systemsDiv = document.getElementById("systemBoxes");
    while (systemsDiv.firstChild) {
        systemsDiv.removeChild(systemsDiv.firstChild);
    }
}

function toast(message, index) {
    var length = 3000;
    var toast = document.getElementById("toast");
    setTimeout(function () { toast.innerText = message; toast.className = "show"; }, length * index);
    setTimeout(function () { toast.className = toast.className.replace("show", ""); }, length + length * index);
}

const webSocket = new WebSocket('ws://localhost:9080/boardcast')

webSocket.onopen = function (event) {
    console.log(event);
};

webSocket.onmessage = function (event) {
    var data = event.data;
    clearDisplay();
    doc = JSON.parse(data);
    doc.forEach(addToDisplay);
    if (doc.length > 0) {
        document.getElementById("systemDisplay").style.display = 'flex';
    } else {
        document.getElementById("systemDisplay").style.display = 'none';
    }
    var status = document.getElementById("status");
    status.classList.add("hidden");
    status.innerHTML = "...";
};

webSocket.onerror = function (event) {
    console.error(event);
};
