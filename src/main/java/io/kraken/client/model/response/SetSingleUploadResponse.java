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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class SetSingleUploadResponse extends AbstractSingleUploadResponse {

    private final String fileName;
    private final Integer originalSize;
    private final Integer krakedSize;
    private final Integer savedBytes;
    private final String krakedUrl;
    private final String originalWidth;
    private final String originalHeight;
    private final String krakedWidth;
    private final String krakedHeight;

    @JsonCreator
    public SetSingleUploadResponse(@JsonProperty("file_name") String fileName,
                                    @JsonProperty("original_size") Integer originalSize,
                                    @JsonProperty("kraked_size") Integer krakedSize,
                                    @JsonProperty("saved_bytes") Integer savedBytes,
                                    @JsonProperty("kraked_url") String krakedUrl,
                                    @JsonProperty("original_width") String originalWidth,
                                    @JsonProperty("original_height") String originalHeight,
                                    @JsonProperty("kraked_width") String krakedWidth,
                                    @JsonProperty("kraked_height") String krakedHeight) {
        super();
        this.fileName = fileName;
        this.originalSize = originalSize;
        this.krakedSize = krakedSize;
        this.savedBytes = savedBytes;
        this.krakedUrl = krakedUrl;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.krakedWidth = krakedWidth;
        this.krakedHeight = krakedHeight;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getOriginalSize() {
        return originalSize;
    }

    public Integer getKrakedSize() {
        return krakedSize;
    }

    public Integer getSavedBytes() {
        return savedBytes;
    }

    public String getKrakedUrl() {
        return krakedUrl;
    }

    public String getOriginalWidth() {
		return originalWidth;
	}

	public String getOriginalHeight() {
		return originalHeight;
	}

	public String getKrakedWidth() {
		return krakedWidth;
	}

	public String getKrakedHeight() {
		return krakedHeight;
	}

	@Override
    public String toString() {
        try {
            return "AbstractUploadResponse:" + objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "SuccessfulUploadResponse{" +
                    ", status='" + getStatus() + '\'' +
                    ", fileName='" + getFileName() + '\'' +
                    ", originalSize=" + getOriginalSize() +
                    ", krakedSize=" + getKrakedSize() +
                    ", savedBytes=" + getSavedBytes() +
                    ", krakedUrl='" + getKrakedUrl() + '\'' +
                    '}';
        }
    }
}
