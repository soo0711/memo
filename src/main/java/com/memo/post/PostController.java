package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/post")
public class PostController {

	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post-list-view")
	public String postListView(
			@RequestParam(value = "prevId", required = false) Integer prevIdParam,
			@RequestParam(value = "nextId", required = false) Integer nextIdParam,
			Model model,
			HttpSession session) { // session은 request시 받아온다.
		// 로그인 여부 조회
		Integer userId = (Integer)session.getAttribute("userId"); // casting 해줘야한다.
		
		// 비로그인이면 로그인 페이지로 이동
		if (userId == null) {
			return "redirect:/user/sign-in-view";
		}
		
		// DB 글 목록 조회 - mybatis로 만들기 
		List<Post> post = postBO.getPostListById(userId, prevIdParam, nextIdParam);
		int nextId = 0;
		int prevId = 0;
		
		if (post.isEmpty() == false) {
			// postList가 비어있을 때 [] 오류를 방지하기 위함
			prevId = post.get(0).getId();
			nextId = post.get(post.size() - 1).getId();
			
			// 이전 방향의 끝인가?
			// prevId와 post 테이블의 가장 큰 id값과 같으면 이전 페이지 없음
			if (postBO.isPrevLastPageByUserId(userId, prevId)) {
				prevId = 0;
			}
			// 다음 방향의 끝인가?
			// nextId와 post 테이블의 가장 작은 id값과 같으면 다음 페이지 없음
			if (postBO.isNextLastPageByUserId(userId, nextId)){
				nextId = 0;
			}
		}
		model.addAttribute("prevId" ,prevId);
		model.addAttribute("nextId" ,nextId);
		model.addAttribute("postList", post);
		model.addAttribute("viewName", "post/postList");
		
		return "template/layout";
	}
	
	/**
	 * 글 쓰기 화면
	 * @param model
	 * @return
	 */
	@GetMapping("/post-create-view")
	public String postCreateView(
			Model model) {
		// 권한 검사 한번에 넣을 예정이라서 생략
		model.addAttribute("viewName", "post/postCreate");
		return "template/layout";
	}
	
	@GetMapping("/post-detail-view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			Model model,
			HttpSession session) {
		
		int userId = (int)session.getAttribute("userId");
		
		// DB조회 - postId + userId
		Post post = postBO.getPostByPostIdUserId(postId, userId);
		
		model.addAttribute("post", post);
		model.addAttribute("viewName", "post/postDetail");
		return "template/layout";
	}
	
	
}