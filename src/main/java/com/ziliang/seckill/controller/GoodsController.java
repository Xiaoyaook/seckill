package com.ziliang.seckill.controller;

import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.GoodsKey;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.GoodsService;
import com.ziliang.seckill.service.SeckillUserService;
import com.ziliang.seckill.vo.GoodsDetailVo;
import com.ziliang.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@CrossOrigin(origins = {"http://localhost:8081"}, allowCredentials = "true")
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value="/to_list")
    @ResponseBody
    public Result<List<GoodsVo>> list(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        return Result.success(goodsList);

    }

    // 进行页面缓存
//    @RequestMapping(value="/to_list", produces="text/html")
//    @ResponseBody
//    public String list(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
//        model.addAttribute("user", user);
//        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
//        if(!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        model.addAttribute("goodsList", goodsList);
//        //return "goods_list";
//        WebContext ctx = new WebContext(request,response,
//                request.getServletContext(),request.getLocale(), model.asMap());
//        //手动渲染,保存到redis中
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
//        if(!StringUtils.isEmpty(html)) {
//            redisService.set(GoodsKey.getGoodsList, "", html);
//        }('getdetail'
//        return html;
//    }
    /*
    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model,SeckillUser user,
                         @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);
GoodsKey
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    } */

    // 商品详情接口,前端用来调用的接口,进行页面静态化,前后端分离
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
                                        @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);
        return Result.success(vo);
    }

//    @RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
//    @ResponseBody
//    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,SeckillUser user,
//                          @PathVariable("goodsId")long goodsId) {
//        model.addAttribute("user", user);
//
//        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
//        if(!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        //手动渲染
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//
//        long startAt = goods.getStartDate().getTime();
//        long endAt = goods.getEndDate().getTime();
//        long now = System.currentTimeMillis();
//
//        int seckillStatus = 0;
//        int remainSeconds = 0;
//        if(now < startAt ) {//秒杀还没开始，倒计时
//            seckillStatus = 0;
//            remainSeconds = (int)((startAt - now )/1000);
//        }else  if(now > endAt){//秒杀已经结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        }else {//秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
////        return "goods_detail";
//
//        WebContext ctx = new WebContext(request,response,
//                request.getServletContext(),request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
//        if(!StringUtils.isEmpty(html)) {
//            redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
//        }
//        return html;
//    }
}
