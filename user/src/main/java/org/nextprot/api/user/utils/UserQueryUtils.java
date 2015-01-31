package org.nextprot.api.user.utils;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.user.domain.UserQuery;

public class UserQueryUtils {

	public static List<UserQuery> filterByTag(List<UserQuery> queries, String tag) {
		
		List<UserQuery> res = new ArrayList<UserQuery>();
		for(UserQuery q : queries){
			if(q.getTags().contains(tag)){
				res.add(q);
			}
		}
		return res;
	}
	
	public static List<UserQuery> removeQueriesContainingTag(List<UserQuery> queries, String tag) {
		
		List<UserQuery> res = new ArrayList<UserQuery>();
		for(UserQuery q : queries){
			if(!q.getTags().contains(tag)){
				res.add(q);
			}
		}
		return res;
	}

}
