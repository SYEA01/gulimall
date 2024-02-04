package com.example.gulimall.cart.controller;

import com.example.gulimall.cart.interceptor.CartInterceptor;
import com.example.gulimall.cart.service.CartService;
import com.example.gulimall.cart.to.UserInfoTo;
import com.example.gulimall.cart.vo.Cart;
import com.example.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author taoao
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 去购物车页面
     * 浏览器有一个cookie：user-key：标识用户身份，一个月后过期
     * 如果第一次使用京东的购物车功能，都会给一个临时的user-key
     * 浏览器以后保存，每次访问都会带上这个cookie；
     * <p>
     * 如果登录了，session中有
     * 如果没登录，就按照cookie里面带的user-key来做
     * 第一次；如果没有临时用户，帮忙创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
//        // 获取拦截器中的ThreadLocal中的数据
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
//        System.out.println("userInfoTo = " + userInfoTo);

        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * <p>
     * RedirectAttributes ra
     * ra.addFlashAttribute() 将数据放在session中，可以在页面取出，但是只能取一次
     * ra.addAttribute()  将数据放在url后面
     *
     * @param skuId 商品skuId
     * @param num   添加几件到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num,
                            RedirectAttributes ra) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        ra.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        // 重定向到成功页面。再次查询购物车数据即可
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item", item);
        return "success";
    }

}
