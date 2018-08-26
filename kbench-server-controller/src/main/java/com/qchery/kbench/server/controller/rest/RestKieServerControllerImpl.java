/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qchery.kbench.server.controller.rest;

import org.kie.server.api.model.KieServerInfo;
import org.kie.server.controller.api.model.KieServerSetup;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.server.controller.impl.KieServerControllerImpl;
import org.kie.server.controller.impl.storage.InMemoryKieServerControllerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;

import static com.qchery.kbench.server.controller.rest.ControllerUtils.marshal;
import static com.qchery.kbench.server.controller.rest.ControllerUtils.unmarshal;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RequestMapping("/controller")
public class RestKieServerControllerImpl extends KieServerControllerImpl {

    private static final Logger logger = LoggerFactory.getLogger(RestKieServerControllerImpl.class);

    private KieServerControllerStorage storage = InMemoryKieServerControllerStorage.getInstance();

    @PutMapping(value = "server/{id}")
    public ResponseEntity<String> connectKieServer(@PathVariable("id") String id,
                                                   @RequestBody String serverInfoPayload,
                                                   @RequestHeader(CONTENT_TYPE) String contentType) {

        logger.debug("Received connect request from server with id {}", id);
        KieServerInfo serverInfo = unmarshal(serverInfoPayload, contentType, KieServerInfo.class);
        logger.debug("Server info {}", serverInfo);
        KieServerSetup serverSetup = connect(serverInfo);

        logger.info("Server with id '{}' connected", id);
        String response = marshal(contentType, serverSetup);

        logger.debug("Returning response for connect of server '{}': {}", id, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @DeleteMapping("server/{id}")
    public ResponseEntity disposeContainer(@PathVariable("id") String id,
                                           @RequestParam("location") String serverLocation) {

        KieServerInfo serverInfo;
        try {
            serverInfo = new KieServerInfo(id, "", "", Collections.emptyList(), URLDecoder.decode(serverLocation, "UTF-8"));
            disconnect(serverInfo);
            logger.info("Server with id '{}' disconnected", id);
        } catch (UnsupportedEncodingException e) {
            logger.debug("Cannot URL decode kie server location due to unsupported encoding exception", e);
        }

        return null;
    }

}
