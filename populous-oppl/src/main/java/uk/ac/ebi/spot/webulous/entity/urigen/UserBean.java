package uk.ac.ebi.spot.webulous.entity.urigen;

/**
 * @author Simon Jupp
 * @date 31/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class UserBean {

    private int id;
    private String userName;
    private String email;
    private String apiKey;
    private boolean admin;

    private UserBean() {
    }

    public UserBean(int id, String userName, String email, String apiKey, boolean admin) {

        this.id = id;
        this.userName = userName;
        this.email = email;
        this.apiKey = apiKey;
        this.admin = admin;
    }

    public int getId () {
        return id;
    }
    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", admin=" + admin +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }
}