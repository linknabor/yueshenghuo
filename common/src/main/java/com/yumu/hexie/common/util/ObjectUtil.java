package com.yumu.hexie.common.util;

import java.math.BigDecimal;

/**
 * <p>锟斤拷    锟斤拷: 锟斤拷锟侥匡拷锟�</p>
 * <p>锟斤拷    锟斤拷: 锟斤拷锟斤拷锟斤拷实锟斤拷锟斤拷</p>
 * <p>锟斤拷    权: Copyright (c) 2010</p>
 * <p>锟斤拷    司: 锟较猴拷锟斤拷锟斤拷锟斤拷息锟狡硷拷锟斤拷锟睫癸拷司</p>
 * <p>锟斤拷锟斤拷时锟斤拷: 2010-12-13 锟斤拷锟斤拷11:58:35</p>
 * @author 锟斤拷品锟斤拷锟斤拷锟斤拷
 * @version 2.0
 * ObjectUtil
 */
public class ObjectUtil {


	public static boolean isEmpty(String str) {

		if( str == null || str.trim().equals("") ) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Object[] array) {

		if( array == null || array.length == 0 ) {
			return true;
		}
		return false;
	}

	public static BigDecimal getZeroBigDecimal() {

		return new BigDecimal(0);
	}

}
