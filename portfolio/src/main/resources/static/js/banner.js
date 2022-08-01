/**
 * Removes the alert banner.
 */
function removeAlertBanner() {
    document.getElementById("alertBanner").hidden = true;
}

/**
 * Displays the alert banner with the given text.
 * @param text text to be shown in the alert banner
 */
function showAlertBanner(text) {
    document.getElementById("alertBanner").hidden = false;
    document.getElementById("alertBannerText").innerText = text;
}

/**
 * Displays the alert toast with the given text.
 * @param text text to be shown in the alert toast
 */
function showAlertToast(text) {
    let toastEl = $("#alertToast");
    toastEl.removeClass("bg-danger")
    toastEl.addClass("bg-success")
    toastEl.toast('show')
    document.getElementById("alertToastText").textContent = text;
}

/**
 * Displays the alert error toast with the given text.
 * @param text text to be shown in the alert error toast
 */
function showAlertErrorToast(text) {
    let toastEl = $("#alertToast");
    toastEl.removeClass("bg-success")
    toastEl.addClass("bg-danger")
    toastEl.toast('show')
    document.getElementById("alertToastText").textContent = text;
}