package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


// NOTE!!!!!    this app uses an in memory H2 database



@Controller
public class HomeController {

    @Autowired
    ContactRepo contactRepo;

    // just show all the contacts in the default route
    @RequestMapping("/")
    public String listContacts(Model model){
        model.addAttribute("contacts", contactRepo.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String contactForm(Model model){
        model.addAttribute("contact", new Contact());
        // contactform POSTS to /process route, where record is updated in db
        return "contactform";
    }

    // like POST for add, this will both save NEW contacts, and update the db for existing contacts
    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute("contact") Contact contact, BindingResult result){
//    public String processForm(@Valid Contact contact, BindingResult result){
        if (result.hasErrors()){
            // always return contactform here, because it's the only way you could have gotten here
            return "contactform";
        }
        // .save both creates NEW and updates EXISTING (if same id, which is in contact)
        contactRepo.save(contact);
        return "redirect:/";
    }

// NOTES ON PATH VARIABLE VS REQUEST PARAM:
//    If URL http://localhost:8080/MyApp/user/1234/invoices?date=12-05-2013 gets the invoices for user 1234 on December 5th, 2013, the controller method would look like:
//
//    @RequestMapping(value="/user/{userId}/invoices", method = RequestMethod.GET)
//    public List<Invoice> listUsersInvoices(
//            @PathVariable("userId") int user,
//            @RequestParam(value = "date", required = false) Date dateOrNull) {
//  ...
//    }
//    Also, request parameters can be optional, but path variables cannot--if they were, it would change the URL path hierarchy and introduce request mapping conflicts. For example, would /user/invoices provide the invoices for user null or details about a user with ID "invoices"?
//

    @RequestMapping("/detail/{id}")
    public String showContact(@PathVariable("id") long id, Model model){
        model.addAttribute("contact", contactRepo.findOne(id));
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateContact(@PathVariable("id") long id, Model model){
        // this will allow the form in contactform to pre-populate with the data from the contact we just got from db
        model.addAttribute("contact", contactRepo.findOne(id));
        return "contactform";
        // contactform POSTS to /process route, where record is updated in db
    }

    @RequestMapping("/delete/{id}")
    public String delContact(@PathVariable("id") long id){
        // you can also pass the whole object to .delete, but it's easier to just pass the id
        contactRepo.delete(id);
        // redirect:  will flush out all models currently associated with '/', maybe not needed here, but FYI
        return "redirect:/";
    }
}