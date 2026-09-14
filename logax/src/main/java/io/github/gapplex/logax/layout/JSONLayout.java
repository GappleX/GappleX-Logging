/*
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
package io.github.gapplex.logax.layout;

import io.github.gapplex.gxl.core.utils.JSONUtil;
import io.github.gapplex.gxl.core.utils.MessageFormatter;
import io.github.gapplex.logax.EventField;
import io.github.gapplex.logax.event.LogEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONLayout implements Layout{
    private final List<Object> format;

    public JSONLayout(String format, EventField... eventFields){
        this.format = MessageFormatter.formatWithArray(format, (Object[]) eventFields);
    }

    @Override
    public String format(LogEvent event) {
        Map<String, Object> rawInput = new LinkedHashMap<>();
        rawInput.put("time", event.getTimestamp());
        rawInput.put("logger", event.getLoggerName());
        rawInput.put("level", event.getLevel().toString());
        rawInput.put("message", event.getMessage());

        return JSONUtil.objectToJson(rawInput);
    }
}
