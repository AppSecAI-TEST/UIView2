package com.hn.d.valley.base.iview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Html;

import com.hn.d.valley.R;
import com.m3b.rbrecoderlib.GPUImage3x3ConvolutionFilter;
import com.m3b.rbrecoderlib.GPUImageAddBlendFilter;
import com.m3b.rbrecoderlib.GPUImageAlphaBlendFilter;
import com.m3b.rbrecoderlib.GPUImageBilateralFilter;
import com.m3b.rbrecoderlib.GPUImageBoxBlurFilter;
import com.m3b.rbrecoderlib.GPUImageBrightnessFilter;
import com.m3b.rbrecoderlib.GPUImageBulgeDistortionFilter;
import com.m3b.rbrecoderlib.GPUImageCGAColorspaceFilter;
import com.m3b.rbrecoderlib.GPUImageChromaKeyBlendFilter;
import com.m3b.rbrecoderlib.GPUImageColorBalanceFilter;
import com.m3b.rbrecoderlib.GPUImageColorBlendFilter;
import com.m3b.rbrecoderlib.GPUImageColorBurnBlendFilter;
import com.m3b.rbrecoderlib.GPUImageColorDodgeBlendFilter;
import com.m3b.rbrecoderlib.GPUImageColorInvertFilter;
import com.m3b.rbrecoderlib.GPUImageContrastFilter;
import com.m3b.rbrecoderlib.GPUImageCrosshatchFilter;
import com.m3b.rbrecoderlib.GPUImageDarkenBlendFilter;
import com.m3b.rbrecoderlib.GPUImageDifferenceBlendFilter;
import com.m3b.rbrecoderlib.GPUImageDilationFilter;
import com.m3b.rbrecoderlib.GPUImageDirectionalSobelEdgeDetectionFilter;
import com.m3b.rbrecoderlib.GPUImageDissolveBlendFilter;
import com.m3b.rbrecoderlib.GPUImageDivideBlendFilter;
import com.m3b.rbrecoderlib.GPUImageEmbossFilter;
import com.m3b.rbrecoderlib.GPUImageExclusionBlendFilter;
import com.m3b.rbrecoderlib.GPUImageExposureFilter;
import com.m3b.rbrecoderlib.GPUImageFalseColorFilter;
import com.m3b.rbrecoderlib.GPUImageFilter;
import com.m3b.rbrecoderlib.GPUImageFilterGroup;
import com.m3b.rbrecoderlib.GPUImageGammaFilter;
import com.m3b.rbrecoderlib.GPUImageGaussianBlurFilter;
import com.m3b.rbrecoderlib.GPUImageGlassSphereFilter;
import com.m3b.rbrecoderlib.GPUImageGrayscaleFilter;
import com.m3b.rbrecoderlib.GPUImageHalftoneFilter;
import com.m3b.rbrecoderlib.GPUImageHardLightBlendFilter;
import com.m3b.rbrecoderlib.GPUImageHazeFilter;
import com.m3b.rbrecoderlib.GPUImageHighlightShadowFilter;
import com.m3b.rbrecoderlib.GPUImageHueBlendFilter;
import com.m3b.rbrecoderlib.GPUImageHueFilter;
import com.m3b.rbrecoderlib.GPUImageKuwaharaFilter;
import com.m3b.rbrecoderlib.GPUImageLaplacianFilter;
import com.m3b.rbrecoderlib.GPUImageLevelsFilter;
import com.m3b.rbrecoderlib.GPUImageLightenBlendFilter;
import com.m3b.rbrecoderlib.GPUImageLinearBurnBlendFilter;
import com.m3b.rbrecoderlib.GPUImageLookupFilter;
import com.m3b.rbrecoderlib.GPUImageLuminosityBlendFilter;
import com.m3b.rbrecoderlib.GPUImageMonochromeFilter;
import com.m3b.rbrecoderlib.GPUImageMultiplyBlendFilter;
import com.m3b.rbrecoderlib.GPUImageNonMaximumSuppressionFilter;
import com.m3b.rbrecoderlib.GPUImageNormalBlendFilter;
import com.m3b.rbrecoderlib.GPUImageOpacityFilter;
import com.m3b.rbrecoderlib.GPUImageOverlayBlendFilter;
import com.m3b.rbrecoderlib.GPUImagePixelationFilter;
import com.m3b.rbrecoderlib.GPUImagePosterizeFilter;
import com.m3b.rbrecoderlib.GPUImageRGBDilationFilter;
import com.m3b.rbrecoderlib.GPUImageRGBFilter;
import com.m3b.rbrecoderlib.GPUImageSaturationBlendFilter;
import com.m3b.rbrecoderlib.GPUImageSaturationFilter;
import com.m3b.rbrecoderlib.GPUImageScreenBlendFilter;
import com.m3b.rbrecoderlib.GPUImageSepiaFilter;
import com.m3b.rbrecoderlib.GPUImageSharpenFilter;
import com.m3b.rbrecoderlib.GPUImageSketchFilter;
import com.m3b.rbrecoderlib.GPUImageSmoothToonFilter;
import com.m3b.rbrecoderlib.GPUImageSobelEdgeDetection;
import com.m3b.rbrecoderlib.GPUImageSoftLightBlendFilter;
import com.m3b.rbrecoderlib.GPUImageSourceOverBlendFilter;
import com.m3b.rbrecoderlib.GPUImageSphereRefractionFilter;
import com.m3b.rbrecoderlib.GPUImageSubtractBlendFilter;
import com.m3b.rbrecoderlib.GPUImageSwirlFilter;
import com.m3b.rbrecoderlib.GPUImageToonFilter;
import com.m3b.rbrecoderlib.GPUImageTransformFilter;
import com.m3b.rbrecoderlib.GPUImageTwoInputFilter;
import com.m3b.rbrecoderlib.GPUImageVignetteFilter;
import com.m3b.rbrecoderlib.GPUImageWeakPixelInclusionFilter;
import com.m3b.rbrecoderlib.GPUImageWhiteBalanceFilter;
import com.m3b.rbrecoderlib.RBLogoFilter;
import com.m3b.rbrecoderlib.RBPaopaoFilter;
import com.m3b.rbrecoderlib.RBTextFilter;

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
            case ORIGIN:
                return new GPUImageFilter();
            case TEXT:
                RBTextFilter textfilter = new RBTextFilter();
                CharSequence htmlText = Html.fromHtml("<font color=\"#7FFF0000\">恐龙谷</font><br/><b>@M3B</b>");
                textfilter.setText(htmlText, Color.WHITE, 30);
                textfilter.setPostion(RBTextFilter.Gravity.RIGHT | RBTextFilter.Gravity.TOP, 100, 100);
                return textfilter;

            case LOGO:
                RBLogoFilter logofilter = new RBLogoFilter(new Rect(100, 100, 200, 200));
                logofilter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.login_logo));
                return logofilter;

            case PAOPAO:
                RBPaopaoFilter paopoafilter = new RBPaopaoFilter(context);
                return paopoafilter;

            /*
            case WATERMARK:
                RBWatermarkFilter watermarkfilter = new RBWatermarkFilter();
                CharSequence htmlText2 = Html.fromHtml("<font color=\"#7FFF0000\">红鸟网络</font><br/><b>@M3B</b>");
                watermarkfilter.setText(htmlText2,  Color.WHITE, 30);
                watermarkfilter.setPostion(RBTextFilter.Gravity.RIGHT | RBTextFilter.Gravity.TOP, 100, 100);
                watermarkfilter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hongbao));
                return  watermarkfilter;
            */

            case CONTRAST:
                return new GPUImageContrastFilter(2.0f);

            case GAMMA:
                return new GPUImageGammaFilter(2.0f);
            case INVERT:
                return new GPUImageColorInvertFilter();

            case PIXELATION:
                return new GPUImagePixelationFilter();
            case HUE:
                return new GPUImageHueFilter(90.0f);
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter(1.5f);
            case GRAYSCALE:
                return new GPUImageGrayscaleFilter();
            case SEPIA:
                return new GPUImageSepiaFilter();
            case SHARPEN:
                GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
                sharpness.setSharpness(2.0f);
                return sharpness;
            case SOBEL_EDGE_DETECTION:
                return new GPUImageSobelEdgeDetection();
            case THREE_X_THREE_CONVOLUTION:
                GPUImage3x3ConvolutionFilter convolution = new GPUImage3x3ConvolutionFilter();
                convolution.setConvolutionKernel(new float[]{
                        -1.0f, 0.0f, 1.0f,
                        -2.0f, 0.0f, 2.0f,
                        -1.0f, 0.0f, 1.0f
                });
                return convolution;
            case EMBOSS:
                return new GPUImageEmbossFilter();
            case POSTERIZE:
                return new GPUImagePosterizeFilter();
            case FILTER_GROUP:
                List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
                filters.add(new GPUImageContrastFilter());
                filters.add(new GPUImageDirectionalSobelEdgeDetectionFilter());
                filters.add(new GPUImageGrayscaleFilter());
                return new GPUImageFilterGroup(filters);
            case SATURATION:
                return new GPUImageSaturationFilter(1.0f);
            case EXPOSURE:
                return new GPUImageExposureFilter(0.0f);
            case HIGHLIGHT_SHADOW:
                return new GPUImageHighlightShadowFilter(0.0f, 1.0f);
            case MONOCHROME:
                return new GPUImageMonochromeFilter(1.0f, new float[]{0.6f, 0.45f, 0.3f, 1.0f});
            case OPACITY:
                return new GPUImageOpacityFilter(1.0f);
            case RGB:
                return new GPUImageRGBFilter(1.0f, 1.0f, 1.0f);
            case WHITE_BALANCE:
                return new GPUImageWhiteBalanceFilter(5000.0f, 0.0f);
            case VIGNETTE:
                PointF centerPoint = new PointF();
                centerPoint.x = 0.5f;
                centerPoint.y = 0.5f;
                return new GPUImageVignetteFilter(centerPoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, 0.75f);
            case BLEND_DIFFERENCE:
                return createBlendFilter(context, GPUImageDifferenceBlendFilter.class);
            case BLEND_SOURCE_OVER:
                return createBlendFilter(context, GPUImageSourceOverBlendFilter.class);
            case BLEND_COLOR_BURN:
                return createBlendFilter(context, GPUImageColorBurnBlendFilter.class);
            case BLEND_COLOR_DODGE:
                return createBlendFilter(context, GPUImageColorDodgeBlendFilter.class);
            case BLEND_DARKEN:
                return createBlendFilter(context, GPUImageDarkenBlendFilter.class);
            case BLEND_DISSOLVE:
                return createBlendFilter(context, GPUImageDissolveBlendFilter.class);
            case BLEND_EXCLUSION:
                return createBlendFilter(context, GPUImageExclusionBlendFilter.class);
            case BLEND_HARD_LIGHT:
                return createBlendFilter(context, GPUImageHardLightBlendFilter.class);
            case BLEND_LIGHTEN:
                return createBlendFilter(context, GPUImageLightenBlendFilter.class);
            case BLEND_ADD:
                return createBlendFilter(context, GPUImageAddBlendFilter.class);
            case BLEND_DIVIDE:
                return createBlendFilter(context, GPUImageDivideBlendFilter.class);
            case BLEND_MULTIPLY:
                return createBlendFilter(context, GPUImageMultiplyBlendFilter.class);
            case BLEND_OVERLAY:
                return createBlendFilter(context, GPUImageOverlayBlendFilter.class);
            case BLEND_SCREEN:
                return createBlendFilter(context, GPUImageScreenBlendFilter.class);
            case BLEND_ALPHA:
                return createBlendFilter(context, GPUImageAlphaBlendFilter.class);
            case BLEND_COLOR:
                return createBlendFilter(context, GPUImageColorBlendFilter.class);
            case BLEND_HUE:
                return createBlendFilter(context, GPUImageHueBlendFilter.class);
            case BLEND_SATURATION:
                return createBlendFilter(context, GPUImageSaturationBlendFilter.class);
            case BLEND_LUMINOSITY:
                return createBlendFilter(context, GPUImageLuminosityBlendFilter.class);
            case BLEND_LINEAR_BURN:
                return createBlendFilter(context, GPUImageLinearBurnBlendFilter.class);
            case BLEND_SOFT_LIGHT:
                return createBlendFilter(context, GPUImageSoftLightBlendFilter.class);
            case BLEND_SUBTRACT:
                return createBlendFilter(context, GPUImageSubtractBlendFilter.class);
            case BLEND_CHROMA_KEY:
                return createBlendFilter(context, GPUImageChromaKeyBlendFilter.class);
            case BLEND_NORMAL:
                return createBlendFilter(context, GPUImageNormalBlendFilter.class);

            case LOOKUP_AMATORKA:
                GPUImageLookupFilter amatorka = new GPUImageLookupFilter();
                amatorka.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_amatorka));
                return amatorka;
            case GAUSSIAN_BLUR:
                return new GPUImageGaussianBlurFilter();
            case CROSSHATCH:
                return new GPUImageCrosshatchFilter();

            case BOX_BLUR:
                return new GPUImageBoxBlurFilter();
            case CGA_COLORSPACE:
                return new GPUImageCGAColorspaceFilter();
            case DILATION:
                return new GPUImageDilationFilter();
            case KUWAHARA:
                return new GPUImageKuwaharaFilter();
            case RGB_DILATION:
                return new GPUImageRGBDilationFilter();
            case SKETCH:
                return new GPUImageSketchFilter();
            case TOON:
                return new GPUImageToonFilter();
            case SMOOTH_TOON:
                return new GPUImageSmoothToonFilter();

            case BULGE_DISTORTION:
                return new GPUImageBulgeDistortionFilter();
            case GLASS_SPHERE:
                return new GPUImageGlassSphereFilter();
            case HAZE:
                return new GPUImageHazeFilter();
            case LAPLACIAN:
                return new GPUImageLaplacianFilter();
            case NON_MAXIMUM_SUPPRESSION:
                return new GPUImageNonMaximumSuppressionFilter();
            case SPHERE_REFRACTION:
                return new GPUImageSphereRefractionFilter();
            case SWIRL:
                return new GPUImageSwirlFilter();
            case WEAK_PIXEL_INCLUSION:
                return new GPUImageWeakPixelInclusionFilter();
            case FALSE_COLOR:
                return new GPUImageFalseColorFilter();
            case COLOR_BALANCE:
                return new GPUImageColorBalanceFilter();
            case LEVELS_FILTER_MIN:
                GPUImageLevelsFilter levelsFilter = new GPUImageLevelsFilter();
                levelsFilter.setMin(0.0f, 3.0f, 1.0f);
                return levelsFilter;
            case HALFTONE:
                return new GPUImageHalftoneFilter();

            case BILATERAL_BLUR:
                return new GPUImageBilateralFilter();

            case TRANSFORM2D:
                return new GPUImageTransformFilter();

            default:
                throw new IllegalStateException("No filter of that type!");
        }

    }


    private static GPUImageFilter createBlendFilter(Context context, Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
            filter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pao_024));
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<FilterBean>  createFilterList() {
        List<FilterBean> beanList = new ArrayList<>();
        beanList.add(new FilterBean("原画", FilterType.ORIGIN));
        beanList.add(new FilterBean("Text", FilterType.TEXT));
        beanList.add(new FilterBean("Logo", FilterType.LOGO));
        beanList.add(new FilterBean("Pappao", FilterType.PAOPAO));
        beanList.add(new FilterBean("Contrast", FilterType.CONTRAST));
        beanList.add(new FilterBean("Invert", FilterType.INVERT));
        beanList.add(new FilterBean("Pixelation", FilterType.PIXELATION));
        beanList.add(new FilterBean("Hue", FilterType.HUE));
        beanList.add(new FilterBean("Gamma", FilterType.GAMMA));
        beanList.add(new FilterBean("Brightness", FilterType.BRIGHTNESS));
        beanList.add(new FilterBean("Sepia", FilterType.SEPIA));
        beanList.add(new FilterBean("Grayscale", FilterType.GRAYSCALE));
        beanList.add(new FilterBean("Sharpness", FilterType.SHARPEN));
        beanList.add(new FilterBean("Sobel Edge Detection", FilterType.SOBEL_EDGE_DETECTION));
        beanList.add(new FilterBean("3x3 Convolution", FilterType.THREE_X_THREE_CONVOLUTION));
        beanList.add(new FilterBean("Emboss", FilterType.EMBOSS));
        beanList.add(new FilterBean("Posterize", FilterType.POSTERIZE));
        beanList.add(new FilterBean("Grouped filters", FilterType.FILTER_GROUP));
        beanList.add(new FilterBean("Saturation", FilterType.SATURATION));
        beanList.add(new FilterBean("Exposure", FilterType.EXPOSURE));
        beanList.add(new FilterBean("Highlight Shadow", FilterType.HIGHLIGHT_SHADOW));
        beanList.add(new FilterBean("Monochrome", FilterType.MONOCHROME));
        beanList.add(new FilterBean("Opacity", FilterType.OPACITY));
        beanList.add(new FilterBean("RGB", FilterType.RGB));
        beanList.add(new FilterBean("White Balance", FilterType.WHITE_BALANCE));
        beanList.add(new FilterBean("Vignette", FilterType.VIGNETTE));

        beanList.add(new FilterBean("Blend (Difference)", FilterType.BLEND_DIFFERENCE));
        beanList.add(new FilterBean("Blend (Source Over)", FilterType.BLEND_SOURCE_OVER));
        beanList.add(new FilterBean("Blend (Color Burn)", FilterType.BLEND_COLOR_BURN));
        beanList.add(new FilterBean("Blend (Color Dodge)", FilterType.BLEND_COLOR_DODGE));
        beanList.add(new FilterBean("Blend (Darken)", FilterType.BLEND_DARKEN));
        beanList.add(new FilterBean("Blend (Dissolve)", FilterType.BLEND_DISSOLVE));
        beanList.add(new FilterBean("Blend (Exclusion)", FilterType.BLEND_EXCLUSION));
        beanList.add(new FilterBean("Blend (Hard Light)", FilterType.BLEND_HARD_LIGHT));
        beanList.add(new FilterBean("Blend (Lighten)", FilterType.BLEND_LIGHTEN));
        beanList.add(new FilterBean("Blend (Add)", FilterType.BLEND_ADD));
        beanList.add(new FilterBean("Blend (Divide)", FilterType.BLEND_DIVIDE));
        beanList.add(new FilterBean("Blend (Multiply)", FilterType.BLEND_MULTIPLY));
        beanList.add(new FilterBean("Blend (Overlay)", FilterType.BLEND_OVERLAY));
        beanList.add(new FilterBean("Blend (Screen)", FilterType.BLEND_SCREEN));
        beanList.add(new FilterBean("Blend (Alpha)", FilterType.BLEND_ALPHA));
        beanList.add(new FilterBean("Blend (Color)", FilterType.BLEND_COLOR));
        beanList.add(new FilterBean("Blend (Hue)", FilterType.BLEND_HUE));
        beanList.add(new FilterBean("Blend (Saturation)", FilterType.BLEND_SATURATION));
        beanList.add(new FilterBean("Blend (Luminosity)", FilterType.BLEND_LUMINOSITY));
        beanList.add(new FilterBean("Blend (Linear Burn)", FilterType.BLEND_LINEAR_BURN));
        beanList.add(new FilterBean("Blend (Soft Light)", FilterType.BLEND_SOFT_LIGHT));
        beanList.add(new FilterBean("Blend (Subtract)", FilterType.BLEND_SUBTRACT));
        beanList.add(new FilterBean("Blend (Chroma Key)", FilterType.BLEND_CHROMA_KEY));
        beanList.add(new FilterBean("Blend (Normal)", FilterType.BLEND_NORMAL));

        beanList.add(new FilterBean("Lookup (Amatorka)", FilterType.LOOKUP_AMATORKA));
        beanList.add(new FilterBean("Gaussian Blur", FilterType.GAUSSIAN_BLUR));
        beanList.add(new FilterBean("Crosshatch", FilterType.CROSSHATCH));

        beanList.add(new FilterBean("Box Blur", FilterType.BOX_BLUR));
        beanList.add(new FilterBean("CGA Color Space", FilterType.CGA_COLORSPACE));
        beanList.add(new FilterBean("Dilation", FilterType.DILATION));
        beanList.add(new FilterBean("Kuwahara", FilterType.KUWAHARA));
        beanList.add(new FilterBean("RGB Dilation", FilterType.RGB_DILATION));
        beanList.add(new FilterBean("Sketch", FilterType.SKETCH));
        beanList.add(new FilterBean("Toon", FilterType.TOON));
        beanList.add(new FilterBean("Smooth Toon", FilterType.SMOOTH_TOON));
        beanList.add(new FilterBean("Halftone", FilterType.HALFTONE));

        beanList.add(new FilterBean("Bulge Distortion", FilterType.BULGE_DISTORTION));
        beanList.add(new FilterBean("Glass Sphere", FilterType.GLASS_SPHERE));
        beanList.add(new FilterBean("Haze", FilterType.HAZE));
        beanList.add(new FilterBean("Laplacian", FilterType.LAPLACIAN));
        beanList.add(new FilterBean("Non Maximum Suppression", FilterType.NON_MAXIMUM_SUPPRESSION));
        beanList.add(new FilterBean("Sphere Refraction", FilterType.SPHERE_REFRACTION));
        beanList.add(new FilterBean("Swirl", FilterType.SWIRL));
        beanList.add(new FilterBean("Weak Pixel Inclusion", FilterType.WEAK_PIXEL_INCLUSION));
        beanList.add(new FilterBean("False Color", FilterType.FALSE_COLOR));

        beanList.add(new FilterBean("Color Balance", FilterType.COLOR_BALANCE));

        beanList.add(new FilterBean("Levels Min (Mid Adjust)", FilterType.LEVELS_FILTER_MIN));

        beanList.add(new FilterBean("Bilateral Blur", FilterType.BILATERAL_BLUR));

        beanList.add(new FilterBean("Transform (2-D)", FilterType.TRANSFORM2D));
        return beanList;
    }
    
    private enum FilterType {
        ORIGIN, TEXT, LOGO, PAOPAO, CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, TONE_CURVE, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN, BLEND_DIFFERENCE,
        BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
        BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE, DILATION, KUWAHARA, RGB_DILATION, SKETCH, TOON, SMOOTH_TOON, BULGE_DISTORTION, GLASS_SPHERE, HAZE, LAPLACIAN, NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION, FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN, BILATERAL_BLUR, HALFTONE, TRANSFORM2D
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
