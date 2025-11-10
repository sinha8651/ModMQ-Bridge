package com.application.close.links.entity;

import java.time.LocalDateTime;

import com.application.close.links.ModbusFunctionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class BridgeExecutor {

	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String bridgeName;

	private int tcpId;

	private int slaveId;

	@Column(name = "offset_value")
	private int offset;

	private int quantity;

	@Enumerated(EnumType.STRING)
	private ModbusFunctionType functionType;

	private String publishTopic;

	private LocalDateTime lastExecuted;
}
