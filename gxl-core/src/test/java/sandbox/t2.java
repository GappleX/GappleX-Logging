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
import io.github.gapplex.gxl.core.utils.JSONUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class t2 {
    public static void main(String[] args) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < 3000; i++){
            objects.add(generateNestedMap());
        }

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++){
            JSONUtil.objectToJson(objects);
        }
        long end = System.nanoTime();

        double seconds = (end - start) / 1_000_000_000.0;
        double throughput = 10000 / seconds;
        System.out.println(throughput+ " ops/s");
    }

    public static Object generateRandomObject(int depth) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (depth <= 0) {
            // 最底层返回基本类型
            int type = random.nextInt(0, 3);
            Object value;
            switch (type) {
                case 0 :{
                    value = UUID.randomUUID().toString().substring(0, 6);
                    break;
                }
                case 1 :{
                    value = random.nextInt(0, 100);
                    break;
                }
                default :{
                    value = random.nextBoolean();
                }
            }
            return value;
        }

        int type = random.nextInt(0, 3);
        // 随机返回 Map、List 或 基本类型
        if (type == 0) {
            // 构造 List（包含 1~3 个随机元素）
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < random.nextInt(1, 4); i++) {
                list.add(generateRandomObject(depth - 1));
            }
            return list;
        } else if (type == 1) {
            // 构造嵌套 Map（随机 1~3 个字段）
            Map<String, Object> nestedMap = new HashMap<>();
            for (int i = 0; i < random.nextInt(1, 4); i++) {
                nestedMap.put("field_" + i, generateRandomObject(depth - 1));
            }
            return nestedMap;
        } else {
            return generateRandomObject(0); // 直接返回基本类型
        }
    }

    // 外部调用方法
    public static Map<String, Object> generateNestedMap() {
        Map<String, Object> root = new HashMap<>();
        root.put("data", generateRandomObject(3)); // 控制嵌套深度为 3
        return root;
    }
}
