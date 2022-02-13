package ua.kiev.prog.controllers;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.kiev.prog.model.Contact;
import ua.kiev.prog.model.Group;
import ua.kiev.prog.services.ContactService;

import java.util.List;

import static ua.kiev.prog.controllers.GroupController.DEFAULT_GROUP_ID;

@Controller
@Transactional
public class ContactController {
    private static final int ITEMS_PER_PAGE = 6;

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @RequestMapping("/")
    public String index(Model model, @RequestParam(required = false, defaultValue = "0") Integer page) {
        if (page < 0) page = 0;

        long totalCount = contactService.count();
        int start = page * ITEMS_PER_PAGE;
        long pageCount = (totalCount / ITEMS_PER_PAGE) +
                ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);

        model.addAttribute("groups", contactService.listGroups());
        model.addAttribute("contacts",
                contactService.listContacts(null, start, ITEMS_PER_PAGE));
        model.addAttribute("pages", pageCount);

        return "index";
    }

    @RequestMapping("/contact_add_page")
    public String contactAddPage(Model model) {
        model.addAttribute("groups", contactService.listGroups());
        return "contact_add_page";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String pattern, Model model) {
        model.addAttribute("groups", contactService.listGroups());
        model.addAttribute("contacts", contactService.searchContacts(pattern));
        return "index";
    }

    @RequestMapping(value = "/contact/delete", method = RequestMethod.POST)
    public ResponseEntity<Void> delete(
            @RequestParam(value = "toDelete[]", required = false)
                    long[] toDelete) {
        if (toDelete != null && toDelete.length > 0)
            contactService.deleteContact(toDelete);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value="/contact/add", method = RequestMethod.POST)
    public String contactAdd(@RequestParam(value = "group") long groupId,
                             @RequestParam String name,
                             @RequestParam String surname,
                             @RequestParam String phone,
                             @RequestParam String email) {
        Group group = (groupId != DEFAULT_GROUP_ID) ?
                contactService.findGroup(groupId) : null;

        Contact contact = new Contact(group, name, surname, phone, email);
        contactService.addContact(contact);

        return "redirect:/";
    }

    @GetMapping(value = "/contact/download")
    public ResponseEntity<byte[]> demo(@RequestParam(value = "type") Integer type,
                                       @RequestParam(value = "group") Long id) { // (1) Return byte array response
        HttpHeaders httpHeaders = new HttpHeaders();// (3) Content-Type: application/octet-stream
//        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Group group = contactService.findGroup(id);
        String returnFile= null;
        if(type == 1 && id == 0){
            returnFile =  returnFileCreator(httpHeaders, group, "json");
        } else if(type == 2 && id == 0){
            returnFile =  returnFileCreator(httpHeaders, group, "xml");
        } else if(type == 1){
            returnFile =  returnFileCreator(httpHeaders, group, "json");
        } else {
            returnFile =  returnFileCreator(httpHeaders, group, "xml");
        }
        return ResponseEntity.ok().headers(httpHeaders).body(returnFile.getBytes()); // (5) Return Response
    }


    private String returnFileCreator(HttpHeaders httpHeaders, Group group, String format){
        String returnFile= null;
        List<Contact> contacts = null;
        contacts = contactService.listContacts(group);
       
        if(format.equals("json")){
            returnFile = contactsToJSON(contacts);
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        } else if(format.equals("xml")){
            returnFile = contactsToXML(contacts);
            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
        }
        httpHeaders.setContentDispositionFormData("attachment", "AllContacts." + format);
        return returnFile;
    }

    private String contactsToXML(List<Contact> list){
        StringBuilder sb = new StringBuilder();
        list.forEach(a -> {
            sb.append(a.toXMLString() + "\n");
        });
        return sb.toString();
    }

    private String contactsToJSON(List<Contact> list){
        StringBuilder sb = new StringBuilder();
        list.forEach(a -> {
            sb.append(a.toJSONString() + "\n");
        });
        return sb.toString();
    }

}
