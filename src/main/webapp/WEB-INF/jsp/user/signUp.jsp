<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div>
	<h1 class="mb-4 text-center">회원가입</h1>
	<div class="d-flex justify-content-center">
		<form id="signUpForm" method="post" action="/user/sign-up">
			<table class="table table-bordered">
				<tr>
					<td class="font-weight-bold text-center bg-light">* 아이디(4자 이상)</td>
					<td>
						<div class="d-flex"> 
							<input type="text" class="form-control mr-3 col-7" name="loginId" id="loginId" placeholder="아이디를 입력하세요.">
							<button type="button" id="loginIdCheckBtn" class="btn btn-success rounded-pill">중복 확인</button>
						</div>
						<small id="idCheckLength" class="text-danger idCheckOk d-none">4자 이상 입력해주세요.</small>
						<small id="idCheckOk" class="text-success idCheckOk d-none">사용 가능한 아이디 입니다.</small>
						<small id="idCheckDuplicated" class="text-danger d-none">중복된 아이디 입니다.</small>
					</td>
				</tr>
				<tr>
					<td class="font-weight-bold text-center bg-light">* 비밀번호</td>
					<td><input type="password" class="form-control" id="password" name="password" placeholder="비밀번호를 입력하세요."></td>
				</tr>
				<tr>
					<td class="font-weight-bold text-center bg-light">* 비밀번호 확인</td>
					<td><input type="password" class="form-control" id="passwordConfirm" placeholder="비밀번호를 입력하세요."></td>
				</tr>
				<tr>
					<td class="font-weight-bold text-center bg-light">* 이름</td>
					<td><input type="text" class="form-control" id="name" name="name" placeholder="이름을 입력해주세요."></td>
				</tr>
				<tr>
					<td class="font-weight-bold text-center bg-light">* 이메일 주소</td>
					<td><input type="text" class="form-control" id="email" name="email" placeholder="이메일을 입력해주세요."></td>
				</tr>			
			</table>
			<button type="submit" id="signUpBtn" class="btn btn-primary float-right">회원가입</button>
		</form>
	</div>
</div>

<script>
	$(document).ready(function() {
		
		// 아이디 중복 확인
		$("#loginIdCheckBtn").on("click", function() {
			// alert("중복 확인");
			
			// 경고 문구 초기화
			$("#idCheckLength").addClass("d-none");
			$("#idCheckDuplicated").addClass("d-none");
			$("#idCheckOk").addClass("d-none");
			
			let loginId = $("#loginId").val().trim();
			if (loginId.length < 4){
				$("#idCheckLength").removeClass("d-none");
				return; // submit은 return false, button는 return만
			}
			
			/*
			$.ajax({
				// request
				url: "/user/is-duplicated-id"
				, data: {"loginId":loginId}
			
				//response
				, success: function(data) { // response
					if (data.code == 200){
						if (data.is_duplicated){ // 중복
							$("#idCheckDuplicated").removeClass("d-none");
						} else { // 사용 가능
							$("#idCheckOk").removeClass("d-none");
						}
					} else {
						alert(data.error_message)
					}
				}
				, error: function(request, status, error){
					alert("중복 확인에 실패했습니다.")
				}
			}); // - ajax
			*/
			
			$.get("/user/is-duplicated-id", {"loginId":loginId}) // request
			.done(function(data) { // response
				if (data.code == 200){
					if (data.is_duplicated){ // 중복
						$("#idCheckDuplicated").removeClass("d-none");
					} else { // 사용 가능
						$("#idCheckOk").removeClass("d-none");
					}
				} else {
					alert(data.error_message)
				}
			}); // - get
			
		}); // - loginIdCheckBtn
		
		// 회원가입
		$("#signUpForm").on("submit", function(e) {
			e.preventDefault(); // submit 기능 막음
			
			// alert("회원가입");
			
			// validation check
			let loginId = $("#loginId").val().trim();
			let password = $("#password").val();
			let passwordConfirm = $("#passwordConfirm").val();
			let name = $("#name").val().trim();
			let email = $("#email").val().trim();
			
			if (!loginId){
				alert("아이디를 입력하세요.");
				return false;
			}
			
			if (!password || !passwordConfirm){
				alert("비밀번호를 입력해주세요.")
				return false;
			}
			
			if (password != passwordConfirm){
				alert("비밀번호가 일치하지 않습니다.")
				return false;
			}
			
			if (!name){
				alert("이름을 입력해주세요.");
				return false;
			}
			
			if (!email){
				alert("이메일을 입력해주세요.");
				return false;
			}
			
			// 중복 확인 후 사용 가능한 아이디인지 확인
			// => idCheckOk 클래스 d-none이 없을 때
			if ($(".idCheckOk").hasClass("d-none")){
				alert("아이디 중복확인을 다시 해주세요.")
				return false;
			}
			
			// alert("회원가입");
			// 1) 서버 전송: submit을 js에서 동작시킴
			// $(this)[0].submit(); // 화면 이동이 된다.
			
			// 2) AJAX: 화면 이동 되지 않음. 응답값 JSON
			let url = $(this).attr("action");
			// alert(url);
			let params = $(this).serialize(); // form 태그에 있는 name 속성과 값으로 parameter를 구성
			console.log(params); // loginId=soo&password=d&name=soo&email=soo 이렇게 된 것을 requestBody에 넣으면 파라미터로 잘 들어간다.
			
			$.post(url, params) // request
			.done(function(data) { // response
				// {"code" : 200, "result" : "성공"}
				if (data.code == 200){
					alert("가입을 환영합니다. 로그인 해주세요.")
					location.href("/user/sign-in-view"); // 무조건 get방식
				} else {
					// 로직 실패
					alert(data.error_message);
				}
			}); // - post
			
		}); // - signUpBtn
	}); // - document
</script>