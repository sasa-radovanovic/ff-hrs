package models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Entity("hrs_config")
public class HrsConfig {

    @Id
    private ObjectId id;

    @Property("config_param")
    @Setter
    @Getter
    private String configParam;

    @Property("config_value")
    @Setter
    @Getter
    private String configValue;

    public HrsConfig() {}

    public HrsConfig(String configParam, String configValue) {
        this.configParam = configParam;
        this.configValue = configValue;
    }

    public ObjectId getId() {
        return (id != null) ? id : null;
    }


    public void setId(String id) {
        //this will prevent class cast exception
        //by morphia and in case it is null
        //nothing will happen
        if(id != null && !id.isEmpty()) {
            this.id = new ObjectId(id);
        }
    }

}
