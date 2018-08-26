/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.server.controller.api.KieServerControllerException;
import org.kie.server.controller.api.KieServerControllerIllegalArgumentException;
import org.kie.server.controller.api.model.spec.*;
import org.kie.server.controller.impl.service.SpecManagementServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;

@RequestMapping("/controller/management")
public class RestSpecManagementServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(RestSpecManagementServiceImpl.class);
    private static final String REQUEST_FAILED_TOBE_PROCESSED = "Request failed to be processed due to: ";

    private SpecManagementServiceImpl specManagementService;

    @ExceptionHandler(KieServerControllerIllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception e) {
        return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(KieServerControllerException.class)
    public ResponseEntity<String> handleKieServerControllerException(Exception e) {
        return ResponseEntity.status(BAD_REQUEST).body(REQUEST_FAILED_TOBE_PROCESSED + e.getMessage());
    }

    @PutMapping("servers/{id}/containers/{containerId}")
    public ResponseEntity<String> saveContainerSpec(@PathVariable("id") String serverTemplateId,
                                                    @PathVariable("containerId") String containerId,
                                                    @RequestBody String containerSpecPayload,
                                                    @RequestHeader(CONTENT_TYPE) String contentType) {

        try {
            logger.debug("Received save container spec request for server template with id {}", serverTemplateId);
            ContainerSpec containerSpec = ControllerUtils.unmarshal(containerSpecPayload, contentType, ContainerSpec.class);
            logger.debug("Container spec is {}", containerSpec);

            specManagementService.saveContainerSpec(serverTemplateId, containerSpec);
            logger.debug("Returning response for save container spec request for server template with id '{}': CREATED", serverTemplateId);
            return ResponseEntity.status(CREATED).body("");
        } catch (Exception e) {
            logger.error("Save container spec request for server template id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @PostMapping("servers/{id}/containers/{containerId}")
    public ResponseEntity<String> updateContainerSpec(@PathVariable("id") String serverTemplateId,
                                                      @PathVariable("containerId") String containerId,
                                                      @RequestBody String containerSpecPayload,
                                                      @RequestHeader(CONTENT_TYPE) String contentType) {

        try {
            logger.debug("Received update container spec request for server template with id {}", serverTemplateId);
            ContainerSpec containerSpec = ControllerUtils.unmarshal(containerSpecPayload, contentType, ContainerSpec.class);
            logger.debug("Container spec is {}", containerSpec);

            specManagementService.updateContainerSpec(serverTemplateId, containerId, containerSpec);
            logger.debug("Returning response for update container spec request for server template with id '{}': CREATED", serverTemplateId);
            return ResponseEntity.status(CREATED).body("");
        } catch (Exception e) {
            logger.error("Save container spec request for server template id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @PutMapping("servers/{id}")
    public ResponseEntity<String> saveServerTemplate(@PathVariable("id") String serverTemplateId,
                                                     @RequestBody String serverTemplatePayload,
                                                     @RequestHeader(CONTENT_TYPE) String contentType) {

        try {
            logger.debug("Received save server template with id {}", serverTemplateId);
            ServerTemplate serverTemplate = ControllerUtils.unmarshal(serverTemplatePayload, contentType, ServerTemplate.class);
            if (serverTemplate == null) {
                return ResponseEntity.status(NOT_FOUND).body("Server template " + serverTemplateId + " not found");
            }
            logger.debug("Server template is {}", serverTemplate);

            specManagementService.saveServerTemplate(serverTemplate);
            logger.debug("Returning response for save server template with id '{}': CREATED", serverTemplateId);
            return ResponseEntity.status(CREATED).body("");
        } catch (Exception e) {
            logger.error("Save server template id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @GetMapping(value = "servers/{id}")
    public ResponseEntity<String> getServerTemplate(@PathVariable("id") String serverTemplateId,
                                                    @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get server template with id {}", serverTemplateId);
            final ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverTemplateId);
            String response = ControllerUtils.marshal(contentType, serverTemplate);
            logger.debug("Returning response for get server template with id '{}': {}", serverTemplateId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get server template id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }

    }

    @GetMapping(value = "servers")
    public ResponseEntity<String> listServerTemplates(
            @RequestHeader(CONTENT_TYPE) String contentType) {

        try {
            logger.debug("Received get server templates");

            ServerTemplateList serverTemplateList = specManagementService.listServerTemplates();
            String response = ControllerUtils.marshal(contentType, serverTemplateList);
            logger.debug("Returning response for get server templates: {}", response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get server templates failed due to {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }


    @GetMapping(value = "servers/{id}/containers")
    public ResponseEntity<String> listContainerSpec(@PathVariable("id") String serverTemplateId,
                                                    @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get containers for server template with id {}", serverTemplateId);

            String response = ControllerUtils.marshal(contentType, specManagementService.listContainerSpec(serverTemplateId));
            logger.debug("Returning response for get containers for server templates with id {}: {}", serverTemplateId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get server templates failed due to {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @GetMapping(value = "servers/{id}/containers/{containerId}")
    public ResponseEntity<String> getContainerSpec(@PathVariable("id") String serverTemplateId,
                                                   @PathVariable("containerId") String containerId,
                                                   @RequestHeader(CONTENT_TYPE) String contentType) {
        try {
            logger.debug("Received get container {} for server template with id {}", containerId, serverTemplateId);

            ContainerSpec containerSpec = specManagementService.getContainerInfo(serverTemplateId, containerId);
            // set it as server template key only to avoid cyclic references between containers and templates
            containerSpec.setServerTemplateKey(new ServerTemplateKey(containerSpec.getServerTemplateKey().getId(), containerSpec.getServerTemplateKey().getName()));

            String response = ControllerUtils.marshal(contentType, containerSpec);
            logger.debug("Returning response for get container {} for server templates with id {}: {}", containerId, serverTemplateId, response);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Get server templates failed due to {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @DeleteMapping(value = "servers/{id}/containers/{containerId}")
    public ResponseEntity<String> deleteContainerSpec(@PathVariable("id") String serverTemplateId,
                                                      @PathVariable("containerId") String containerSpecId) {

        try {
            specManagementService.deleteContainerSpec(serverTemplateId, containerSpecId);
            // return null to produce 204
            return null;
        } catch (Exception e) {
            logger.error("Remove container with id {} from server template with id {} failed due to {}", containerSpecId, serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @DeleteMapping(value = "servers/{id}")
    public ResponseEntity<String> deleteServerTemplate(@PathVariable("id") String serverTemplateId) {
        try {
            specManagementService.deleteServerTemplate(serverTemplateId);
            // return null to produce 204
            return null;
        } catch (Exception e) {
            logger.error("Remove server template with id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @PostMapping(value = "servers/{id}/containers/{containerId}/config/{capability}")
    public ResponseEntity<String> updateContainerConfig(@PathVariable("id") String serverTemplateId,
                                                        @PathVariable("containerId") String containerSpecId,
                                                        @PathVariable("capability") String capabilityStr,
                                                        @RequestBody String containerConfigPayload,
                                                        @RequestHeader(CONTENT_TYPE) String contentType) {

        try {
            ContainerConfig containerConfig;
            Capability capability;
            if (capabilityStr.equals(Capability.PROCESS.name())) {
                capability = Capability.PROCESS;
                logger.debug("Received update container (with id {}) process config request for server template with id {}", containerSpecId, serverTemplateId);
                containerConfig = ControllerUtils.unmarshal(containerConfigPayload, contentType, ProcessConfig.class);
            } else if (capabilityStr.equals(Capability.RULE.name())) {
                capability = Capability.RULE;
                logger.debug("Received update container (with id {}) rule config request for server template with id {}", containerSpecId, serverTemplateId);
                containerConfig = ControllerUtils.unmarshal(containerConfigPayload, contentType, RuleConfig.class);
            } else {
                logger.debug("Not supported configuration type {}, returning bad request response", capabilityStr);
                return ResponseEntity.status(BAD_REQUEST).body("Not supported configuration " + capabilityStr);
            }
            logger.debug("Container configuration is {}", containerConfig);

            specManagementService.updateContainerConfig(serverTemplateId, containerSpecId, capability, containerConfig);
            logger.debug("Returning response for update container (with id {}) config '{}': CREATED", containerSpecId, containerConfig);
            return ResponseEntity.status(CREATED).body("");
        } catch (Exception e) {
            logger.error("Remove server template with id {} failed due to {}", serverTemplateId, e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @PostMapping(value = "servers/{id}/containers/{containerId}/status/stopped")
    public ResponseEntity<String> stopContainer(@PathVariable("id") String serverTemplateId,
                                                @PathVariable("containerId") String containerId) {
        logger.debug("Requesting stop container with id {} server instance: {}", containerId, serverTemplateId);
        try {
            ContainerSpecKey containerSpecKey = new ContainerSpecKey();
            containerSpecKey.setId(containerId);
            containerSpecKey.setServerTemplateKey(new ServerTemplateKey(serverTemplateId, ""));
            specManagementService.stopContainer(containerSpecKey);

            logger.debug("Returning response for stop container with id {} server instance: {}", containerId, serverTemplateId);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            logger.error("Stop container failed due to {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    @PostMapping(value = "servers/{id}/containers/{containerId}/status/started")
    public ResponseEntity<String> startContainer(@PathVariable("id") String serverTemplateId,
                                                 @PathVariable("containerId") String containerId) {
        logger.debug("Requesting start container with id {} server instance: {}", containerId, serverTemplateId);
        try {
            ContainerSpecKey containerSpecKey = new ContainerSpecKey();
            containerSpecKey.setId(containerId);
            containerSpecKey.setServerTemplateKey(new ServerTemplateKey(serverTemplateId, ""));
            specManagementService.startContainer(containerSpecKey);

            logger.debug("Returning response for start container with id {} server instance: {}", containerId, serverTemplateId);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            logger.error("Start container failed due to {}", e.getMessage(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unknown error " + e.getMessage());
        }
    }

    public void setSpecManagementService(final SpecManagementServiceImpl specManagementService) {
        this.specManagementService = specManagementService;
    }
}
