package org.simmi.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EinkunnSerializable implements IsSerializable {
	private String	grader;
	private int		grade;
	private String	comment;
	
	public EinkunnSerializable() {
		grade = 0;
	}
	
	public EinkunnSerializable( String grader, int grade, String comment ) {
		this.grade = grade;
		this.comment = comment;
		this.grader = grader;
	}
	
	public String getGrader() {
		return grader;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public String getComment() {
		return comment;
	}
}
