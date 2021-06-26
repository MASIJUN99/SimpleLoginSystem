package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.utils.RedisUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CaptchaController {

  @Autowired
  private RedisUtil redisUtil;
  @Autowired
  Producer captchaProducer;

  @RequestMapping(value = "/captcha")
  public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //用字节数组存储
    byte[] captchaChallengeAsJpeg = null;
    ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
    ServletOutputStream responseOutputStream =
        response.getOutputStream();
    final HttpSession httpSession = request.getSession();
    try {
      //生产验证码字符串并保存到session中
      String createText = captchaProducer.createText();

      //打印随机生成的字母和数字
      //System.out.println("生成验证码:" + createText);

      httpSession.setAttribute(Constants.KAPTCHA_SESSION_KEY, createText);
//      redisUtil.set(Constants.KAPTCHA_SESSION_KEY, createText,  60 * 5);
      redisUtil.set(Constants.KAPTCHA_SESSION_KEY, createText,  60 * 5);

      //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
      BufferedImage challenge = captchaProducer.createImage(createText);
      ImageIO.write(challenge, "jpg", jpegOutputStream);
      captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
      response.setHeader("Cache-Control", "no-store");
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 0);
      response.setContentType("image/jpeg");
      //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
      responseOutputStream.write(captchaChallengeAsJpeg);
      responseOutputStream.flush();
    } catch (IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    } finally {
      responseOutputStream.close();
    }
  }

  @RequestMapping("/verifyCode")
  @ResponseBody
  public String verifyCode(String vercode, HttpServletRequest request) {
//    String captcha = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
    String captcha = (String) redisUtil.get(Constants.KAPTCHA_SESSION_KEY);

    JSONObject json = new JSONObject();
    String msg;
    if (captcha == null) {
      msg = "expired";
    } else if (captcha.equals(vercode)) {
      msg = "valid";
    } else {
      msg = "invalid";
    }
    json.put("msg", msg);
    return json.toJSONString();
  }
}
