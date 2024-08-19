package in.demo.myapplication.Model;

import java.util.Map;

public class User {
    public String id;
    public String name;
    public String email;
    public String age;
    public String description;
    public String diet;
    public String dob;
    public String drinkingStatus;
    public String educationLevel;
    public String gender;
    public String height;
    public String imageurl;
    public String imageurl1;
    public String imageurl2;
    public String imageurl3;
    public String jobType;
    public String area;
    public String state;
    public String maritalStatus;
    public String motherTongue;
    public String personality;
    public String religion;
    public String smokingStatus;
    public String salary;
    public String prefGender;
    private Boolean status;
    private Boolean hideAge;
    private Boolean hideName;
    private Boolean hideProfile;
    private Boolean hideLocation;
    private String  lastSeen;
    private Boolean typing;
    private double latitude;
    private double longitude;
    private int maxDistance;
    private int ageRange;
    private boolean isLocationHidden;
    private boolean isAgeHidden;
    private boolean isNameHidden;
    private boolean isProfileHidden;
    private String phoneNumber;
    private String password;
    private String fcmToken;
    private Map<String, Boolean> likesList;  // Use Map instead of List
    private Map<String, Boolean> visitsList;
    private Map<String, Boolean> blockedList;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, Map<String, Boolean> blockedList, Map<String, Boolean> visitsList, Map<String, Boolean> likesList, String fcmToken, String password, String phoneNumber, boolean isProfileHidden, boolean isNameHidden, boolean isAgeHidden, boolean isLocationHidden, int ageRange, int maxDistance, double longitude, double latitude, Boolean typing, String lastSeen, Boolean hideLocation, Boolean hideProfile, Boolean hideName, Boolean hideAge, Boolean status, String prefGender, String salary, String smokingStatus, String religion, String personality, String motherTongue, String maritalStatus, String state, String area, String jobType, String imageurl3, String imageurl2, String imageurl1, String imageurl, String height, String gender, String educationLevel, String drinkingStatus, String dob, String diet, String description, String age, String email, String name) {
        this.id = id;
        this.blockedList = blockedList;
        this.visitsList = visitsList;
        this.likesList = likesList;
        this.fcmToken = fcmToken;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isProfileHidden = isProfileHidden;
        this.isNameHidden = isNameHidden;
        this.isAgeHidden = isAgeHidden;
        this.isLocationHidden = isLocationHidden;
        this.ageRange = ageRange;
        this.maxDistance = maxDistance;
        this.longitude = longitude;
        this.latitude = latitude;
        this.typing = typing;
        this.lastSeen = lastSeen;
        this.hideLocation = hideLocation;
        this.hideProfile = hideProfile;
        this.hideName = hideName;
        this.hideAge = hideAge;
        this.status = status;
        this.prefGender = prefGender;
        this.salary = salary;
        this.smokingStatus = smokingStatus;
        this.religion = religion;
        this.personality = personality;
        this.motherTongue = motherTongue;
        this.maritalStatus = maritalStatus;
        this.state = state;
        this.area = area;
        this.jobType = jobType;
        this.imageurl3 = imageurl3;
        this.imageurl2 = imageurl2;
        this.imageurl1 = imageurl1;
        this.imageurl = imageurl;
        this.height = height;
        this.gender = gender;
        this.educationLevel = educationLevel;
        this.drinkingStatus = drinkingStatus;
        this.dob = dob;
        this.diet = diet;
        this.description = description;
        this.age = age;
        this.email = email;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDrinkingStatus() {
        return drinkingStatus;
    }

    public void setDrinkingStatus(String drinkingStatus) {
        this.drinkingStatus = drinkingStatus;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl1() {
        return imageurl1;
    }

    public void setImageurl1(String imageurl1) {
        this.imageurl1 = imageurl1;
    }

    public String getImageurl2() {
        return imageurl2;
    }

    public void setImageurl2(String imageurl2) {
        this.imageurl2 = imageurl2;
    }

    public String getImageurl3() {
        return imageurl3;
    }

    public void setImageurl3(String imageurl3) {
        this.imageurl3 = imageurl3;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMotherTongue() {
        return motherTongue;
    }

    public void setMotherTongue(String motherTongue) {
        this.motherTongue = motherTongue;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(String smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPrefGender() {
        return prefGender;
    }

    public void setPrefGender(String prefGender) {
        this.prefGender = prefGender;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getHideAge() {
        return hideAge;
    }

    public void setHideAge(Boolean hideAge) {
        this.hideAge = hideAge;
    }

    public Boolean getHideName() {
        return hideName;
    }

    public void setHideName(Boolean hideName) {
        this.hideName = hideName;
    }

    public Boolean getHideProfile() {
        return hideProfile;
    }

    public void setHideProfile(Boolean hideProfile) {
        this.hideProfile = hideProfile;
    }

    public Boolean getHideLocation() {
        return hideLocation;
    }

    public void setHideLocation(Boolean hideLocation) {
        this.hideLocation = hideLocation;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getTyping() {
        return typing;
    }

    public void setTyping(Boolean typing) {
        this.typing = typing;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(int ageRange) {
        this.ageRange = ageRange;
    }

    public boolean isLocationHidden() {
        return isLocationHidden;
    }

    public void setLocationHidden(boolean locationHidden) {
        isLocationHidden = locationHidden;
    }

    public boolean isAgeHidden() {
        return isAgeHidden;
    }

    public void setAgeHidden(boolean ageHidden) {
        isAgeHidden = ageHidden;
    }

    public boolean isNameHidden() {
        return isNameHidden;
    }

    public void setNameHidden(boolean nameHidden) {
        isNameHidden = nameHidden;
    }

    public boolean isProfileHidden() {
        return isProfileHidden;
    }

    public void setProfileHidden(boolean profileHidden) {
        isProfileHidden = profileHidden;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Map<String, Boolean> getLikesList() {
        return likesList;
    }

    public void setLikesList(Map<String, Boolean> likesList) {
        this.likesList = likesList;
    }

    public Map<String, Boolean> getVisitsList() {
        return visitsList;
    }

    public void setVisitsList(Map<String, Boolean> visitsList) {
        this.visitsList = visitsList;
    }

    public Map<String, Boolean> getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(Map<String, Boolean> blockedList) {
        this.blockedList = blockedList;
    }
}
