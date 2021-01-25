window.GOVUKFrontend.initAll();
window.HMRCFrontend.initAll();

// =====================================================
// Back link mimics browser back functionality
// =====================================================
// store referrer value to cater for IE - https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10474810/  */
const docReferrer = document.referrer

// prevent resubmit warning
if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
  window.history.replaceState(null, null, window.location.href);
}

backLink = document.getElementById("back-link")
if(typeof(backLink) != 'undefined' && backLink != null){
    backLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (window.history && window.history.back && typeof window.history.back === 'function' &&
            (docReferrer !== "" && docReferrer.indexOf(window.location.host) !== -1)) {
            window.history.back();
        }
    });
}
