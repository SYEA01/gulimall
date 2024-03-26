package com.example.gulimall.search.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.common.exception.BizCodeEnume;
import com.example.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义sentinel的流控响应
 */
@Configuration
public class SecKillSentinelConfig {

    /**
     * 在构造方法中添加一个web的回调管理器
     */
    public SecKillSentinelConfig() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            /**
             * 在这个方法中，自定义请求过多的返回数据
             * @param httpServletRequest
             * @param httpServletResponse
             * @param e
             * @throws IOException
             */
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMessage());
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}
