package com.application.close.mqtt.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MqttParamPayload {

	@Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters")
	private String name;

	@NotBlank(message = "Client ID is required")
	@Size(min = 1, max = 23, message = "Client ID must be 1 to 23 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Client ID must be alphanumeric with '-' or '_'")
	private String clientId;

	@NotNull(message = "Host is required")
	@Pattern(regexp = "^(?!-)[a-zA-Z0-9.-]+(?<!-)$", message = "Invalid host format")
	private String host;

	@NotNull(message = "Port is required")
	@Min(value = 1, message = "Port must be > 0")
	@Max(value = 65535, message = "Port must be <= 65535")
	private String port;

//	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters")
	private String userName;

//	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
	private String password;

	@NotNull(message = "Connection timeout is required")
	@Min(value = 1, message = "Timeout must be at least 1 second")
	@Max(value = 120, message = "Timeout must not exceed 120 seconds")
	private byte connectTimeout;

	@NotNull(message = "Keep-alive interval is required")
	@Min(value = 5, message = "Keep-alive must be at least 5 seconds")
	@Max(value = 300, message = "Keep-alive must not exceed 300 seconds")
	private byte keepAlive;

	@NotNull(message = "Auto-reconnect flag is required")
	private boolean autoReconnect;

	@NotNull(message = "Clean start flag is required")
	private boolean cleanStart;

	@NotNull(message = "QoS level is required")
	@Min(value = 0, message = "QoS must be 0, 1, or 2")
	@Max(value = 2, message = "QoS must be 0, 1, or 2")
	private byte qos;
}
