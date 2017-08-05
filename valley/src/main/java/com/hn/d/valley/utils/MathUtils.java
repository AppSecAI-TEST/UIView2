package com.hn.d.valley.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/3
 * 修改人员：cjh
 * 修改时间：2017/8/3
 * 修改备注：
 * Version: 1.0.0
 */
public class MathUtils {

    public static float nextFloat(float min , float max) {
        return min + ((float)Math.random() * (max - min));
    }

    public static int nextInt(int min , int max) {
        long seed1 = System.nanoTime();
        Random random = new Random(seed1);
        return min + random.nextInt(max - min);
    }

    public static float decimal(float amount,int scale) {
        BigDecimal b = new BigDecimal(amount);
        return b.setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static List<Integer> buildPokers(int count) {
        List<Integer> values = new ArrayList<>();
        Random random = new Random(System.nanoTime());
        while (values.size() != count) {
            int anInt = random.nextInt(54);
            if (!values.contains(anInt)) {
                values.add(anInt);
            }
        }
        return values;
    }

}
