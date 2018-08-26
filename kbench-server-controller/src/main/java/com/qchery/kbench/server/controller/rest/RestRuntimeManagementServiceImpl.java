/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.qchery.kbench.server.controller.rest;

import org.kie.server.controller.api.KieServerControllerException;
import org.kie.server.controller.api.KieServerControllerIllegalArgumentException;
import org.kie.server.controller.api.model.runtime.ContainerList;
import org.kie.server.controller.api.model.runtime.ServerInstanceKeyList;
import org.kie.server.controller.impl.service.RuntimeManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.qchery.kbench.server.controller.rest.ControllerUtils.marshal;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;

@RequestMapping("/controller/runtime")
public class RestRuntimeManagementServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RestRuntimeManagementServiceImpl.class);
    private static final String REQUEST_FAILED_TOBE_PROCESSED = "Request failed to be processed due to: ";

    private RuntimeManagementServiceImpl runtimeManagementService;

    @GetMapping(value = "servers/{id}/instances")
    public ResponseEntity<String> getServerInstances(@PathVariable("id") String serverTemplateId,
                                                     @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get server template with id {}", serverTemplateId);
            final ServerInstanceKeyList instances = runtimeManagementService.getServerInstances(serverTemplateId);
            String response = marshal(contentType, instances);
            logger.debug("Returning response for get server instance with server template id '{}': {}", serverTemplateId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get server instances using server template id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }

    }

    @ExceptionHandler(KieServerControllerException.class)
    private ResponseEntity<String> handleKieServerControllerException(KieServerControllerException e) {
        return ResponseEntity.status(BAD_REQUEST).body(REQUEST_FAILED_TOBE_PROCESSED + e.getMessage());
    }

    @ExceptionHandler(KieServerControllerIllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(KieServerControllerIllegalArgumentException e) {
        return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
    }

    @GetMapping(value = "servers/{id}/instances/{instanceId}/containers")
    public ResponseEntity<String> getServerInstanceContainers(@PathVariable("id") String serverTemplateId,
                                                              @PathVariable("instanceId") String instanceId,
                                                              @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get containers for server template with id {} and instance id {}", serverTemplateId, instanceId);

            ContainerList containers = runtimeManagementService.getServerInstanceContainers(serverTemplateId, instanceId);
            String response = marshal(contentType, containers);
            logger.debug("Returning response for get containers for server template with id {} and instance id {}: {}", serverTemplateId, instanceId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get containers using server template id {} and instance id {} failed due to {}", serverTemplateId, instanceId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @GetMapping(value = "servers/{id}/containers/{containerId}/instances")
    public ResponseEntity<String> getServerTemplateContainers(@PathVariable("id") String serverTemplateId,
                                                              @PathVariable("containerId") String containerId,
                                                              @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get container {} for server template with id {}", containerId, serverTemplateId);

            ContainerList containers = runtimeManagementService.getServerTemplateContainers(serverTemplateId, containerId);
            String response = marshal(contentType, containers);
            logger.debug("Returning response for get containers for server template with id {} and container id {}: {}", serverTemplateId, containerId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get containers using server template id {} and container id {} failed due to {}", serverTemplateId, containerId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    public void setRuntimeManagementService(final RuntimeManagementServiceImpl runtimeManagementService) {
        this.runtimeManagementService = runtimeManagementService;
    }
}
