package org.kysubrse.tigernguyen.japaneseanalysis.function;

import java.io.File;
import java.util.List;

import org.kysubrse.tigernguyen.japaneseanalysis.data.OutputData;

public class Util {
	static File fileKanji = new File("DictionaryFile/jv(Hiragana).dd");
	static File fileKata = new File("DictionaryFile/jv(Katakana).dd");
	public static final String FILENAMEKANJI = fileKanji.getAbsolutePath();
	public static final String FILENAMEKATA = fileKanji.getAbsolutePath();

	// Find word from dictionary with input = kanji
	public static String findWord(List<String> dataList, String word) {
		String[] strLineArr;
		for (String string : dataList) {
			strLineArr = string.split("#");
			if (strLineArr.length > 3) {
				if (strLineArr[2].contains("「 " + word + " 」") || (strLineArr[2].equals(word))) {
					return OutputFormat(strLineArr[3]);
				} else if (strLineArr[3].contains("「 " + word + " 」")) {
					return OutputFormat(strLineArr[3], word);
				}
			}
		}
		return "";
	}

	// Find word from dictionary with input = katanana
	public static String findWordKata(List<String> dataList, String word) {
		String[] strLineArr;
		for (String string : dataList) {
			strLineArr = string.split("#");
			if (strLineArr.length > 3 && strLineArr[2].equals(word)) {
				return strLineArr[3];
			}
		}
		return "";
	}

	public static boolean isNotExist(List<OutputData> lstOutputData, String strWordJP) {
		if (lstOutputData != null && lstOutputData.size() > 0) {
			for (OutputData item : lstOutputData) {
				if (item.getStrWordJP().equals(strWordJP)) {
					return false;
				}
			}
		}
		return true;
	}

	private static String OutputFormat(String strResult) {
		String[] strOutput = strResult.replaceAll("\\|-\\|\\=", "\\|\\=").replaceAll("\\|\\=\\|\\=", "\\|\\=").split("\\|\\=");
		if (strOutput.length > 0) {
			if(strResult.startsWith("|=") && strOutput.length > 2 && JapaneseString.containsAlphabet(strOutput[2])) {
				return (strOutput[1] + " | " + strOutput[2]);
			}
			return (strOutput[1]);
		}
		return "";
	}

	private static String OutputFormat(String strResult, String word) {
		if (strResult.trim() == "")
			return "";
		String[] strOutput = strResult.replaceAll("\\|-\\|\\=", "\\|\\=").replaceAll("\\|\\=\\|\\=", "\\|\\=").split("\\|\\=");
		for (int count = 0; count < strOutput.length; count++) {
			if (strOutput[count].contains("「 " + word + " 」") && count < strOutput.length - 2) {
				for (int countWord = count; countWord < strOutput.length; countWord++)
					if (!strOutput[countWord + 1].equals("")) {
						if(countWord <= strOutput.length - 3 && JapaneseString.containsAlphabet(strOutput[countWord + 2])) {
							return (strOutput[countWord + 1] + " | " + strOutput[countWord + 2]);
						}
						return strOutput[countWord + 1];
					}
			}
		}
		return "";
	}

}
