package com.application.close.modtcp.entity;

import com.application.close.modtcp.payload.TcpPayload;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

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
@Table(name = "tcp_param")
public class TcpData {

	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String host;

	private int port;

	private boolean keepAlive;

	private int connectionTimeout;

	public static TcpData of(TcpPayload tcp) {
		TcpData data = new TcpData();
		data.setHost(tcp.getHost());
		data.setKeepAlive(tcp.isKeepAlive());
		data.setPort(tcp.getPort());
		data.setConnectionTimeout(tcp.getConnectionTimeout());
		return data;
	}

}
