package com.rms.controller;

import com.rms.model.FoodOrder;
import com.rms.model.Item;
import com.rms.repository.FoodOrderRepository;
import com.rms.repository.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
public class OrderController {

    @Autowired
    private FoodOrderRepository foodOrderRepository;

    @Autowired
    private ItemRepository itemRepository;

    // GET /neworder => show order page with all menu items
    @GetMapping("/neworder")
    public String showOrderPage(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "orderpage";
    }

    // POST /submit-order => place a new order
    @PostMapping("/submit-order")
    public String submitOrder(@RequestParam(required = false) List<String> dishes,
                              @RequestParam Integer tableNumber,
                              HttpSession session) {

        String employeeName = (String) session.getAttribute("employeeName");
        Long employeeId = (Long) session.getAttribute("employeeId");

        if (employeeName == null) {
            return "redirect:/employee-login";
        }

        if (dishes == null || dishes.isEmpty()) {
            return "redirect:/neworder";
        }

        FoodOrder order = new FoodOrder();
        order.setEmployeeId(employeeId);
        order.setItems(dishes);
        order.setEname(employeeName);
        order.setDate(LocalDate.now());
        order.setTnum(tableNumber);
        foodOrderRepository.save(order);

        return "redirect:/empportal";
    }

    // GET /prevorder => show previous orders for the logged-in employee
    @GetMapping("/prevorder")
    public String showPreviousOrders(HttpSession session, Model model) {
        String employeeName = (String) session.getAttribute("employeeName");

        if (employeeName == null) {
            return "redirect:/employee-login";
        }

        List<FoodOrder> eorders = foodOrderRepository.findByEname(employeeName);
        model.addAttribute("eorders", eorders);
        return "employee-orders";
    }

    // POST /generate-bill => generate bill for an order
    @PostMapping("/generate-bill")
    public String generateBill(@RequestParam String date,
                               @RequestParam Integer tnum,
                               @RequestParam(name = "items[]", required = false) List<String> items,
                               Model model) {

        if (items == null || items.isEmpty()) {
            return "redirect:/prevorder";
        }

        List<Item> itemPricesList = itemRepository.findByInameIn(items);

        Map<String, Double> itemPrices = new HashMap<>();
        double subtotal = 0;

        for (Item item : itemPricesList) {
            itemPrices.put(item.getIname(), item.getPrice());
            subtotal += item.getPrice();
        }

        double taxRate = 0.035; // 3.5%
        double cgst = subtotal * taxRate;
        double sgst = subtotal * taxRate;
        double grandTotal = subtotal + cgst + sgst;

        model.addAttribute("date", date);
        model.addAttribute("tnum", tnum);
        model.addAttribute("items", items);
        model.addAttribute("itemPrices", itemPrices);
        model.addAttribute("subtotal", String.format("%.2f", subtotal));
        model.addAttribute("cgst", String.format("%.2f", cgst));
        model.addAttribute("sgst", String.format("%.2f", sgst));
        model.addAttribute("grandTotal", String.format("%.2f", grandTotal));

        return "bill_template";
    }
}
