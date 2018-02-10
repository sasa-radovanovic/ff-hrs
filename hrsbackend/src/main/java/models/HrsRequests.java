package models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Entity("hrs_requests")
public class HrsRequests {

    @Id
    private ObjectId id;

    @Reference
    @Getter
    @Setter
    private HrsUser user;

    @Property("start_date")
    @Getter
    @Setter
    private Date startDate;

    @Property("end_date")
    @Getter
    @Setter
    private Date endDate;


    @Property("approved_team_admin")
    @Getter
    @Setter
    private boolean approvedByTeamAdmin;


    @Property("approved_admin")
    @Getter
    @Setter
    private boolean approvedByAdmin;

    public HrsRequests() {}


    public HrsRequests(HrsUser user, Date startDate, Date endDate, boolean approvedByTeamAdmin, boolean approvedByAdmin) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvedByTeamAdmin = approvedByTeamAdmin;
        this.approvedByAdmin = approvedByAdmin;
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



}
