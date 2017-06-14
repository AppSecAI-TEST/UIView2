package com.m3b.rbrecoderlib;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.Matrix;
import android.text.Html;

import com.m3b.rbrecoderlib.filters.*;


import java.util.LinkedList;
import java.util.List;


public class GPUImageFilterTools {
    public static void showDialog(final Context context,
            final OnGpuImageFilterChosenListener listener) {
        final FilterList filters = new FilterList();
        filters.addFilter("原画",FilterType.YUANHUA);
        filters.addFilter("美颜",FilterType.BEAUTY);
        filters.addFilter("美肤",FilterType.SKINWHITEN);
        filters.addFilter("经典",FilterType.JINGDIAN);
        filters.addFilter("月光",FilterType.CALM);
        filters.addFilter("复古",FilterType.FUGU);
        filters.addFilter("蔷薇",FilterType.N1977);
        filters.addFilter("流年",FilterType.SIERRA);
        filters.addFilter("童话",FilterType.FAIRYTALE);
        filters.addFilter("粉嫩",FilterType.FENNEN);
        filters.addFilter("浪漫",FilterType.ROMANCE);
        filters.addFilter("梦幻",FilterType.EVERGREEN);
        filters.addFilter("候鸟",FilterType.HOUNIAO);
        filters.addFilter("薄荷",FilterType.EMERALD);
        filters.addFilter("黑白",FilterType.HEIBAI);
        filters.addFilter("慵懒",FilterType.LOMO);
        filters.addFilter("黄昏",FilterType.SUNSET);
        filters.addFilter("暮光",FilterType.COOL);
        filters.addFilter("怀旧",FilterType.NOSTALGIA);
        filters.addFilter("夕阳",FilterType.LATTE);
        filters.addFilter("暖阳",FilterType.WARM);
        filters.addFilter("淡雅",FilterType.DANYA);
        filters.addFilter("哥特风",FilterType.GETEFENG);



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter");
        builder.setItems(filters.names.toArray(new String[filters.names.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int item) {
                        listener.onGpuImageFilterChosenListener(
                                createFilterForType(context, filters.filters.get(item)));
                    }
                });
        builder.create().show();
    }


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
                MagicSierraFilter magicSierraFilter =  new MagicSierraFilter(context);
                return magicSierraFilter;

            case FAIRYTALE:
                MagicFairytaleFilter magicFairytaleFilter = new MagicFairytaleFilter(context);
                return magicFairytaleFilter;

            case FENNEN:
                GPUImageRGBFilter fennenFilter = new GPUImageRGBFilter(1.0F, 0.72F, 0.75F);
                return  fennenFilter;


            case ROMANCE:
                MagicRomanceFilter magicRomanceFilter = new MagicRomanceFilter(context);
                return magicRomanceFilter;


            case EVERGREEN:
                MagicEvergreenFilter magicEvergreenFilter = new MagicEvergreenFilter(context);
                return magicEvergreenFilter;

            case HOUNIAO:
                MagicBlackCatFilter houniaoFilter = new MagicBlackCatFilter(context);
                return  houniaoFilter;


            case EMERALD:
                MagicEmeraldFilter magicEmeraldFilter = new MagicEmeraldFilter(context);
                return magicEmeraldFilter;


            case HEIBAI:
                GPUImageGrayscaleFilter gpuImageGrayscaleFilter = new GPUImageGrayscaleFilter();
                return gpuImageGrayscaleFilter;

            case LOMO:
                MagicLomoFilter magicLomoFilter = new MagicLomoFilter(context);
                return  magicLomoFilter;


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


    public interface OnGpuImageFilterChosenListener {
        void onGpuImageFilterChosenListener(GPUImageFilter filter);
    }

    private enum FilterType {
        YUANHUA,JINGDIAN,FUGU,FENNEN,EMERALD,HOUNIAO,ANTIQUE,BLACKCAT,CALM,COOL,EVERGREEN,FAIRYTALE,LATTE,LOMO,N1977,ROMANCE,BEAUTY,SUNSET,CRAYON,SKETCH,SKINWHITEN,SIERRA,HEIBAI,NOSTALGIA,WARM,DANYA,GETEFENG
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
