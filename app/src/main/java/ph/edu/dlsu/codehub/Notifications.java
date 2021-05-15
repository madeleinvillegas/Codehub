package ph.edu.dlsu.codehub;

public class Notifications {
    //linkUID is the id for node being referred
    public int notificationType, numberOfPeopleThatLikedYourPost, numberOfPeopleWhoFollowedYou;
    public String content, notificationContent, creationDate, time, linkUID, mostRecentPersonWhoLikedYourPost, personYouFollowed, personWhoFollowedYou, image;

    public Notifications()
    {

    }

    public Notifications(int notificationType, int numberOfPeopleThatLikedYourPost, int numberOfPeopleWhoFollowedYou, String content, String notificationContent, String creationDate, String time, String linkUID, String mostRecentPersonWhoLikedYourPost, String personYouFollowed, String personWhoFollowedYou, String image) {
        this.notificationType = notificationType;
        this.numberOfPeopleThatLikedYourPost = numberOfPeopleThatLikedYourPost;
        this.numberOfPeopleWhoFollowedYou = numberOfPeopleWhoFollowedYou;
        this.content = content;
        this.notificationContent = notificationContent;
        this.creationDate = creationDate;
        this.time = time;
        this.linkUID = linkUID;
        this.mostRecentPersonWhoLikedYourPost = mostRecentPersonWhoLikedYourPost;
        this.personYouFollowed = personYouFollowed;
        this.personWhoFollowedYou = personWhoFollowedYou;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public int getNumberOfPeopleThatLikedYourPost() {
        return numberOfPeopleThatLikedYourPost;
    }

    public void setNumberOfPeopleThatLikedYourPost(int numberOfPeopleThatLikedYourPost) {
        this.numberOfPeopleThatLikedYourPost = numberOfPeopleThatLikedYourPost;
    }

    public int getNumberOfPeopleWhoFollowedYou() {
        return numberOfPeopleWhoFollowedYou;
    }

    public void setNumberOfPeopleWhoFollowedYou(int numberOfPeopleWhoFollowedYou) {
        this.numberOfPeopleWhoFollowedYou = numberOfPeopleWhoFollowedYou;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getMostRecentPersonWhoLikedYourPost() {
        return mostRecentPersonWhoLikedYourPost;
    }

    public void setMostRecentPersonWhoLikedYourPost(String mostRecentPersonWhoLikedYourPost) {
        this.mostRecentPersonWhoLikedYourPost = mostRecentPersonWhoLikedYourPost;
    }

    public String getPersonYouFollowed() {
        return personYouFollowed;
    }

    public void setPersonYouFollowed(String personYouFollowed) {
        this.personYouFollowed = personYouFollowed;
    }

    public String getPersonWhoFollowedYou() {
        return personWhoFollowedYou;
    }

    public void setPersonWhoFollowedYou(String personWhoFollowedYou) {
        this.personWhoFollowedYou = personWhoFollowedYou;
    }
}
