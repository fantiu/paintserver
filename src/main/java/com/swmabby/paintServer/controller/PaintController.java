package com.swmabby.paintServer.controller;

import com.swmabby.paintServer.server.PaintServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PaintController {

    @Autowired
    private PaintServer paintServer;

    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/admin")
    public String admin(Model model) {
        int num = paintServer.getOnlineNum();
        List<String> list = paintServer.getOnlineUsers();

        model.addAttribute("num",num);
        model.addAttribute("users",list);
        return "admin";
    }

    @RequestMapping("sendmsg")
    @ResponseBody
    public String sendMsg(String msg, String username){
        String [] persons = username.split(",");
        PaintServer.SendMany(msg,persons);
        return "success";
    }

    @RequestMapping("sendAll")
    @ResponseBody
    public String sendAll(String msg){
        PaintServer.sendAll(msg);
        return "success";
    }
}
