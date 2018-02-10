package models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.util.List;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Entity("hrs_user")
public class HrsUser {

    @Id
    private ObjectId id;

    @Indexed
    @Property("username")
    @Setter
    @Getter
    private String username;

    @Indexed
    @Property("email")
    @Setter
    @Getter
    private String email;

    @Property("password")
    @Setter
    @Getter
    private String password;

    @Property("active")
    @Setter
    @Getter
    private Boolean active;

    @Property("super_admin")
    @Setter
    @Getter
    private Boolean superAdmin;

    @Property("team")
    @Setter
    @Getter
    private String team;

    @Property("first_name")
    @Setter
    @Getter
    private String firstName;

    @Property("last_name")
    @Setter
    @Getter
    private String lastName;

    @Property("team_admin")
    @Setter
    @Getter
    private List<String> teamsAdmin;


    public HrsUser () {

    }

    public HrsUser(ObjectId id, String username, String email, String password, String team, String firstName, String lastName, List<String> teamsAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.team = team;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamsAdmin = teamsAdmin;
    }

    public ObjectId getId() {
        return (id != null) ? id : null;
    }


    public void setId(String id) {
        //this will prevent class cast exception
        //by morphia and in case it is null
        //nothing will happen
        if(id != null && !id.isEmpty()) {
            this.id = new ObjectId(id);
        }
    }


    @Override
    public String toString() {
        String basicInfo = "id = " + id +
                "\nfirst_name = " + getFirstName() +
                "\nlast_name = " + getLastName() +
                "\nusername = " + getUsername() +
                "\npassword = " + getPassword() +
                "\nteam = " + getTeam() +
                "\nemail = " + getEmail() +
                "\nsuperadmin = " + getSuperAdmin() +
                "\n";

        basicInfo += "teams admin = ";

        for (String team : getTeamsAdmin()) {
            basicInfo += "\nteam: " + team;
        }

        basicInfo += "\n";

        return basicInfo;
    }

}
