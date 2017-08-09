package com.hn.d.valley.main.seek

import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.angcyo.github.utilcode.utils.SpannableStringUtils
import com.angcyo.uiview.base.UIBaseView
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.github.pickerview.DateDialog
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.OverLayCardLayoutManager
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.RRecyclerView
import com.angcyo.uiview.recycler.RenRenCallback
import com.angcyo.uiview.recycler.adapter.RBaseAdapter
import com.angcyo.uiview.resources.ResUtil
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.utils.ThreadExecutor
import com.angcyo.uiview.utils.file.FileUtil
import com.angcyo.uiview.view.DelayClick
import com.angcyo.uiview.view.RClickListener
import com.facebook.drawee.view.SimpleDraweeView
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.MatchBean
import com.hn.d.valley.bean.MatchModel
import com.hn.d.valley.helper.AudioPlayHelper
import com.hn.d.valley.library.fresco.DraweeViewUtil
import com.hn.d.valley.main.me.UserDetailUIView2
import com.hn.d.valley.main.message.audio.BaseAudioControl
import com.hn.d.valley.main.message.audio.Playable
import com.hn.d.valley.main.message.session.SessionHelper
import com.hn.d.valley.nim.CustomBean
import com.hn.d.valley.nim.NoticeAttachment
import com.hn.d.valley.service.ShowService
import com.hn.d.valley.utils.RAmap
import com.hn.d.valley.widget.HnGenderView
import com.netease.nimlib.sdk.msg.model.IMMessage

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：打招呼
 * 创建人员：Robi
 * 创建时间：2017/08/04 17:59
 * 修改人员：Robi
 * 修改时间：2017/08/04 17:59
 * 修改备注：
 * Version: 1.0.0
 */
class MatchUIView : BaseContentUIView() {

    val mRecyclerView: RRecyclerView by lazy {
        v<RRecyclerView>(R.id.recycler_view)
    }

    val mControlLayout: LinearLayout by lazy {
        v<LinearLayout>(R.id.control_layout)
    }

    lateinit var mRenRenCallback: RenRenCallback
    lateinit var mFriendAdapter: FriendAdapter

    override fun inflateContentLayout(baseContentLayout: ContentLayout, inflater: LayoutInflater) {
        inflate(R.layout.view_seek_match)
    }

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()

        mAudioPlayHelper = AudioPlayHelper(mActivity)

        click(R.id.disagree_view) { v -> onControlClick(v) }
        click(R.id.attention_view) { v -> onControlClick(v) }

        mRecyclerView.setItemAnim(false)
        mRecyclerView.layoutManager = OverLayCardLayoutManager(mActivity)
                .setTopOffset(ResUtil.dpToPx(mActivity, 20f).toInt())
        mFriendAdapter = FriendAdapter(mActivity)
        mRecyclerView.adapter = mFriendAdapter

        mRenRenCallback = RenRenCallback()
        ItemTouchHelper(mRenRenCallback).attachToRecyclerView(mRecyclerView)

        mRenRenCallback.setSwipeListener(object : RenRenCallback.OnSwipeListener {
            override fun onSwiped(adapterPosition: Int, direction: Int) {
                val allDatas = mFriendAdapter.allDatas
                if (direction == ItemTouchHelper.DOWN || direction == ItemTouchHelper.UP) {
                    allDatas.add(0, allDatas.removeAt(adapterPosition))
                    mFriendAdapter.notifyDataSetChanged()
                } else {
                    mAudioPlayHelper?.stopAudio()
                    val remove = allDatas.removeAt(adapterPosition)
                    if (direction == ItemTouchHelper.LEFT) {
                        allDatas.add(0, remove)
                        mFriendAdapter.notifyDataSetChanged()
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        //发送打招呼消息
                        SessionHelper.sendTextMsg(remove.uid, "Hi, 可以认识你吗?")
                    }
                }
                mFriendAdapter.notifyDataSetChanged()
                if (mFriendAdapter.itemCount == 0) {
                    showEmptyLayout()
                }
            }

            override fun onSwipeTo(viewHolder: RecyclerView.ViewHolder, offset: Float) {

            }
        })
    }

    override fun getDefaultLayoutState(): UIBaseView.LayoutState {
        return UIBaseView.LayoutState.LOAD
    }

    override fun onViewShowFirst(bundle: Bundle?) {
        super.onViewShowFirst(bundle)
        getAllMessage()
    }

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setShowBackImageView(true).setTitleString("打招呼")
    }

    private fun getAllMessage() {
        showLoadView()
        add(RRetrofit.create(ShowService::class.java)
                .match(Param.buildMap("lng:${RAmap.instance().longitude}",
                        "lat:${RAmap.instance().latitude}",
                        "limit:50"))
                .compose(Rx.transformer(MatchModel::class.java))
                .subscribe(object : BaseSingleSubscriber<MatchModel>() {
                    override fun onSucceed(bean: MatchModel?) {
                        super.onSucceed(bean)
                        if (bean == null || bean.data_list == null || bean.data_list.isEmpty()) {
                            showEmptyLayout()
                        } else {
                            showContentLayout()
                            mFriendAdapter.resetData(bean.data_list)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                        if (isError) {
                            showNonetLayout { getAllMessage() }
                        }
                    }
                }))
    }

    fun onControlClick(view: View) {
        when (view.id) {
            R.id.disagree_view ->
                //不喜欢
                mRenRenCallback.toLeft(mRecyclerView)
            R.id.attention_view ->
                //喜欢
                mRenRenCallback.toRight(mRecyclerView)
        }
    }

    private fun getBean(bean: IMMessage): CustomBean {
        val attachment = bean.attachment as NoticeAttachment
        val customBean = attachment.bean
        return customBean
    }

    private var mAudioPlayHelper: AudioPlayHelper? = null

    private fun initVoiceView(holder: RBaseViewHolder, voice_introduce: String?) {
        val controlLayout = holder.v<LinearLayout>(R.id.voice_control_layout)
        val voicePlayView = holder.v<ImageView>(R.id.voice_play_view)
        val voiceTimeView = holder.v<TextView>(R.id.voice_time_view)
        voiceTimeView.setTextColor(SkinHelper.getSkin().themeSubColor)

        if (TextUtils.isEmpty(voice_introduce)) {
            controlLayout.visibility = View.GONE
        } else {
            if (controlLayout.visibility == View.GONE) {
                controlLayout.visibility = View.INVISIBLE
                controlLayout.post {
                    ViewCompat.setTranslationX(controlLayout, controlLayout.measuredWidth.toFloat())
                    controlLayout.visibility = View.VISIBLE
                    ViewCompat.animate(controlLayout).translationX(0f).setDuration(300).start()
                }
            }
            mAudioPlayHelper?.initPlayImageView(voicePlayView)

            voiceTimeView.text = if (Integer.valueOf(getVoiceTime(voice_introduce)) < 1) "1" else getVoiceTime(voice_introduce)

            controlLayout.setOnClickListener(object : RClickListener(1000, true) {

                override fun onRClick(view: View?) {
                    mAudioPlayHelper?.playAudio(getVoiceUrl(voice_introduce),
                            getVoiceDuration(voice_introduce),
                            object : BaseAudioControl.AudioControlListener {
                                override fun onAudioControllerReady(playable: Playable) {

                                }

                                override fun onEndPlay(playable: Playable) {
                                    ThreadExecutor.instance().onMain { initVoiceView(holder, voice_introduce) }
                                }

                                override fun updatePlayingProgress(playable: Playable, curPosition: Long) {
                                    voiceTimeView.text = (curPosition / 1000).toString()
                                }
                            })
                }
            })
        }
    }

    fun getVoiceDuration(voice_introduce: String?): Long {
        return java.lang.Long.parseLong(getVoiceTime(voice_introduce).replace("s".toRegex(), "").replace("S".toRegex(), "").trim { it <= ' ' })
    }

    fun getVoiceUrl(voice_introduce: String?): String {
        if (TextUtils.isEmpty(voice_introduce)) {
            return ""
        } else {
            val voiceIntroduces: Array<String> = voice_introduce!!.split("--".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (voiceIntroduces.size == 2) {
                return voiceIntroduces[0]
            } else {
                return voice_introduce
            }
        }
    }

    fun getVoiceTime(voice_introduce: String?): String {
        if (TextUtils.isEmpty(voice_introduce)) {
            return "0"
        } else {
            var voiceIntroduces: Array<String>
            voiceIntroduces = voice_introduce!!.split("--".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (voiceIntroduces.size == 2) {
                return voiceIntroduces[1]
            } else {
                voiceIntroduces = voice_introduce.split("_t_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val introd1 = voiceIntroduces[1]
                val duration = FileUtil.getFileNameNoEx(introd1)

                if (voiceIntroduces.size == 2) {
                    return duration
                }
            }
        }
        return "0"
    }

    override fun onViewHide() {
        super.onViewHide()
        mAudioPlayHelper?.stopAudio()
    }

    inner class FriendAdapter(context: Context) : RBaseAdapter<MatchBean>(context) {

        override fun getItemLayoutId(viewType: Int): Int {
            return R.layout.item_seek_match
        }

        override fun onBindView(holder: RBaseViewHolder, position: Int, bean: MatchBean) {
            DraweeViewUtil.resize(holder.v<View>(R.id.avatar) as SimpleDraweeView, bean.avatar)

            //距离
            holder.tv(R.id.show_distance_view).text = bean.show_distance
            //昵称
            holder.tv(R.id.username).text = bean.username
            //性别,星座
            val gradeView: HnGenderView = holder.v(R.id.grade)
            gradeView.setGender2(bean.sex, DateDialog.getBirthday(bean.birthday), bean.constellation)
            //粉丝值
            holder.tv(R.id.fans_count_view).text = SpannableStringUtils
                    .getBuilder("粉丝值:")
                    .append("${bean.fans_count}")
                    .setForegroundColor(SkinHelper.getSkin().themeSubColor)
                    .create()
            //魅力值
            holder.tv(R.id.charm_view).text = SpannableStringUtils
                    .getBuilder("魅力值:")
                    .append(bean.charm)
                    .setForegroundColor(SkinHelper.getSkin().themeSubColor)
                    .create()

            initVoiceView(holder, bean.voice_introduce)

            holder.delayClick(R.id.card_root_layout, object : DelayClick() {
                override fun onRClick(view: View?) {
                    startIView(UserDetailUIView2(bean.uid))
                }

            })
        }
    }

    override fun needTransitionExitAnim(): Boolean {
        return true
    }

    override fun needTransitionStartAnim(): Boolean {
        return true
    }
}