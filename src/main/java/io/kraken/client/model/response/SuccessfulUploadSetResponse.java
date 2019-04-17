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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Clinton LaForest
 * @since 1.1.2
 */
public class SuccessfulUploadSetResponse extends AbstractUploadResponse {
	
	private final List<SuccessfulUploadResponse> results;
	private final String fileName;
	private final String timestamp;
	
	@JsonCreator
	public SuccessfulUploadSetResponse(@JsonProperty("success") Boolean success,
									   @JsonProperty("results") List<SuccessfulUploadResponse> results,
									   @JsonProperty("file_name") String fileName,
									   @JsonProperty("timestamp") String timestamp){
		super(success);
		this.results = results;
		this.fileName = fileName;
		this.timestamp = timestamp;
	}
	
	public String getFileName() {
        return fileName;
    }
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public List<SuccessfulUploadResponse> getResults(){
		return results;
	}
	
	@Override
    public String toString() {
        try {
            return "AbstractUploadResponse:" + objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "SuccessfulUploadSetResponse{" +
                    "success='" + getSuccess() + '\'' +
                    ", status='" + getStatus() + '\'' +
                    ", timestamp='" + getTimestamp() + '\'' +
                    ", fileName='" + getFileName() + '\'' +
                    ", results=" + getResults() + '\'' +
                    '}';
        }
    }

}
