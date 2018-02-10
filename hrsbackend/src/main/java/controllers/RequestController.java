package controllers;

import com.google.gson.Gson;
import domain.requests.NewHolidayRequest;
import domain.requests.NewTeamRequest;
import service.RequestService;
import spark.Request;
import spark.Response;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by sasaradovanovic on 1/9/18.
 */
public class RequestController {

    private RequestService requestService;

    public RequestController() {
        this.requestService = new RequestService();
    }


    public String approveRequest(boolean superAdmin, String id) {
        return new Gson().toJson(requestService.approveRequest(superAdmin, id));
    }

    public String deleteRequest(String username, String id) {
        return new Gson().toJson(requestService.deleteRequest(username, id));
    }

    public String pendingRequests () {
        return new Gson().toJson(requestService.pendingRequests());
    }

    public String approveRequests() {
        return new Gson().toJson(requestService.approvedRequests());
    }

    public String getMyRequests(String username) {
        return new Gson().toJson(requestService.getMyRequests(username));
    }

    public String validateDates(String username, LocalDate startDate, LocalDate endDate) {
        return new Gson().toJson(requestService.validateDates(username, startDate, endDate));
    }

    public String newRequest(Request req, Response res) throws ParseException {
        NewHolidayRequest newHolidayRequest = new Gson().fromJson(req.body(), NewHolidayRequest.class);
        String username;
        if (req.session() != null && req.session().attribute("username") != null) {
            username = req.session().attribute("username");
        } else {
            username = "timko.adminko";
        }
        return new Gson().toJson(requestService.newRequest(username, newHolidayRequest));
    }
}
