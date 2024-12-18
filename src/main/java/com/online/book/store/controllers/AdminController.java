package com.online.book.store.controllers;


import com.online.book.store.entities.BookRegistration;
import com.online.book.store.entities.OrderRegistration;
import com.online.book.store.entities.UserRegistration;
import com.online.book.store.repositories.BookRepository;
import com.online.book.store.repositories.OrderRepository;
import com.online.book.store.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BookRepository bookRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

    static public String user_session;

    @RequestMapping("/Admin_Home")
    public ModelAndView Admin_Home() {
        ModelAndView mv = new ModelAndView("Admin_View");
        return mv;
    }

    @RequestMapping("/Book_Management")
    public ModelAndView Book_Management() {
        ModelAndView mv = new ModelAndView("Book_Management");
        return mv;
    }

    @RequestMapping("/selectoperation")
    public ModelAndView selectOperation(String book_operation) {
        ModelAndView mv = new ModelAndView("Book_Management");

        if (book_operation.equals("None")) {
            return mv;
        } else if (book_operation.equals("Add")) {
            mv.addObject("selectAdd", "Add");
        } else if (book_operation.equals("Delete")) {
            mv.addObject("selectDelete", "Delete");
        }
        return mv;
    }

    @RequestMapping("/book_Add")
    public ModelAndView addBook(BookRegistration breg, String Book_title) {
        ModelAndView mv = new ModelAndView("Book_Management");
        Optional<BookRegistration> book1 = bookRepository.findById(Book_title);
        if (book1.isPresent()) {
            mv.addObject("PrintSwal", "Book_Exist");
        } else {
            mv.addObject("PrintSwal", "Add_Sucess");
            bookRepository.save(breg);
        }
        return mv;
    }

    @RequestMapping("/book_Delete")
    public ModelAndView deleteBook(String Book_title) {
        ModelAndView mv = new ModelAndView("Book_Management");

        Optional<BookRegistration> book1 = bookRepository.findById(Book_title);
        if (book1.isPresent()) {
            bookRepository.deleteById(Book_title);
            mv.addObject("PrintSwal", "Delete_Sucess");
        } else {
            mv.addObject("PrintSwal", "Delete_Failed");
        }
        return mv;
    }

    @PostMapping("/book_Update")
    public ModelAndView updateBook(@ModelAttribute BookRegistration updatedBook) {
        ModelAndView mv = new ModelAndView("Book_Management");
        Optional<BookRegistration> existingBook = bookRepository.findById(updatedBook.getBook_title());
        if (existingBook.isPresent()) {
            BookRegistration book = existingBook.get();
            book.setAuthor(updatedBook.getAuthor());
            book.setReviews(updatedBook.getReviews());
            book.setPrice(updatedBook.getPrice());
            book.setRate(updatedBook.getRate());
            bookRepository.save(book);
            mv.addObject("PrintSwal", "Update_Success");
        } else {
            mv.addObject("PrintSwal", "Update_Failed");
        }
        return mv;
    }
    @RequestMapping("/Book_Details")
    public ModelAndView bookDetails(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        ModelAndView mv = new ModelAndView("Book_Details");
        Page<BookRegistration> book1 = bookRepository.findAll(PageRequest.of(page, size, Sort.by("price")));

        if (book1.isEmpty()) {
            mv.addObject("PrintSwal", "Book_Details_Empty");
            mv.setViewName("Admin_View");
        } else {
            mv.addObject("BookArray", null);
            mv.addObject("BookObject", book1.getContent());
            mv.addObject("currentPage", page);
            mv.addObject("totalPages", book1.getTotalPages());
        }
        return mv;
    }

    @RequestMapping("/User_Details")
    public ModelAndView userDetails() {
        ModelAndView mv = new ModelAndView("User_Details");

        List<UserRegistration> ureg1 = userRepository.findAll();

        if (ureg1.isEmpty()) {
            mv.addObject("PrintSwal", "User_Details_Empty");

            mv.setViewName("Admin_View");
        } else {
            UserRegistration user = null;
            mv.addObject("UserArray", user);
            mv.addObject("UserObject", ureg1);
        }
        return mv;
    }

    @RequestMapping("/Admin_Order_Details")
    public ModelAndView adminOrderDetails() {
        ModelAndView mv = new ModelAndView("Admin_Order_Details");
        List<OrderRegistration> oreg1 = orderRepository.findAll();
        if (oreg1.isEmpty()) {
            mv.addObject("PrintSwal", "Order_Details_Empty");

            mv.setViewName("Admin_View");
        } else {
            OrderRegistration order = null;
            mv.addObject("OrderArray", order);
            mv.addObject("OrderObject", oreg1);
        }
        return mv;
    }

    @RequestMapping("/sendNewBooksNotification")
    public ModelAndView sendNewBooksNotification() {
        ModelAndView mv = new ModelAndView("Admin_View");

        List<UserRegistration> users = userRepository.findAll();
        if (users.isEmpty()) {
            mv.addObject("PrintSwal", "NoUsersFound");
            return mv;
        }
        for (UserRegistration user : users) {
            sendEmail(user.getEmail(), "Появились новые книги!", "Уважаемый пользователь, у нас появились новые книги в магазине. Посетите наш сайт для подробностей!");
        }
        mv.addObject("PrintSwal", "EmailsSent");
        return mv;
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
