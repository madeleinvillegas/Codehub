package ph.edu.dlsu.codehub.helperClasses;

public class Report {
    private String reporter, reason, postId;

    private Report()
    {

    }
    public Report(String reporter, String reason, String postId) {
        this.reporter = reporter;
        this.reason = reason;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
}
