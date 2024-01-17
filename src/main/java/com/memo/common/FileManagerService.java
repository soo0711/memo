package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component // spirng bean으로 등록, auto werid를 할 수 있다.
public class FileManagerService { // 이미지 업로드, 이미지 삭제 

	// 실제 업로드 된 이미지가 저장될 경로(서버)
	// static final 상수 => 안 바꾼다.
	// 학원용 
	// 마지막에 꼭 / 넣어야한다.
	public static final String FILE_UPLOAD_PATH = "D:\\jeonsoohyun\\6_spring_project\\memo\\memo_workspace\\images/";

	// input: File 원본, userLoginId(폴더명)		output: 이미지 경로
	public String saveFile(String loginId, MultipartFile file) {
		// 폴더(디렉토리) 생성
		// 예: aaaa_1231324687/sun.png
		String directoryName = loginId + "_" + System.currentTimeMillis();
		String filePath = FILE_UPLOAD_PATH + directoryName; // D:\\jeonsoohyun\\6_spring_project\\memo\\memo_workspace\\images/aaaa_1231324687
		
		File directory = new File(filePath);
		if (directory.mkdir() == false) {
			// 폴더 생성 실패 시 이미지 경로 null로 리턴
			return null;
		}
		
		// 파일 업로드: byte 단위로 업로드
		try {
			byte[] bytes = file.getBytes();
			// ★★★ !!한글이름 이미지는 올릴 수 없으므로 나중에 영문자로 바꿔서 올리기!! ★★★
			Path path = Paths.get(filePath + "/" + file.getOriginalFilename());
			Files.write(path, bytes); // 실제 파일 업로드
		} catch (IOException e) {
			e.printStackTrace();
			return null; // 이미지 업로드 실패시 null 리턴
		}
		
		// path mapping 설정을 해줘야지  http://localhost/images/aaaa_1234354/gg.png
		// 파일 업로드가 성공했으면 웹 이미지 url path를 리턴
		
		return "/images/" + directoryName + "/" + file.getOriginalFilename() ;
	}
}
