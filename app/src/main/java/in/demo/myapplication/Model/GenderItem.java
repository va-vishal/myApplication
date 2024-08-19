package in.demo.myapplication.Model;

public class GenderItem {
    private String genderName;
    private int imageResId;
    private boolean isSelected;

    public GenderItem(String genderName, int imageResId) {
        this.genderName = genderName;
        this.imageResId = imageResId;
        this.isSelected = false;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
