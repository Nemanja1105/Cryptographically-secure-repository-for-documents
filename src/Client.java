import java.util.List;

public class Client {
    private String username;//unique
    private String passwordHash;//password or hash

    private String countryName;
    private String stateName;
    private String cityName;
    private String organizationName;
    private String organizationalUnitName;
    private String email;

    private List<String> storedDocuments;
    private String clientKeyPath;

    public Client() {
        super();
    }

    public Client(String username, String passwordHash, String countryName, String stateName, String cityName, String organizationName, String organizationalUnitName, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.countryName = countryName;
        this.stateName = stateName;
        this.cityName = cityName;
        this.organizationName = organizationName;
        this.organizationalUnitName = organizationalUnitName;
        this.email = email;
    }


    public String getUsername() {
        return this.username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public String getStateName() {
        return this.stateName;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public String getOrganizationalUnitName() {
        return this.organizationalUnitName;
    }

    public String getEmail() {
        return this.email;
    }

    public List<String> getStoredDocuments() {
        return this.storedDocuments;
    }

    public String getClientKeyPath() {
        return this.clientKeyPath;
    }

    public void setStoredDocuments(List<String> value) {
        this.storedDocuments = value;
    }

    public void setClientKeyPath(String value) {
        this.clientKeyPath = value;
    }

    @Override
    public String toString() {
        return this.username + " " + this.passwordHash + " " + this.countryName + " " + this.stateName + " " + this.cityName + " " + this.organizationName + " " + this.organizationalUnitName + " " + this.email;
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}
