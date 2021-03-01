// ==UserScript==
// @name         NIDAC Amend Claim AutoComplete
// @namespace    http://tampermonkey.net/
// @version      0.1

// @description  NIDAC Amend Claim AutoComplete
// @author       NIDAC Team
// @match        http*://*/national-import-duty-adjustment-centre/amend/*
// @grant        none
// @updateURL    https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC_Amend_Claim.js
// ==/UserScript==

(function () {
    'use strict';
    document.getElementsByTagName("body")[0].appendChild(createQuickButton());
})();

function createQuickButton() {
    let button = document.createElement('button');
    button.id = "quickSubmit";
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start');
    } else {
        button.classList.add('govuk-button');
    }
    button.style.position = "absolute"
    button.style.top = "50px"
    button.innerHTML = 'Quick Submit';
    button.onclick = () => completePage();
    return button;
}

function currentPageIs(path) {
    let matches = window.location.pathname.match(path + "$");
    return matches && matches.length > 0
}

function submit() {
    document.getElementsByClassName("govuk-button")[0].click();
}

function completePage() {

    if (currentPageIs("/national-import-duty-adjustment-centre/amend/claim-reference-number")) {
        document.getElementById("caseReference").value = "NID21134557697RM8WIB13";
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/amend/additional-information")) {
        document.getElementById("furtherInformation").value = "Some new information that has been added";
        submit();
    }

}
