package org.nextprot.api.core.domain.publication;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObjectField;

public abstract class BookResourceLocator extends PublicationResourceLocator  implements Serializable {

    private static final long serialVersionUID = 0L;
	
    @ApiObjectField(description = "The first page")
    private String firstPage;

    @ApiObjectField(description = "The last page")
    private String lastPage;

    public String getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    public String getLastPage() {
        return lastPage;
    }

    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }

}
