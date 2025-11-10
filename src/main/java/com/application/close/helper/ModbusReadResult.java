package com.application.close.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ModbusReadResult {

	private boolean[] coilOrDiscreteInput;

	private int[] registers;

}
