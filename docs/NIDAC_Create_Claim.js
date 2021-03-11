// ==UserScript==
// @name         NIDAC Create Claim AutoComplete
// @namespace    http://tampermonkey.net/
// @version      0.13

// @description  NIDAC Create Claim AutoComplete
// @author       NIDAC Team
// @match        http*://*/national-import-duty-adjustment-centre/*
// @grant        none
// @updateURL    https://raw.githubusercontent.com/hmrc/national-import-duty-adjustment-centre-frontend/master/docs/NIDAC_Create_Claim.js
// ==/UserScript==

(function () {
    'use strict';
    document.getElementsByTagName("body")[0].appendChild(createQuickButton());
})();

function createQuickButton() {
    let button = document.createElement('button');
    button.id = "quickSubmit";
    if (!!document.getElementById('global-header')) {
        button.classList.add('button-start', 'govuk-!-display-none-print');
    } else {
        button.classList.add('govuk-button', 'govuk-!-display-none-print');
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

    /* START */
    if (currentPageIs("/national-import-duty-adjustment-centre/what-do-you-want-to-do")) {
        document.getElementById("what_do_you_want_to_do").checked = true;
        submit();
    }

    /* AMEND */
    if (currentPageIs("/national-import-duty-adjustment-centre/amend/claim-reference-number")) {
        document.getElementById("caseReference").value = "NID21134557697RM8WIB13";
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/amend/attach-more-documents")) {
        document.getElementById("yesOrNo").checked = true;
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/amend/your-uploads")) {
        document.getElementById("yesOrNo-2").checked = true;
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/amend/additional-information")) {
        document.getElementById("furtherInformation").value = "Some new information that has been added";
        submit();
    }

    /* CREATE */
    if (currentPageIs("/national-import-duty-adjustment-centre/create/importer-representative")) {
        document.getElementById("representation_type-2").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/claim-type")) {
        document.getElementById("claim_type").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/entry-details")) {
        document.getElementById("entryProcessingUnit").value = "123";
        document.getElementById("entryNumber").value = "123456Q";
        document.getElementById("entryDate").value = "12";
        document.getElementById("entryDate_month").value = "12";
        document.getElementById("entryDate_year").value = "2020";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/item-numbers")) {
        document.getElementById("itemNumbers").value = "1, 2, 7-10";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/reclaiming")) {
        document.getElementById("reclaimDutyType").checked = true;
        document.getElementById("reclaimDutyType-2").checked = true;
        document.getElementById("reclaimDutyType-3").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/enter-customs-duty")) {
        document.getElementById("actuallyPaid").value = "100.00";
        document.getElementById("shouldPaid").value = "89.99";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/enter-import-vat")) {
        document.getElementById("actuallyPaid").value = "80.00";
        document.getElementById("shouldPaid").value = "72.50";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/enter-other-duties")) {
        document.getElementById("actuallyPaid").value = "50.00";
        document.getElementById("shouldPaid").value = "49.99";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/claim-reason")) {
        document.getElementById("claimReason").value = "I believe I have been over-charged";
        submit();
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/create/upload-supporting-documents")) {
    }

    if (currentPageIs("/national-import-duty-adjustment-centre/create/uploaded-files")) {
        document.getElementById("yesOrNo-2").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/contact-details")) {
        document.getElementById("firstName").value = "Tim";
        document.getElementById("lastName").value = "Tester";
        document.getElementById("emailAddress").value = "tim@testing.com";
        document.getElementById("telephoneNumber").value = "01234567890";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/your-address")) {
        document.getElementById("name").value = "ACME Importers Ltd";
        document.getElementById("addressLine1").value = "Unit 42";
        document.getElementById("addressLine2").value = "West Industrial Estate";
        document.getElementById("city").value = "Middlewich";
        document.getElementById("postcode").value = "MD123KD";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/eori-number")) {
        document.getElementById("yesOrNo").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/enter-eori-number")) {
        document.getElementById("eoriNumber").value = "GB123456789536";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/who-to-repay")) {
        document.getElementById("repay_to-2").checked = true;
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/bank-details")) {
        document.getElementById("accountName").value = "ACME Importers Ltd";
        document.getElementById("sortCode").value = "400530";
        document.getElementById("accountNumber").value = "71584685";
        submit();
    }
    if (currentPageIs("/national-import-duty-adjustment-centre/create/importer-correspondence-address")) {
        document.getElementById("name").value = "Representatives Client Importer";
        document.getElementById("addressLine1").value = "Unit 17";
        document.getElementById("addressLine2").value = "North Industrial Estate";
        document.getElementById("city").value = "Southwich";
        document.getElementById("postcode").value = "SO123KD";
        submit();
    }
}
