package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.domain.Post;
import com.memo.post.mapper.PostMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostBO {

	// private Logger logger = LoggerFactory.getLogger(PostBO.class);
	// private Logger logger = LoggerFactory.getLogger(this.getClass());
	// @Slf4j을 사용했을 때 위와 같다.
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private FileManagerService fileManagerService;
	
	// input: userId(로그인된 사람)		output: List<Post>
	public List<Post> getPostListById(int userId){
		return postMapper.selectPostListById(userId);
	}
	
	// input: params		output: X
	public void addPost(int userId, String userLoginId, 
			String subject, String content, MultipartFile file) {
		
		String imagePath = null;
		
		// 업로드할 이미지가 있을 때 업로드
		if (file != null) {
			imagePath = fileManagerService.saveFile(userLoginId, file);
		}
		
		postMapper.insertPost(userId, userLoginId, subject, content, imagePath);
	}
	
	// input: postId + userId		output: Post
	public Post getPostByPostIdUserId(int postId, int userId) {
		return postMapper.selectPostListByPostIdUserId(postId, userId);
	}
	
	// input: params 			output: X
	public void updatePostByPostId(int userId, String userLoginId,
									int postId, String subject, String content,
									MultipartFile file) {
		// 기존글을 가져온다. (1. 이미지 교체시 삭제하기 위해  
		//				 2. 업데이트 대상이 있는지 확인)
		Post post = postMapper.selectPostListByPostIdUserId(postId, userId);
		if (post == null) {
			log.info(" [글 수정] post is null. postId: {}, userId: {}", postId, userId);
			return;
		}
		
		// 파일이 있다면
		// 1) 새 이미지를 업로드 한다.
		// 2) 1번 단계가 성공하면 기존 이미지 제거 (기존 이미지가 있다면)
		String imagePath = null;
		
		if (file != null) {
			// 업로드
			imagePath = fileManagerService.saveFile(userLoginId, file);
			// 업로드 성공 시 기존 이미지가 있으면 제거
			if (imagePath != null && post.getImagePath() != null) {
				// 업로드 성공 기존 이미지 있으면 서버의 파일 제거
				fileManagerService.deleteFile(post.getImagePath());
			}
		}
		
		// DB 업데이트
		postMapper.updatePostByPostId(postId, subject, content, imagePath);
		
	}
	
	
	// input: postId, userId	output: X
	public void deletePostByPostIdUserId(int postId, int userId) {
		
		// 기존글이 있는지 확인
		Post post = postMapper.selectPostListByPostIdUserId(postId, userId);
		if (post == null) {
			log.info("[글 삭제] post is null. postId: {}", postId);
			return;
		}
		
		// db 삭제
		int deleteRowCount = postMapper.deletePostByPostIdUserId(postId, userId);
		
		// image가 존재하면 삭제 & DB 삭제도 성공
		if (deleteRowCount > 0 && post.getImagePath() != null) {
			// 파일 삭제
			fileManagerService.deleteFile(post.getImagePath());
		}

	}
}
