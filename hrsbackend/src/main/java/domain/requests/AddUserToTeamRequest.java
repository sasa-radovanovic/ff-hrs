package domain.requests;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 12/30/17.
 */
@Data
public class AddUserToTeamRequest {

    private String team;

    private String username;

}
