package com.rms.controller;

import com.rms.model.Employee;
import com.rms.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // GET /employee-signup => show signup form
    @GetMapping("/employee-signup")
    public String showSignupForm() {
        return "employee-signup";
    }

    // GET /employee-login => show login form
    @GetMapping("/employee-login")
    public String showLoginForm() {
        return "empl";
    }

    // GET /empportal => show employee portal
    @GetMapping("/empportal")
    public String showEmployeePortal() {
        return "empportal";
    }

    // POST /employee-signup => register a new employee
    @PostMapping("/employee-signup")
    public String signup(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String phone,
                         @RequestParam String password,
                         RedirectAttributes redirectAttributes) {

        Optional<Employee> existing = employeeRepository.findByEmail(email);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/employee-signup";
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setPassword(passwordEncoder.encode(password));
        employeeRepository.save(employee);

        return "redirect:/employee-login";
    }

    // POST /employee-login => authenticate employee
    @PostMapping("/employee-login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isPresent() && passwordEncoder.matches(password, employeeOpt.get().getPassword())) {
            Employee employee = employeeOpt.get();
            session.setAttribute("employeeName", employee.getName());
            session.setAttribute("employeeId", employee.getId());
            return "redirect:/empportal";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid credentials");
        return "redirect:/employee-login";
    }

    // POST /elogout => logout employee
    @PostMapping("/elogout")
    public String logout(HttpSession session) {
        session.removeAttribute("employeeName");
        session.removeAttribute("employeeId");
        return "redirect:/employee-login";
    }
}
