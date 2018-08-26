package com.qchery.kbench;

import com.qchery.kbench.backend.helpers.HttpGetHelper;
import com.qchery.kbench.backend.helpers.HttpPostHelper;
import com.qchery.kbench.backend.helpers.HttpPutHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chery
 * @date 2018/8/25 21:16
 */
@WebServlet(urlPatterns = "/maven2/*")
public class M2Servlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(M2Servlet.class);

    @Autowired
    private HttpPostHelper httpPostHelper;

    @Autowired
    private HttpPutHelper httpPutHelper;

    @Autowired
    private HttpGetHelper httpGetHelper;

    @Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response) throws IOException {
        log.info("GET request received for " + request.getPathInfo());
        httpGetHelper.handle(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException {
        log.info("POST request received.");
        httpPostHelper.handle(request, response);
    }

    @Override
    public void doPut(final HttpServletRequest request,
                      final HttpServletResponse response) throws IOException {
        log.info("PUT request received for " + request.getPathInfo());
        httpPutHelper.handle(request, response, request.getPathInfo());
    }

}
