package com.social.beFriendly.tools;

import com.social.beFriendly.service.FriendService;

public class DbAggregationTool {
public static void main(String args[]){
	FriendService friendService = new FriendService();
	friendService.aggregation("5af546bee2e9a70900b73333");
}
}
