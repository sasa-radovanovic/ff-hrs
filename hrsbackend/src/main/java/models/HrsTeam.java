package models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Entity("hrs_team")
public class HrsTeam {


    @Id
    private ObjectId id;

    @Property("team_name")
    @Setter
    @Getter
    private String teamName;


    @Property("team_members")
    @Setter
    @Getter
    private Integer teamMembers;

    public HrsTeam() {

    }

    public HrsTeam(String teamName) {
        this.teamName = teamName;
        this.teamMembers = 0;
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
        return "id = " + id +
                "\nteam_name = " + getTeamName() +
                "\nmembers = " + getTeamMembers() +
                "\n";

    }
}
