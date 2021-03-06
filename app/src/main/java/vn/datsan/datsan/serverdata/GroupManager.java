package vn.datsan.datsan.serverdata;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.datsan.datsan.models.Group;
import vn.datsan.datsan.utils.AppLog;
import vn.datsan.datsan.utils.Constants;

/**
 * Created by xuanpham on 7/29/16.
 */
public class GroupManager {
    private static final String TAG = FieldManager.class.getName();
    private static GroupManager instance;
    private DatabaseReference groupDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GROUPS);
    private List<Group> userGroups;

    public GroupManager() {
    }

    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    public String getNewKey() {
        return groupDatabaseRef.push().getKey();
    }


    public void addGroup(Group group, String groupKey, DatabaseReference.CompletionListener listener) {
        String key = groupKey;
        if (key == null || key.isEmpty())
            groupDatabaseRef.push().getKey();
        group.setId(key);
        groupDatabaseRef.child(key).setValue(group, listener);
    }

    public List<Group> getUserGroups(final CallBack.OnResultReceivedListener callBack) {
        AppLog.log(AppLog.LogType.LOG_ERROR, "group", "get");
//        if (userGroups != null && !userGroups.isEmpty()) {
//            if (callBack != null)
//                callBack.onResultReceived(userGroups);
//            return userGroups;
//        }

        groupDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppLog.log(AppLog.LogType.LOG_ERROR, "group", "onReceived");
                userGroups = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Group field = postSnapshot.getValue(Group.class);
                    if (field != null)
                        userGroups.add(field);
                }
                if (callBack != null)
                    callBack.onResultReceived(userGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AppLog.log(AppLog.LogType.LOG_ERROR, "group", databaseError.toString());
            }
        });

        return userGroups;
    }

    public Group getGroup(String id) {
        groupDatabaseRef.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Group group = dataSnapshot.getValue(Group.class);

                        AppLog.d("Find group" + group.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        AppLog.e(TAG, "getUserId:onCancelled: " + databaseError.toException());
                    }
                });
        return null;

    }
}
