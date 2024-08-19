package in.demo.myapplication.Model;

public class SmokingItem {
    private String smokingStatus;
    private boolean isSelected;
    private int imageResId; // Resource ID for the image

    public SmokingItem(String smokingStatus, int imageResId) {
        this.smokingStatus = smokingStatus;
        this.isSelected = false;
        this.imageResId = imageResId;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
