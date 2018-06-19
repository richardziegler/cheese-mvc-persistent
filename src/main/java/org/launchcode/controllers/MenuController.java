package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "cheese/index";
    }

    /**
     * Add a Menu controllers
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddCategoryForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddCategoryForm(@ModelAttribute @Valid Menu menu,
                                         Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model) {

        model.addAttribute("menu", menuDao.findOne(menuId));

        return "view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuId, Model model) {

        Menu currentMenu = menuDao.findOne(menuId);

        model.addAttribute("menu", currentMenu);

        AddMenuItemForm form = new AddMenuItemForm(currentMenu, cheeseDao.findAll());

       model.addAttribute("form", form);
       model.addAttribute("title", "Add item to menu: " + currentMenu.getName());

        return "add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm theMenu,
                          Errors errors, Model model){

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item");
            return "menu/add-item";
        }

        int cheeseInt = theMenu.getCheeseId();
        Cheese cheese = cheeseDao.findOne(cheeseInt);

        int menuInt = theMenu.getMenuId();
        Menu menu = menuDao.findOne(menuInt);


        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:/menu/view/" + menu.getId();

    }


}
