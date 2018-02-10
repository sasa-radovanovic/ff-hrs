package domain.requests;

import lombok.Data;

/**
 * Created by sasaradovanovic on 2/5/18.
 */
@Data
public class NewHolidayRequest {

    private String startDate;

    private String endDate;

}
