package com.application.close.mqtt.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
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
public class MqttParam {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String clientId;

	private String url;

	private boolean sslEnabled;

	private boolean authEnabled;

	private String username;

	private String password;

	private byte connectTimeout; // in seconds

	private byte keepAlive; // in seconds

	private boolean autoReconnect;

	private boolean cleanStart;

	private byte qos;

	private boolean connected;

	private List<String> publishTopics;

	private List<String> subscribeTopics;

}
