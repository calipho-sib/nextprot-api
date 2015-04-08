package org.nextprot.api.user.utils;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.domain.UserQuery;

import com.google.common.base.Function;

public class UserQueryUtils {

	public static List<UserQuery> filterByTag(List<UserQuery> queries, String tag) {

		List<UserQuery> res = new ArrayList<UserQuery>();
		for (UserQuery q : queries) {
			if (q.getTags().contains(tag)) {
				res.add(q);
			}
		}
		return res;
	}

	public static List<UserQuery> removeQueriesContainingTag(List<UserQuery> queries, String tag) {

		List<UserQuery> res = new ArrayList<UserQuery>();
		for (UserQuery q : queries) {
			if (!q.getTags().contains(tag)) {
				res.add(q);
			}
		}
		return res;
	}

	public static final Function<UserQuery, Long> EXTRACT_QUERY_ID = new Function<UserQuery, Long>() {
		@Override
		public Long apply(UserQuery query) {

			return query.getUserQueryId();
		}
	};

	public static String getTutoQueryNameFromId(long id) {
		return String.format("NXQ_%05d", id);
	}


}
