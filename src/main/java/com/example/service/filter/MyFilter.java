package com.example.service.filter;

import com.example.service.http.utils.log.Log;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.INCLUDE}, urlPatterns = {"/*"})
public class MyFilter implements Filter {
    private static final String TAG = "MyFilter";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        Log.d(TAG, String.format("[URI: %s://%s:%d%s]", req.getScheme(), req.getServerName(), req.getServerPort(), request.getRequestURI()));
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

}
