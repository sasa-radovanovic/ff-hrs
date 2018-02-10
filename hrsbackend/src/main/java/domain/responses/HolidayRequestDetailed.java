package domain.responses;

import lombok.Data;

/**
 * Created by sasaradovanovic on 1/20/18.
 */
@Data
public class HolidayRequestDetailed {

    private String id;

    private String startDate;

    private String endDate;

    private Boolean approvedByTeamAdmin;

    private Boolean approvedBySuperAdmin;

    private String firstName;

    private String lastName;

    private String team;

    private String username;

}




