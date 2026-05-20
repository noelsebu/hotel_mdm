package com.hotelmdm.config;

import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.chain.model.*;
import com.hotelmdm.domain.chain.repository.BrandStandardRepository;
import com.hotelmdm.domain.chain.repository.HotelBrandRepository;
import com.hotelmdm.domain.chain.repository.HotelChainRepository;
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
    private final HotelChainRepository chainRepository;
    private final HotelBrandRepository brandRepository;
    private final BrandStandardRepository standardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding Strawberry HotelMDM data...");

        createUsers();
        createQualityRules();
        createAmenities();
        createStrawberryHierarchy();
        createGuests();
        createSuppliers();
        createStewardTasks();

        log.info("Data seeding complete.");
    }

    // ── Users ──────────────────────────────────────────────────────────────────

    private void createUsers() {
        if (userRepository.count() > 0) return;
        userRepository.save(new AppUser("admin", passwordEncoder.encode("admin123"),
                "System Administrator", "admin@strawberry.no", UserRole.ADMIN));
        userRepository.save(new AppUser("manager", passwordEncoder.encode("manager123"),
                "Data Manager", "manager@strawberry.no", UserRole.DATA_MANAGER));
        userRepository.save(new AppUser("steward", passwordEncoder.encode("steward123"),
                "Data Steward", "steward@strawberry.no", UserRole.DATA_STEWARD));
        userRepository.save(new AppUser("viewer", passwordEncoder.encode("viewer123"),
                "Read Only User", "viewer@strawberry.no", UserRole.VIEWER));
    }

    // ── Quality Rules ──────────────────────────────────────────────────────────

    private void createQualityRules() {
        if (qualityRuleRepository.count() > 0) return;

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

    // ── Amenities ──────────────────────────────────────────────────────────────

    private void createAmenities() {
        if (amenityRepository.count() > 0) return;

        amenityRepository.save(amenity("Rooftop Bar & Lounge", "Sky-high cocktails with Nordic views", AmenityCategory.RESTAURANT));
        amenityRepository.save(amenity("Full-Service Spa", "Nordic wellness treatments and sauna", AmenityCategory.SPA));
        amenityRepository.save(amenity("Fitness Center", "24-hour gym with modern equipment", AmenityCategory.GYM));
        amenityRepository.save(amenity("Business Center", "Meeting rooms and co-working space", AmenityCategory.BUSINESS));
        amenityRepository.save(amenity("Valet Parking", "Complimentary valet service", AmenityCategory.PARKING));
        amenityRepository.save(amenity("Conference Facilities", "Multiple conference halls up to 1,000 delegates", AmenityCategory.CONFERENCE));
        amenityRepository.save(amenity("All Day Dining", "Scandinavian cuisine from breakfast to late night", AmenityCategory.RESTAURANT));
        amenityRepository.save(amenity("EV Charging Stations", "Electric vehicle charging in the car park", AmenityCategory.PARKING));
        amenityRepository.save(amenity("Complimentary Breakfast", "Daily Scandinavian breakfast buffet included", AmenityCategory.RESTAURANT));
        amenityRepository.save(amenity("Airport Shuttle", "Scheduled transfers to/from airport", AmenityCategory.TRANSPORT));
    }

    private Amenity amenity(String name, String desc, AmenityCategory cat) {
        Amenity a = new Amenity();
        a.setName(name);
        a.setDescription(desc);
        a.setCategory(cat);
        return a;
    }

    // ── Strawberry Chain Hierarchy ─────────────────────────────────────────────

    private void createStrawberryHierarchy() {
        if (chainRepository.count() > 0) return;

        // ── The Strawberry Chain ───────────────────────────────────────────────
        HotelChain strawberry = new HotelChain();
        strawberry.setName("Strawberry");
        strawberry.setCode("STR");
        strawberry.setFormerName("Nordic Choice Hotels");
        strawberry.setFoundedYear(1996);
        strawberry.setHeadquarters("Oslo");
        strawberry.setCountry("Norway");
        strawberry.setWebsite("www.strawberry.no");
        strawberry.setCeoName("Petter A. Stordalen");
        strawberry.setDescription(
            "Strawberry (formerly Nordic Choice Hotels) is one of the largest hotel companies in the Nordic and Baltic regions, " +
            "operating over 200 properties across Scandinavia, Finland, and the Baltics. " +
            "Founded by entrepreneur Petter A. Stordalen, the group is renowned for its commitment to sustainability, " +
            "design-led hospitality, and the Strawberry loyalty programme.");
        strawberry.setWorkflowStatus(WorkflowStatus.APPROVED);
        strawberry = chainRepository.save(strawberry);

        // ── 1. Clarion Hotels ─────────────────────────────────────────────────
        HotelBrand clarion = brand(strawberry, "Clarion Hotels", "CLR",
                BrandTier.UPPER_UPSCALE, BrandSegment.BUSINESS,
                "Clarion Hotels is Strawberry's flagship upper-upscale brand, targeting business and conference travellers. " +
                "Properties are typically large-scale city and airport hotels featuring extensive conference facilities, " +
                "full-service restaurants, and signature design.",
                4, 5, "#1B3A6B", WorkflowStatus.APPROVED);

        addStandard(clarion, "All Day Dining Restaurant", "Full-service restaurant open from 06:00 to midnight", StandardCategory.FOOD_BEVERAGE, true);
        addStandard(clarion, "Conference Facilities min 10 meeting rooms", "Dedicated conference wing with at least 10 bookable meeting rooms", StandardCategory.DESIGN, true);
        addStandard(clarion, "Business Center 24/7", "Staffed business centre available around the clock", StandardCategory.SERVICE, true);
        addStandard(clarion, "High-speed fibre internet throughout", "Minimum 100 Mbps Wi-Fi available in all rooms and public areas", StandardCategory.TECHNOLOGY, true);
        addStandard(clarion, "EV Charging Stations", "At least 4 EV charging points in hotel car park", StandardCategory.SUSTAINABILITY, true);
        addStandard(clarion, "Strawberry loyalty enrolment at check-in", "All agents trained to enrol guests in Strawberry loyalty at arrival", StandardCategory.SERVICE, true);

        // ── 2. Quality Hotels ─────────────────────────────────────────────────
        HotelBrand quality = brand(strawberry, "Quality Hotels", "QLT",
                BrandTier.UPSCALE, BrandSegment.MIXED,
                "Quality Hotels is positioned in the upscale segment, offering a blend of business practicality and lifestyle appeal. " +
                "Known for generous room sizes, on-site dining, and a welcoming Scandinavian atmosphere.",
                4, 4, "#C8102E", WorkflowStatus.APPROVED);

        addStandard(quality, "Full service restaurant on-site", "Restaurant open for breakfast, lunch, and dinner daily", StandardCategory.FOOD_BEVERAGE, true);
        addStandard(quality, "Free high-speed Wi-Fi in all areas", "Complimentary Wi-Fi in all guest rooms and public spaces", StandardCategory.TECHNOLOGY, true);
        addStandard(quality, "Dedicated fitness area", "Minimum 60 sqm gym with cardio and strength equipment", StandardCategory.DESIGN, true);
        addStandard(quality, "Sustainability certification required", "Properties must hold a recognised Nordic eco-label or equivalent", StandardCategory.SUSTAINABILITY, true);
        addStandard(quality, "24/7 front desk staffing", "Manned reception around the clock", StandardCategory.SERVICE, true);

        // ── 3. Comfort Hotels ─────────────────────────────────────────────────
        HotelBrand comfort = brand(strawberry, "Comfort Hotels", "CMF",
                BrandTier.MIDSCALE, BrandSegment.LEISURE,
                "Comfort Hotels offers an affordable, design-forward stay underpinned by Scandinavian simplicity. " +
                "The brand is known for its inclusive complimentary breakfast and CO₂-neutral certification across all properties.",
                3, 4, "#F47920", WorkflowStatus.APPROVED);

        addStandard(comfort, "Complimentary breakfast buffet daily", "Full Scandinavian breakfast included in all room rates", StandardCategory.FOOD_BEVERAGE, true);
        addStandard(comfort, "Free Wi-Fi throughout property", "Complimentary wireless internet in all areas", StandardCategory.TECHNOLOGY, true);
        addStandard(comfort, "Scandi-design interior", "Interiors must follow Strawberry's Nordic design brief with warm tones and natural materials", StandardCategory.DESIGN, true);
        addStandard(comfort, "CO₂-neutral certification", "All Comfort properties must achieve and maintain CO₂-neutral status", StandardCategory.SUSTAINABILITY, true);
        addStandard(comfort, "Pillow menu available on request", "At least 3 pillow types available for guest selection", StandardCategory.SERVICE, false);

        // ── 4. Clarion Collection ─────────────────────────────────────────────
        HotelBrand clarionCollection = brand(strawberry, "Clarion Collection", "CLC",
                BrandTier.UPPER_UPSCALE, BrandSegment.BOUTIQUE,
                "Clarion Collection is a boutique brand within the Strawberry family, featuring intimate apartment-style " +
                "properties with a distinctive local character. The hallmark is the complimentary evening serving — a light " +
                "supper included in the room rate.",
                4, 4, "#4A4A8A", WorkflowStatus.APPROVED);

        addStandard(clarionCollection, "Evening serving included", "Complimentary light evening meal served to all guests", StandardCategory.FOOD_BEVERAGE, true);
        addStandard(clarionCollection, "Apartment-style units minimum 30%", "At least 30% of inventory must be apartment or suite-type rooms", StandardCategory.DESIGN, true);
        addStandard(clarionCollection, "Maximum 150 rooms per property", "Boutique scale — properties above 150 keys require brand exemption", StandardCategory.DESIGN, false);
        addStandard(clarionCollection, "Premium linens 400+ thread count", "All beds must use a minimum 400-thread-count cotton linen set", StandardCategory.DESIGN, true);
        addStandard(clarionCollection, "Local art and design references", "Lobby and corridors must feature curated local artwork or design elements", StandardCategory.DESIGN, false);

        // ── 5. Nordic Collection ─────────────────────────────────────────────
        HotelBrand nordic = brand(strawberry, "Nordic Collection", "NDC",
                BrandTier.LUXURY, BrandSegment.BOUTIQUE,
                "Nordic Collection represents Strawberry's luxury tier — a curated portfolio of iconic, design-driven " +
                "flagship hotels. Each property is an architectural statement with a unique concept, Michelin-calibre " +
                "dining, and bespoke guest experiences.",
                5, 5, "#1A7A4A", WorkflowStatus.APPROVED);

        addStandard(nordic, "Fine dining restaurant — Michelin level", "On-site restaurant must achieve or maintain Michelin star or equivalent recognition", StandardCategory.FOOD_BEVERAGE, true);
        addStandard(nordic, "Butler service available", "On-demand butler available to all guests during stay", StandardCategory.SERVICE, true);
        addStandard(nordic, "Minimum 40 sqm room size", "No standard room may be below 40 square metres", StandardCategory.DESIGN, true);
        addStandard(nordic, "Personalised arrival experience", "Each arriving guest receives a tailored welcome — name recognition, preferences met pre-arrival", StandardCategory.SERVICE, true);
        addStandard(nordic, "Curated art collection on-site", "Property must display a permanent or rotating curated art collection", StandardCategory.DESIGN, false);
        addStandard(nordic, "Spa and wellness facilities", "Full-service spa with Nordic sauna tradition required", StandardCategory.DESIGN, true);

        // ── Hotels ────────────────────────────────────────────────────────────
        Amenity confFac = findAmenity("Conference Facilities");
        Amenity spa = findAmenity("Full-Service Spa");
        Amenity gym = findAmenity("Fitness Center");
        Amenity bar = findAmenity("Rooftop Bar & Lounge");
        Amenity biz = findAmenity("Business Center");
        Amenity breakfast = findAmenity("Complimentary Breakfast");
        Amenity shuttle = findAmenity("Airport Shuttle");
        Amenity ev = findAmenity("EV Charging Stations");
        Amenity allDay = findAmenity("All Day Dining");

        // -- Clarion Hotels (2 properties) --
        Hotel clarionOsloAirport = hotel(
                "Clarion Hotel & Congress Oslo Airport", "CLR-OSL01",
                "Gardermoen Airport Area", "Gardermoen", "Norway",
                "+47 63 93 80 00", "info.osl@clarion.no", "www.strawberry.no/clarion-oslo-airport",
                500, 5,
                "Scandinavia's largest hotel, located steps from Oslo Gardermoen Airport. A premier conference " +
                "and congress destination with 500 rooms and event space for up to 4,000 delegates.",
                clarion, WorkflowStatus.APPROVED,
                amenitySet(confFac, gym, bar, biz, allDay, ev, shuttle)
        );
        createRoomsForHotel(clarionOsloAirport, new String[]{"101","102","201","202","501","601"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.SUITE,RoomType.DELUXE,RoomType.PRESIDENTIAL},
                new double[]{149,199,189,450,380,1800});

        Hotel clarionPost = hotel(
                "Clarion Hotel Post", "CLR-GOT02",
                "Drottningtorget 10", "Gothenburg", "Sweden",
                "+46 31 619 00 00", "info.got@clarion.se", "www.strawberry.no/clarion-post",
                500, 5,
                "Located inside Gothenburg's historic central post office building, Clarion Hotel Post is an award-winning " +
                "full-service hotel with rooftop pool and panoramic city views.",
                clarion, WorkflowStatus.APPROVED,
                amenitySet(confFac, spa, gym, bar, allDay, biz)
        );
        createRoomsForHotel(clarionPost, new String[]{"101","102","201","202","301","501"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.DOUBLE,RoomType.SUITE,RoomType.DELUXE,RoomType.PRESIDENTIAL},
                new double[]{139,189,189,420,350,1600});

        // -- Quality Hotels (2 properties) --
        Hotel qualityExpo = hotel(
                "Quality Hotel Expo", "QLT-STO01",
                "Mässvägen 2", "Stockholm", "Sweden",
                "+46 8 50 66 90 00", "info.expo@quality.se", "www.strawberry.no/quality-expo",
                385, 4,
                "Directly connected to Stockholm's Stockholm International Fairs (Stockholmsmässan), Quality Hotel Expo " +
                "is the go-to destination for trade fair visitors and business travellers.",
                quality, WorkflowStatus.APPROVED,
                amenitySet(gym, biz, allDay, confFac)
        );
        createRoomsForHotel(qualityExpo, new String[]{"101","102","201","202","301","401"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.DOUBLE,RoomType.SUITE,RoomType.DELUXE},
                new double[]{129,169,159,179,350,290});

        Hotel qualityEdvard = hotel(
                "Quality Hotel Edvard Grieg", "QLT-BGO02",
                "Sandsliåsen 2", "Bergen", "Norway",
                "+47 55 98 00 00", "info.bgo@quality.no", "www.strawberry.no/quality-edvard-grieg",
                266, 4,
                "Named after Bergen's famous composer, Quality Hotel Edvard Grieg stands beside Bergen Airport, " +
                "offering easy access to the fjords and city centre.",
                quality, WorkflowStatus.PENDING_APPROVAL,
                amenitySet(gym, allDay, shuttle)
        );
        createRoomsForHotel(qualityEdvard, new String[]{"101","102","201","202","301"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.SUITE,RoomType.DELUXE},
                new double[]{119,159,149,320,270});
        approvalRepository.save(new ApprovalRequest("HOTEL", qualityEdvard.getId(), qualityEdvard.getName(), "steward"));

        // -- Comfort Hotels (2 properties) --
        Hotel comfortRunway = hotel(
                "Comfort Hotel Runway", "CMF-OSL01",
                "Edvard Munchs veg 2", "Oslo", "Norway",
                "+47 64 81 60 00", "info.runway@comfort.no", "www.strawberry.no/comfort-runway",
                200, 3,
                "Comfort Hotel Runway sits beside Oslo Airport's runway with direct Flytoget express train access, " +
                "offering an unbeatable transit stay with complimentary breakfast and CO₂-neutral credentials.",
                comfort, WorkflowStatus.APPROVED,
                amenitySet(breakfast, gym, shuttle)
        );
        createRoomsForHotel(comfortRunway, new String[]{"101","102","201","202","301"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.DOUBLE,RoomType.SUITE},
                new double[]{99,139,129,149,290});

        Hotel comfortVesterbro = hotel(
                "Comfort Hotel Vesterbro", "CMF-CPH02",
                "Vesterbrogade 23-29", "Copenhagen", "Denmark",
                "+45 33 78 80 00", "info.vesterbro@comfort.dk", "www.strawberry.no/comfort-vesterbro",
                560, 3,
                "Comfort Hotel Vesterbro is one of Copenhagen's largest hotels, located in the trendy Vesterbro " +
                "neighbourhood. Ideal for leisure guests exploring the Danish capital.",
                comfort, WorkflowStatus.DRAFT,
                amenitySet(breakfast, gym)
        );
        createRoomsForHotel(comfortVesterbro, new String[]{"101","102","201","202"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.SUITE},
                new double[]{89,129,119,280});

        // -- Clarion Collection (2 properties) --
        Hotel ccBastion = hotel(
                "Clarion Collection Hotel Bastion", "CLC-OSL01",
                "Skippergate 7", "Oslo", "Norway",
                "+47 22 47 77 00", "info.bastion@clarionc.no", "www.strawberry.no/clarion-collection-bastion",
                99, 4,
                "Set in a beautifully restored 18th-century fortification building in the heart of Oslo's old town, " +
                "Clarion Collection Hotel Bastion offers a historic boutique experience with complimentary evening serving.",
                clarionCollection, WorkflowStatus.APPROVED,
                amenitySet(biz)
        );
        createRoomsForHotel(ccBastion, new String[]{"101","102","201","202","301"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.TWIN,RoomType.SUITE,RoomType.DELUXE},
                new double[]{159,209,199,420,350});

        Hotel ccDrott = hotel(
                "Clarion Collection Hotel Drott", "CLC-KRL02",
                "Kungsgatan 23", "Karlstad", "Sweden",
                "+46 54 10 02 00", "info.drott@clarionc.se", "www.strawberry.no/clarion-collection-drott",
                93, 4,
                "A charming boutique hotel in the heart of Karlstad, Sweden, featuring apartment-style suites, " +
                "complimentary evening serving, and a warm, home-away-from-home atmosphere.",
                clarionCollection, WorkflowStatus.DRAFT,
                amenitySet()
        );
        createRoomsForHotel(ccDrott, new String[]{"101","102","201","301"},
                new RoomType[]{RoomType.SINGLE,RoomType.DOUBLE,RoomType.SUITE,RoomType.DELUXE},
                new double[]{149,189,380,310});

        // -- Nordic Collection (2 properties) --
        Hotel theThief = hotel(
                "The Thief", "NDC-OSL01",
                "Landgangen 1, Tjuvholmen", "Oslo", "Norway",
                "+47 24 00 40 00", "post@thethief.com", "www.thethief.com",
                119, 5,
                "The Thief is Oslo's most design-forward luxury hotel, perched on Tjuvholmen — the city's art district — " +
                "with a private art collection, rooftop bar, and direct waterfront access. " +
                "A Condé Nast Traveller Top 100 hotel.",
                nordic, WorkflowStatus.APPROVED,
                amenitySet(spa, gym, bar)
        );
        createRoomsForHotel(theThief, new String[]{"101","201","301","401","P01","P02"},
                new RoomType[]{RoomType.DOUBLE,RoomType.DELUXE,RoomType.SUITE,RoomType.SUITE,RoomType.PRESIDENTIAL,RoomType.PRESIDENTIAL},
                new double[]{399,599,1200,1400,3500,4200});

        Hotel villaCopenhagen = hotel(
                "Villa Copenhagen", "NDC-CPH02",
                "Tietgensgade 35-37", "Copenhagen", "Denmark",
                "+45 78 74 14 00", "info@villacph.com", "www.villacopenhagen.com",
                390, 5,
                "Villa Copenhagen is a landmark luxury hotel housed in Copenhagen's restored 1930 Central Post & Telegraph " +
                "building. Featuring a rooftop pool with city skyline views, a world-class spa, and the acclaimed " +
                "Palaeo Restaurant.",
                nordic, WorkflowStatus.PENDING_APPROVAL,
                amenitySet(spa, gym, bar, allDay)
        );
        createRoomsForHotel(villaCopenhagen, new String[]{"101","201","301","401","P01"},
                new RoomType[]{RoomType.DOUBLE,RoomType.DELUXE,RoomType.SUITE,RoomType.SUITE,RoomType.PRESIDENTIAL},
                new double[]{349,549,1100,1350,4000});
        approvalRepository.save(new ApprovalRequest("HOTEL", villaCopenhagen.getId(), villaCopenhagen.getName(), "steward"));
    }

    private HotelBrand brand(HotelChain chain, String name, String code,
                              BrandTier tier, BrandSegment segment,
                              String description, int minStars, int maxStars,
                              String color, WorkflowStatus status) {
        HotelBrand b = new HotelBrand();
        b.setChain(chain);
        b.setName(name);
        b.setCode(code);
        b.setTier(tier);
        b.setSegment(segment);
        b.setDescription(description);
        b.setMinStarRating(minStars);
        b.setMaxStarRating(maxStars);
        b.setColorCode(color);
        b.setWorkflowStatus(status);
        return brandRepository.save(b);
    }

    private void addStandard(HotelBrand brand, String title, String description,
                              StandardCategory category, boolean mandatory) {
        BrandStandard s = new BrandStandard();
        s.setBrand(brand);
        s.setTitle(title);
        s.setDescription(description);
        s.setCategory(category);
        s.setMandatory(mandatory);
        standardRepository.save(s);
    }

    private Hotel hotel(String name, String code, String address, String city, String country,
                         String phone, String email, String website, int rooms, int stars,
                         String description, HotelBrand brand, WorkflowStatus status,
                         Set<Amenity> amenities) {
        Hotel h = new Hotel();
        h.setName(name);
        h.setCode(code);
        h.setAddress(address);
        h.setCity(city);
        h.setCountry(country);
        h.setPhone(phone);
        h.setEmail(email);
        h.setWebsite(website);
        h.setTotalRooms(rooms);
        h.setStarRating(stars);
        h.setDescription(description);
        h.setBrand(brand);
        h.setWorkflowStatus(status);
        h.setAmenities(amenities);
        return hotelRepository.save(h);
    }

    private Set<Amenity> amenitySet(Amenity... amenities) {
        Set<Amenity> set = new HashSet<>();
        for (Amenity a : amenities) {
            if (a != null) set.add(a);
        }
        return set;
    }

    private Amenity findAmenity(String name) {
        return amenityRepository.findAllByOrderByNameAsc().stream()
                .filter(a -> a.getName().equals(name))
                .findFirst().orElse(null);
    }

    private void createRoomsForHotel(Hotel hotel, String[] roomNums, RoomType[] types, double[] prices) {
        for (int i = 0; i < roomNums.length; i++) {
            Room room = new Room();
            room.setRoomNumber(roomNums[i]);
            room.setRoomType(types[i]);
            room.setFloor(Character.getNumericValue(roomNums[i].charAt(0)));
            room.setPricePerNight(BigDecimal.valueOf(prices[i]));
            room.setMaxOccupancy(types[i] == RoomType.SINGLE ? 1 : 2);
            room.setAvailable(i % 3 != 0);
            room.setHotel(hotel);
            roomRepository.save(room);
        }
    }

    // ── Guests ─────────────────────────────────────────────────────────────────

    private void createGuests() {
        if (guestRepository.count() > 0) return;

        Guest g1 = new Guest();
        g1.setFirstName("Erik"); g1.setLastName("Bergström");
        g1.setEmail("erik.bergstrom@example.se");
        g1.setPhone("+46-70-555-1234");
        g1.setNationality("Swedish");
        g1.setDateOfBirth(LocalDate.of(1978, 4, 12));
        g1.setLoyaltyPoints(52000); g1.recalculateTier();
        g1.setWorkflowStatus(WorkflowStatus.APPROVED);
        g1 = guestRepository.save(g1);
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.ROOM_TYPE, "Suite", g1));
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.BED_TYPE, "King", g1));
        g1.getPreferences().add(new GuestPreference(PreferenceCategory.FOOD_DIETARY, "Vegetarian", g1));
        guestRepository.save(g1);

        Guest g2 = new Guest();
        g2.setFirstName("Astrid"); g2.setLastName("Haugen");
        g2.setEmail("astrid.haugen@example.no");
        g2.setPhone("+47-98-555-0000");
        g2.setNationality("Norwegian");
        g2.setDateOfBirth(LocalDate.of(1991, 9, 3));
        g2.setLoyaltyPoints(8500); g2.recalculateTier();
        g2.setWorkflowStatus(WorkflowStatus.APPROVED);
        g2 = guestRepository.save(g2);
        g2.getPreferences().add(new GuestPreference(PreferenceCategory.FLOOR, "High Floor", g2));
        g2.getPreferences().add(new GuestPreference(PreferenceCategory.NEWSPAPER, "Aftenposten", g2));
        guestRepository.save(g2);

        Guest g3 = new Guest();
        g3.setFirstName("Lars"); g3.setLastName("Mikkelsen");
        g3.setEmail("lars.mikkelsen@example.dk");
        g3.setPhone("+45-20-555-3333");
        g3.setNationality("Danish");
        g3.setLoyaltyPoints(2100); g3.recalculateTier();
        g3.setWorkflowStatus(WorkflowStatus.DRAFT);
        guestRepository.save(g3);

        Guest g4 = new Guest();
        g4.setFirstName("Siiri"); g4.setLastName("Virtanen");
        g4.setEmail("siiri.virtanen@example.fi");
        g4.setNationality("Finnish");
        g4.setLoyaltyPoints(17500); g4.recalculateTier();
        g4.setWorkflowStatus(WorkflowStatus.APPROVED);
        guestRepository.save(g4);

        Guest g5 = new Guest();
        g5.setFirstName("Magnus"); g5.setLastName("Karlsson");
        g5.setEmail("magnus.karlsson@example.se");
        g5.setPhone("+46-73-555-8888");
        g5.setNationality("Swedish");
        g5.setLoyaltyPoints(500); g5.recalculateTier();
        g5.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        g5 = guestRepository.save(g5);
        approvalRepository.save(new ApprovalRequest("GUEST", g5.getId(), g5.getFullName(), "steward"));
    }

    // ── Suppliers ──────────────────────────────────────────────────────────────

    private void createSuppliers() {
        if (supplierRepository.count() > 0) return;

        Supplier s1 = new Supplier();
        s1.setName("Norrmejerier AB"); s1.setCode("NRM-001");
        s1.setCategory(SupplierCategory.FOOD_BEVERAGE);
        s1.setStatus(SupplierStatus.ACTIVE);
        s1.setEmail("orders@norrmejerier.se");
        s1.setPhone("+46-90-555-1234");
        s1.setCity("Umeå"); s1.setCountry("Sweden");
        s1.setRating(5); s1.setWorkflowStatus(WorkflowStatus.APPROVED);
        s1 = supplierRepository.save(s1);
        addContact(s1, "Gunnar Lindqvist", "Key Account Manager", "g.lindqvist@norrmejerier.se", true);
        addContract(s1, "NRM-CNT-2024-001", LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31),
                new BigDecimal("320000"), ContractStatus.ACTIVE);

        Supplier s2 = new Supplier();
        s2.setName("VisBook AS"); s2.setCode("VBK-002");
        s2.setCategory(SupplierCategory.TECHNOLOGY);
        s2.setStatus(SupplierStatus.ACTIVE);
        s2.setEmail("support@visbook.com");
        s2.setPhone("+47-22-555-9900");
        s2.setCity("Oslo"); s2.setCountry("Norway");
        s2.setRating(4); s2.setWorkflowStatus(WorkflowStatus.DRAFT);
        s2 = supplierRepository.save(s2);
        addContact(s2, "Ingrid Olsen", "Technical Lead", "i.olsen@visbook.com", true);

        Supplier s3 = new Supplier();
        s3.setName("Elis Textile Services"); s3.setCode("ELS-003");
        s3.setCategory(SupplierCategory.LINEN_LAUNDRY);
        s3.setStatus(SupplierStatus.ACTIVE);
        s3.setEmail("info@elis.com");
        s3.setCity("Stockholm"); s3.setCountry("Sweden");
        s3.setRating(5); s3.setWorkflowStatus(WorkflowStatus.APPROVED);
        s3 = supplierRepository.save(s3);
        addContact(s3, "Anna Björk", "Sales Director", "a.bjork@elis.com", true);
        addContract(s3, "ELS-CNT-2025-001", LocalDate.of(2025, 1, 1), LocalDate.of(2027, 12, 31),
                new BigDecimal("210000"), ContractStatus.ACTIVE);
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

    // ── Steward Tasks ──────────────────────────────────────────────────────────

    private void createStewardTasks() {
        if (taskRepository.count() > 0) return;

        DataStewardTask t1 = new DataStewardTask();
        t1.setTitle("Verify Comfort Hotel Vesterbro contact details");
        t1.setDescription("Cross-check hotel phone and email against official Strawberry.no listing before approving for publication.");
        t1.setEntityType("HOTEL");
        t1.setAssignedTo("steward");
        t1.setCreatedBy("manager");
        t1.setDueDate(LocalDate.now().plusDays(3));
        t1.setPriority(TaskPriority.HIGH);
        t1.setStatus(TaskStatus.OPEN);
        taskRepository.save(t1);

        DataStewardTask t2 = new DataStewardTask();
        t2.setTitle("Enrich VisBook AS supplier profile");
        t2.setDescription("Add missing address, contract terms, and website URL to the VisBook supplier record.");
        t2.setEntityType("SUPPLIER");
        t2.setAssignedTo("steward");
        t2.setCreatedBy("manager");
        t2.setDueDate(LocalDate.now().plusDays(7));
        t2.setPriority(TaskPriority.MEDIUM);
        t2.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(t2);

        DataStewardTask t3 = new DataStewardTask();
        t3.setTitle("Deduplicate guest profiles from loyalty migration");
        t3.setDescription("Review 12 flagged duplicate guest records from the legacy Strawberry loyalty system migration. " +
                           "Match by email + DOB and merge confirmed duplicates.");
        t3.setAssignedTo("steward");
        t3.setCreatedBy("admin");
        t3.setDueDate(LocalDate.now().plusDays(14));
        t3.setPriority(TaskPriority.CRITICAL);
        t3.setStatus(TaskStatus.OPEN);
        taskRepository.save(t3);

        DataStewardTask t4 = new DataStewardTask();
        t4.setTitle("Review Nordic Collection brand standards compliance");
        t4.setDescription("Audit The Thief and Villa Copenhagen against all mandatory Nordic Collection brand standards. " +
                           "Flag any gaps and create remediation tasks.");
        t4.setEntityType("HOTEL");
        t4.setAssignedTo("manager");
        t4.setCreatedBy("admin");
        t4.setDueDate(LocalDate.now().plusDays(30));
        t4.setPriority(TaskPriority.HIGH);
        t4.setStatus(TaskStatus.OPEN);
        taskRepository.save(t4);
    }
}
