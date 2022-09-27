/**
 * Redirects to the evidence page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToEvidence(userId) {
    localStorage.setItem('tab', 'profile-tab');
    window.location.href = "account?userId=" + userId;
}

/**
 * redirect to skill page
 * @param userId current user ID (note: it is not necessarily the user that logged in)
 * @param skillId the id of a skill
 */
function redirectToSkillsPage(userId, skillId) {
    window.location.href = "evidence-skills?userId="+userId+"&skillId="+skillId;
}