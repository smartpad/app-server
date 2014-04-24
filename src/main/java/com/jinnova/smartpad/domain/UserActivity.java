package com.jinnova.smartpad.domain;

import java.io.Serializable;

public class UserActivity implements Serializable {

	private static final long serialVersionUID = 18226521633617242L;

	public enum ActivityAction {
		
		LIKE(new SPAction() {

			public void action(Object... params) {
			}
		}),
		HIDE(new SPAction() {

			public void action(Object... params) {
			}
		});
		
		private interface SPAction {
			void action(Object... params);
		}
		
		private final SPAction action;
		
		private ActivityAction() {
			this(null);
		}
		
		private ActivityAction(SPAction action) {
			this.action = action;
		}
		
		public void action(Object... params) {
			if (action == null) {
				return;
			}
			action.action(params);
		}
	}
	
	private String userId;
	
	private String feedId;
	
	private ActivityAction action;

	public UserActivity() {
	}
	
	public UserActivity(String userId, String feedId, ActivityAction action) {
		super();
		this.userId = userId;
		this.feedId = feedId;
		this.action = action;
	}

	public String getUserId() {
		return userId;
	}

	public String getFeedId() {
		return feedId;
	}

	public ActivityAction getAction() {
		return action;
	}

	public void action(Object... params) {
		this.action.action(params);
	}
	
}