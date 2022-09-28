/**
 * Configures the leaderboard table's properties (e.g. pagination, colours for 1st, 2nd, and 3rd place).
 */
function configureLeaderboardTable() {
    $('table').DataTable({searching: false, paging: true, info: false, select: false, sort: false,
        'rowCallback': function(row, data) {
            const rankToColour = {'1': 'gold', '2': 'silver', '3': 'bronze'}
            if (data[0] in rankToColour) {
                $(row).addClass(rankToColour[data[0]])
            }
        },
        dom: '<"top"i>rt<"bottom"pl>'
    });
}

/**
 * Loads in an updated table from the controller, keeps the user on the same page, and updates the notification to
 * inform the user when the table has updated.
 * @param notification the notification to update
 * @param operation The type of operation, such as 'add' or 'delete'.
 */
function updateLeaderboard(notification, operation) {
    const leaderboardTable = $('table').DataTable();
    const page = leaderboardTable.page();
    const url = "/leaderboard-table";
    $("#leaderboardTable").load(url + " #leaderboardTable>*", "", function () {
        configureLeaderboardTable();
        $('table').DataTable().page(page).draw('page');
        if (operation === ADDEVIDENCEACTION) {
            notification.action = UPDATELEADERBOARDADDACTION;
        } else {
            notification.action = UPDATELEADERBOARDDELETEACTION;
        }
        notification.show();
        notification.hideTimed(SECONDS_TILL_HIDE);
    });
}

