package com.edofic.yodalib.examples.benchmark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.edofic.yodalib.database.Datasource;

import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    public void readAll(View v) {
        Datasource<TestClass> db = new Datasource<TestClass>(this, TestClass.class);

        List<TestClass> all;
        long start = System.currentTimeMillis();
        all = db.getAll();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void insert100(View v) {
        Datasource<TestClass> db = new Datasource<TestClass>(this, TestClass.class);
        long start = System.currentTimeMillis();
        try {
            db.beginTransaction();
            for (int i = 0; i < 25; i++) {
                db.insert(new TestClass(0, 12.3, "bla"));
                db.insert(new TestClass(0, 1.3, "blabla"));
                db.insert(new TestClass(0, 13.37, "works"));
                db.insert(new TestClass(0, 1337, "workss"));
            }
            db.transactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
            db.close();
        }
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();

    }

    public void clear(View v) {
        Datasource<TestClass> db = new Datasource<TestClass>(this, TestClass.class);

        long start = System.currentTimeMillis();
        db.clear();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }
}
