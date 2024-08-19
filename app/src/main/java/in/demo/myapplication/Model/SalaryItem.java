package in.demo.myapplication.Model;

public class SalaryItem {
    private String salaryRange;
    private boolean isSelected;

    public SalaryItem(String salaryRange) {
        this.salaryRange = salaryRange;
        this.isSelected = false;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
