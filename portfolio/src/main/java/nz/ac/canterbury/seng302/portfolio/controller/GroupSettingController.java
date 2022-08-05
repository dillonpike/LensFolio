package nz.ac.canterbury.seng302.portfolio.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupSettingController {


    @GetMapping("/groupSetting")
    public String groupSettingPage() {
        return "groupSetting";
    }
}

