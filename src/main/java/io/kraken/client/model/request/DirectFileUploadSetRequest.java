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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kraken.client.model.Convert;
import io.kraken.client.model.Metadata;
import io.kraken.client.model.resize.AbstractResize;

import java.io.File;
import java.util.Set;



/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class DirectFileUploadSetRequest extends AbstractUploadSetRequest {

    @JsonIgnore
    private final File image;

    private DirectFileUploadSetRequest(Boolean dev,
                                    Boolean webp,
                                    Boolean lossy,
                                    Integer quality,
                                    Set<AbstractResize> resize,
                                    Set<Metadata> preserveMeta,
                                    Convert convert,
                                    File image) {
        super(dev, true, webp, lossy, quality, resize, preserveMeta, convert);

        //checkNotNull(image, "image must not be null");
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public static Builder builder(File image) {
        return new Builder(image);
    }

    public static class Builder extends AbstractUploadSetRequest.Builder<Builder> {
        private final File image;

        private Builder(File image) {
            this.image = image;
        }

        public DirectFileUploadSetRequest build() {
            return new DirectFileUploadSetRequest(
                    dev,
                    webp,
                    lossy,
                    quality,
                    resize,
                    preserveMeta,
                    convert,
                    image
            );
        }
    }
}
