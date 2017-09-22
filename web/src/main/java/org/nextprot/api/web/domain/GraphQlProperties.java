package org.nextprot.api.web.domain;

/**
 * Created by dteixeir on 21.09.17.
 */
public class GraphQlProperties {
    public String getRootQueryName() {
        return rootQueryName;
    }

    public void setRootQueryName(String rootQueryName) {
        this.rootQueryName = rootQueryName;
    }

    public String getRootQueryDescription() {
        return rootQueryDescription;
    }

    public void setRootQueryDescription(String rootQueryDescription) {
        this.rootQueryDescription = rootQueryDescription;
    }

    public String getRootMutationName() {
        return rootMutationName;
    }

    public void setRootMutationName(String rootMutationName) {
        this.rootMutationName = rootMutationName;
    }

    public String getRootMutationDescription() {
        return rootMutationDescription;
    }

    public void setRootMutationDescription(String rootMutationDescription) {
        this.rootMutationDescription = rootMutationDescription;
    }

    private String rootQueryName = "queries";
    private String rootQueryDescription = "";
    private String rootMutationName = "mutations";
    private String rootMutationDescription = "";

}