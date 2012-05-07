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

package com.edofic.yodalib.database;

import java.util.HashMap;
import java.util.Map;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 1:01 PM
 * Static factory of meta data for classes with internal pool for caching
 */
class MetaDataFactory {
    private static final Map<Class, TableMetaData> data = new HashMap<Class, TableMetaData>();

    public static TableMetaData get(Class c) {
        TableMetaData t = data.get(c);
        if (t == null) {
            t = new TableMetaData(c);
            data.put(c, t);
        }
        return t;
    }

    private MetaDataFactory() {
    }
}
