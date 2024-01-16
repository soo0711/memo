package com.memo.post.domain;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@ToString 
@Data // getters / setters 두개 다 있다.
public class Post {
	private int id;
	private int userId;
	private String subject;
	private String content;
	private String imagePath;
	private Date createdAt;
	private Date updatedAt;
	
}
