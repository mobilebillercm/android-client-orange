package cm.softinovplus.mobilebiller.orange.utils;

/**
 * Created by nkalla on 19/10/18.
 */

public class Tenant {

    private String name, id;

    public Tenant( String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
