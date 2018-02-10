package service;

import database.DatabaseHelper;
import domain.ResponseBuilder;
import domain.requests.NewHolidayRequest;
import domain.responses.HolidayRequest;
import domain.responses.HolidayRequestDetailed;
import domain.responses.MyRequests;
import domain.responses.ValidDates;
import models.HrsRequests;
import models.HrsUser;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import utils.Constants;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sasaradovanovic on 1/9/18.
 */
public class RequestService {


    private DatabaseHelper databaseHelper;

    private ResponseBuilder responseBuilder;

    public RequestService() {
        databaseHelper = new DatabaseHelper();
        responseBuilder = new ResponseBuilder();
    }

    public HolidayRequest approveRequest(boolean superAdmin, String id) {
        HrsRequests hrsRequests = databaseHelper.getDataStore().createQuery(HrsRequests.class).field("_id").equal(new ObjectId(id)).get();
        if (superAdmin) {
            hrsRequests.setApprovedByAdmin(true);
        } else {
            hrsRequests.setApprovedByTeamAdmin(true);
        }
        databaseHelper.getDataStore().save(hrsRequests);
        return responseBuilder.hrsRequestToHolidayRequest(hrsRequests);
    }


    public HolidayRequest deleteRequest(String username, String id) {
        HrsRequests hrsRequests = databaseHelper.getDataStore().createQuery(HrsRequests.class).field("_id").equal(new ObjectId(id)).get();
        if (username != null) {
            if (hrsRequests.getUser().getUsername().equalsIgnoreCase(username)) {
                databaseHelper.getDataStore().delete(hrsRequests);
            }
        } else {
            databaseHelper.getDataStore().delete(hrsRequests);
        }
        return responseBuilder.hrsRequestToHolidayRequest(hrsRequests);
    }


    public List<HolidayRequestDetailed> pendingRequests() {
        Query<HrsRequests> query = databaseHelper.getDataStore().createQuery(HrsRequests.class);

        query.or(
                query.criteria("approved_team_admin").equal(false)
        ).or(
                query.criteria("approved_admin").equal(false)
        );

        List<HrsRequests> requests = query.asList();

        List<HolidayRequestDetailed> holidayRequests = new ArrayList<>();

        requests.forEach(r -> {
            holidayRequests.add(responseBuilder.hrsRequestToHolidayRequestDetailed(r));
        });

        return holidayRequests;
    }

    public List<HolidayRequestDetailed> approvedRequests() {

        Query<HrsRequests> query = databaseHelper.getDataStore().createQuery(HrsRequests.class);

        query.and(
                query.criteria("approved_team_admin").equal(true)
        ).and(
                query.criteria("approved_admin").equal(true)
        );

        List<HrsRequests> requests = query.asList();

        List<HolidayRequestDetailed> holidayRequests = new ArrayList<>();

        requests.forEach(r -> {
            holidayRequests.add(responseBuilder.hrsRequestToHolidayRequestDetailed(r));
        });


        return holidayRequests;
    }

    public MyRequests getMyRequests(String username) {

        MyRequests myRequests = new MyRequests();

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(username).get();

        if (hrsUser == null) {
            return myRequests;
        }

        List<HrsRequests> userRequests = databaseHelper.getDataStore().find(HrsRequests.class).disableValidation().field("user.$id").equal(hrsUser.getId()).asList();
        List<HolidayRequestDetailed> userReqDetailed = new ArrayList<>();
        userRequests.forEach(r -> {
            userReqDetailed.add(responseBuilder.hrsRequestToHolidayRequestDetailed(r));
        });
        myRequests.setMyRequests(userReqDetailed);

        List<HolidayRequestDetailed> teamRequestsDetailed = new ArrayList<>();

        if (hrsUser.getTeamsAdmin() != null && hrsUser.getTeamsAdmin().size() > 0) {
            hrsUser.getTeamsAdmin().forEach(team -> {
                List<HrsUser> teamUsers = databaseHelper.getDataStore().find(HrsUser.class).field("team").equalIgnoreCase(team).asList();
                if (teamUsers != null && teamUsers.size() > 0) {
                    teamUsers.forEach(teamUser -> {
                        List<HrsRequests> teamUserRequests = databaseHelper.getDataStore().find(HrsRequests.class).disableValidation().field("user.$id").equal(teamUser.getId()).asList();
                        if (teamUserRequests != null && teamUserRequests.size() > 0) {
                            teamUserRequests.forEach(teamUserRequest -> {
                                teamRequestsDetailed.add(responseBuilder.hrsRequestToHolidayRequestDetailed(teamUserRequest));
                            });
                        }
                    });
                }
            });
        }

        myRequests.setTeamRequests(teamRequestsDetailed);

        return myRequests;
    }

    public ValidDates validateDates(String username, LocalDate startDate, LocalDate endDate) {
        ValidDates validDates = new ValidDates();
        validDates.setValidDates(true);


        System.out.println("USERNAME IS " + username);

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(username).get();

        Query<HrsRequests> query = databaseHelper.getDataStore().createQuery(HrsRequests.class).disableValidation();

        query.and(
                query.criteria("user.$id").equal(hrsUser.getId())
        );

        List<HrsRequests> requests = query.asList();

        if (requests == null || requests.size() == 0) {
            validDates.setValidDates(true);
            return validDates;
        }

        Date startD = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endD = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        System.out.println("START DATE " + startD);
        System.out.println("END DATE " + endD);

        System.out.println("REQUESTS SIZE " + requests.size());

        loop:
        for (HrsRequests holidayRequest : requests) {

            System.out.println("REQUEST START " + holidayRequest.getStartDate());
            System.out.println("REQUEST END " + holidayRequest.getEndDate());

            System.out.println("CHECK START DATE BETWEEN REQUEST DATES");
            if (holidayRequest.getStartDate().before(startD) && holidayRequest.getEndDate().after(startD)) {
                System.out.println("NOT VALID 1");
                validDates.setValidDates(false);
                break loop;
            }
            System.out.println("CHECK REQUEST INSIDE CURRENT DATES");
            if (holidayRequest.getStartDate().after(startD) && holidayRequest.getEndDate().before(endD)) {
                System.out.println("NOT VALID 2");
                validDates.setValidDates(false);
                break loop;
            }
            System.out.println("CHECK END DATE BETWEEN REQUEST DATES");
            if (holidayRequest.getStartDate().before(endD) && holidayRequest.getEndDate().after(endD)) {
                System.out.println("NOT VALID 3");
                validDates.setValidDates(false);
                break loop;
            }
        }


        return validDates;

    }

    public HolidayRequest newRequest(String username, NewHolidayRequest newHolidayRequest) throws ParseException {

        HrsRequests hrsRequests = new HrsRequests();
        hrsRequests.setApprovedByTeamAdmin(false);
        hrsRequests.setApprovedByAdmin(false);
        hrsRequests.setStartDate(Constants.TimeConstants.simpleDateFormatter.parse(newHolidayRequest.getStartDate()));
        hrsRequests.setEndDate(Constants.TimeConstants.simpleDateFormatter.parse(newHolidayRequest.getEndDate()));

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(username).get();

        hrsRequests.setUser(hrsUser);

        databaseHelper.getDataStore().save(hrsRequests);

        return new ResponseBuilder().hrsRequestToHolidayRequest(hrsRequests);
    }
}
