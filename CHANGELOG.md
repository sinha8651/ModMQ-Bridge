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

## [0.3.1] - 2025-10-10

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
