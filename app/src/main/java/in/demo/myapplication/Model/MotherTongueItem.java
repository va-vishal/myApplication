package in.demo.myapplication.Model;

public class MotherTongueItem {

    private String motherTongueName;
    private int imageResId;
    private boolean isSelected;

    public MotherTongueItem(String motherTongueName, int imageResId) {
        this.motherTongueName = motherTongueName;
        this.imageResId = imageResId;
        this.isSelected = false;
    }

    public String getMotherTongueName() {
        return motherTongueName;
    }

    public void setMotherTongueName(String motherTongueName) {
        this.motherTongueName = motherTongueName;
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
