package cn.pumch.web.controller;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.pumch.web.model.PsUser;
import cn.pumch.web.service.PsUserService;
import cn.pumch.web.util.AESEncrypUtil;
import cn.pumch.web.util.CommonUtils;
import cn.pumch.web.util.ParseSystemUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.mysql.jdbc.StringUtils;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * 通用控制器
 **/
@Controller
public class CommonController {

    @Resource
    private PsUserService userService;

    @Autowired
    private Producer captchaProducer;

    @RequestMapping(value = {"/","/login","/web/login"}, method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        PsUser user = (PsUser) request.getSession().getAttribute("userInfo");
        String jump = "login";
        if (CommonUtils.isMobileAgent(request.getHeader("user-agent"))) {
            jump = "mLogin";
        }

        if(null==user) {
            Cookie[] cookies = request.getCookies();
            for(Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if(cookieName.equals(REMEMBERME_TOKEN_NAME)) {
                    String cookieValue = cookie.getValue();
                    byte[] valueArray = ParseSystemUtil.parseHexStr2Byte(cookieValue);
                    String decrypt = new String(AESEncrypUtil.decrypt(valueArray));
                    String[] np = decrypt.split(":");
                    try {
                        jump = "forward:" + doLogin(np[0], np[1], request);
                        logger.info("用户通过Cookie登录成功：" + np[0]);
                    } catch (AuthenticationException e) {

                    }
                }
            }
        } else {
            Subject subject = SecurityUtils.getSubject();
            if (subject.hasRole("mt")) {
                jump = "forward:/mt/sList";
            } else if (subject.hasRole("t")) {
                jump = "forward:/t/mySignIn";
            } else {
                jump = "forward:/s/mySignList";
            }
        }

        return jump;
    }

    /**
     * 用户登录
     *
     * @param request
     * @param json
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/web/login", method = RequestMethod.POST)
    public JSONObject doLogin(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response) {

        String loginName = json.getString("loginName");
        String password = json.getString("password");
        boolean rememberMe = json.getBoolean("rememberMe");

        try {
            String jump = doLogin(loginName, password, request);

            json.put("resultInfo", jump);
            json.put("result", "success");

            if(rememberMe) {
                String remMeToken = loginName + ":" + password;
                Cookie cookie = new Cookie(REMEMBERME_TOKEN_NAME, AESEncrypUtil.encrypt(remMeToken));
                cookie.setPath("/");
                cookie.setMaxAge(2592000);
                response.addCookie(cookie);
            }
            logger.info(loginName+" 登录成功。");
        } catch (AuthenticationException e) {
            // 身份验证失败
            logger.error("用户验证失败，用户名：" + loginName, e);
            json.put("result", "error");
            json.put("resultInfo", "用户名或密码错误！");
            return json;
        }
        return json;
    }

    @RequestMapping(value = "/web/error/{eCode}")
    public String error(@PathVariable String eCode, HttpServletRequest request) {
        request.setAttribute("eCode", eCode);
        return "error";
    }

    /**
     * 用户登出
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        PsUser userInfo = (PsUser) request.getSession().getAttribute("userInfo");
        request.getSession().removeAttribute("userInfo");
        // 登出操作
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(REMEMBERME_TOKEN_NAME.equals(cookie.getName())) {
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        logger.info("用户"+userInfo.getLoginName()+"登出成功！");
        return "forward:/login";
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    public String updatePassword() {
        return "updatePassword";
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updatePassword(@RequestBody JSONObject queryParam, HttpSession session) {
        PsUser user = (PsUser) session.getAttribute("userInfo");
        String loginName = user.getLoginName();
        String oPassword = queryParam.getString("oPassword");
        String nPassword = queryParam.getString("nPassword");

        if (userService.updatePassword(loginName, oPassword, nPassword)) {
            queryParam.put("result", "success");
        } else {
            queryParam.put("result", "failed");
        }
        return queryParam;
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success() {
        return "success";
    }

    private String doLogin(String loginName, String password, HttpServletRequest request)
            throws AuthenticationException {
        String jump = "/s/mySignList";
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            // 身份验证
            subject.login(new UsernamePasswordToken(loginName, password));
            // 验证成功在Session中保存用户信息
            PsUser authUserInfo = userService.getUserByLoginName(loginName);
            request.getSession().setAttribute("userInfo", authUserInfo);
            authUserInfo.setLoginTime(new Date());
            userService.update(authUserInfo);
        } // 已经登录直接跳转
        if(subject.hasRole("mt")) {
            jump = "/mt/sList";
        } else if (subject.hasRole("t")) {
            jump = "/t/mySignIn";
        } else if (subject.hasRole("s")) {
            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            String urlBeforeRedirect = null;
            if (null != savedRequest) {
                urlBeforeRedirect = WebUtils.getSavedRequest(request).getRequestUrl();
            }
            if (!StringUtils.isNullOrEmpty(urlBeforeRedirect) && !urlBeforeRedirect.endsWith("logout")) {
                urlBeforeRedirect = urlBeforeRedirect.replace(request.getContextPath(), "");
                if (urlBeforeRedirect.length() > 2) {
                    jump = urlBeforeRedirect;
                } else {
                    jump = "/s/mySignList";
                }
            }
        }

        return jump;
    }

    @RequestMapping("/web/kaptcha")
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");
        // create the text for the image
        String capText = captchaProducer.createText();
        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    @RequestMapping("/web/register")
    public String regPage(HttpServletRequest request) {
        if (CommonUtils.isMobileAgent(request.getHeader("user-agent"))) {
            return "mRegister";
        }
        return "register";
    }

    @RequestMapping("/web/doReg")
    @ResponseBody
    public JSONObject doRegister(@RequestBody JSONObject queryParam, HttpServletRequest request){
        if(checkKaptcha(request, queryParam)) {
            return queryParam;
        }

        String loginName = queryParam.getString("loginName");
        PsUser user = userService.getUserByLoginName(loginName);
        if(null!=user) {
            queryParam.put("result", "error");
            queryParam.put("resultInfo", "登录名重复");
            return queryParam;
        }

        String idNo = queryParam.getString("idNo");
        if(userService.getUserCountByIdNo(idNo)>0) {
            queryParam.put("result", "error");
            queryParam.put("resultInfo", "学号重复");
            return queryParam;
        }

        String nickName = queryParam.getString("nickName");
        String password = queryParam.getString("password");

        user = new PsUser();
        user.setLoginName(loginName);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setCreateTime(new Date());
        user.setIdNo(idNo);
        user.setuState("1");
        boolean result = userService.doReg(user);
        if(result) {
            logger.info("学号为："+idNo+"的同学："+nickName +"注册成功。");
        } else {
            logger.warn("学号为："+idNo+"的同学："+nickName +"注册失败！");
        }

        queryParam.put("result", "success");
        return queryParam;
    }

    /**
     * 检查用户验证码
     * @param request
     * @param json
     * @return
     */
    private boolean checkKaptcha(HttpServletRequest request, JSONObject json) {
        String kaptchaExpected = (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        String kaptchaReceived = json.getString("kaptcha");

        boolean result = kaptchaReceived == null || !kaptchaReceived.equalsIgnoreCase(kaptchaExpected);
        if(result) {
            json.put("result", "error");
            json.put("resultInfo", "验证码错误");
        }
        return result;
    }

    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    private final static String REMEMBERME_TOKEN_NAME = "Pum_Session";
}
