package cn.com.erayton.usagreement.data.db;


import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.com.erayton.usagreement.data.db.table.VideoRecord;
import cn.com.erayton.usagreement.data.db.table.VideoRecord_Table;

/**
 * 数据库操作工具
 * Created by Administrator on 2019/1/8.
 */

public class DbTools {

    /**
     * 添加视频记录
     * @param fileName  文件名
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param channel   通道号
     * @param fileSize  文件大小
     */
    public static void insertVideoRecord(String fileName, long startTime,
         long endTime, int channel, long fileSize){
        VideoRecord videoRecord = new VideoRecord() ;
        videoRecord.setName(fileName);
        videoRecord.setStartTime(startTime);
        videoRecord.setEndTime(endTime);
        videoRecord.setChannel(channel);
        videoRecord.setSourceType(0);
        videoRecord.setStreamType(1);
        videoRecord.setMemoryType(1);
        videoRecord.setSize(fileSize);
        //  报警类型暂时不使用此字段
        videoRecord.setWarning("");
        videoRecord.save() ;
    }

    /** 查找指定时间内的数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return  数据列表 VideoRecord
     */
    public static List<VideoRecord> queryVideoRecord(long startTime, long endTime){
        return SQLite.select()
                .from(VideoRecord.class)
                .where(VideoRecord_Table.startTime.greaterThanOrEq(startTime),
                        VideoRecord_Table.endTime.lessThanOrEq(endTime))
                .queryList() ;
    }
    public static VideoRecord queryVideoRecord(long startTime){
        return SQLite.select()
                .from(VideoRecord.class)
                .where(VideoRecord_Table.startTime.greaterThanOrEq(startTime))
                .querySingle() ;
    }
    public static List<VideoRecord> queryVideoRecord(){
        return SQLite.select()
                .from(VideoRecord.class)
                .queryList() ;
    }

    /** 删除指定时间内的数据
     * 同时需要清除对应文件夹里面的记录
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public static void delQueryVideoRecord(long startTime, long endTime){
        for (VideoRecord v:queryVideoRecord(startTime, endTime)){
            //  TODO 删除文件
            SQLite.delete(VideoRecord.class)
                    .where(VideoRecord_Table.name.eq(v.getName()))
                    .execute();
        }
//        SQLite.delete(VideoRecord.class)
//                .where(VideoRecord_Table.startTime.greaterThanOrEq(startTime),
//                        VideoRecord_Table.endTime.lessThanOrEq(endTime))
//                .execute();

    }

    /** 删除表内的所有记录
     * 如果是图片或者音视频，同时需要清除对应文件夹里面的记录
     * @param cls   表名
     * @param <T>   Java 类
     */
    public static <T> void deleteAllData(Class<T> cls){
        Delete.table(cls);
    }


//    /**
//     * 对于向数据库插入数据的操作，对于已经继承了 BaseModel 的 bean，
//     * 我们可以直接 new 一个出来，给相应的属性赋值之后，直接调用 save() 方法
//     */
//    public static void insertChatRecord(){
//        ChatRecord chatRecord=new ChatRecord();
//        chatRecord.save();
//    }
//
//    /**
//     * 保存一条语音记录
//     * @param name
//     * @param headUrl
//     * @param date
//     * @param gropId
//     * @param cusId
//     * @param voiceContent
//     */
//    public static void insertChatRecord(String name, String headUrl, long date, String gropId, String cusId, byte[] voiceContent){
//        ChatRecord chatRecord=new ChatRecord();
//        chatRecord.name=name;
//        chatRecord.headUrl=headUrl;
//        chatRecord.date=date;
//        chatRecord.gropId=gropId;
//        chatRecord.cusId=cusId;
//        chatRecord.voiceContent=voiceContent;
//        chatRecord.save();
//    }
//    /**
//     *查询ChatRecord
//     */
//    public static void queryChatRecord(){
////        List<ChatRecord> users = SQLite.select().from(ChatRecord.class).queryList();// 查询所有记录
////
////        ChatRecord user = SQLite.select().from(ChatRecord.class).querySingle();//   查询第一条记录
//
////        List<ChatRecord> chatRecords = SQLite.select()
////                .from(ChatRecord.class)
////                .where(ChatRecord_Table.name.isNotNull(), ChatRecord_Table.id.greaterThanOrEq(5L))// 这里的条件也可以多个
////                .orderBy(ChatRecord_Table.id, true)// 按照 id 升序
////                .limit(3)// 限制 3 条
////                .queryList();// 返回的 list 不为 null，但是可能为 empty
//
//
////        //根据 name 查询第一个
////        ChatRecord dbFlowModel = new Select()
////                .from(ChatRecord.class)
////                .where(ChatRecord_Table.name.is("Ruomiz"))
////                .querySingle();
//
//    }
//
//    /**
//     * 根据groupId查询历史记录
//     * @param groupId
//     */
//    public static List<ChatRecord> queryChatRecord(String groupId){
//             return SQLite.select()
//                .from(ChatRecord.class)
//                .where(ChatRecord_Table.gropId.eq(groupId))// 这里的条件也可以多个
//                .orderBy(ChatRecord_Table.id, true)// 按照 id 升序
//                .queryList();// 返回的 list 不为 null，但是可能为 empty
//    }
//    /**
//     * 根据groupId和时间查询历史记录
//     * @param groupId
//     */
//    public static List<ChatRecord> queryChatRecord(String groupId, long date){
//        return SQLite.select()
//                .from(ChatRecord.class)
//                .where(ChatRecord_Table.gropId.eq(groupId),ChatRecord_Table.date.greaterThanOrEq(date))// 这里的条件也可以多个
//                .orderBy(ChatRecord_Table.id, true)// 按照 id 升序
//                .queryList();// 返回的 list 不为 null，但是可能为 empty
//    }
//    /**
//     * 删除ChatRecord
//     */
//    @Deprecated
//    public static void deleteChatRecord(){
//        // 第一种 先查后删  删除最新的一条
////        ChatRecord product = SQLite.select()
////                .from(ChatRecord.class)
////                .querySingle();
////        if (product != null) {
////            product.delete();
////        }
////        // 第二种
////        SQLite.delete(ChatRecord.class)
////                .where(ChatRecord_Table.name.eq("PXXXX"))
////                .execute();
////        //3.SQLite.delete()
////        SQLite.delete(ChatRecord.class)
////                .where(ChatRecord_Table.name.is("title"))
////                .and(ChatRecord_Table.id.is(Long.valueOf(10)))
////                .async()
////                .execute();
////        //删除整张表
////        Delete.table(ChatRecord.class);
////        //删除多张表
////        Delete.table(ChatRecord.class,ChatRecord1.class);
//
//    }
//
//    /**
//     * 根据群组删除聊天记录
//     * @param groupid
//     */
//    public static void deleteChatRecordByGroupId(String groupid){
//        SQLite.delete(ChatRecord.class)
//                .where(ChatRecord_Table.gropId.eq(groupid))
//                .execute();
//    }
//
//    /**
//     * 根据群组和时间删除聊天记录
//     * @param groupid
//     * @param date
//     */
//    public static void deleteChatRecordByDay(String groupid, long date){
//        SQLite.delete(ChatRecord.class)
//                .where(ChatRecord_Table.gropId.eq(groupid),ChatRecord_Table.date.greaterThanOrEq(date))
//                .execute();
//    }
//
//    /**
//     * 删除所有聊天记录
//     */
//    public static void deleteAllChatRecord(){
//        //删除整张表
//        Delete.table(ChatRecord.class);
//    }
//    /**
//     * 更新ChatRecord
//     */
//    public static void upDateChatRecord(){
////        // 第一种 先查后改 不过查到的是第一条？
////        ChatRecord product = SQLite.select()
////                .from(ChatRecord.class)
////                .querySingle();// 区别与 queryList()
////        if (product != null) {
////            product.name = "P0000";
////            product.update();
////        }
////        // 第二种
////        SQLite.update(ChatRecord.class)
////                .set(ChatRecord_Table.name.eq("PXXXX"))
////                .where(ChatRecord_Table.name.eq("P0000"))
////                .execute();
//    }
//
//    /**
//     * 插入成员
//     * @param memberStatus
//     */
//    public static void insertMember(String currGrpId, QueryMemberResp.MemberStatus memberStatus){
//        GroupMember member = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(memberStatus.getMsId()),GroupMember_Table.currGrpId.eq(currGrpId))
//                .querySingle();//   查询第一条记录
//        if (member==null){
//            member=new GroupMember();
//            member.msId=memberStatus.getMsId();
//            member.callSet=memberStatus.getCallSet();
//            member.currGrpId=currGrpId;
//            member.msName=memberStatus.getMsName();
//            member.msType=memberStatus.getMsType();
//            member.on_grp=memberStatus.getOn_grp();
//            member.online=memberStatus.getOnline();
//            member.save();
//        }else {
//            member.msId=memberStatus.getMsId();
//            member.callSet=memberStatus.getCallSet();
//            member.currGrpId=currGrpId;
//            member.msName=memberStatus.getMsName();
//            member.msType=memberStatus.getMsType();
//            member.on_grp=memberStatus.getOn_grp();
//            member.online=memberStatus.getOnline();
//            member.update();
//        }
//
//    }
//
//    /**
//     * 插入群组成员
//     * @param currGrpId
//     * @param bean
//     */
//    public static void insertMember(String currGrpId, UserAllRespon.DataBean.GrpsBean.GrpUsersBean bean){
//        GroupMember member = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(bean.getMsId()),GroupMember_Table.currGrpId.eq(currGrpId))
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .querySingle();//   查询第一条记录
//        if (member==null){
//            member=new GroupMember();
//            member.msId=bean.getMsId();
//            member.currGrpId=currGrpId;
//            member.msName=bean.getName();
//            member.icon_url=bean.getIconUrl();
//            member.save();
//        }else {
//            member.msId=bean.getMsId();
//            member.currGrpId=currGrpId;
//            member.msName=bean.getName();
//            member.icon_url=bean.getIconUrl();
//            member.update();
//        }
//    }
//
//    public static void insertMember(AddFriendMessge messge){
//        GroupMember member = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(messge.getUserId()),GroupMember_Table.currGrpId.eq(messge.getGrpId()))
//                .querySingle();//   查询第一条记录
//        if (member==null){
//            member=new GroupMember();
//            member.msId=messge.getUserId();
//            member.currGrpId=messge.getGrpId();
//            member.msName=messge.getUserName();
//            member.on_grp=0;
//            member.icon_url=messge.getIconurl();
//            member.online=1;
//            member.save();
//        }else {
//            member.msId=messge.getUserId();
//            member.currGrpId=messge.getGrpId();
//            member.msName=messge.getUserName();
//            member.on_grp=0;
//            member.icon_url=messge.getIconurl();
//            member.online=1;
//            member.update();
//        }
//    }
//    /**
//     * 群组成员切换群组 更新
//     * @param msId
//     * @param currGrpId
//     */
//    public static void upDateMember(String msId, String currGrpId, byte on_grp){
////                SQLite.update(GroupMember.class)
////                .set(GroupMember_Table.currGrpId.eq(currGrpId),GroupMember_Table.on_grp.eq(on_grp))
////                .where(GroupMember_Table.msId.eq(msId))
////                .execute();
//
//        // 第一种 先查后改 不过查到的是第一条？
////        GroupMember product = SQLite.select()
////                .from(GroupMember.class)
////                .where(GroupMember_Table.msId.eq(msId))
////                .querySingle();// 区别与 queryList()
////        if (product != null) {
////            product.currGrpId = currGrpId;
////            product.on_grp=on_grp;
////            product.update();
////        }else {
////            product=new GroupMember();
////            product.currGrpId = currGrpId;
////            product.on_grp=on_grp;
////            product.save();
////        }
//    }
//
//    /**
//     * 成员在线状态更新
//     * @param msId
//     * @param online
//     */
//    public static void upDataMember(String msId, byte online){
////        GroupMember groupMember = SQLite.select()
////                .from(GroupMember.class)
////                .where(GroupMember_Table.msId.eq(msId))
////                .querySingle();//   查询第一条记录.
////        if (groupMember==null){
////            groupMember=new GroupMember();
////            groupMember.online=online;
////            groupMember.save();
////        }else {
////            groupMember.online=online;
////            groupMember.update();
////        }
//    }
//
//    /**
//     * 更新用户头像
//     * @param msId
//     * @param url
//     */
//    public static void upDataMember(String msId, String url){
//        GroupMember groupMember = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId))
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .querySingle();//   查询第一条记录
//        if (groupMember==null){
//            groupMember=new GroupMember();
//            groupMember.icon_url=url;
//            groupMember.save();
//        }else {
//            groupMember.icon_url=url;
//            groupMember.update();
//        }
//    }
//
//    /**
//     * 更新群组成员昵称
//     * @param msId
//     * @param groupId
//     * @param name
//     */
//    public static void upDateMemberName(String msId, String groupId, String name){
//        GroupMember groupMember = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId),GroupMember_Table.currGrpId.eq(groupId))
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .querySingle();//   查询第一条记录
//        if (groupMember!=null){
//            groupMember.msName=name;
//            groupMember.update();
//        }
//    }
//
//    /**
//     * 获取群组成员昵称
//     * @param msId
//     * @param groupId
//     * @return
//     */
//    public static String queryGroupMemberName(String msId, String groupId){
//        GroupMember groupMember= SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId),GroupMember_Table.currGrpId.eq(groupId))
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .querySingle();
//        if (groupMember==null|| TextUtils.isEmpty(groupMember.msName)){
//            return "";
//        }else {
//            return groupMember.msName;
//        }
//    }
//    /**
//     * 查询用户头像
//     * @param msId
//     * @return
//     */
//    public static String queryMemberIcon(String msId){
//        GroupMember groupMember=SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId))// 这里的条件也可以多个
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .querySingle();
//        if (groupMember==null|| TextUtils.isEmpty(groupMember.icon_url)){
//            return "";
//        }else {
//            return groupMember.icon_url;
//        }
//    }
//
//    /**
//     * 更新群组信息
//     * @param gripId
//     * @param msId
//     * @param headUrl
//     */
//    public static void insertGripInfo(String gripId, String msId, String headUrl){
//        GroupInfo info = SQLite.select()
//                .from(GroupInfo.class)
//                .where(GroupInfo_Table.msId.eq(msId),GroupInfo_Table.gripId.eq(gripId))
//                .querySingle();//   查询第一条记录
//        if (info==null){
//            info=new GroupInfo();
//            info.gripId=gripId;
//            info.msId=msId;
//            info.headUrl=headUrl;
//            info.save();
//        }else {
//            info.gripId=gripId;
//            info.msId=msId;
//            info.headUrl=headUrl;
//            info.update();
//        }
//    }
//    /**
//     * 获取群成员头像列表
//     * @param gripId
//     * @return
//     */
//    public static List<String> qureyMemberIconList(String gripId){
//       List<GroupInfo> list= SQLite.select()
//                .from(GroupInfo.class)
//                .where(GroupInfo_Table.gripId.eq(gripId))// 这里的条件也可以多个
//                .queryList();
//       if (list==null||list.size()==0){
//           List<String> list1=new ArrayList<>();
//           return list1;
//       }else {
//           List<String> list2=new ArrayList<>();
//           for (GroupInfo groupMember:list){
//               list2.add(groupMember.headUrl);
//           }
//           return list2;
//       }
//    }
//    /**
//     * 查询用户在线
//     * @param msId
//     * @return
//     */
//    public static int queryMemberOnlineStaus(String msId){
//        GroupMember groupMember= SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId),
//                        GroupMember_Table.online.eq((byte) 1))// 这里的条件也可以多个
//                .querySingle();
//        return groupMember==null?-1:groupMember.online;
//    }
//    /**
//     * 查询用户昵称
//     * @param msId
//     * @return
//     */
//    public static String queryMemberName(String msId){
//        return SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId))// 这里的条件也可以多个
//                .querySingle()
//                .msName;
//    }
//    /**
//     * 根据群组id 查所有成员数
//     * @param groupId
//     * @return
//     */
//    public static int  queryAllMemberSize(String groupId){
//        return SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.currGrpId.eq(groupId))// 这里的条件也可以多个
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .queryList().size();// 返回的 list 不为 null，但是可能为 empty
//
//    }
//
//    /**
//     * 获取成员列表
//     * @param groupId
//     * @return
//     */
//    public static List<GroupMember> queryAllMember(String groupId){
//        return SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.currGrpId.eq(groupId))// 这里的条件也可以多个
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .queryList();// 返回的 list 不为 null，但是可能为 empty
//
//    }
//    /**
//     * 根据群组id 查所有在线成员数
//     * @param groupId
//     * @return
//     */
//    public static int  queryOnlineMemberSize(String groupId){
//        return SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.currGrpId.eq(groupId),GroupMember_Table.online.eq((byte) 1),GroupMember_Table.on_grp.eq((byte) 1))// 这里的条件也可以多个
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .queryList().size();// 返回的 list 不为 null，但是可能为 empty
//
//    }
//
//    public static List<GroupMember> queryOnlineMemberSize1(String groupId){
//        return SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.currGrpId.eq(groupId))// 这里的条件也可以多个
//                .orderBy(GroupMember_Table.id, true)// 按照 id 升序
//                .queryList();// 返回的 list 不为 null，但是可能为 empty
//
//    }
//    /**
//     * 移除群成员
//     * @param groupId
//     * @param msId
//     */
//    public static void deleteMember(String groupId, String msId){
//        GroupMember product = SQLite.select()
//                .from(GroupMember.class)
//                .where(GroupMember_Table.msId.eq(msId),GroupMember_Table.currGrpId.eq(groupId))
//                .querySingle();// 区别与 queryList()
//        if (product != null) {
//            product.delete();
//        }
//    }
//    /**
//     * 删除成员表
//     */
//    public static void deleteMember(){
//        //删除整张表
//        Delete.table(GroupMember.class);
//    }
//
//    private static final String TAG = "DbTools";
//    /**
//     * 保存添加好友推送
//     * @param messge
//     */
//    public static void insetNewFriend(AddFriendMessge messge){
//        NewFriend newFriend = SQLite.select()
//                .from(NewFriend.class)
//                .where(NewFriend_Table.msId.eq(messge.getId()),NewFriend_Table.friendId.eq(messge.getFriendId()))
//                .querySingle();//   查询第一条记录
//        if (newFriend==null){
//            newFriend=new NewFriend();
//        }
//        newFriend.friendId=messge.getFriendId();
//        newFriend.friendName=messge.getFriendName();
//        newFriend.msId=messge.getId();
//        newFriend.msg=messge.getMsg();
//        newFriend.msgType=messge.getMsgType();
//        newFriend.save();
//    }
//
//    /**
//     * 删除添加好友推送数据
//     * @param FriendId
//     * @param msId
//     */
//    public static void deleteNewFriend(String FriendId, String msId){
//        SQLite.delete(NewFriend.class)
//                .where(NewFriend_Table.friendId.eq(FriendId),NewFriend_Table.msId.eq(msId))
//                .execute();
//    }
//
//    public static List<NewFriend> queryNewFriendList(String msId){
//        return SQLite.select()
//                .from(NewFriend.class)
//                .where(NewFriend_Table.msId.eq(msId))
//                .orderBy(NewFriend_Table.id, true)// 按照 id 升序
//                .queryList();
//    }
//
//    /**
//     * 插入好友数据
//     * @param bean
//     */
//    public static void insertFriend(UserAllRespon.DataBean.FriendsBean bean){
//        Friend friend = SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.eq(bean.getMsId()))
//                .orderBy(NewFriend_Table.id, true)// 按照 id 升序
//                .querySingle();//   查询第一条记录
//        if (friend==null){
//            friend=new Friend();
//            friend.icon_url=bean.getIconUrl();
//            friend.msId=bean.getMsId();
//            friend.isTop=bean.getIsTop();
//            friend.msName=bean.getName();
//            friend.save();
//        }else {
//            friend.icon_url=bean.getIconUrl();
//            friend.msId=bean.getMsId();
//            friend.isTop=bean.getIsTop();
//            friend.msName=bean.getName();
//            friend.update();
//        }
//    }
//
//    /**
//     * 更新好友状态
//     * @param msId
//     * @param IsTop
//     */
//    public static void upDateFriend(String msId, int IsTop){
//        //1:在线
//        Friend friend = SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.eq(msId))
//                .querySingle();//   查询第一条记录
//        if (friend != null) {
//            friend.isTop=IsTop;
//            friend.update();
//        }
//    }
//
//    /**
//     * 查询好友头像
//     * @param msId
//     * @return
//     */
//    public static String queryFriendIcon(String msId){
//        Friend friend = SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.eq(msId))
//                .querySingle();//   查询第一条记录
//        if (friend==null|| TextUtils.isEmpty(friend.icon_url)){
//            return "";
//        }
//        return friend.icon_url;
//    }
//
//    public static boolean isExitFriend(String msId){
//        Friend friend = SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.eq(msId))
//                .querySingle();
//        return friend==null?false:true;
//    }
//
//    public static void deletFriend(String msId){
//        SQLite.delete(Friend.class)
//                .where(Friend_Table.msId.eq(msId))
//                .execute();
//    }
//
//    public static void deletFriend(){
//        Delete.table(Friend.class);
//    }
//    /**
//     * 查询好友名字
//     * @param msId
//     * @return
//     */
//    public static String queryFriendName(String msId){
//        Friend friend=SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.eq(msId))
//                .querySingle();//   查询第一条记录
//        if (friend==null|| TextUtils.isEmpty(friend.getMsName())){
//            return "";
//        }
//        return  friend.msName;
//    }
//
//    /**
//     * 模糊查询
//     * @param content
//     * @return
//     */
//    public static List<Friend> queryFuzzyFriend(String content){
//        return SQLite.select()
//                .from(Friend.class)
//                .where(Friend_Table.msId.like("%"+content+"%"))
//                .or(Friend_Table.msName.like("%"+content+"%"))
//                .queryList();
//    }
////-----------------------------------------------------------二期--------------------------------------------------------------------------------------//
//
//    static Object insertGroup =new Object();
//
//    /**
//     * 插入群组新信息
//     * @param groupId
//     * @param groupName
//     * @param onlineNum
//     * @param allNum
//     */
//    public static void insertGroupInfo(String groupId, String groupName, String onlineNum, String allNum){
//        synchronized (insertGroup){
//        }
//    }
//
//    /**
//     * 插入群组信息
//     * @param bean
//     */
//    public static void insertGroupInfo(UserAllRespon.DataBean.GrpsBean bean){
//        if (bean==null){return;}
//        synchronized (insertGroup){
//            GroupTable info = SQLite.select()
//                    .from(GroupTable.class)
//                    .where(GroupTable_Table.groupId.eq(bean.getGrpId()))
//                    .querySingle();//   查询第一条记录
//            if (info==null){
//                info=new GroupTable();
//                info.groupId=bean.getGrpId();
//                info.groupName=bean.getGrpName();
//                info.topNum=bean.getTopNum();
//                info.sum=bean.getSum();
//                info.grpOwner=bean.getGrpOwner();
//                info.open=bean.isOpen();
//                info.type=bean.getType();
//                info.save();
//            }else {
//                info.groupName=bean.getGrpName();
//                info.topNum=bean.getTopNum();
//                info.sum=bean.getSum();
//                info.grpOwner=bean.getGrpOwner();
//                info.open=bean.isOpen();
//                info.type=bean.getType();
//                info.update();
//            }
//        }
//    }
//
//    /**
//     * 查询群组信息
//     * @param groupId
//     * @return
//     */
//    public static GroupTable queryGroupTable(String groupId){
//        return   SQLite.select()
//                .from(GroupTable.class)
//                .where(GroupTable_Table.groupId.eq(groupId))
//                .querySingle();//   查询第一条记录
//    }
//    static Object insertGroupMember= new Object();
//
//    /**
//     * 插入群组成员
//     * @param inGroupId
//     * @param bean
//     */
//    public static void insertGroupMember(String inGroupId, UserAllRespon.DataBean.GrpsBean.GrpUsersBean bean){
//        if (bean==null){return;}
//        synchronized (insertGroupMember){
//            GroupMemberTable info = SQLite.select()
//                    .from(GroupMemberTable.class)
//                    .where(GroupMemberTable_Table.msId.eq(bean.getMsId()),GroupMemberTable_Table.inGroupId.eq(inGroupId))
//                    .querySingle();//   查询第一条记录
//            if (info==null){
//                info=new GroupMemberTable();
//                info.inGroupId=inGroupId;
//                info.isFriend=bean.getIsTop();
//                info.msId=bean.getMsId();
//                info.msName=bean.getName();
//                info.icon_url=bean.getIconUrl();
//                info.isTop=bean.getIsTop();
//                info.pttGrp=bean.getPttGrp();
//                info.save();
//            }else {
//                info.isFriend=bean.getIsTop();
//                info.msId=bean.getMsId();
//                info.msName=bean.getName();
//                info.icon_url=bean.getIconUrl();
//                info.isTop=bean.getIsTop();
//                info.pttGrp=bean.getPttGrp();
//                info.update();
//            }
//        }
//    }
//
//    static Object insertRecent =new Object();
//
//    /**
//     * 插入最近对讲
//     * @param messageId
//     * @param type
//     * @param title
//     * @param subtitle
//     * @param content
//     * @param time
//     */
//    public static void insertRecentRecord(String messageId, int type, String title, String subtitle, String content, String time){
//        synchronized (insertRecent){
//            String msid = ClientManagers.getAccount();
//            if (TextUtils.isEmpty(msid)){
//                Log.d(TAG, "TextUtils.isEmpty(msid): ");
//                return;
//            }
//            RecentRecordTable info = SQLite.select()
//                    .from(RecentRecordTable.class)
//                    .where(RecentRecordTable_Table.messageId.eq(messageId))
//                    .querySingle();//   查询第一条记录
//            if (info==null){
//                info=new RecentRecordTable();
//                info.msid=msid;
//                info.messageId=messageId;
//                info.title=title;
//                info.subtitle=subtitle;
//                info.content=content;
//                info.time=time;
//                info.save();
//            }else {
//                info.delete();
//                info=new RecentRecordTable();
//                info.msid=msid;
//                info.messageId=messageId;
//                info.title=title;
//                info.subtitle=subtitle;
//                info.content=content;
//                info.time=time;
//                info.save();
//            }
//        }
//    }
//
//    /**
//     * 获取最近对讲数据
//     * @return
//     */
//    public static AsyncQuery<RecentRecordTable> queryRecentList(){
//        String msid = ClientManagers.getAccount();
//       return SQLite.select().from(RecentRecordTable.class)
//                .where(RecentRecordTable_Table.msid.is(msid))
//                .orderBy(GroupMember_Table.id, false)// 按照 id 升序
//                .async();//异步查询
//    }
}
