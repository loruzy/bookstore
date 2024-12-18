package com.online.book.store.controllers;

import com.online.book.store.entities.UserRegistration;
import com.online.book.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    UserRepository userRepository;


    String print = null;

    static public String user_session;


    @RequestMapping("/")
    public ModelAndView Home() {
        ModelAndView mv = new ModelAndView("Home");
        return mv;
    }

    @RequestMapping("/Login")
    public ModelAndView Login(String print, String User, String Pass) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("PrintSwal", print);

        if (User != null && Pass != null) {
            mv.addObject("User_Name", User);
            mv.addObject("Pass", Pass);
        } else {
            mv.addObject("User_Name", "admin");
            mv.addObject("Pass", "admin");
        }

        mv.setViewName("Login_Form");
        return mv;
    }

    @RequestMapping("/User")
    public ModelAndView User() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("Registration_Form");
        return mv;
    }

    @RequestMapping("/VerifyLogin")
    public ModelAndView verifyLogin(String email, String password, UserController u1) {
        ModelAndView mv = new ModelAndView("Login_Form");

        UserRegistration user = userRepository.findByEmailAndPassword(email, password);
        if (email.equals("admin") && password.equals("admin")) {
            print = "Admin";
            mv.addObject("PrintSwal", print);
            user_session = "Admin";
            mv.setViewName("Admin_View");
        } else if (user != null) {
            print = "UserLogin";
            mv.addObject("User", user.getFullname());
            user_session = user.getFullname();
            return u1.userHome(user_session, print);
        } else {
            print = "Failed";
            mv.addObject("PrintSwal", print);
        }
        return mv;
    }


    @RequestMapping("/User_Registration")
    public ModelAndView userRegistration(UserRegistration ureg, String email, String password) {
        ModelAndView mv = new ModelAndView("Registration_Form");
        Optional<UserRegistration> ureg1 = userRepository.findById(email);
        if (ureg1.isPresent()) {
            print = "User_Exists";

        } else {
            userRepository.save(ureg);
            print = "Reg_Sucess";
            mv.addObject("PrintSwal", print);
            return Login(print, email, password);

        }
        mv.addObject("PrintSwal", print);
        return mv;
    }
}
