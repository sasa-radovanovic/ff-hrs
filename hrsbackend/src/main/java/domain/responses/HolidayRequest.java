package domain.responses;

import lombok.Data;

/**
 * Created by sasaradovanovic on 12/31/17.
 */
@Data
public class HolidayRequest {

    private String id;

    private String startDate;

    private String endDate;

    private Boolean approvedByTeamAdmin;

    private Boolean approvedBySuperAdmin;
}
