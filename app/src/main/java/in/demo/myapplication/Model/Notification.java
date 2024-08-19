package in.demo.myapplication.Model;

public class Notification {
    private String body;
    private String currentuserid; // Note the exact case
    private String notificationType; // Note the exact case
    private String otheruserid; // Note the exact case
    private long timestamp;
    private String title;

    // Default constructor required for Firebase
    public Notification() {
    }

    // Getters and Setters
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCurrentuserid() {
        return currentuserid;
    }

    public void setCurrentuserid(String currentuserid) {
        this.currentuserid = currentuserid;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getOtheruserid() {
        return otheruserid;
    }

    public void setOtheruserid(String otheruserid) {
        this.otheruserid = otheruserid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
