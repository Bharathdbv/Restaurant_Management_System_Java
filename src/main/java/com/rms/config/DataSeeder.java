package com.rms.config;

import com.rms.model.Admin;
import com.rms.model.Item;
import com.rms.repository.AdminRepository;
import com.rms.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds initial data into the database on startup (only if tables are empty).
 * Creates a default admin and sample menu items.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        // Seed admin if none exists, or update existing
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("vamsidude9999@gmail.com");
            admin.setPassword("bv_dude99");
            adminRepository.save(admin);
            System.out.println("Default admin created: vamsidude9999@gmail.com / bv_dude99");
        } else if (adminRepository.findByUsername("vamsidude9999@gmail.com").isEmpty()) {
            // Update old admin to new creds
            adminRepository.deleteAll();
            Admin admin = new Admin();
            admin.setUsername("vamsidude9999@gmail.com");
            admin.setPassword("bv_dude99");
            adminRepository.save(admin);
            System.out.println("Admin updated to: vamsidude9999@gmail.com / bv_dude99");
        }

        // Seed menu items if none exist
        if (itemRepository.count() == 0) {
            // Starters
            itemRepository.save(new Item(null, "starter", "Chicken Tikka", 250.0));
            itemRepository.save(new Item(null, "starter", "Paneer Tikka", 200.0));
            itemRepository.save(new Item(null, "starter", "Veg Spring Rolls", 150.0));
            itemRepository.save(new Item(null, "starter", "Fish Fingers", 280.0));

            // Main Course
            itemRepository.save(new Item(null, "main_course", "Butter Chicken", 350.0));
            itemRepository.save(new Item(null, "main_course", "Veg Biryani", 250.0));
            itemRepository.save(new Item(null, "main_course", "Dal Makhani", 220.0));
            itemRepository.save(new Item(null, "main_course", "Chicken Biryani", 300.0));

            // Desserts
            itemRepository.save(new Item(null, "desserts", "Gulab Jamun", 120.0));
            itemRepository.save(new Item(null, "desserts", "Rasgulla", 100.0));
            itemRepository.save(new Item(null, "desserts", "Ice Cream", 150.0));

            System.out.println("Sample menu items seeded!");
        }
    }
}
