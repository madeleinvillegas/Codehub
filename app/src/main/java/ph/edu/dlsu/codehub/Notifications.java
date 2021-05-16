//EVENT TYPES:
//Event Codes:
// 0: Someone Liked Your Post
// 1: Someone Commented On Your Post
// 2: Someone Followed You
// 3: You Followed Someone
// 4?: Someone Replied To Your Comment (HIGHLY QUESTIONABLE)

//NOTIFICATION CODE is needed to properly link stuff sa firebase app and database

//Instead of Person A and B liked your post do:
//Person A liked your post
//Person B liked your post
//Delete Notification After 1 month



package ph.edu.dlsu.codehub;

public class Notifications {
    //Honestly, why go for this when we can just use hashmaps sa recycler view
    //go for a naive notification system (since school proj lang)
    //linkUID is the id for node being referred

    public int notificationType;
    public String notificationContent, creationDate, time, linkUID, image;

    public Notifications()
    {

    }

    public Notifications(int notificationType, String notificationContent, String creationDate, String time, String linkUID, String image) {
        this.notificationType = notificationType;
        this.notificationContent = notificationContent;
        this.creationDate = creationDate;
        this.time = time;
        this.linkUID = linkUID;
        this.image = image;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLinkUID() {
        return linkUID;
    }

    public void setLinkUID(String linkUID) {
        this.linkUID = linkUID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
