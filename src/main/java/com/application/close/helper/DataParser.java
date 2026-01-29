package com.application.close.helper;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DataParser {

	private int tcpId;

	private int slaveId;

	private int offset;

	private int quantity;

	private String functionType;

	private String receivedAt;

	private Map<Integer, Object> data;

}
