package no.boco.backend.fill;

import no.boco.backend.category.Category;
import no.boco.backend.category.CategoryService;
import no.boco.backend.location.Location;
import no.boco.backend.location.LocationRepository;
import no.boco.backend.organization.Organization;
import no.boco.backend.organization.OrganizationRepository;
import no.boco.backend.picture.Picture;
import no.boco.backend.picture.PictureRepository;
import no.boco.backend.post.Post;
import no.boco.backend.post.PostRepository;
import no.boco.backend.rental.Rental;
import no.boco.backend.rental.RentalRepository;
import no.boco.backend.user.Role;
import no.boco.backend.user.User;
import no.boco.backend.user.UserRepository;
import no.boco.backend.user.UserService;
import no.boco.backend.userorg.OrganizationRole;
import no.boco.backend.userorg.UserOrganization;
import no.boco.backend.userorg.UserOrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

@Service
public class FillService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserOrganizationRepository userOrganizationRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private LocationRepository locationRepository;

    Location location1 = new Location(63.43638306013904,10.422946214675903,"Innherredsveien 60","Trondheim","7067","Norge");
    Location location2 = new Location(63.42636950000001,10.4718022,"Tungavegen 26","Trondheim","7047","Norge");
    Location location3 = new Location(60.120178104160715,11.480327811508765,"Rådyrvegen 1","Årnes","2150","Norge");
    Location location4 = new Location(60.26120815358961,5.067768851914876,"Lyngvegen 3","Skogsvåg","5382","Norge");
    Location location5 = new Location(63.794162447769374,11.484832763671875,"Lektor Musums gate 2","Verdal","7650","Norge");

    public void fillWithTestData(){
        try {
            location1 = locationRepository.save(location1);
            location2 = locationRepository.save(location2);
            location3 = locationRepository.save(location3);
            location4 = locationRepository.save(location4);
            location5 = locationRepository.save(location5);

            User user1 = saveUser("ola@normann.no", "Ola", "Normann", location1, "a");
            User user2 = saveUser("kari@hansen.no", "Kari", "Hansen", location3, "b");
            User user3 = saveUser("johan@yahoo.no", "Johan", "Klepp", location4, "c");
            saveUser("user@user.com","Bruker","Brukson", location5, "d");

            Organization organization = new Organization(938786054L, "Obs Bygg Lade");
            organization.setOrgName("Obs BYGG City Lade");
            File file = new File("src/main/resources/img/coop.jpg");
            String encoded = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            organization.setProfilePicture(encoded);
            organization = organizationRepository.save(organization);

            UserOrganization userOrganization = new UserOrganization(user1, organization, OrganizationRole.ADMIN);
            userOrganizationRepository.save(userOrganization);

            Category tools = categoryService.getByName("Verktøy");
            Category industry = categoryService.getByName("Industri");
            Category electronics = categoryService.getByName("Elektronikk");
            Category vehicles = categoryService.getByName("Kjøretøy");
            Category movies = categoryService.getByName("Film og medie");
            Category house = categoryService.getByName("Husholdning");
            Category data = categoryService.getByName("Datateknologi");
            Category services = categoryService.getByName("Tjenester");
            Category furniture = categoryService.getByName("Møbler");
            Category homes = categoryService.getByName("Bolig");

            ArrayList<Post> posts = new ArrayList<>();
            posts.add(savePost("Hammer", 50, tools, "Meget god hammer", user1, null, 1));
            posts.add(savePost("Hammer", 45, tools, "", user2, null, 2));
            posts.add(savePost("Hammer", 60, tools, "Utmerket hammer", user1, organization, 3));
            posts.add(savePost("Sag", 70, tools, "Veldig skarp", user3, null, 4));
            posts.add(savePost("Sag", 55, tools, "Meget god sag", user1, null, 5));
            posts.add(savePost("Sirkelsag", 150, tools, "Denne snurrer fort", user1, organization, 6));
            posts.add(savePost("Trillebår", 100, tools, "Triller ganske godt", user2, null, 7));
            posts.add(savePost("Trillebår", 105, tools, "100L Trillebår", user3, null, 8));
            posts.add(savePost("Gressklipper", 200, tools, "1000RPM går på bensin", user1, organization, 9));
            posts.add(savePost("Gressklipper", 180, tools, "Liten og lett :)", user1, null, 10));
            posts.add(savePost("Drill", 95, tools, "500W Drill", user2, null, 11));
            posts.add(savePost("Motorsag", 140, tools, "Sagde en eik med den, funker bra", user3, null, 12));
            posts.add(savePost("Motorsag", 190, tools, "Bensindrevet", user1, organization, 13));
            posts.add(savePost("Sementblander", 400, industry, "Denne snurrer godt og holder sementen flytende", user1, organization, 14));
            posts.add(savePost("Kantklipper", 110, tools, "Fantastik flink klipper", user2, null, 15));
            posts.add(savePost("Vinkelsliper", 90, tools, "Håndterer alle vinkler", user3, null, 16));
            posts.add(savePost("Skralle", 40, tools, "Denne kan skralle alt du kan trenge", user1, null, 17));
            posts.add(savePost("Skyvelær", 50, tools, "I god stand", user1, organization, 18));
            posts.add(savePost("Høytrykkspyler", 300, tools, "Denne fungerer", user2, null, 19));
            posts.add(savePost("Rake", 40, tools, "", user3, null, 20));
            posts.add(savePost("VHS Spiller", 160, electronics, "I relativt god stand", user1, null, 21));
            posts.add(savePost("Høyttaler", 120, electronics, "8 timer batteritid", user3, null, 22));
            posts.add(savePost("Høyttaler", 200, electronics, "140db", user3, null, 23));
            posts.add(savePost("CD Brenner", 100, electronics, "", user1, null, 24));
            posts.add(savePost("Ethernet kabel", 75, data, "Robust kabel", user2, null, 25));
            posts.add(savePost("Saab 900", 1500, vehicles, "340 000km, god stand.", user1, null, 26));
            posts.add(savePost("Nissan Almera", 1200, vehicles, "Har turbo.", user3, null, 27));
            posts.add(savePost("Shrek", 40, movies, "Med norske stemmer", user2, null, 28));
            posts.add(savePost("Flukten fra hønsegården", 35, movies, "", user2, null, 29));
            posts.add(savePost("Roomba", 350, house, "", user1, null, 30));
            posts.add(savePost("Damprenser", 100, house, "", user2, null, 31));
            posts.add(savePost("Hekkeklipper", 1000, services, "Jeg kan klippe hekken din", user1, null, 32));
            posts.add(savePost("Sovesofa", 100, furniture, "", user3, null, 33));
            posts.add(savePost("Klappstol", 50, furniture, "Tåler 90kg", user2, null, 34));
            posts.add(savePost("50kvm moderne", 1000, homes, "", user3, null, 35));
            posts.add(savePost("Maler", 800, services, "Kjøp egen maling, jeg maler bare.", user1, null, 36));
            posts.add(savePost("Sentral leilighet", 750, homes, "35kvm steinkast unna Bunnpris", user2, null, 37));

            Random random = new Random();
            for (int i = 0; i < posts.size(); i++) {
                for (int j = 3; j <= 8; j++) {
                    String fromDate = "2022-" + j + "-" + (random.nextInt(16) + 1);
                    String toDate = "2022-" + j + "-" + (random.nextInt(6) + 17);
                    User customer;
                    int choice = random.nextInt(2);
                    if (posts.get(i).getUser().equals(user1)){ customer = (choice == 0) ? user2 : user3;}
                    else if(posts.get(i).getUser().equals(user2)){ customer = (choice == 0) ? user1 : user3;}
                    else{ customer = (choice == 0) ? user1 : user2;}
                    saveRental(fromDate, toDate, posts.get(i), customer, (j < 6));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private User saveUser(String email, String firstName, String lastName, Location location, String index) throws Exception{
        User user = new User(email,"123",firstName,lastName,location,null,Role.USER);
        user.setPassword(userService.getbCrypt().encode("123"));

        File file = new File("src/main/resources/img/" + index + ".jpg");
        String encoded = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        user.setProfilePicture(encoded);
        user.setActivation(null);
        return userRepository.save(user);
    }

    private Post savePost(String title, Integer pricePerDay, Category category, String description, User user, Organization organization, int index) throws Exception {
        Post post = new Post(title,pricePerDay,category,description,null, user,organization,null);
        if(index == 2 || index == 34) {
            post.setInventory(5);
        }

        if(organization == null){
            Location usersLocation = user.getLocation();
            post.setLocation(new Location(usersLocation.getLatitude(), usersLocation.getLongitude(), usersLocation.getAddress(), usersLocation.getCity(), usersLocation.getPostCode(), usersLocation.getCountry()));
        } else {
            post.setLocation(location2);
        }
        post = postRepository.save(post);

        File file = new File("src/main/resources/img/" + index + ".jpg");
        String encoded = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        Picture picture = new Picture(post.getPostId() + "_" + index,encoded);
        picture.setPost(post);
        pictureRepository.save(picture);

        return postRepository.findById(post.getPostId()).get();
    }

    private Rental saveRental(String fromDate, String toDate, Post post, User customer, boolean approved){
        Rental rental = new Rental(Date.valueOf(fromDate),Date.valueOf(toDate),post,customer,approved);
        LocalDate from = Date.valueOf(fromDate).toLocalDate();
        LocalDate to = Date.valueOf(toDate).toLocalDate();
        int days = (int) ChronoUnit.DAYS.between(from, to);
        rental.setTotalPrice(post.getPricePerDay() * days);
        return rentalRepository.save(rental);
    }
}
