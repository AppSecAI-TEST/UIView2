package com.hn.d.valley.nim;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.DiscussRecommAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicMsgAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.HotSpotInfoAttachment;
import com.hn.d.valley.main.message.attachment.InviteUploadProfileAttachment;
import com.hn.d.valley.main.message.attachment.KLGCoinConsumeAttachment;
import com.hn.d.valley.main.message.attachment.LikeMsgAttachment;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.attachment.ReceiptsNoticeAttachment;
import com.hn.d.valley.main.message.attachment.RechargeMsgAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.attachment.RefundMsgAttachment;
import com.hn.d.valley.main.message.attachment.ShareNewsAttachment;
import com.hn.d.valley.main.message.attachment.SystemPushAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONObject;

public class CustomAttachParser implements MsgAttachmentParser {

    public static final String EXTEND_TYPE = "extend_type";


    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            String type = object.getString(EXTEND_TYPE);
            switch (type) {
                case CustomAttachmentType.PersonalCard_:
                    attachment = new PersonalCardAttachment(Json.from(json, PersonalCard.class));
                    break;
                case CustomAttachmentType.ManagerPush:
                    SystemPushAttachment pushAttachment = new SystemPushAttachment(json);
                    String subType = pushAttachment.getSubType();
                    attachment = pushAttachment.newInstance(subType);
                    break;
                case CustomAttachmentType.NEWBAG:
                    attachment = new RedPacketAttachment(json);
                    break;
                case CustomAttachmentType.GRABREDBAG:
                    attachment = new GrabedMsgAttachment(json);
                    break;
                case CustomAttachmentType.COLLECTION_CONFIRM:
                    attachment = new ReceiptsNoticeAttachment(json);
                    break;
                case CustomAttachmentType.RECEIPTES:
                    attachment = new ReceiptsNoticeAttachment(json);
                    break;
                case CustomAttachmentType.REFUND:
                    attachment = new RefundMsgAttachment(json);
                    break;
                case CustomAttachmentType.RECHARGE:
                    attachment = new RechargeMsgAttachment(json);
                    break;
                case CustomAttachmentType.WITHDRAWAL_MSG:
                    attachment = new WithDrawalAttachment(json);
                    break;
                case CustomAttachmentType.WITHDRAWAL_FAIL_MSG:
                    attachment = new WithDrawalFailAttachment(json);
                    break;
                case CustomAttachmentType.KLGGIF_MSG:
                    attachment = new CustomExpressionAttachment(json);
                    break;
                case CustomAttachmentType.LIKE_MSG:
                    attachment = new LikeMsgAttachment(json);
                    break;
                case CustomAttachmentType.FORWARD_MSG:
                    attachment = new DynamicMsgAttachment(json);
                    break;
                case CustomAttachmentType.KLG_COIN_CONSUME:
                    attachment = new KLGCoinConsumeAttachment(json);
                    break;
                case CustomAttachmentType.HOTSPOTIFNO_MSG:
                    attachment = new HotSpotInfoAttachment(json);
                    break;
                case CustomAttachmentType.SHARE_DYNAMIC_MSG:
                    attachment = new DynamicDetailAttachment(json);
                    break;
                case CustomAttachmentType.SHARE_NEWS:
                    attachment = new ShareNewsAttachment(json);
                    break;
                case CustomAttachmentType.INVITE_UPLOAD_PHOTOS:
                    attachment = new InviteUploadProfileAttachment(json);
                    break;
                case CustomAttachmentType.GIFT:
                    attachment = new GiftReceiveAttachment(json);
                    break;
                case CustomAttachmentType.DISCUSS_RECOMM:
                    attachment = new DiscussRecommAttachment(json);
                    break;
                default:
                    attachment = new NoticeAttachment(json);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachment;
    }

}