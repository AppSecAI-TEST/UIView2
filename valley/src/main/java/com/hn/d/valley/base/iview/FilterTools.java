package com.hn.d.valley.base.iview;

import android.content.Context;

import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.filters.GPUImageColorBalanceFilter;
import com.m3b.rbrecoderlib.filters.GPUImageGrayscaleFilter;
import com.m3b.rbrecoderlib.filters.GPUImageMonochromeFilter;
import com.m3b.rbrecoderlib.filters.GPUImageRGBFilter;
import com.m3b.rbrecoderlib.filters.GPUImageWhiteBalanceFilter;
import com.m3b.rbrecoderlib.filters.MagiSkinWhitenFilter;
import com.m3b.rbrecoderlib.filters.MagicAmaroFilter;
import com.m3b.rbrecoderlib.filters.MagicBeautyFilter;
import com.m3b.rbrecoderlib.filters.MagicBlackCatFilter;
import com.m3b.rbrecoderlib.filters.MagicCalmFilter;
import com.m3b.rbrecoderlib.filters.MagicCoolFilter;
import com.m3b.rbrecoderlib.filters.MagicEmeraldFilter;
import com.m3b.rbrecoderlib.filters.MagicEvergreenFilter;
import com.m3b.rbrecoderlib.filters.MagicFairytaleFilter;
import com.m3b.rbrecoderlib.filters.MagicLatteFilter;
import com.m3b.rbrecoderlib.filters.MagicLomoFilter;
import com.m3b.rbrecoderlib.filters.MagicN1977Filter;
import com.m3b.rbrecoderlib.filters.MagicNostalgiaFilter;
import com.m3b.rbrecoderlib.filters.MagicRomanceFilter;
import com.m3b.rbrecoderlib.filters.MagicSierraFilter;
import com.m3b.rbrecoderlib.filters.MagicSunsetFilter;
import com.m3b.rbrecoderlib.filters.MagicWarmFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/07 18:25
 * 修改人员：Robi
 * 修改时间：2017/03/07 18:25
 * 修改备注：
 * Version: 1.0.0
 */
public class FilterTools {
    private static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
        switch (type) {
            case YUANHUA:
                GPUImageFilter yuanhuafilter = new GPUImageFilter();
                return yuanhuafilter;

            case BEAUTY:
                MagicBeautyFilter magicBeautyFilter = new MagicBeautyFilter(context);
                magicBeautyFilter.setBeautyLevel(3);
                return magicBeautyFilter;

            case SKINWHITEN:
                MagiSkinWhitenFilter magiSkinWhitenFilter = new MagiSkinWhitenFilter(context);
                return magiSkinWhitenFilter;


            case JINGDIAN:
                MagicAmaroFilter jingdianFilter = new MagicAmaroFilter(context);
                return jingdianFilter;

            case CALM:
                MagicCalmFilter magicCalmFilter = new MagicCalmFilter(context);
                return magicCalmFilter;

            case FUGU:
                GPUImageMonochromeFilter fuguFilter = new GPUImageMonochromeFilter(0.75F, new float[]{0.6F, 0.45F, 0.3F, 1.0F});
                return fuguFilter;

            case N1977:
                MagicN1977Filter magicN1977Filter = new MagicN1977Filter(context);
                return magicN1977Filter;

            case SIERRA:
                MagicSierraFilter magicSierraFilter = new MagicSierraFilter(context);
                return magicSierraFilter;

            case FAIRYTALE:
                MagicFairytaleFilter magicFairytaleFilter = new MagicFairytaleFilter(context);
                return magicFairytaleFilter;

            case FENNEN:
                GPUImageRGBFilter fennenFilter = new GPUImageRGBFilter(1.0F, 0.72F, 0.75F);
                return fennenFilter;


            case ROMANCE:
                MagicRomanceFilter magicRomanceFilter = new MagicRomanceFilter(context);
                return magicRomanceFilter;


            case EVERGREEN:
                MagicEvergreenFilter magicEvergreenFilter = new MagicEvergreenFilter(context);
                return magicEvergreenFilter;

            case HOUNIAO:
                MagicBlackCatFilter houniaoFilter = new MagicBlackCatFilter(context);
                return houniaoFilter;


            case EMERALD:
                MagicEmeraldFilter magicEmeraldFilter = new MagicEmeraldFilter(context);
                return magicEmeraldFilter;


            case HEIBAI:
                GPUImageGrayscaleFilter gpuImageGrayscaleFilter = new GPUImageGrayscaleFilter();
                return gpuImageGrayscaleFilter;

            case LOMO:
                MagicLomoFilter magicLomoFilter = new MagicLomoFilter(context);
                return magicLomoFilter;


            case SUNSET:
                MagicSunsetFilter magicSunsetFilter = new MagicSunsetFilter(context);
                return magicSunsetFilter;


            case COOL:
                MagicCoolFilter magicCoolFilter = new MagicCoolFilter(context);
                return magicCoolFilter;


            case NOSTALGIA:
                MagicNostalgiaFilter magicNostalgiaFilter = new MagicNostalgiaFilter(context);
                return magicNostalgiaFilter;

            case LATTE:
                MagicLatteFilter magicLatteFilter = new MagicLatteFilter(context);
                return magicLatteFilter;


            case WARM:
                MagicWarmFilter magicWarmFilter = new MagicWarmFilter(context);
                return magicWarmFilter;

            case DANYA:
                GPUImageWhiteBalanceFilter mWhiteBalanceFilter = new GPUImageWhiteBalanceFilter();
                mWhiteBalanceFilter.setTemperature(3600F);
                return mWhiteBalanceFilter;

            case GETEFENG:
                GPUImageColorBalanceFilter mColorBalanceFilter = new GPUImageColorBalanceFilter();
                mColorBalanceFilter.setMidtones(new float[]{0.15F, 0.33F, 0.22F});
                return mColorBalanceFilter;


            default:
                throw new IllegalStateException("No filter of that type!");
        }

    }

    public static List<FilterBean> createFilterList() {
        List<FilterBean> beanList = new ArrayList<>();
        beanList.add(new FilterBean("原画", FilterType.YUANHUA));
        beanList.add(new FilterBean("美颜", FilterType.BEAUTY));
        beanList.add(new FilterBean("美肤", FilterType.SKINWHITEN));
        beanList.add(new FilterBean("经典", FilterType.JINGDIAN));
        beanList.add(new FilterBean("月光", FilterType.CALM));
        beanList.add(new FilterBean("复古", FilterType.FUGU));
        beanList.add(new FilterBean("蔷薇", FilterType.N1977));
        beanList.add(new FilterBean("流年", FilterType.SIERRA));
        beanList.add(new FilterBean("童话", FilterType.FAIRYTALE));
        beanList.add(new FilterBean("粉嫩", FilterType.FENNEN));
        beanList.add(new FilterBean("浪漫", FilterType.ROMANCE));
        beanList.add(new FilterBean("梦幻", FilterType.EVERGREEN));
        beanList.add(new FilterBean("候鸟", FilterType.HOUNIAO));
        beanList.add(new FilterBean("薄荷", FilterType.EMERALD));
        beanList.add(new FilterBean("黑白", FilterType.HEIBAI));
        beanList.add(new FilterBean("慵懒", FilterType.LOMO));
        beanList.add(new FilterBean("黄昏", FilterType.SUNSET));
        beanList.add(new FilterBean("暮光", FilterType.COOL));
        beanList.add(new FilterBean("怀旧", FilterType.NOSTALGIA));
        beanList.add(new FilterBean("夕阳", FilterType.LATTE));
        beanList.add(new FilterBean("暖阳", FilterType.WARM));
        beanList.add(new FilterBean("淡雅", FilterType.DANYA));
        beanList.add(new FilterBean("哥特风", FilterType.GETEFENG));
        return beanList;
    }

    private enum FilterType {
        YUANHUA, JINGDIAN, FUGU, FENNEN, EMERALD, HOUNIAO, ANTIQUE, BLACKCAT, CALM, COOL, EVERGREEN, FAIRYTALE, LATTE, LOMO, N1977, ROMANCE, BEAUTY, SUNSET, CRAYON, SKETCH, SKINWHITEN, SIERRA, HEIBAI, NOSTALGIA, WARM, DANYA, GETEFENG
    }


    public static class FilterBean {
        public String name;
        public FilterType type;

        public FilterBean(String name, FilterType type) {
            this.name = name;
            this.type = type;
        }

        public GPUImageFilter createFilterForType(Context context) {
            return FilterTools.createFilterForType(context, type);
        }
    }

    private static class FilterList {
        public List<String> names = new LinkedList<String>();
        public List<FilterType> filters = new LinkedList<FilterType>();

        public void addFilter(final String name, final FilterType filter) {
            names.add(name);
            filters.add(filter);
        }
    }
}
