package org.nextprot.api.core.domain.release;

import java.io.Serializable;

/**
 * @author Valentine Rech de Laval
 * @since 2019-11-20
 */
public class ReleaseContentsStatQueries implements Serializable {

    private static final long serialVersionUID = -6011398362534026261L;

    private String tag, queryId;

    public ReleaseContentsStatQueries(String tag, String queryId) {
        this.tag = tag;
        this.queryId = queryId;
    }

    public String getTag() {
        return tag;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }
}
