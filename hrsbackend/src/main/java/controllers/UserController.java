package controllers;

import com.google.gson.Gson;
import domain.requests.NewUser;
import domain.responses.UserDetailed;
import service.UserService;
import spark.Request;
import spark.Response;

/**
 * Created by sasaradovanovic on 12/30/17.
 */
public class UserController {

    private UserService userService;


    public UserController() {
        userService = new UserService();
    }



    public String addOrEditUser (Request request, Response response) {
        NewUser newUser = new Gson().fromJson(request.body(), NewUser.class);
        return new Gson().toJson(userService.createOrUpdateUser(newUser));
    }



    public String removeUser (Request request, Response response) {
        return new Gson().toJson(userService.removeUser(request.params("username")));
    }


    public String getUserDetailedData (Request request, Response response) {
        return new Gson().toJson(userService.getDetailedUserData(request.params("username")));
    }


    public String partialSearchOfUsers (String criteria) {
        return new Gson().toJson(userService.partialSearch(criteria));
    }


    public String checkIfEmailIsUsed (String criteria) {
        return new Gson().toJson(userService.isEmailInUse(criteria));
    }


    public String checkIfUsernameIsUsed (String criteria) {
        return new Gson().toJson(userService.isUsernameInUse(criteria));
    }
}
