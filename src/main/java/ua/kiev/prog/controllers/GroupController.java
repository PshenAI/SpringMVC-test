package ua.kiev.prog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.kiev.prog.model.Contact;
import ua.kiev.prog.model.Group;
import ua.kiev.prog.services.ContactService;
import ua.kiev.prog.services.ContactServiceImpl;

@Controller
public class GroupController {
    static final int DEFAULT_GROUP_ID = -1;

    private final ContactService contactService;

    public GroupController(ContactService contactService) {
        this.contactService = contactService;
    }

    @RequestMapping("/group_add_page")
    public String groupAddPage() {
        return "group_add_page";
    }

    @RequestMapping(value="/group/add", method = RequestMethod.POST)
    public String groupAdd(@RequestParam String name) {
        contactService.addGroup(new Group(name));
        return "redirect:/";
    }

    @RequestMapping("/group/{id}")
    public String listGroup(@PathVariable(value = "id") long groupId,
                            Model model) {
        Group group = (groupId != DEFAULT_GROUP_ID) ?
                contactService.findGroup(groupId) : null;

        model.addAttribute("groups", contactService.listGroups());
        model.addAttribute("contacts", contactService.listContacts(group));

        return "index";
    }

    @RequestMapping(value = "/deleteGroup")
    public String deleteGroup(@RequestParam(value = "groupId")
                                      long groupId){
        System.out.println("Method working check! Group id is " + groupId);
        Group group = contactService.findGroup(groupId);
        contactService.deleteGroup(group);
        return "redirect:/";
    };
}
