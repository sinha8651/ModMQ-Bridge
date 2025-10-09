package com.application.close.mqtt.payload;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TopicPayload {

	@NotNull(message = "Id must not be null")
	@Min(value = 1, message = "Id must be greater than 0")
	private Integer mqttParamId;

	@NotEmpty(message = "At least one topic must be provided")
	private List<@Pattern(regexp = "^(?!.*[#+]).*$", message = "Publish topic must not contain wildcards") 
				 @NotBlank(message = "Topic cannot be blank") String> topics;

}
