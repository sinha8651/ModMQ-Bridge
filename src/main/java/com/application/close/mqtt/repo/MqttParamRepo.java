package com.application.close.mqtt.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.close.mqtt.entity.MqttParam;

public interface MqttParamRepo extends JpaRepository<MqttParam, Integer> {

}
