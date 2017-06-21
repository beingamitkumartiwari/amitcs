package com.amtee.camscanner.utilities;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.camscanner/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.camscanner/files/";
		}
	}
}
