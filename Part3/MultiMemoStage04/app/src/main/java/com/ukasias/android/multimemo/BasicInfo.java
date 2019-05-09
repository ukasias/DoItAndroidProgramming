package com.ukasias.android.multimemo;

import java.text.SimpleDateFormat;

public class BasicInfo {

    public static String language = "ko";

    /**
     * 외장 메모리 패스
     */
    public static String ExternalPath = "/sdcard/";

    /**
     * 외장 메모리 패스 체크 여부
     */
    public static boolean ExternalPathChecked = false;

    /**
	 * 사진 저장 위치
	 */
	public static String FOLDER_PHOTO 		= "MultiMemo/photo/";

	/**
	 * 동영상 저장 위치
	 */
	public static String FOLDER_VIDEO 		= "MultiMemo/video/";

	/**
	 * 녹음 저장 위치
	 */
	public static String FOLDER_VOICE 		= "MultiMemo/voice/";

	/**
	 * 손글씨 저장 위치
	 */
	public static String FOLDER_HANDWRITING 	= "MultiMemo/handwriting/";

	/**
	 * 미디어 포맷
	 */
	public static final String URI_MEDIA_FORMAT		= "content://media";

	/**
	 * 데이터 베이스 이름
	 */
	public static String DATABASE_NAME = "MultiMemo/memo.db";


    /**
     * 날짜, 시간 포맷
     */
    public static SimpleDateFormat dateTimeKorFormat
			= new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
    public static SimpleDateFormat dateTimeFormat
			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat dateKorFormat
            = new SimpleDateFormat("yyyy년 MM월 dd일");
    public static SimpleDateFormat dateFormat
            = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat timeKorFormat
			= new SimpleDateFormat("HH시 mm분");
    public static SimpleDateFormat timeFormat
			= new SimpleDateFormat("HH:mm:ss");

	/**
	 * 인텐트 전달 시의 부가 정보
	 */
	public static final String KEY_MEMO_MODE = "MEMO_MODE";

	/**
	 * key MEMO_MODE에 쓰일 상수.
	 */
	public static final String MEMO_MODE_INSERT = "MODE_INSERT";
	public static final String MEMO_MODE_MODIFY = "MODE_MODIFY";
	public static final String MEMO_MODE_VIEW = "MODE_VIEW";

	/**
	 * 인텐트 전달 시 한 item(memo)에 대한 정보를 전달하기 위한 Key 값
	 */
	public static final String KEY_MEMO_ID 				= "MEMO_ID";
	public static final String KEY_MEMO_DATE 			= "MEMO_DATE";
	public static final String KEY_MEMO_TEXT			= "MEMO_TEXT";
	public static final String KEY_ID_PHOTO				= "ID_PHOTO";
	public static final String KEY_URI_PHOTO			= "URI_PHOTO";
	public static final String KEY_ID_VIDEO				= "ID_VIDEO";
	public static final String KEY_URI_VIDEO			= "URI_VIDEO";
	public static final String KEY_ID_VOICE				= "ID_VOICE";
	public static final String KEY_URI_VOICE			= "URI_VOICE";
	public static final String KEY_ID_HANDWRITING		= "ID_HANDWRITING";
	public static final String KEY_URI_HANDWRITING		= "URI_HANDWRITING";


	/**
	 * Activity에 대한 Request Code
	 */
	// MemoInsertActivity를 띄울 때의 request code
	public static final int REQ_INSERT_ACTIVITY = 1001;
	public static final int REQ_VIEW_ACTIVITY = 1002;

	// PhotoCaptureActivity, PhotoSelectionActivity를 띄울 때의 request code
	public static final int REQ_PHOTO_CAPTURE_ACTIVITY = 1501;
	public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;
	public static final int REQ_VIDEO_RECORDING_ACTIVITY = 1503;
	public static final int REQ_VIDEO_SELECTION_ACTIVITY = 1504;
	public static final int REQ_VOICE_RECORDING_ACTIVITY = 1505;
	public static final int REQ_HANDWRITING_MAKING_ACTIVITY = 1506;

	/**
	 *	각종 Activity에서 Dialog를 띄울 때의 키 값
	 *  이 키 값에 따라 Dialog의 구성을 달리한다.
	 */
	public static final int WARNING_INSERT_SDCARD = 1001;	/* Error 상황 */
	public static final int IMAGE_CANNOT_BE_STORED = 1002;


	public static final int CONTENT_PHOTO = 2001;
	public static final int CONTENT_VIDEO = 2002;
	public static final int CONTENT_VOICE = 2003;
	public static final int CONTENT_PHOTO_EX = 2005;
	public static final int CONTENT_VIDEO_EX = 2006;
	public static final int CONTENT_VOICE_EX = 2007;
	/**
	 * MemoInsertAcitivity에서의 Dialog를 띄울 때의 키 값
	 */
	public static final int CONFIRM_DELETE = 3001;
	public static final int CONFIRM_TEXT_INPUT = 3002;

	public static boolean isAbsoluteVideoPath(String videoUri) {
		if (videoUri.startsWith(URI_MEDIA_FORMAT)) {
			return false;
		}
		else {
			return true;
		}
	}
}
