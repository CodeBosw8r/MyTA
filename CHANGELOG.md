# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added 

### Fixed

## [1.1.1] - 2022-03-27

### Fixed

- Updated jackson-databind to fix CVE-2020-36518

## [1.1.0] - 2021-05-14

### Added

- Dockerfile and docker-compose.yml
- toString() in SmtpConfiguration

### Changed

- ContextListener now logs relay servers upon init
- now using newer jackson lib
- now using junit 5
- now using servlet 4.0 and log4j 2
- now using maven build
- fixed createMultipartAlternative() in MessageComposer

### Removed

- no longer using ant build

## [1.0] - 2021-05-10

- first release

[Unreleased]: https://github.com/CodeBosw8r/MyTA/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/CodeBosw8r/MyTA/compare/v1_0...v1.1.0
