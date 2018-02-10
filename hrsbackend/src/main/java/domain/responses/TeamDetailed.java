package domain.responses;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 12/30/17.
 */
@Data
public class TeamDetailed {

    public String name;

    public List<UserDetailed> users;

    public List<UserDetailed> teamAdmins;

}
