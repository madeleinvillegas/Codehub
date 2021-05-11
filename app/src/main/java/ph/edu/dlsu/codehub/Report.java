package ph.edu.dlsu.codehub;

public class Report {
    public Report(String reporter, String reason) {
        this.reporter = reporter;
        this.reason = reason;
    }

    public Report(){}
    private String reporter, reason;

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
