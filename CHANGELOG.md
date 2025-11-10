# Change log

All notable changes to this project will be documented here.

## [Unreleased]

### Added

-  

### Changed

- 

### Removed

- 

### Fixed

-

### Example scenario:

- 1.0.0.0 → First release.

- 2.0.0.0 → Major overhaul, breaking changes.

- 2.1.0.0 → Added new feature without breaking old ones.

- 2.1.1.0 → Fixed minor bugs.

- 2.1.1.42 → 42nd build of this version.

## [0.5.1] - 2025-11-10

### Added

- Added Executor thread processing modbus data.
- now publishing modbus data in fixed message pattern.

### Fixed

- Multiple circular error fixed.
- Due to multiple naming conflicts, I renamed one of the variables and methods.


## [0.5.0] - 2025-10-30

### Added

- Added ModMqttLinks for establishing links between Modbus and MQTT.
- Introduced Bridge Executor for task execution via executor threads.
- Created corresponding service layers for both components.

### Fixed

- Deleting a TcpData entry now correctly removes associated links, executors, and objects.
- Applied the same fix for MQTT deletions.
- Updated disconnection logic — the client object now remains persistent, and only its 
  connection state (connect/disconnect) changes instead of removing the object from the map.
  
## [0.4.1] - 2025-10-26

### Added

- Added Modbus–MQTT bridge service and controller.
- Added MqttController for MQTT service operations.

## [0.4.0] - 2025-10-25

### Added

- Added multiple methods for subscribing and publishing, along with a disconnect method.

### Removed

- SSL and SSL-with-auth connections have been temporarily removed and will be reintroduced after the release of the stable version.

## [0.3.1] - 2025-10-10

### Added

- Added two connection methods: one for normal connection and another for authenticated connection.

### Fixed

- Added boolean variables for SSL and authentication to allow four types of connections with mqtt: 
  1) normal, 2) with authentication, 3) with SSL, and 4) with both SSL and authentication.

## [0.3.0] - 2025-10-09

### Added

- Added MQTT Parameter Controller, Service, and Entity with complete CRUD and topic management operations.

### Fixed

- Added NotNull annotations for every id , in TcpData and MqttParam.
- username and password can be null.


## [0.2.1] - 2025-10-02

### Added

- controller added for the modbus service

## [0.2.0] - 2025-09-21

### Added

- Implemented read and write operations for coils and registers in Modbus service
- Added input validation and consistent error handling

## [0.1.1] - 2025-09-20

### Added

- Implemented Modbus slave connection operations:
  - Connect to slave
  - Disconnect from slave
  - Reconnect to existing slave session

### Fixed

- Updated/fixed Modbus connection parameters (host, port, keep-alive) in the application.

## [0.1.0] - 2025-09-08

### Added

- Implemented TcpDataController with CRUD operation APIs for managing TCP data.
- Integrated Swagger/OpenAPI for API testing and documentation.
- Added changelog functionality to keep track of all changes and updates.
