package domain.responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasaradovanovic on 12/31/17.
 */
@Data
public class UserDetailed {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String team;

    private Boolean active;

    private List<String> adminTeams;

    private Boolean superAdmin;

    private ArrayList<HolidayRequest> requests;
}
