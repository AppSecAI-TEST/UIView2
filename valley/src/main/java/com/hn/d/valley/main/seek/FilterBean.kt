package com.hn.d.valley.main.seek

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/08/04 15:07
 * 修改人员：Robi
 * 修改时间：2017/08/04 15:07
 * 修改备注：
 * Version: 1.0.0
 */
data class FilterBean(var distance: String?,
                      var age_start: String?,
                      var age_end: String?,
                      var sex: String?,
                      var c: String?)

/**sex	否	int	1-男 2-女 0-全部【默认0】*/

/**星座说明

ID	备注	说明
1	水瓶座	--
2	双鱼座	--
3	白羊座	--
4	金牛座	--
5	双子座	--
6	巨蟹座	--
7	狮子座	--
8	处女座	--
9	天秤座	--
10	天蝎座	--
11	射手座	--
12	摩羯座*/