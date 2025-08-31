package com.application.close.modtcp.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TcpPayload {

	@NotBlank(message = "Host cannot be null")
	private String host;

	@Min(value = 1, message = "Port must be greater than 0")
	@Max(value = 65535, message = "Port must be less than or equal to 65535")
	@NotNull(message = "Port cannot be null")
	private int port;

	private boolean keepAlive;

	@Min(value = 1000, message = "Connection timeout must be at least 1000 ms")
	@Max(value = 60000, message = "Connection timeout cannot exceed 60000 ms")
	@NotNull(message = "Connection Timeout cannot be null")
	private int connectionTimeout;

}
