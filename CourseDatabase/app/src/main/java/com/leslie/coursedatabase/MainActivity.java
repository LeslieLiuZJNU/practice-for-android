package com.leslie.coursedatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TABLE = "courseInfo";
    private final static String TAG = "CourseInformation";
    private CourseHelper helper;
    private ArrayList<Course> courseList;
    private EditText et_number, et_name, et_credit;
    private Button btn_add, btn_del, btn_upd, btn_query;
    private ListView lv_course;
    private String number, name, credit;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new CourseHelper(this.getApplicationContext());
        courseList = new ArrayList<Course>();

        et_number = findViewById(R.id.editText_number);
        et_name = findViewById(R.id.editText_name);
        et_credit = findViewById(R.id.editText_credit);
        btn_add = findViewById(R.id.button_store);
        btn_del = findViewById(R.id.button_delete);
        btn_upd = findViewById(R.id.button_update);
        btn_query = findViewById(R.id.button_query);
        lv_course = findViewById(R.id.listView_course);

        btn_add.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_upd.setOnClickListener(this);
        btn_query.setOnClickListener(this);

        Log.i(TAG, "OPEN");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_store:
                number = et_number.getText().toString().trim();
                name = et_name.getText().toString().trim();
                credit = et_credit.getText().toString().trim();
                if (!number.isEmpty() && !name.isEmpty() && !credit.isEmpty()) {
                    db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("number", et_number.getText().toString().trim());
                    values.put("name", et_name.getText().toString().trim());
                    values.put("credit", et_credit.getText().toString().trim());
                    long position = db.insert(TABLE, null, values);
                    if (position == -1) {
                        Log.i(TAG, "Insert failed.");
                        Toast.makeText(this, "新增失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "Insert at " + position + ".");
                        Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                } else {
                    Log.i(TAG, "Invalid input.");
                    Toast.makeText(this, "缺少信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_delete:
                number = et_number.getText().toString().trim();
                if (!number.isEmpty()) {
                    db = helper.getWritableDatabase();
                    int num = db.delete(TABLE, "number=?", new String[]{number});
                    if (num == 0) {
                        Log.i(TAG, "Delete failed.");
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, num + " record removed.");
                        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                } else {
                    Log.i(TAG, "Invalid input.");
                    Toast.makeText(this, "缺少信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_update:
                number = et_number.getText().toString().trim();
                name = et_name.getText().toString().trim();
                credit = et_credit.getText().toString().trim();
                if (!number.isEmpty() && !name.isEmpty() && !credit.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("credit", credit);
                    db = helper.getWritableDatabase();
                    int num = db.update(TABLE, values, "number=?", new String[]{number});
                    if (num == 0) {
                        Log.i(TAG, "Update failed.");
                        Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, num + " records updated.");
                        Toast.makeText(this, "更改成功", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                } else {
                    Log.i(TAG, "Invalid input.");
                    Toast.makeText(this, "缺少信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_query:
                db = helper.getReadableDatabase();
                Cursor cursor = db.query(TABLE, null, null, null, null, null, null);
                if (cursor.getCount() == 0) {
                    Log.i(TAG, "No table rows.");
                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
                } else {
                    courseList.clear();
                    String info = "Table rows: \n";
                    while (cursor.moveToNext()) {
                        info += cursor.getColumnName(0) + " " + cursor.getString(0) + "   " + cursor.getColumnName(1) + " " + cursor.getString(1) + "   " + cursor.getColumnName(2) + " " + cursor.getString(2) + "\n";
                        Course course = new Course(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                        courseList.add(course);
                    }
                    lv_course.setAdapter(new CourseAdapter(cursor.getCount()));
                    Log.i(TAG, info);
                    Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                db.close();
                break;
        }
    }

    private class CourseAdapter extends BaseAdapter {
        private int count;

        public CourseAdapter(int count) {
            this.count = count;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = View.inflate(MainActivity.this, R.layout.course_item_layout, null);

            TextView tv_number = itemView.findViewById(R.id.textView_number);
            TextView tv_name = itemView.findViewById(R.id.textView_name);
            TextView tv_credit = itemView.findViewById(R.id.textView_credit);

            tv_number.setText(courseList.get(i).getNumber());
            tv_name.setText(courseList.get(i).getName());
            tv_credit.setText(courseList.get(i).getCredit().toString());

            return itemView;
        }
    }
}
