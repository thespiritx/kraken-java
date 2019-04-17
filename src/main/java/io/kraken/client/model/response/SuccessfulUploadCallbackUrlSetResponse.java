/**
 * Copyright (C) 2015 Nekkra UG (oss@kraken.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kraken.client.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class SuccessfulUploadCallbackUrlSetResponse {

    @JsonIgnore
    protected final ObjectMapper objectMapper = new ObjectMapper();

    private final String id;

    @JsonCreator
    public SuccessfulUploadCallbackUrlSetResponse(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        try {
            return "AbstractUploadResponse:" + objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "SuccessfulUploadCallbackUrlResponse{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
