package in.demo.myapplication.Model;

public class MaritalStatusItem {
    private String status;
    private boolean isSelected;
    private int imageResId; // Resource ID for the image

    public MaritalStatusItem(String status, int imageResId) {
        this.status = status;
        this.isSelected = false;
        this.imageResId = imageResId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
