package database;

import com.mongodb.MongoClient;
import models.HrsConfig;
import models.HrsRequests;
import models.HrsTeam;
import models.HrsUser;
import net.sf.cglib.core.Local;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import utils.Constants;
import utils.PasswordUtil;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
public class DatabaseHelper {


    private static Morphia morphia = new Morphia();
    private static Datastore datastore = null;


    public DatabaseHelper() {
        if (!modelsMapped()) {
            morphia.map(HrsUser.class);
            morphia.map(HrsTeam.class);
            morphia.map(HrsConfig.class);
            morphia.map(HrsRequests.class);
            initDatastore();
            injectTestData();
        } else {
            System.out.println("Database already mapped");
        }

    }

    private void initDatastore() {

        MongoClient mongoClient = new MongoClient(Constants.Database.DB_HOST, Constants.Database.DB_PORT);
        datastore = morphia.createDatastore(mongoClient, Constants.Database.DB_NAME);

        datastore.ensureIndexes();

        System.out.println("Database initialized and database connection prepared...");

    }


    public Datastore getDataStore() {
        if (datastore == null) {
            initDatastore();
        }
        return datastore;
    }


    private boolean modelsMapped() {
        if (!morphia.isMapped(HrsUser.class)) {
            return false;
        }
        if (!morphia.isMapped(HrsTeam.class)) {
            return false;
        }
        if (!morphia.isMapped(HrsRequests.class)) {
            return false;
        }
        if (!morphia.isMapped(HrsConfig.class)) {
            return false;
        }
        return true;
    }


    private void injectTestData() {

        HrsUser adminIsThere = getDataStore().createQuery(HrsUser.class).field("username").equalIgnoreCase("admin").get();

        if (adminIsThere == null) {
            HrsUser adminUser = new HrsUser();
            adminUser.setFirstName("Sasa");
            adminUser.setLastName("Radovanovic");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@sasa.com");
            adminUser.setSuperAdmin(true);
            adminUser.setTeam("");
            adminUser.setActive(true);
            adminUser.setTeamsAdmin(new ArrayList<>());
            adminUser.setPassword(PasswordUtil.hashPassword("Fiorentina2"));

            getDataStore().save(adminUser);


            HrsUser user1 = new HrsUser();
            user1.setFirstName("User1");
            user1.setLastName("1");
            user1.setUsername("user1");
            user1.setEmail("user1@sasa.com");
            user1.setSuperAdmin(false);
            user1.setTeam("myTeam");
            user1.setTeamsAdmin(new ArrayList<>());
            user1.setPassword("user1");
            user1.setActive(true);

            getDataStore().save(user1);

            HrsRequests hrsRequests = new HrsRequests();

            hrsRequests.setApprovedByAdmin(false);
            hrsRequests.setApprovedByTeamAdmin(false);
            LocalDate localDate = LocalDate.now().plusDays(7);
            LocalDate localDate2 = LocalDate.now().plusDays(14);
            hrsRequests.setStartDate(Constants.TimeConstants.fromLocalDate(localDate));
            hrsRequests.setEndDate(Constants.TimeConstants.fromLocalDate(localDate2));
            hrsRequests.setUser(user1);

            getDataStore().save(hrsRequests);


            HrsUser user2 = new HrsUser();
            user2.setFirstName("User2");
            user2.setLastName("2");
            user2.setUsername("user2");
            user2.setEmail("user2@sasa.com");
            user2.setSuperAdmin(false);
            user2.setTeam("myTeam");
            user2.setTeamsAdmin(new ArrayList<>());
            user2.setPassword("user2");
            user2.setActive(true);



            getDataStore().save(user2);

            HrsUser teamAdmin = new HrsUser();
            teamAdmin.setFirstName("Timko");
            teamAdmin.setLastName("Adminko");
            teamAdmin.setUsername("timko.adminko");
            teamAdmin.setEmail("timko.adminko@sasa.com");
            teamAdmin.setSuperAdmin(false);
            teamAdmin.setTeam("myTeam2");
            ArrayList<String> teamAdminTeams = new ArrayList<>();
            teamAdminTeams.add("myTeam");
            teamAdmin.setTeamsAdmin(teamAdminTeams);
            teamAdmin.setPassword(PasswordUtil.hashPassword("Fiorentina2"));
            teamAdmin.setActive(true);

            HrsRequests hrsR1 = new HrsRequests();

            hrsR1.setApprovedByAdmin(false);
            hrsR1.setApprovedByTeamAdmin(true);
            LocalDate ld11 = LocalDate.now().plusDays(7);
            LocalDate ld21 = LocalDate.now().plusDays(14);
            hrsR1.setStartDate(Constants.TimeConstants.fromLocalDate(ld11));
            hrsR1.setEndDate(Constants.TimeConstants.fromLocalDate(ld21));
            hrsR1.setUser(teamAdmin);

            getDataStore().save(teamAdmin);

            getDataStore().save(hrsR1);


            for (int i=0; i < 50; i++) {
                HrsUser u = new HrsUser();
                u.setFirstName("User " + i);
                u.setLastName("Useric " + (i+1110));
                u.setUsername("Useruseric" + i);
                u.setEmail("user." + i + "@sasa.com");
                u.setSuperAdmin(false);
                u.setTeam("");
                u.setTeamsAdmin(new ArrayList<>());
                u.setPassword("user2");

                if (i % 2 ==0) {
                    u.setActive(true);
                } else {
                    u.setActive(false);
                }

                getDataStore().save(u);

                HrsRequests hrsR = new HrsRequests();

                hrsR.setApprovedByAdmin(false);
                hrsR.setApprovedByTeamAdmin(true);
                LocalDate ld1 = LocalDate.now().plusDays(7);
                LocalDate ld2 = LocalDate.now().plusDays(14);
                hrsR.setStartDate(Constants.TimeConstants.fromLocalDate(ld1));
                hrsR.setEndDate(Constants.TimeConstants.fromLocalDate(ld2));
                hrsR.setUser(u);

                getDataStore().save(hrsR);

            }

        }


        HrsTeam hrsTeam = getDataStore().createQuery(HrsTeam.class).field("team_name").equalIgnoreCase("myTeam").get();


        if (hrsTeam == null) {
            hrsTeam = new HrsTeam("myTeam");
            hrsTeam.setTeamMembers(2);
            getDataStore().save(hrsTeam);
            HrsTeam team2 = new HrsTeam("myTeam2");
            team2.setTeamMembers(1);
            getDataStore().save(team2);

            for (int i=0; i<50; i++) {
                HrsTeam testTeam = new HrsTeam("testTeam" + i);
                getDataStore().save(testTeam);
            }
        }


    }

}
