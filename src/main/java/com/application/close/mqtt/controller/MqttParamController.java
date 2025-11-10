package com.application.close.mqtt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.payload.MqttParamPayload;
import com.application.close.mqtt.service.MqttParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Tag(name = "Mqtt Param", description = "Operations related to Mqtt data management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mqttParam")
public class MqttParamController {

	private final MqttParamService paramService;

	@Operation(summary = "Create a new Mqtt param entry", description = "Adds a new Mqtt param record to the system.")
	@PostMapping
	public ResponseEntity<MqttParam> create(@Valid @RequestBody MqttParamPayload paramPayload) {
		MqttParam param = paramService.create(paramPayload);
		return ResponseEntity.status(HttpStatus.CREATED).body(param);
	}

	@Operation(summary = "Update an existing Mqtt param entry", description = "Updates details of a specific Mqtt param by its ID.")
	@PutMapping("/{paramId}")
	public ResponseEntity<MqttParam> update(@PathVariable @NotNull Integer paramId,
			@Valid @RequestBody MqttParamPayload paramPayload) {
		MqttParam updatedParam = paramService.update(paramId, paramPayload);
		return ResponseEntity.ok(updatedParam);
	}

	@Operation(summary = "Get Mqtt param by ID", description = "Fetches a Mqtt param record by its ID.")
	@GetMapping("/{paramId}")
	public ResponseEntity<MqttParam> getById(@PathVariable @NotNull Integer paramId) {
		MqttParam param = paramService.getById(paramId);
		return ResponseEntity.ok(param);
	}

	@Operation(summary = "Get all Mqtt param entries", description = "Retrieves all Mqtt param records.")
	@GetMapping
	public ResponseEntity<List<MqttParam>> getAll() {
		List<MqttParam> allData = paramService.getAll();
		return ResponseEntity.ok(allData);
	}

	@Operation(summary = "Delete Mqtt param by ID", description = "Removes a Mqtt param record by its ID.")
	@DeleteMapping("/{paramId}")
	public ResponseEntity<Void> deleteById(@PathVariable @NotNull Integer paramId) {
		paramService.delete(paramId);
		return ResponseEntity.noContent().build();
	}

//	@Operation(summary = "Add publish topics", description = "Adds or updates publish topics for a specific Mqtt param.")
//	@PatchMapping("/add/publishTopics")
//	public ResponseEntity<MqttParam> addPublishTopics(@Valid @RequestBody TopicPayload topicPayload) {
//		MqttParam updatedParam = paramService.addPublishTopics(topicPayload);
//		return ResponseEntity.ok(updatedParam);
//	}
//
//	@Operation(summary = "Add subscribe topics", description = "Adds or updates subscribe topics for a specific Mqtt param.")
//	@PatchMapping("/add/subscribeTopics")
//	public ResponseEntity<MqttParam> addSubscribeTopics(@Valid @RequestBody TopicPayload topicPayload) {
//		MqttParam updatedParam = paramService.addSubscribeTopics(topicPayload);
//		return ResponseEntity.ok(updatedParam);
//	}
}
