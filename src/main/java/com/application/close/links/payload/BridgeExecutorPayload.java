package com.application.close.links.payload;

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
public class BridgeExecutorPayload {

	@NotBlank(message = "Bridge name is required and cannot be blank")
	@Size(max = 25, message = "Bridge name must not exceed 25 characters")
	private String bridgeName;

	@Min(value = 1, message = "Modbus TCP ID must be greater than 0")
	private int tcpId;

	@Min(value = 1, message = "Slave ID must be greater than 0")
	@Max(value = 247, message = "Slave ID must not exceed 247 (Modbus standard limit)")
	private int slaveId;

	@Min(value = 0, message = "Offset must be non-negative")
	private int offset;

	@Min(value = 1, message = "Quantity must be at least 1")
	@Max(value = 125, message = "Quantity must not exceed 125 (Modbus standard limit for holding/input registers)")
	private int quantity;

	@NotNull(message = "Function type is required")
	private String functionType;

	@NotBlank(message = "Publish topic is required and cannot be blank")
	@Pattern(regexp = "^[^#+]*$", message = "Publish topic must not contain wildcard characters '+' or '#'")
	@Size(max = 255, message = "Publish topic must not exceed 255 characters")
	private String publishTopic;
}
