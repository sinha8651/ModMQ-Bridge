package com.application.close.helper;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import lombok.Getter;

@Getter
@Service
public class MemoryBuffer {

	// Maps a unique TCP data ID to its corresponding ModbusMasterTCP instance.
	private ConcurrentHashMap<Integer, ModbusMaster> modbusMaster;

	public MemoryBuffer() {
		super();
		this.modbusMaster = new ConcurrentHashMap<>();
	}

}
