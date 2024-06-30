package com.userfront.controller;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsTransaction;
import com.userfront.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.userfront.domain.Appointment;
import com.userfront.domain.User;
import com.userfront.service.AppointmentService;
import com.userfront.service.UserService;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/create",method = RequestMethod.GET)
    public String createAppointment(Model model) {
        Appointment appointment = new Appointment();
        model.addAttribute("appointment", appointment);
        model.addAttribute("dateString", "");

        return "appointment";
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String createAppointmentPost(@ModelAttribute("appointment") Appointment appointment, @ModelAttribute("dateString") String date, Model model, Principal principal) throws ParseException {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date d1 = format1.parse( date );
        appointment.setDate(d1);

        User user = userService.findByUsername(principal.getName());
        appointment.setUser(user);

        appointmentService.createAppointment(appointment);

        return "redirect:/userFront";
    }


    @RestController
    @RequestMapping("/api")
    @PreAuthorize("hasRole('ADMIN')")
    public static class UserResource {

        @Autowired
        private UserService userService;

        @Autowired
        private TransactionService transactionService;

        @RequestMapping(value = "/user/all", method = RequestMethod.GET)
        public List<User> userList() {
            return userService.findUserList();
        }

        @RequestMapping(value = "/user/primary/transaction", method = RequestMethod.GET)
        public List<PrimaryTransaction> getPrimaryTransactionList(@RequestParam("username") String username) {
            return transactionService.findPrimaryTransactionList(username);
        }

        @RequestMapping(value = "/user/savings/transaction", method = RequestMethod.GET)
        public List<SavingsTransaction> getSavingsTransactionList(@RequestParam("username") String username) {
            return transactionService.findSavingsTransactionList(username);
        }

        @RequestMapping("/user/{username}/enable")
        public void enableUser(@PathVariable("username") String username) {
            userService.enableUser(username);
        }

        @RequestMapping("/user/{username}/disable")
        public void diableUser(@PathVariable("username") String username) {
            userService.disableUser(username);
        }
    }
}
