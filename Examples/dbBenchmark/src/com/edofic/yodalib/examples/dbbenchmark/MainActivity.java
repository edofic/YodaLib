package com.edofic.yodalib.examples.dbbenchmark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.edofic.yodalib.database.Datasource;

import java.util.List;

/**
 * User: andraz
 * Date: 5/2/12
 * Time: 12:41 PM
 */
public class MainActivity extends Activity {
    private DatasourceManual dm;
    private Datasource<Person> da;
    private static final int BATCH_REPS = 100;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dm = new DatasourceManual(this);
        da = new Datasource<Person>(this, Person.class);
    }

    public void insertSingleManual(View v) {
        long start = System.currentTimeMillis();
        dm.open();
        dm.insertPerson(new Person(0, "manny", 133));
        dm.close();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void insertBatchManual(View v) {
        long start = System.currentTimeMillis();
        Person p = new Person(0, "manny batch", 12);
        dm.open();
        for (int i = 0; i < BATCH_REPS; i++) {
            dm.insertPerson(p);
        }
        dm.close();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void clearManual(View v) {
        long start = System.currentTimeMillis();
        dm.open();
        dm.clear();
        dm.close();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void loadAllManual(View v) {
        long start = System.currentTimeMillis();
        dm.open();
        List<Person> list = dm.getAllPersons();
        dm.close();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    /**
     * *********************************
     */

    public void insertSingle(View v) {
        long start = System.currentTimeMillis();
        da.insertSingle(new Person(0, "auto", -1));
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void insertBatch(View v) {
        long start = System.currentTimeMillis();
        da.beginTransaction();
        Person p = new Person(0, "auto batch", 12);
        for (int i = 0; i < BATCH_REPS; i++) {
            da.insert(p);
        }
        da.transactionSuccessful();
        da.endTransaction();
        da.close();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void clear(View v) {
        long start = System.currentTimeMillis();
        da.clear();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }

    public void loadAll(View v) {
        long start = System.currentTimeMillis();
        List<Person> all = da.getAll();
        long stop = System.currentTimeMillis();
        Toast.makeText(this, (stop - start) + "ms", Toast.LENGTH_LONG).show();
    }
}