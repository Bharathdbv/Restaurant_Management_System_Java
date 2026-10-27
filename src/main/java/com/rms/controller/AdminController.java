package com.rms.controller;

import com.rms.model.Admin;
import com.rms.model.Item;
import com.rms.repository.AdminRepository;
import com.rms.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ItemRepository itemRepository;

    // GET /adminlogin => show admin login form
    @GetMapping("/adminlogin")
    public String showAdminLogin() {
        return "adminlogin";
    }

    // POST /adminlogin => authenticate admin
    @PostMapping("/adminlogin")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             RedirectAttributes redirectAttributes) {

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isPresent()) {
            // Plain text comparison (same as JS version)
            if (adminOpt.get().getPassword().equals(password.trim())) {
                return "redirect:/adminportal";
            }
            redirectAttributes.addFlashAttribute("error", "Invalid password");
            return "redirect:/adminlogin";
        }

        redirectAttributes.addFlashAttribute("error", "Admin not found");
        return "redirect:/adminlogin";
    }

    // GET /adminportal => show admin portal with categories
    @GetMapping("/adminportal")
    public String showAdminPortal(Model model) {
        List<String> categories = itemRepository.findDistinctCategories();
        model.addAttribute("categories", categories);
        return "adminportal";
    }

    // GET /get-items?category=xxx => return items for a category (JSON API)
    @GetMapping("/get-items")
    @ResponseBody
    public java.util.Map<String, List<String>> getItemsByCategory(@RequestParam String category) {
        List<Item> items = itemRepository.findByCategory(category);
        List<String> names = items.stream().map(Item::getIname).collect(Collectors.toList());
        return java.util.Map.of("items", names);
    }

    // POST /updateprice => update item price
    @PostMapping("/updateprice")
    public String updatePrice(@RequestParam String dishName,
                              @RequestParam Double nprice,
                              RedirectAttributes redirectAttributes) {

        Item item = itemRepository.findByIname(dishName);
        if (item == null) {
            redirectAttributes.addFlashAttribute("error", "Item not found");
            return "redirect:/adminportal";
        }

        item.setPrice(nprice);
        itemRepository.save(item);

        redirectAttributes.addFlashAttribute("message", "Price updated successfully");
        return "redirect:/adminportal";
    }

    // POST /add-item => add a new menu item
    @PostMapping("/add-item")
    public String addItem(@RequestParam String addCategory,
                          @RequestParam String itemName,
                          @RequestParam Double itemPrice,
                          RedirectAttributes redirectAttributes) {

        Item newItem = new Item();
        newItem.setCategory(addCategory);
        newItem.setIname(itemName);
        newItem.setPrice(itemPrice);
        itemRepository.save(newItem);

        redirectAttributes.addFlashAttribute("message", "Item added successfully");
        return "redirect:/adminportal";
    }

    // POST /alogout => logout admin
    @PostMapping("/alogout")
    public String adminLogout() {
        return "redirect:/adminlogin";
    }
}
