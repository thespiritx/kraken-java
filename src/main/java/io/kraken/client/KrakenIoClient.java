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
package io.kraken.client;

import io.kraken.client.model.request.DirectFileUploadCallbackUrlRequest;
import io.kraken.client.model.request.DirectFileUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.DirectFileUploadRequest;
import io.kraken.client.model.request.DirectFileUploadSetRequest;
import io.kraken.client.model.request.DirectUploadCallbackUrlRequest;
import io.kraken.client.model.request.DirectUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.DirectUploadRequest;
import io.kraken.client.model.request.DirectUploadSetRequest;
import io.kraken.client.model.request.ImageUrlUploadCallbackUrlRequest;
import io.kraken.client.model.request.ImageUrlUploadCallbackUrlSetRequest;
import io.kraken.client.model.request.ImageUrlUploadRequest;
import io.kraken.client.model.request.ImageUrlUploadSetRequest;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlResponse;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlSetResponse;
import io.kraken.client.model.response.SuccessfulUploadResponse;
import io.kraken.client.model.response.SuccessfulUploadSetResponse;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public interface KrakenIoClient {
    SuccessfulUploadResponse directUpload(DirectUploadRequest directUploadRequest);
    SuccessfulUploadSetResponse directUpload(DirectUploadSetRequest directUploadSetRequest);
    SuccessfulUploadResponse directUpload(DirectFileUploadRequest directFileUploadRequest);
    SuccessfulUploadSetResponse directUpload(DirectFileUploadSetRequest directFileUploadSetRequest);
    SuccessfulUploadResponse imageUrlUpload(ImageUrlUploadRequest imageUrlUploadRequest);
    SuccessfulUploadSetResponse imageUrlUpload(ImageUrlUploadSetRequest imageUrlUploadSetRequest);

    SuccessfulUploadCallbackUrlResponse directUpload(DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest);
    SuccessfulUploadCallbackUrlSetResponse directUpload(DirectUploadCallbackUrlSetRequest directUploadCallbackUrlSetRequest);
    SuccessfulUploadCallbackUrlResponse directUpload(DirectFileUploadCallbackUrlRequest directFileUploadCallbackUrlRequest);
    SuccessfulUploadCallbackUrlSetResponse directUpload(DirectFileUploadCallbackUrlSetRequest directFileUploadCallbackUrlSetRequest);
    SuccessfulUploadCallbackUrlResponse imageUrlUpload(ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest);
    SuccessfulUploadCallbackUrlSetResponse imageUrlUpload(ImageUrlUploadCallbackUrlSetRequest imageUrlUploadCallbackUrlSetRequest);
}
