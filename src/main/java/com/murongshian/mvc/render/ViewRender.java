package com.murongshian.mvc.render;

import com.murongshian.Doodle;
import com.murongshian.mvc.RequestHandlerChain;
import com.murongshian.mvc.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 渲染页面
 */
public class ViewRender implements Render {
    private ModelAndView mv;

    public ViewRender(Object mv) {
        if (mv instanceof ModelAndView) {
            this.mv = (ModelAndView) mv;
        } else if (mv instanceof String) {
            this.mv = new ModelAndView().setView((String) mv);
        } else {
            throw new RuntimeException("返回类型不合法");
        }
    }
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        HttpServletRequest req = handlerChain.getRequest();
        HttpServletResponse resp = handlerChain.getResponse();
        String path = mv.getView();
        Map<String, Object> model = mv.getModel();
        model.forEach(req::setAttribute);
        req.getRequestDispatcher(Doodle.getConfiguration().getViewPath() + path).forward(req, resp);
    }
}