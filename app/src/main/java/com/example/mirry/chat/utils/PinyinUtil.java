package com.example.mirry.chat.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class PinyinUtil {

	/**
	 * 根据传入的字符串(包含汉字),得到拼音
	 * @param str 字符串
	 */
	public static String getPinyin(String str) {
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		
		StringBuilder sb = new StringBuilder();
		
		char[] charArray = str.toCharArray();
		for (char c : charArray) {
			// 如果是空格, 跳过
			if(Character.isWhitespace(c)){
				continue;
			}
			if((c >= 0x4e00)&&(c <= 0x9fbb)){
				//如果为汉字，转化为拼音
				String s = "";
				try {
					// 通过char得到拼音集合.
					s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
					sb.append(s);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					sb.append(s);
				}
			}else{
				//如果不是汉字，直接拼接
				sb.append(c);
			}
		}
		
		return sb.toString();
	}

}
