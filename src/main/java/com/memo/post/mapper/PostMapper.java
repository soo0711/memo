package com.memo.post.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.memo.post.domain.Post;

@Mapper
public interface PostMapper {

	// input: X		output: List<Map>
	public List<Map<String, Object>> selectPostList();
	
	// input: userId	output:List<Post>
	public List<Post> selectPostListById(int userId);
}
