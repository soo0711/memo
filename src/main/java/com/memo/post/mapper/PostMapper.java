package com.memo.post.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.memo.post.domain.Post;

@Mapper
public interface PostMapper {

	// input: X		output: List<Map>
	public List<Map<String, Object>> selectPostList();
	
	// input: userId	output:List<Post>
	public List<Post> selectPostListById(int userId);
	
	// input: userId, subject, content		output: X
	public void insertPost(
			@Param("userId") int userId,
			@Param("userLoginId") String userLoginId,
			@Param("subject") String subject,
			@Param("content") String content,
			@Param("imagePath") String imagePath);
	
	// input: postId + userId		output: Post
	public Post selectPostListByPostIdUserId(
			@Param("postId") int postId,
			@Param("userId") int userId);
}
