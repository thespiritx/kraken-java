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



/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public abstract class AbstractUploadCallbackUrlSetRequest extends AbstractUploadSetRequest {

    @JsonProperty("callback_url")
    private final URL callbackUrl;

    protected AbstractUploadCallbackUrlSetRequest(Boolean dev,
                                               Boolean webp,
                                               Boolean lossy,
                                               Integer quality,
                                               Set<AbstractResize> resize,
                                               Set<Metadata> preserveMeta,
                                               Convert convert,
                                               URL callbackUrl) {
        super(dev, false, webp, lossy, quality, resize, preserveMeta, convert);

        //checkNotNull(callbackUrl, "callbackUrl must not be null");
        this.callbackUrl = callbackUrl;
    }

    public URL getCallbackUrl() {
        return callbackUrl;
    }

    protected static class Builder<T extends Builder> extends AbstractUploadSetRequest.Builder<T> {

        protected final URL callbackUrl;

        protected Builder(URL callbackUrl) {
            this.callbackUrl = callbackUrl;
        }
    }
}
