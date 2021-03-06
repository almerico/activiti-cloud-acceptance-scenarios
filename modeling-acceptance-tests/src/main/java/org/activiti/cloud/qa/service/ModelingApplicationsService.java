/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.qa.service;

import java.io.File;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import org.activiti.cloud.acc.shared.rest.feign.FeignRestDataClient;
import org.activiti.cloud.organization.api.Application;
import org.activiti.cloud.organization.api.Model;
import org.springframework.hateoas.Resource;

import static org.activiti.cloud.qa.rest.ModelingFeignConfiguration.modelingDecoder;
import static org.activiti.cloud.qa.rest.ModelingFeignConfiguration.modelingEncoder;

/**
 * Modeling groups service
 */
public interface ModelingApplicationsService extends FeignRestDataClient<ModelingApplicationsService, Application> {

    String PATH = "/v1/applications";

    @RequestLine("GET")
    @Headers("Content-Type: application/json")
    Response exportApplication();

    @RequestLine("POST")
    @Headers("Content-Type: multipart/form-data")
    Resource<Model> importApplicationModel(@Param("file") File file);

    @Override
    default Class<ModelingApplicationsService> getType() {
        return ModelingApplicationsService.class;
    }

    default Response exportApplicationByUri(String uri) {
        return FeignRestDataClient
                .builder(new FormEncoder(new JacksonEncoder()),
                         new Decoder.Default())
                .target(getType(),
                        uri)
                .exportApplication();
    }

    default Resource<Model> importApplicationModelByUri(String uri,
                                                        File file) {
        return FeignRestDataClient
                .builder(new FormEncoder(new JacksonEncoder()),
                         modelingDecoder)
                .target(getType(),
                        uri)
                .importApplicationModel(file);
    }

    @Override
    default Encoder encoder() {
        return modelingEncoder;
    }

    @Override
    default Decoder decoder() {
        return modelingDecoder;
    }

    static ModelingApplicationsService build(Encoder encoder,
                                             Decoder decoder,
                                             String baseUrl) {
        return FeignRestDataClient
                .builder(encoder,
                         decoder)
                .target(ModelingApplicationsService.class,
                        baseUrl + PATH);
    }
}
