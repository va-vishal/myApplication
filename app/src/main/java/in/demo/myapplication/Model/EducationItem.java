package in.demo.myapplication.Model;

public class EducationItem {
    private String educationLevel;
    private boolean isSelected;
    private int imageResId; // Resource ID for the image

    public EducationItem(String educationLevel, int imageResId) {
        this.educationLevel = educationLevel;
        this.isSelected = false;
        this.imageResId = imageResId;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
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
