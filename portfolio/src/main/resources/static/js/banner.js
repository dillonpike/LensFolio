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