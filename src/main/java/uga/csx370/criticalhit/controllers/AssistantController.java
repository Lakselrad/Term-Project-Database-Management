package uga.csx370.criticalhit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.services.AssistantService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /assistant, the natural language "find me a game" page.
 */
@Controller
@RequestMapping("/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    @Autowired
    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "q", required = false) String prompt) {
        ModelAndView mv = new ModelAndView("assistant_page");
        mv.addObject("pageTitle", "Game assistant");
        mv.addObject("activeAssistant", true);
        mv.addObject("examples", List.of(
                "Best RPGs on PC",
                "Cozy games on Switch",
                "Top shooters released after 2019",
                "Great platformers before 2018"));
        if (!Format.isBlank(prompt)) {
            mv.addObject("prompt", prompt);
            mv.addObject("result", assistantService.ask(prompt));
        }
        return mv;
    }
}
