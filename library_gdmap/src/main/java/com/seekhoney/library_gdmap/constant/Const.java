package com.seekhoney.library_gdmap.constant;

import android.graphics.Color;

/**
 * @since 3.3.0
 * Created by hongming.wang on 2016/12/19.
 */

public class Const {
	/**
	 * 地图中绘制多边形、圆形的边界颜色
	 * @since 3.3.0
	 */
	public static final int STROKE_COLOR = Color.argb(180, 63, 145, 252);
	/**
	 * 地图中绘制多边形、圆形的填充颜色
	 * @since 3.3.0
	 */
	public static final int FILL_COLOR = Color.argb(163, 118, 212, 243);

	/**
	 * 地图中绘制多边形、圆形的边框宽度
	 * @since 3.3.0
	 */
	public static final float STROKE_WIDTH = 5F;

	/**
	 * 返回码: 传值错误
	 * */
	public static final int BAD_REQUEST = -2;

	/**
	 * 返回码 : 定位失败
	 * */
	public static final int FAIL_LOCATE = -1;

	/**
	 * 返回码 : 在范围内
	 * */
	public static final int WITHIN_AREA = 0;

	/**
	 * 返回码 : 不在范围内
	 * */
	public static final int WITHOUT_AREA = 1;

	/**
	 * Handler 用
	 * */
	public static final int LOAD_IMG = 2;

	/**
	 * 权限备用
	 * */
	public static final int READ_CONTACTS = 0xE9;
	public static final int GET_LOCATIONS = 0xE7;
	public static final int READ_STORAGE = 0xE8;

	/**
	 * sharePreference用
	 * */
	public static final String SP_SELECT = "SP_SELECT";
	public static final String KEY_HISTORY = "history";


	public static final int REQUEST_CODE = 3;
	public static final int RESPONSE_CODE = 4;

}
