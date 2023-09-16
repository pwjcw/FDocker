package com.pwjcw.servlet.docker;

import com.pwjcw.servlet.base.ModelBaseServlet;

import java.io.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PageManager", value = "/pagemanager")
public class PageManager extends ModelBaseServlet {
    public void toIndex(HttpServletRequest request,HttpServletResponse response) throws IOException {
        processTemplate("admin/index",request,response);
    }

}