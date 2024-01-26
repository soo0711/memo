package com.memo.post.bo;

import java.util.Collections;
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
	
	private static final int POST_MAX_SIZE = 3;
	
	// input: userId(로그인된 사람)		output: List<Post>
	public List<Post> getPostListById(int userId, Integer prevId, Integer nextId){
		// 게시글 번호: 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 4 3 2 페이지에 있을 때
		// 1) 다음: 2보다 작은 3개 DESC
		// 2) 이전: 4보다 큰 3개 ASC(5 6 7) => List reverse(7, 6, 5)
		// 3) 페이지 정보없음: 최신순 3개만 DESC
		
		Integer standardId = null; 	// 기준이 되는 postId
		String direction = null;	// 방향
		
		if (prevId != null) { // 2) 이전
			standardId = prevId;
			direction = "prev";
			List<Post> postList = postMapper.selectPostListById(userId, standardId, direction, POST_MAX_SIZE);
			Collections.reverse(postList);
			
			return postList;
			
		} else if (nextId != null) { // 3) 다음
			standardId = nextId;
			direction = "next";
		}
		
		// 3) 페이지 정보 없음, 1) 다음
		return postMapper.selectPostListById(userId, standardId, direction, POST_MAX_SIZE);
	}
	
	
	// 이전 페이지의 마지막인가?
	// input: userId, prevId		output: boolean
	public boolean isPrevLastPageByUserId(int userId, int prevId) {
		int postId = postMapper.selectPostIdByUserIdSort(userId, "DESC");
		return postId == prevId; // 같으면 마지막
	}
	
	public boolean isNextLastPageByUserId(int userId, int nextId) {
		int postId = postMapper.selectPostIdByUserIdSort(userId, "ASC");
		return postId == nextId; // 같으면 마지막
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
