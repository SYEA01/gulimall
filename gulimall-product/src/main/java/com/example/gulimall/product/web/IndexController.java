package com.example.gulimall.product.web;

import com.example.gulimall.product.entity.CategoryEntity;
import com.example.gulimall.product.service.CategoryService;
import com.example.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author taoao
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    /**
     * 配置不论输入 / 还是 /index.html 都会跳转到首页
     *
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        // TODO 查出所有的 1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        // 放到请求域中
        model.addAttribute("categorys", categoryEntities);

        // 默认是转发
        return "index";
    }

    /**
     * 返回二级分类三级分类的json数据
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 1、获取一把锁，锁的名字随便写，只要名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");  // 锁的名字随便写

        // 2、加索  默认加的锁都是30秒时间，如果时间不够，会在业务时间自动续期
        lock.lock();  // 阻塞式等待
        //- 1、锁的自动续期，如果业务超长，Redisson会在代码运行期间自动给锁续上新的30秒周期。不用担心业务时间长，锁自动过期被删掉
        //- 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁也会默认在30秒以后自动删除
        try {
            System.out.println("加锁成功，执行业务" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            // 3、解锁  假设解锁代码没有运行，redisson不会死锁
            System.out.println("释放锁。。。" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

}
