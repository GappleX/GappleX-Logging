package sandbox;/*
 * Copyright 2026 GapplX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.LoggerFactory;
import io.github.gapplex.gxl.core.utils.JSONUtil;

import java.util.*;

public class ti {
    public static void main(String[] args) {
        Map<Object, Object> map = new LinkedHashMap<>();
        Map<String, Object> map2 = new LinkedHashMap<>();
        Set<Object> set = new LinkedHashSet<>();
        Object[] array = {"hi", 1, '2'};
        int[][] IntArray = {{1, 2, 3},{1, 2, 3},{1, 2, 3}};

        set.add("hi");
        set.add(1);
        set.add('2');

        map2.put("\"\n\b\f\r\thi", "\"\n\b\f\r\thi");
        map2.put("hi2", 2);
        map2.put("hi3", '2');

        map.put("hi", "hi");
        map.put("hi2", 2);
        map.put("hi3", '2');
        map.put("map", map2);
        map.put("nullTest", null);
        map.put("nan", Double.NaN);
        map.put("set", set);
        map.put("array", array);
        map.put("unknownObject", new Object());
        map.put("intArray", IntArray);
        map.put("test", map);

        System.out.println(JSONUtil.objectToJson(map));
        System.out.println(JSONUtil.objectToJson(array));
        System.out.println(JSONUtil.objectToJson(new Object()));
        System.out.println(JSONUtil.objectToJson(IntArray));

        LoggerFactory.setLoggerCreator(Logger::simple);
        List<String> name = new ArrayList<>();
        name.add("Java");
        name.add("Python");
        name.add("C");
        name.add("C++");
        name.add("Go");
        name.add("Rust");
        name.add("PHP");
        name.add("Kotlin");
        name.add("JS");
        name.add("Groovy");
        for (int i =0; i < 10; i++){
            Logger logger = LoggerFactory.getLogger(name.get(i));
            logger.info("Hi!I am " + name.get(i));
        }
        Logger logger = LoggerFactory.getLogger(ti.class);
        logger.info(LoggerFactory.getStatus());
    }
}
