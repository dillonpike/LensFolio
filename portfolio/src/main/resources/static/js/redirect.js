/**
 * Redirects to the evidence page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToEvidence(userId) {
    localStorage.setItem('tab', 'profile-tab');
    window.location.href = "account?userId=" + userId;
}