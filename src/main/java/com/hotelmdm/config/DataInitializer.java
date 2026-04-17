package com.hotelmdm.config;

import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.guest.model.*;
import com.hotelmdm.domain.guest.repository.GuestRepository;
import com.hotelmdm.domain.property.model.*;
import com.hotelmdm.domain.property.repository.AmenityRepository;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.domain.property.repository.RoomRepository;
import com.hotelmdm.domain.vendor.model.*;
import com.hotelmdm.domain.vendor.repository.ContractRepository;
import com.hotelmdm.domain.vendor.repository.SupplierContactRepository;
import com.hotelmdm.domain.vendor.repository.SupplierRepository;
import com.hotelmdm.governance.model.*;
import com.hotelmdm.governance.repository.ApprovalRepository;
import com.hotelmdm.governance.repository.DataStewardTaskRepository;
import com.hotelmdm.quality.model.DataQualityRule;
import com.hotelmdm.quality.model.RuleSeverity;
import com.hotelmdm.quality.model.RuleType;
import com.hotelmdm.quality.repository.DataQualityRuleRepository;
import com.hotelmdm.security.model.AppUser;
import com.hotelmdm.security.model.UserRole;
import com.hotelmdm.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final AmenityRepository amenityRepository;
    private final GuestRepository guestRepository;
    private final SupplierRepository supplierRepository;
    private final ContractRepository contractRepository;
    private final SupplierContactRepository contactRepository;
    private final DataQualityRuleRepository qualityRuleRepository;
    private final ApprovalRepository approvalRepository;
    private final DataStewardTaskRepository taskRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding HotelMDM data...");

        createUsers();
        createQualityRules();
        createAmenities();
        createHotelsAndRooms();
        createGuests();
        createSuppliers();
        createStewardTasks();

        log.info("Data seeding complete.");
    }

    private void createUsers() {
        if (userRepository.count() > 0) return;
        userRepository.save(new AppUser("admin", passwordEncoder.encode("admin123"),
                "System Administrator", "admin@hotelmdm.com", UserRole.ADMIN));
        userRepository.save(new AppUser("manager", passwordEncoder.encode("manager123"),
                "Data Manager", "manager@hotelmdm.com", UserRole.DATA_MANAGER));
        userRepository.save(new AppUser("steward", passwordEncoder.encode("steward123"),
                "Data Steward", "steward@hotelmdm.com", UserRole.DATA_STEWARD));
        userRepository.save(new AppUser("viewer", passwordEncoder.encode("viewer123"),
                "Read Only User", "viewer@hotelmdm.com", UserRole.VIEWER));
    }

    private void createQualityRules() {
        if (qualityRuleRepository.count() > 0) return;

        // Hotel rules
        qualityRuleRepository.save(rule("Hotel Name Required", "HOTEL", "name",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Hotel name must not be blank"));
        qualityRuleRepository.save(rule("Hotel Name Min Length", "HOTEL", "name",
                RuleType.MIN_LENGTH, "2", RuleSeverity.ERROR, "Hotel name must be at least 2 characters"));
        qualityRuleRepository.save(rule("Hotel Code Required", "HOTEL", "code",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Hotel code must not be blank"));
        qualityRuleRepository.save(rule("Hotel Email Format", "HOTEL", "email",
                RuleType.EMAIL_FORMAT, null, RuleSeverity.WARNING, "Hotel email address is invalid"));
        qualityRuleRepository.save(rule("Hotel Phone Format", "HOTEL", "phone",
                RuleType.PHONE_FORMAT, null, RuleSeverity.WARNING, "Hotel phone number is invalid"));

        // Guest rules
        qualityRuleRepository.save(rule("Guest First Name Required", "GUEST", "firstName",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Guest first name is required"));
        qualityRuleRepository.save(rule("Guest Last Name Required", "GUEST", "lastName",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Guest last name is required"));
        qualityRuleRepository.save(rule("Guest Email Required", "GUEST", "email",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Guest email is required"));
        qualityRuleRepository.save(rule("Guest Email Format", "GUEST", "email",
                RuleType.EMAIL_FORMAT, null, RuleSeverity.ERROR, "Guest email address is invalid"));
        qualityRuleRepository.save(rule("Guest Phone Format", "GUEST", "phone",
                RuleType.PHONE_FORMAT, null, RuleSeverity.WARNING, "Guest phone number format is invalid"));

        // Supplier rules
        qualityRuleRepository.save(rule("Supplier Name Required", "SUPPLIER", "name",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Supplier name is required"));
        qualityRuleRepository.save(rule("Supplier Code Required", "SUPPLIER", "code",
                RuleType.NOT_NULL, null, RuleSeverity.ERROR, "Supplier code is required"));
        qualityRuleRepository.save(rule("Supplier Email Format", "SUPPLIER", "email",
                RuleType.EMAIL_FORMAT, null, RuleSeverity.WARNING, "Supplier email address is invalid"));
    }

    private DataQualityRule rule(String name, String entityType, String fieldName,
                                  RuleType type, String value, RuleSeverity severity, String message) {
        DataQualityRule r = new DataQualityRule();
        r.setName(name);
        r.setEntityType(entityType);
        r.setFieldName(fieldName);
        r.setRuleType(type);
        r.setRuleValue(value);
        r.setSeverity(severity);
        r.setMessage(message);
        r.setActive(true);
        return r;
    }

    private void createAmenities() {
        if (amenityRepository.count() > 0) return;

        amenityRepository.save(amenity("Swimming Pool", "Outdoor infinity pool", AmenityCategory.POOL));
        amenityRepository.save(amenity("Rooftop Bar", "Sky lounge with panoramic views", AmenityCategory.RESTAURANT));
        amenityRepository.save(amenity("Full-Service Spa", "Treatments, massages, and wellness", AmenityCategory.SPA));
        amenityRepository.save(amenity("Fitness Center", "24-hour gym with modern equipment", AmenityCategory.GYM));
        amenityRepository.save(amenity("Business Center", "Meeting rooms and co-working space", AmenityCategory.BUSINESS));
        amenityRepository.save(amenity("Valet Parking", "Complimentary valet service", AmenityCategory.PARKING));
        amenityRepository.save(amenity("Kids Club", "Supervised activities for children", AmenityCategory.KIDS));
        amenityRepository.save(amenity("Conference Hall", "Capacity up to 500 guests", AmenityCategory.CONFERENCE));
        amenityRepository.save(amenity("Fine Dining", "Award-winning restaurant", AmenityCategory.RESTAURANT));
        amenityRepository.save(amenity("Airport Shuttle", "Scheduled transfers to/from airport", AmenityCategory.TRANSPORT));
    }

    private Amenity amenity(String name, String desc, AmenityCategory cat) {
        Amenity a = new Amenity();
        a.setName(name);
        a.setDescription(desc);
        a.setCategory(cat);
        return a;
    }

    private void createHotelsAndRooms() {
        if (hotelRepository.count() > 0) return;

        Amenity pool = amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals("Swimming Pool")).findFirst().orElse(null);
        Amenity spa = amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals("Full-Service Spa")).findFirst().orElse(null);
        Amenity gym = amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals("Fitness Center")).findFirst().orElse(null);
        Amenity bar = amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals("Rooftop Bar")).findFirst().orElse(null);
        Amenity biz = amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals("Business Center")).findFirst().orElse(null);

        // Hotel 1 - APPROVED flagship
        Hotel grandPalace = new Hotel();
        grandPalace.setName("Grand Palace Hotel");
        grandPalace.setCode("GPH-001");
        grandPalace.setStarRating(5);
        grandPalace.setAddress("1 Royal Boulevard");
        grandPalace.setCity("Dubai");
        grandPalace.setCountry("UAE");
        grandPalace.setPhone("+971-4-555-1234");
        grandPalace.setEmail("info@grandpalace.ae");
        grandPalace.setWebsite("www.grandpalace.ae");
        grandPalace.setTotalRooms(250);
        grandPalace.setWorkflowStatus(WorkflowStatus.APPROVED);
        grandPalace.setDescription("Iconic luxury hotel in the heart of Dubai");
        Set<Amenity> gpAmenities = new HashSet<>();
        if (pool != null) gpAmenities.add(pool);
        if (spa != null) gpAmenities.add(spa);
        if (gym != null) gpAmenities.add(gym);
        if (bar != null) gpAmenities.add(bar);
        grandPalace.setAmenities(gpAmenities);
        grandPalace = hotelRepository.save(grandPalace);
        createRoomsForHotel(grandPalace);

        // Hotel 2 - DRAFT
        Hotel cityView = new Hotel();
        cityView.setName("CityView Business Hotel");
        cityView.setCode("CVB-002");
        cityView.setStarRating(4);
        cityView.setAddress("45 Commerce Street");
        cityView.setCity("Singapore");
        cityView.setCountry("Singapore");
        cityView.setPhone("+65-6-555-9876");
        cityView.setEmail("reservations@cityview.sg");
        cityView.setTotalRooms(180);
        cityView.setWorkflowStatus(WorkflowStatus.DRAFT);
        cityView.setDescription("Modern business hotel in Singapore's CBD");
        Set<Amenity> cvAmenities = new HashSet<>();
        if (biz != null) cvAmenities.add(biz);
        if (gym != null) cvAmenities.add(gym);
        cityView.setAmenities(cvAmenities);
        cityView = hotelRepository.save(cityView);
        createRoomsForHotel(cityView);

        // Hotel 3 - PENDING_APPROVAL
        Hotel sunsetResort = new Hotel();
        sunsetResort.setName("Sunset Beach Resort");
        sunsetResort.setCode("SBR-003");
        sunsetResort.setStarRating(5);
        sunsetResort.setAddress("Ocean Drive, Maldives Atoll");
        sunsetResort.setCity("Malé");
        sunsetResort.setCountry("Maldives");
        sunsetResort.setPhone("+960-555-7777");
        sunsetResort.setEmail("hello@sunsetresort.mv");
        sunsetResort.setTotalRooms(80);
        sunsetResort.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        sunsetResort.setDescription("Overwater bungalow resort in pristine Maldivian waters");
        if (pool != null) sunsetResort.getAmenities().add(pool);
        if (spa != null) sunsetResort.getAmenities().add(spa);
        sunsetResort = hotelRepository.save(sunsetResort);
        // Create pending approval request
        ApprovalRequest ar = new ApprovalRequest("HOTEL", sunsetResort.getId(),
                sunsetResort.getName(), "steward");
        approvalRepository.save(ar);
    }

    private void createRoomsForHotel(Hotel hotel) {
        String[] roomNums = {"101", "102", "103", "201", "202", "301"};
        RoomType[] types = {RoomType.SINGLE, RoomType.DOUBLE, RoomType.TWIN,
                            RoomType.SUITE, RoomType.DELUXE, RoomType.PRESIDENTIAL};
        double[] prices = {150, 220, 200, 450, 380, 1200};

        for (int i = 0; i < roomNums.length; i++) {
            Room room = new Room();
            room.setRoomNumber(roomNums[i]);
            room.setRoomType(types[i]);
            room.setFloor(Integer.parseInt(roomNums[i].substring(0, 1)));
            room.setPricePerNight(BigDecimal.valueOf(prices[i]));
            room.setMaxOccupancy(types[i] == RoomType.SINGLE ? 1 : 2);
            room.setAvailable(i % 3 != 0); // Some unavailable
            room.setHotel(hotel);
            roomRepository.save(room);
        }
    }

    private void createGuests() {
        if (guestRepository.count() > 0) return;

        Guest g1 = new Guest();
        g1.setFirstName("James"); g1.setLastName("Whitfield");
        g1.setEmail("james.whitfield@example.com");
        g1.setPhone("+1-555-234-5678");
        g1.setNationality("American");
        g1.setDateOfBirth(LocalDate.of(1980, 3, 15));
        g1.setLoyaltyPoints(52000); g1.recalculateTier();
        g1.setWorkflowStatus(WorkflowStatus.APPROVED);
        g1 = guestRepository.save(g1);
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.ROOM_TYPE, "Suite", g1));
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.BED_TYPE, "King", g1));
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.FOOD_DIETARY, "Vegetarian", g1));
        guestRepository.save(g1);

        Guest g2 = new Guest();
        g2.setFirstName("Aiko"); g2.setLastName("Tanaka");
        g2.setEmail("aiko.tanaka@example.jp");
        g2.setPhone("+81-3-555-0000");
        g2.setNationality("Japanese");
        g2.setDateOfBirth(LocalDate.of(1990, 7, 22));
        g2.setLoyaltyPoints(8500); g2.recalculateTier();
        g2.setWorkflowStatus(WorkflowStatus.APPROVED);
        g2 = guestRepository.save(g2);
        g2.getPreferences().add(new GuestPreference(PreferenceCategory.FLOOR, "High Floor", g2));
        g2.getPreferences().add(new GuestPreference(PreferenceCategory.NEWSPAPER, "Financial Times", g2));
        guestRepository.save(g2);

        Guest g3 = new Guest();
        g3.setFirstName("Omar"); g3.setLastName("Al-Hassan");
        g3.setEmail("omar.hassan@example.ae");
        g3.setPhone("+971-50-555-3333");
        g3.setNationality("Emirati");
        g3.setLoyaltyPoints(2100); g3.recalculateTier();
        g3.setWorkflowStatus(WorkflowStatus.DRAFT);
        guestRepository.save(g3);

        Guest g4 = new Guest();
        g4.setFirstName("Sophie"); g4.setLastName("Moreau");
        g4.setEmail("sophie.moreau@example.fr");
        g4.setNationality("French");
        g4.setLoyaltyPoints(17500); g4.recalculateTier();
        g4.setWorkflowStatus(WorkflowStatus.APPROVED);
        guestRepository.save(g4);

        Guest g5 = new Guest();
        g5.setFirstName("Carlos"); g5.setLastName("Rivera");
        g5.setEmail("carlos.rivera@example.mx");
        g5.setPhone("+52-55-555-8888");
        g5.setNationality("Mexican");
        g5.setLoyaltyPoints(500); g5.recalculateTier();
        g5.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        g5 = guestRepository.save(g5);
        ApprovalRequest gAr = new ApprovalRequest("GUEST", g5.getId(), g5.getFullName(), "steward");
        approvalRepository.save(gAr);
    }

    private void createSuppliers() {
        if (supplierRepository.count() > 0) return;

        Supplier s1 = new Supplier();
        s1.setName("Fresh Farms Co."); s1.setCode("FF-001");
        s1.setCategory(SupplierCategory.FOOD_BEVERAGE);
        s1.setStatus(SupplierStatus.ACTIVE);
        s1.setEmail("orders@freshfarms.com");
        s1.setPhone("+1-800-555-1234");
        s1.setCity("New York"); s1.setCountry("USA");
        s1.setRating(5); s1.setWorkflowStatus(WorkflowStatus.APPROVED);
        s1 = supplierRepository.save(s1);
        addContact(s1, "Michael Green", "Account Manager", "m.green@freshfarms.com", true);
        addContract(s1, "FF-CNT-2024-001", LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31),
                new BigDecimal("250000"), ContractStatus.ACTIVE);

        Supplier s2 = new Supplier();
        s2.setName("TechSolutions Ltd."); s2.setCode("TS-002");
        s2.setCategory(SupplierCategory.TECHNOLOGY);
        s2.setStatus(SupplierStatus.ACTIVE);
        s2.setEmail("support@techsolutions.com");
        s2.setPhone("+44-20-555-9900");
        s2.setCity("London"); s2.setCountry("UK");
        s2.setRating(4); s2.setWorkflowStatus(WorkflowStatus.DRAFT);
        s2 = supplierRepository.save(s2);
        addContact(s2, "Emily Watson", "Technical Lead", "e.watson@techsolutions.com", true);

        Supplier s3 = new Supplier();
        s3.setName("LuxLinens International"); s3.setCode("LL-003");
        s3.setCategory(SupplierCategory.LINEN_LAUNDRY);
        s3.setStatus(SupplierStatus.ACTIVE);
        s3.setEmail("info@luxlinens.com");
        s3.setCity("Paris"); s3.setCountry("France");
        s3.setRating(5); s3.setWorkflowStatus(WorkflowStatus.APPROVED);
        s3 = supplierRepository.save(s3);
        addContact(s3, "Claire Dubois", "Sales Director", "c.dubois@luxlinens.com", true);
        addContract(s3, "LL-CNT-2025-001", LocalDate.of(2025, 1, 1), LocalDate.of(2027, 12, 31),
                new BigDecimal("180000"), ContractStatus.ACTIVE);
    }

    private void addContact(Supplier s, String name, String title, String email, boolean primary) {
        SupplierContact c = new SupplierContact();
        c.setName(name); c.setTitle(title);
        c.setEmail(email); c.setPrimaryContact(primary);
        c.setSupplier(s);
        contactRepository.save(c);
    }

    private void addContract(Supplier s, String num, LocalDate start, LocalDate end,
                              BigDecimal value, ContractStatus status) {
        Contract c = new Contract();
        c.setContractNumber(num);
        c.setStartDate(start); c.setEndDate(end);
        c.setTotalValue(value);
        c.setStatus(status);
        c.setSupplier(s);
        contractRepository.save(c);
    }

    private void createStewardTasks() {
        if (taskRepository.count() > 0) return;

        DataStewardTask t1 = new DataStewardTask();
        t1.setTitle("Verify Sunset Beach Resort contact info");
        t1.setDescription("Cross-check hotel phone and email against official website");
        t1.setEntityType("HOTEL");
        t1.setAssignedTo("steward");
        t1.setCreatedBy("manager");
        t1.setDueDate(LocalDate.now().plusDays(3));
        t1.setPriority(TaskPriority.HIGH);
        t1.setStatus(TaskStatus.OPEN);
        taskRepository.save(t1);

        DataStewardTask t2 = new DataStewardTask();
        t2.setTitle("Enrich TechSolutions supplier profile");
        t2.setDescription("Add missing address, phone, and website to TechSolutions Ltd.");
        t2.setEntityType("SUPPLIER");
        t2.setAssignedTo("steward");
        t2.setCreatedBy("manager");
        t2.setDueDate(LocalDate.now().plusDays(7));
        t2.setPriority(TaskPriority.MEDIUM);
        t2.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(t2);

        DataStewardTask t3 = new DataStewardTask();
        t3.setTitle("Deduplicate guest profiles from legacy import");
        t3.setDescription("Review 12 flagged duplicate guest records from the legacy PMS import");
        t3.setAssignedTo("steward");
        t3.setCreatedBy("admin");
        t3.setDueDate(LocalDate.now().plusDays(14));
        t3.setPriority(TaskPriority.CRITICAL);
        t3.setStatus(TaskStatus.OPEN);
        taskRepository.save(t3);
    }
}
