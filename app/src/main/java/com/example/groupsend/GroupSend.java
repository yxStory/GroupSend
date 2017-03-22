package com.example.groupsend;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupSend extends FragmentActivity implements HintDialogFragment.DialogFragmentCallback
        , HintDialogFragmentContracts.DialogFragmentCallbackCONTRACTS {
    EditText numbers,content;
    Button select,send;
    SmsManager sManager;
    //创建需要群发的号码列表
    ArrayList<String> sendList=new ArrayList<String>();
    // this request code is used to distinguish system permission dialogs
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTRACTS = 1;

    // this request code is used to distinguish normal dialogs
    private static final int HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE = 1;
    private static final int HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE_CONTRACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_send);
        select=(Button)findViewById(R.id.select);
        send=(Button)findViewById(R.id.send);
        numbers=(EditText)findViewById(R.id.numbers);
        content=(EditText)findViewById(R.id.content);
        sManager=SmsManager.getDefault();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                selectContracts(view1);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                textMessage(view2);
            }
        });
    }

    /**
     * 获取联系人
     * @param view1
     */
    public void selectContracts(View view1) {
        //检查权限
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
                !=PackageManager.PERMISSION_GRANTED){
            //do not have permission

            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(GroupSend.this,
                    Manifest.permission.READ_CONTACTS)){

                //去设置关闭通讯录权限时，点击选择联系人，跳出DialogFragment对话框
                DialogFragment newFragment=HintDialogFragmentContracts.newInstance(
                        R.string.hint_description_contracts_title,
                        R.string.hint_description_why_we_need_the_permission_contracts,
                        HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE_CONTRACTS);
                newFragment.show(getSupportFragmentManager(),"Authority Dialog");
                Toast.makeText(GroupSend.this, "这个设置关闭的通讯录", Toast.LENGTH_SHORT).show();

            }else{
                // No explanation needed, we can request the permission.
                //申请授权，点击后跳出是否开启通讯录权限，点击是后打开通讯录权限
                ActivityCompat.requestPermissions(GroupSend.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTRACTS);
            }
        }
        else{
            //has permission, do operation directly
            //选择通讯录中要发送消息的号码
            final Cursor cursor=getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,null,null);
            BaseAdapter baseAdapter=new BaseAdapter() {
                @Override
                public int getCount() {
                    return cursor.getCount();
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View view, ViewGroup viewGroup) {
                    cursor.moveToPosition(position);
                    CheckBox rb=new CheckBox(GroupSend.this);
                    //获取联系人的电话号码,去掉电话号码中的“-”
                    String number=cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace("-","");
                    rb.setText(number);
                    if(isChecked(number)){
                        rb.setChecked(true);
                    }
                    return rb;
                }
            };
            //加载list.xml对应的View
            View selectView=getLayoutInflater().inflate(R.layout.list,null);
            //获取selectView中id为list的listView
            final ListView listView=(ListView)selectView.findViewById(R.id.list);
            listView.setAdapter(baseAdapter);
            new AlertDialog.Builder(GroupSend.this)
                    .setView(selectView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //清空sendList集合
                            sendList.clear();
                            //遍历listView组件的每个列表项
                            for(int i=0;i<listView.getCount();i++){
                                CheckBox checkBox=(CheckBox)listView.getChildAt(i);
                                //如果列表项被勾选，加入到sendList
                                try{
                                    if(checkBox.isChecked()){
                                        sendList.add(checkBox.getText().toString());
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            numbers.setText(sendList.toString());
                        }
                    })
                    .show();
        }
    }

    //判断某个号码是否在群发范围
    private boolean isChecked(String number) {
        for(String s1:sendList){
            if(s1.equals(number)){
                return true;
            }
        }
        return false;
    }

    /**
     * 群发消息
     * @param view2
     */
    public void textMessage(View view2) {
        //检查权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            //do not have permission

            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(GroupSend.this,
                    Manifest.permission.SEND_SMS)){

                //去设置关闭短信权限时点击发送消息，跳出DialogFragment对话框
                DialogFragment newFragment=HintDialogFragment.newInstance(
                        R.string.hint_description_title,
                        R.string.hint_description_why_we_need_the_permission,
                        HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE);
                newFragment.show(getSupportFragmentManager(),"Authority Dialog");
                Toast.makeText(GroupSend.this, "这个设置关闭的消息", Toast.LENGTH_SHORT).show();

            }else{
                // No explanation needed, we can request the permission.
                //申请授权
                ActivityCompat.requestPermissions(GroupSend.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }

        }else{
            //has permission, do operation directly
            //发送消息,遍历所有的号码，逐一发送
            for(String number:sendList){
                PendingIntent pi = PendingIntent.getActivity(GroupSend.this
                        , 0, new Intent(), 0);
                sManager.sendTextMessage(number,null,
                        content.getText().toString(),pi,null);
            }
            Toast.makeText(GroupSend.this,"消息发送完毕!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void doPositiveClick(int requestCode){
        //申请授权
        if(HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE==requestCode){
            ActivityCompat.requestPermissions(GroupSend.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void doPositiveContractsClick(int requestCode){
        //申请授权
        if(HINT_DIALOG_EXPLAIN_CALENDAR_PERMISSION_REQUEST_CODE_CONTRACTS==requestCode){
            ActivityCompat.requestPermissions(GroupSend.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTRACTS);
        }
    }

    /**
     * 跳出权限，点击关闭后回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS ||
                requestCode==MY_PERMISSIONS_REQUEST_READ_CONTRACTS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(GroupSend.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}
