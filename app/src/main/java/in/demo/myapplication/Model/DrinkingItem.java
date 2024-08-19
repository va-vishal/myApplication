package in.demo.myapplication.Model;

public class DrinkingItem {
    private String drinkStatus;
    private boolean isSelected;
    private int imageResId; // Resource ID for the image

    public DrinkingItem(String drinkStatus, int imageResId) {
        this.drinkStatus = drinkStatus;
        this.isSelected = false;
        this.imageResId = imageResId;
    }

    public String getDrinkStatus() {
        return drinkStatus;
    }

    public void setDrinkStatus(String drinkStatus) {
        this.drinkStatus = drinkStatus;
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
