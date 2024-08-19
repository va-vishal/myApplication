package in.demo.myapplication.Model;

public class DietItem {
    private String dietName;
    private int imageResource;
    private boolean isSelected;

    public DietItem(String dietName, int imageResource) {
        this.dietName = dietName;
        this.imageResource = imageResource;
        this.isSelected = false;
    }

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
