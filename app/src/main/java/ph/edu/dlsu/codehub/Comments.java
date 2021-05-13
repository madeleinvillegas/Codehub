package ph.edu.dlsu.codehub;

public class Comments {
    public String comment;
    public String fullName;
    public String date;

    public Comments(){}
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullname) {
        this.fullName = fullname;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;

    public Comments(String comment, String fullName, String date, String time) {
        this.comment = comment;
        this.fullName = fullName;
        this.date = date;
        this.time = time;
    }


}
