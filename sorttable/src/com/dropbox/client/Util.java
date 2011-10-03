package com.dropbox.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Util {

    public static void authorizeForm(String url, String testing_user, String testing_password) throws IOException {
        BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));


        assert url != null : "You must give a url.";
        assert testing_user != null : "You gave a null testing_user.";
        assert testing_password != null : "You gave a null testing_password.";

        System.out.println("AUTHORIZING: " + url);

        WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);

        HtmlPage page = (HtmlPage)webClient.getPage(url);
        HtmlForm form = (HtmlForm)page.getForms().get(1);
        HtmlSubmitInput button = (HtmlSubmitInput)form.getInputByValue("Log in");


        HtmlTextInput emailField = (HtmlTextInput)form.getInputByName("login_email");
        emailField.setValueAttribute(testing_user);

        HtmlPasswordInput password = (HtmlPasswordInput)form.getInputByName("login_password");
        password.setValueAttribute(testing_password);

        // Now submit the form by clicking the button and get back the second page.
        HtmlPage page2 = (HtmlPage)button.click();

        try {
            form = (HtmlForm)page2.getForms().get(1);
            button = (HtmlSubmitInput)form.getInputByValue("Allow");
            button.click();
        } catch(ElementNotFoundException e) {
            System.out.println("No allow button, must be already approved.");
        } catch(IndexOutOfBoundsException e) {
            System.out.println("No second form, must be already approved.");
        }
    }

}
