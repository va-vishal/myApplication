package in.demo.myapplication.Model;

public class ReligionItem {
    private String religionName;
    private int imageResource;
    private boolean isSelected;

    public ReligionItem(String religionName, int imageResource) {
        this.religionName = religionName;
        this.imageResource = imageResource;
        this.isSelected = false;
    }

    public String getReligionName() {
        return religionName;
    }

    public void setReligionName(String religionName) {
        this.religionName = religionName;
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
