package in.demo.myapplication.Model;

public class PersonalityItem {
    private String personalityName;
    private int imageResId;

    public PersonalityItem(String personalityName, int imageResId) {
        this.personalityName = personalityName;
        this.imageResId = imageResId;
    }

    public String getPersonalityName() {
        return personalityName;
    }

    public int getImageResId() {
        return imageResId;
    }
}

