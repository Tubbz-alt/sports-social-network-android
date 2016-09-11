package vn.datsan.datsan.serverdata.chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.datsan.datsan.models.chat.Member;
import vn.datsan.datsan.serverdata.CallBack;
import vn.datsan.datsan.utils.AppConstants;
import vn.datsan.datsan.utils.AppLog;

/**
 * Created by yennguyen on 8/13/16.
 */
public class MemberService {

    private static final String TAG = MemberService.class.getSimpleName();

    private DatabaseReference memberDatabaseRef = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_MEMBERS);
    private static MemberService instance = new MemberService();

    private MemberService() {}

    public static MemberService getInstance() {
        return instance;
    }

    public void add(String chatId, Member member) {
        getMemberDatabaseRef(chatId).setValue(member.toUserRoleMap());
    }

    public DatabaseReference getMemberDatabaseRef(String chatId) {
        return memberDatabaseRef.child(chatId);
    }

    // Get members uid for a chat
    public void getMembers(String chatId, final CallBack.OnResultReceivedListener callback) {

        getMemberDatabaseRef(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppLog.d(TAG, ":ValueEventListener:onDataChange");

                List<Member> members = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Member member = new Member(data.getKey(), (String) data.getValue());
                    members.add(member);
                }
                if (callback != null) {
                    callback.onResultReceived(members);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
