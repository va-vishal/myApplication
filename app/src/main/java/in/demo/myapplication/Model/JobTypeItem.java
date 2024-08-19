package in.demo.myapplication.Model;

public class JobTypeItem {
    private String jobTypeName;
    private int imageResource;
    private boolean isSelected;

    public JobTypeItem(String jobTypeName, int imageResource) {
        this.jobTypeName = jobTypeName;
        this.imageResource = imageResource;
        this.isSelected = false;
    }

    public String getJobTypeName() {
        return jobTypeName;
    }

    public void setJobTypeName(String jobTypeName) {
        this.jobTypeName = jobTypeName;
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
