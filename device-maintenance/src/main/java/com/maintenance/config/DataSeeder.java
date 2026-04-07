package com.maintenance.config;

import com.maintenance.entity.*;
import com.maintenance.enums.*;
import com.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("=== Seeding database with sample data ===");

        // --- USERS ---
        User admin = userRepository.save(User.builder()
                .username("admin").password(passwordEncoder.encode("admin123"))
                .email("admin@maintenance.com").fullName("Nguyen Van Admin")
                .phone("0901234567").role(Role.ROLE_ADMIN).enabled(true).build());

        User manager = userRepository.save(User.builder()
                .username("manager").password(passwordEncoder.encode("manager123"))
                .email("manager@maintenance.com").fullName("Tran Thi Manager")
                .phone("0902345678").role(Role.ROLE_MANAGER).enabled(true).build());

        User tech1 = userRepository.save(User.builder()
                .username("tech1").password(passwordEncoder.encode("tech123"))
                .email("tech1@maintenance.com").fullName("Le Van Technician")
                .phone("0903456789").role(Role.ROLE_TECHNICIAN).enabled(true).build());

        User tech2 = userRepository.save(User.builder()
                .username("tech2").password(passwordEncoder.encode("tech123"))
                .email("tech2@maintenance.com").fullName("Pham Thi Technician")
                .phone("0904567890").role(Role.ROLE_TECHNICIAN).enabled(true).build());

        userRepository.save(User.builder()
                .username("user1").password(passwordEncoder.encode("user123"))
                .email("user1@maintenance.com").fullName("Hoang Van User")
                .phone("0905678901").role(Role.ROLE_USER).enabled(true).build());

        // --- DEVICES ---
        Device d1 = deviceRepository.save(Device.builder()
                .deviceCode("DEV-001").name("May Lanh Daikin P1")
                .deviceType("He thong dieu hoa").manufacturer("Daikin")
                .modelNumber("FTKS35GVMW").serialNumber("SN-2021-001")
                .purchaseDate(LocalDate.of(2021, 3, 15))
                .warrantyExpiryDate(LocalDate.of(2024, 3, 15))
                .purchasePrice(15000000.0).status(DeviceStatus.ACTIVE)
                .location("Tang 1").locationDetail("Phong hop A101")
                .description("May lanh 2 chieu cong suat 18000 BTU")
                .maintenanceCycleDays(90).build());

        Device d2 = deviceRepository.save(Device.builder()
                .deviceCode("DEV-002").name("May Phat Dien Cummins")
                .deviceType("Thiet bi dien").manufacturer("Cummins")
                .modelNumber("C150D5").serialNumber("SN-2020-002")
                .purchaseDate(LocalDate.of(2020, 6, 1))
                .warrantyExpiryDate(LocalDate.of(2023, 6, 1))
                .purchasePrice(120000000.0).status(DeviceStatus.ACTIVE)
                .location("Tang Ham").locationDetail("Phong may phat dien")
                .description("May phat dien 150KVA du phong")
                .maintenanceCycleDays(30).build());

        Device d3 = deviceRepository.save(Device.builder()
                .deviceCode("DEV-003").name("Thang May Mitsubishi")
                .deviceType("Thang may").manufacturer("Mitsubishi")
                .modelNumber("NEXIEZ-MRL").serialNumber("SN-2019-003")
                .purchaseDate(LocalDate.of(2019, 1, 10))
                .warrantyExpiryDate(LocalDate.of(2022, 1, 10))
                .purchasePrice(500000000.0).status(DeviceStatus.UNDER_MAINTENANCE)
                .location("Toa nha chinh").locationDetail("Thang may so 1")
                .description("Thang may 10 tang tai trong 1000kg")
                .maintenanceCycleDays(180).build());

        Device d4 = deviceRepository.save(Device.builder()
                .deviceCode("DEV-004").name("He Thong PCCC")
                .deviceType("An toan phong chay").manufacturer("Hochiki")
                .modelNumber("ESP-120C").serialNumber("SN-2022-004")
                .purchaseDate(LocalDate.of(2022, 8, 20))
                .warrantyExpiryDate(LocalDate.of(2025, 8, 20))
                .purchasePrice(200000000.0).status(DeviceStatus.ACTIVE)
                .location("Toan toa nha").locationDetail("He thong phun nuoc tu dong")
                .maintenanceCycleDays(365).build());

        // --- INCIDENTS ---
        incidentRepository.save(Incident.builder()
                .incidentCode("INC-20240101-0001")
                .title("May lanh P1 khong lanh").description("May lanh tang 1 chay nhung khong xuong nhiet")
                .severity(SeverityLevel.HIGH).status(IncidentStatus.IN_PROGRESS)
                .occurredAt(LocalDateTime.now().minusDays(3))
                .device(d1).reportedBy(admin).assignedTechnician(tech1).build());

        incidentRepository.save(Incident.builder()
                .incidentCode("INC-20240102-0002")
                .title("May phat dien rung manh").description("Tieng on bat thuong khi may hoat dong")
                .severity(SeverityLevel.CRITICAL).status(IncidentStatus.REPORTED)
                .occurredAt(LocalDateTime.now().minusDays(1))
                .device(d2).reportedBy(manager).build());

        incidentRepository.save(Incident.builder()
                .incidentCode("INC-20231215-0003")
                .title("Thang may dung dot ngot").description("Thang may dung giua tang, hanh khach bi ket")
                .severity(SeverityLevel.CRITICAL).status(IncidentStatus.RESOLVED)
                .occurredAt(LocalDateTime.now().minusDays(20))
                .resolvedAt(LocalDateTime.now().minusDays(18))
                .resolutionNote("Da kiem tra va sua chua bo dong co")
                .device(d3).reportedBy(admin).assignedTechnician(tech2).build());

        // --- MAINTENANCE SCHEDULES ---
        scheduleRepository.save(MaintenanceSchedule.builder()
                .scheduleCode("MS-20240201-0001")
                .title("Bao tri dinh ky May Lanh Daikin P1")
                .maintenanceType("Dinh ky").description("Kiem tra, ve sinh bo loc, kiem tra gas lanh")
                .scheduledDate(LocalDateTime.now().plusDays(3))
                .estimatedDurationHours(2).status(MaintenanceStatus.SCHEDULED).cost(500000.0)
                .device(d1).assignedTechnician(tech1).createdBy(manager).build());

        scheduleRepository.save(MaintenanceSchedule.builder()
                .scheduleCode("MS-20240202-0002")
                .title("Bao tri dinh ky May Phat Dien")
                .maintenanceType("Dinh ky").description("Kiem tra dau, thay loc nhot, chay thu tai")
                .scheduledDate(LocalDateTime.now().plusDays(7))
                .estimatedDurationHours(4).status(MaintenanceStatus.SCHEDULED).cost(2000000.0)
                .device(d2).assignedTechnician(tech2).createdBy(manager).build());

        scheduleRepository.save(MaintenanceSchedule.builder()
                .scheduleCode("MS-20231201-0003")
                .title("Bao tri Thang May Mitsubishi")
                .maintenanceType("Sua chua").description("Kiem tra toan bo he thong, tra dau cac khop noi")
                .scheduledDate(LocalDateTime.now().minusDays(5))
                .actualStartDate(LocalDateTime.now().minusDays(5))
                .estimatedDurationHours(8).status(MaintenanceStatus.IN_PROGRESS).cost(5000000.0)
                .isAutoGenerated(false)
                .device(d3).assignedTechnician(tech1).createdBy(admin).build());

        log.info("=== Database seeded successfully ===");
        log.info("Test accounts:");
        log.info("  admin/admin123 - ROLE_ADMIN");
        log.info("  manager/manager123 - ROLE_MANAGER");
        log.info("  tech1/tech123 - ROLE_TECHNICIAN");
        log.info("  user1/user123 - ROLE_USER");
    }
}
