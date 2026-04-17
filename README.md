# HotelMDM

A Master Data Management (MDM) system for a hotel/hospitality group, built with Spring Boot 3 and Thymeleaf, inspired by TIBCO EBX concepts.

## Overview

HotelMDM centralises and governs master data across three core domains:

- **Property** — hotels, rooms, and amenities
- **Guest** — guest profiles, preferences, and loyalty tiers (Bronze → Platinum)
- **Vendor** — suppliers, contracts, and contacts

## Features

| Feature | Description |
|---|---|
| Governance Workflow | Records move through DRAFT → PENDING_APPROVAL → APPROVED / REJECTED |
| Data Quality Rules | Configurable validation rules (regex, length, format, null checks) per entity |
| Audit Trail | Every create / update / approve / reject action logged with user and timestamp |
| Stewardship Tasks | Assignable tasks with priority and due dates for data stewards |
| RBAC | Four roles — Admin, Manager, Data Steward, Viewer |

## Tech Stack

- **Backend** — Spring Boot 3.3, Spring Security 6, Spring Data JPA (Hibernate 6)
- **Database** — H2 in-memory
- **Frontend** — Thymeleaf 3 with Layout Dialect, Bootstrap 5

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+

### Run locally

```bash
git clone https://github.com/noelsebu/hotel_mdm.git
cd hotel_mdm
mvn spring-boot:run
```

Open **http://localhost:8080**

### Demo accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Administrator |
| `manager` | `manager123` | Manager |
| `steward` | `steward123` | Data Steward |
| `viewer` | `viewer123` | Viewer |

### H2 Console

Available at **http://localhost:8080/h2-console**  
JDBC URL: `jdbc:h2:mem:hotelmdm` — no password required.

## Deployment

The app reads `PORT` from the environment, making it ready for platforms like Railway.

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/new/template)
