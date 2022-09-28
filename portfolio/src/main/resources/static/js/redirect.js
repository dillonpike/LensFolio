/**
 * Redirects to the evidence page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToEvidence(userId) {
    localStorage.setItem('tab', 'evidence-tab');
    window.location.href = "account?userId=" + userId;
}

/**
 * Redirects to the profile page of the user with userId.
 * @param userId id of user whose evidence page to redirect to
 */
function redirectToProfile(userId) {
    localStorage.setItem('tab', 'profile-tab');
    window.location.href = "account?userId=" + userId;
}

/**
 * Redirect to skill page
 * @param userId current user ID (note: it is not necessarily the user that logged in)
 * @param skillId the id of a skill
 */
function redirectToSkillsPage(userId, skillId) {
    window.location.href = "evidence-skills?userId="+userId+"&skillId="+skillId;
}

/**
 * Redirect to category page
 * @param userId current user ID (note: it is not necessarily the user that logged in)
 * @param categoryId the id of a category
 */
function redirectToCategoriesPage(userId, categoryId) {
    console.log(userId, categoryId)
    window.location.href = "evidence-categories?userId="+userId+"&skillId="+categoryId;
}