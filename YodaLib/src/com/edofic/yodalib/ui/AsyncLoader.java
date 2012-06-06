/*
 * Copyright 2012 Andraz Bajt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edofic.yodalib.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.edofic.yodalib.R;

/**
 * User: andraz
 * Date: 5/25/12
 * Time: 9:33 AM
 * Relative layout which supports hiding its children when executing a task.
 * first set  a task
 * then call load
 * children will be hidden and your task exected
 * remember to calll done from your task's onPostExecute to bring children back
 */
public class AsyncLoader<T1, T2, T3> extends RelativeLayout {
    private AsyncTask task;
    private View progress;

    public AsyncLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        progress = inflate(getContext(), R.layout.loading, null);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        progress.setLayoutParams(lp);
        addView(progress);
    }

    private void setChildrenVisibility(boolean visible) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            v.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
        progress.setVisibility(!visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setTask(AsyncTask<T1, T2, T3> task) {
        this.task = task;
    }

    public void load(T1... params) {
        setChildrenVisibility(false);
        task.execute(params);
    }

    public void done() {
        setChildrenVisibility(true);
    }
}
