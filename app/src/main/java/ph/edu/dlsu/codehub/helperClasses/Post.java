package ph.edu.dlsu.codehub.helperClasses;

public class Post {
    private String uid, time, date, title, body, profilePic, fullname;
    public Post() {}
    public Post(String uid, String time, String date, String title, String body, String profilePic, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.title = title;
        this.body = body;
        this.profilePic = profilePic;
        this.fullname = fullname;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
