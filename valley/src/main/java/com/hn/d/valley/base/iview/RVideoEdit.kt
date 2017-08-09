package com.hn.d.valley.base.iview

import android.app.Activity
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.util.Log
import com.angcyo.uiview.Root
import com.angcyo.uiview.utils.T_
import com.m3b.rbvideolib.listener.CompressListener
import com.m3b.rbvideolib.listener.CutterListener
import com.m3b.rbvideolib.listener.GifMakerListener
import com.m3b.rbvideolib.listener.InitListener
import com.m3b.rbvideolib.processor.Compressor
import com.m3b.rbvideolib.processor.Cutter
import com.m3b.rbvideolib.processor.GifMaker
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/12 11:57
 * 修改人员：Robi
 * 修改时间：2017/07/12 11:57
 * 修改备注：
 * Version: 1.0.0
 */

object RVideoEdit {

    /**视频裁剪*/
    private var mCutter: Cutter? = null

    private fun initCutter(activity: Activity, onLoadSuccess: () -> Unit) {
        if (mCutter == null) {
            mCutter = Cutter(activity)
            mCutter?.loadBinary(object : InitListener {
                override fun onLoadSuccess() {
                    Log.v("###", "load library succeed")
                    onLoadSuccess.invoke()
                }

                override fun onLoadFail(reason: String) {
                    Log.i("###", "load library fail:" + reason)
                }
            })
        } else {
            onLoadSuccess.invoke()
        }
    }

    /**视频压缩*/
    private var mCompressor: Compressor? = null

    private fun initCompressor(activity: Activity, onLoadSuccess: () -> Unit) {
        if (mCompressor == null) {
            mCompressor = Compressor(activity)
            mCompressor?.loadBinary(object : InitListener {
                override fun onLoadSuccess() {
                    Log.v("###", "load library succeed")
                    onLoadSuccess.invoke()
                }

                override fun onLoadFail(reason: String) {
                    Log.i("###", "load library fail:" + reason)
                }
            })
        } else {
            onLoadSuccess.invoke()
        }
    }

    /**GIF生成*/
    private var mGifmaker: GifMaker? = null

    private fun initGifMaker(activity: Activity, onLoadSuccess: () -> Unit) {
        if (mGifmaker == null) {
            mGifmaker = GifMaker(activity)
            mGifmaker?.loadBinary(object : InitListener {
                override fun onLoadSuccess() {
                    Log.v("###", "load library succeed")
                    onLoadSuccess.invoke()
                }

                override fun onLoadFail(reason: String) {
                    Log.i("###", "load library fail:" + reason)
                }
            })
        } else {
            onLoadSuccess.invoke()
        }
    }

    fun makeGIF(activity: Activity, video_path: String, _outPath: String, listener: OnExecCommandListener?) {
        initGifMaker(activity) {
            if (TextUtils.isEmpty(video_path)) {
                listener?.onExecFail("视频路径不能为空")
                return@initGifMaker
            }

            lastpercent = 0

            //String ffpmegString = "-ss 0 -t 3 -i " + _cutPath + " -vf fps=10,scale=150:-1:sws_dither=ed -gifflags -transdiff -y " + _outPath;
            //val ffpmegString = "-threads 8 -ss 0 -t 3 -i $video_path -vf fps=25 -r 15 -y $_outPath"
            val ffpmegString = "-threads 16 -ss 0 -t 3 -i $video_path -vf fps=10,scale=150:-1 -r 15 -y $_outPath"

            val command = ffpmegString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mGifmaker?.execCommand(command, object : GifMakerListener {

                override fun onExecFail(reason: String) {
                    listener?.onExecFail("GIF制作失败")
                    Log.e("###", reason)
                    lastpercent = 0
                }

                override fun onExecStart() {
                    listener?.onExecStart()
                }

                override fun onExecSuccess(message: String) {
                    listener?.onExecSuccess("GIF成功")
                }

                override fun onExecProgress(message: String) {
                    Log.e("onProgress", " " + message)
                    //progressDialog.setProgress((int)getProgress(message,videoLength));
                }
            })
        }
    }

    /**裁剪视频*/
    fun cutAndCompressVideo(activity: Activity, path: String, _outPath: String,
                            _logoPath: String?, startTime: String, durtaion: String,
                            listener: OnExecCommandListener?) {
        initCutter(activity) {
            val cutOutPath = "${File(_outPath).parent}/${Root.createFileName(".mp4")}"

            val ffpmegString = "-ss $startTime -t $durtaion -y -i $path -vcodec copy -acodec copy $cutOutPath"
            //Log.d("cutVideo", "ffpmegString==>" + ffpmegString);
            val command = ffpmegString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            mCutter?.execCommand(command, object : CutterListener {
                override fun onExecStart() {
                    listener?.onExecStart()
                }

                override fun onExecSuccess(message: String) {
                    compressVideo(activity, cutOutPath, _outPath, _logoPath, getVideoLength(cutOutPath), listener)
                }

                override fun onExecFail(reason: String) {
                    listener?.onExecFail(reason)
                }

                override fun onExecProgress(message: String) {
                    Log.e("onProgress", "" + message)
                }
            })
        }
    }

    /**压缩视频*/
    fun compressVideo(activity: Activity, _cutPath: String /*需要压缩的视频路径*/,
                      _outPath: String /*输出的视频路径*/, _logoPath: String? /*水印的路径*/,
                      videoLength: Long /*视频时长,秒*/, listener: OnExecCommandListener?) {
        if (TextUtils.isEmpty(_cutPath)) {
            T_.error("视频路径不能为空")
            listener?.onExecFail("视频路径不能为空")
            return
        }

        /**
         * 1、设置比特率 " -b:v 1800k"  33s
         * 2、设置裁剪后视频分辨率 " -s 960x540"
         * 3、用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高  " -crf 30 "

         */

        //        String ffpmegString = "-threads 2" + " -i " + cutPath + " -vcodec libx264" + " -acodec aac" + " -preset ultrafast" + " -b:v 1800k" +" -s 960x540"+ " -crf 30 " + outPath;


        initCompressor(activity) {
            var ffpmegString: String
            if (_logoPath.isNullOrEmpty() || !File(_logoPath).exists()) {
                ffpmegString = "-threads 8 -y -i $_cutPath -vcodec libx264 -strict -2 -acodec aac -preset ultrafast -crf 30 $_outPath"
            } else {
                ffpmegString = "-threads 8 -y -i $_cutPath -i $_logoPath -filter_complex overlay=(main_w-overlay_w):0 -vcodec libx264 -strict -2 -acodec aac -preset ultrafast -crf 30 $_outPath"
            }

            Log.d("compressVideo", "ffpmegString==>" + ffpmegString)
            val command = ffpmegString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            mCompressor?.execCommand(command, object : CompressListener {


                override fun onExecFail(reason: String) {
                    T_.error("压缩失败")
                    Log.e("###", reason)
                    lastpercent = 0

                    listener?.onExecFail(reason)
                }

                override fun onExecStart() {
                    listener?.onExecStart()
                }

                override fun onExecSuccess(message: String) {
                    lastpercent = 0
//                seekBar.resetIndicatorAnimator()
//                videoView.start()
//                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY).toInt())
                    listener?.onExecSuccess(message)
                }

                override fun onExecProgress(message: String) {
                    Log.e("onProgress", " " + message)
                    listener?.onExecProgress(getProgress(message, videoLength).toInt())
                }
            })
        }
    }

    var lastpercent: Long = 0

    private fun getVideoLength(path: String): Long {
        val retr = MediaMetadataRetriever()
        retr.setDataSource(path)
        var _videoLength: Long
        val time = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)//获取视频时长
        //7680
        try {
            _videoLength = java.lang.Long.valueOf(time)!!.toLong() / 1000
        } catch (e: Exception) {
            e.printStackTrace()
            _videoLength = 0
        } finally {
            try {
                retr.release()
            } catch(e: Exception) {
            }
        }

        return _videoLength
    }

    private fun getProgress(message: String, videoLength: Long): Long {

        var percent: Long = 0
        if (message.contains("speed") || message.contains("frame") && message.contains("time")) {
            //frame=  164 fps= 28 q=-1.0 Lsize=    1009kB time=00:00:13.10 bitrate= 630.7kbits/s dup=5 drop=5 speed=2.22x
            //oframe=   29 fps=0.0 q=31.0 size=     109kB time=00:00:01.13 bitrate= 785.2kbits/s
            val pattern = Pattern.compile("time=([\\d\\w:]+)")
            val matcher = pattern.matcher(message)
            if (matcher.find()) {
                val tempTime = matcher.group(1).toString()
                //Log.d("###", "getProgress: tempTime " + tempTime);
                val arrayTime = tempTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val currentTime = TimeUnit.HOURS.toSeconds(arrayTime[0].toLong()) +
                        TimeUnit.MINUTES.toSeconds(arrayTime[1].toLong()) +
                        arrayTime[2].toLong()

                percent = 100 * currentTime / videoLength
                lastpercent = percent
            }
            //Log.d("###", "currentTime -> " + currentTime + "s % -> " + percent);
            return percent
        } else
            return lastpercent
    }

    fun release() {
        mCutter?.killRunningProcesses()
        mCompressor?.killRunningProcesses()

        mCutter = null
        mCompressor = null
    }
}

interface OnExecCommandListener {

    /**0-100*/
    fun onExecProgress(progress: Int)

    fun onExecSuccess(message: String)

    fun onExecStart()

    fun onExecFail(reason: String)
}