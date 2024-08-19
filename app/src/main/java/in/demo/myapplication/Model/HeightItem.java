package in.demo.myapplication.Model;

// HeightItem.java
public class HeightItem {
    private String height;
    private boolean isSelected;

    public HeightItem(String height, boolean isSelected) {
        this.height = height;
        this.isSelected = isSelected;
    }

    public HeightItem(String height) {
        this.height = height;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
