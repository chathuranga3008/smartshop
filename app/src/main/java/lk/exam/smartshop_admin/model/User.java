package lk.exam.smartshop_admin.model;

public class User {
    private String id;
    private String firstName;
    private String email;
    private String mobile;
    private String image;

    public User() {
    }

    public User(String id, String firstName, String email, String mobile, String image) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
