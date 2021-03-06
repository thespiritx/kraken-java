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
package io.kraken.client.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kraken.client.model.Convert;
import io.kraken.client.model.Metadata;
import io.kraken.client.model.resize.AbstractResize;

import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class ImageUrlUploadCallbackUrlSetRequest extends AbstractUploadCallbackUrlSetRequest {

    @JsonProperty("url")
    private final URL imageUrl;

    private ImageUrlUploadCallbackUrlSetRequest(Boolean dev,
                                             Boolean webp,
                                             Boolean lossy,
                                             Integer quality,
                                             Set<AbstractResize> resize,
                                             Set<Metadata> preserveMeta,
                                             Convert convert,
                                             URL callbackUrl,
                                             URL imageUrl) {
        super(dev, webp, lossy, quality, resize, preserveMeta, convert, callbackUrl);

        checkNotNull(imageUrl, "imageUrl must not be null");
        this.imageUrl = imageUrl;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public static Builder builder(URL imageUrl, URL callbackUrl) {
        return new Builder(imageUrl, callbackUrl);
    }

    public static class Builder extends AbstractUploadCallbackUrlSetRequest.Builder<Builder> {
        private final URL imageUrl;

        private Builder(URL imageUrl, URL callbackUrl) {
            super(callbackUrl);
            this.imageUrl = imageUrl;
        }

        public ImageUrlUploadCallbackUrlSetRequest build() {
            return new ImageUrlUploadCallbackUrlSetRequest(
                    dev,
                    webp,
                    lossy,
                    quality,
                    resize,
                    preserveMeta,
                    convert,
                    callbackUrl,
                    imageUrl
            );
        }
    }
}
