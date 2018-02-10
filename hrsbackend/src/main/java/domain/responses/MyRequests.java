package domain.responses;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 1/21/18.
 */
@Data
public class MyRequests {

    private List<HolidayRequestDetailed> myRequests;

    private List<HolidayRequestDetailed> teamRequests;

}
