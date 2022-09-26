package nz.ac.canterbury.seng302.portfolio.model;

/**
 *  LeaderboardEntry Class to represent a leaderboard entry.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private String username;
    private String firstName;
    private String lastName;
    private int score;
    private int rank;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int userId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public LeaderboardEntry(String username, String firstName, String lastName, int score, int rank, int userId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
        this.rank = rank;
        this.userId = userId;
    }

    /** Method to create a comparator for sorting LeaderboardEntries by score
     * @param leaderboardEntry the LeaderboardEntry to compare to
     */
    @Override
    public int compareTo(LeaderboardEntry leaderboardEntry) {
        if (this.score == leaderboardEntry.getScore()) {
            return 0;
        }
        return this.score < leaderboardEntry.getScore() ? 1 : -1;
    }

    /**
     * Method to override the equal method for LeaderboardEntry
     * @param newLeaderboardEntry  the LeaderboardEntry to compare to
     * @return true if the LeaderboardEntries are equal, false otherwise
     */
    @Override
    public boolean equals(Object newLeaderboardEntry)
    {
        if (!(newLeaderboardEntry instanceof LeaderboardEntry leaderboardEntry))
            return false;
        return leaderboardEntry.username.equals(this.username) &&
                leaderboardEntry.firstName.equals(this.firstName) &&
                leaderboardEntry.lastName.equals(this.lastName) &&
                leaderboardEntry.score == this.score &&
                leaderboardEntry.rank == this.rank;
    }


}
