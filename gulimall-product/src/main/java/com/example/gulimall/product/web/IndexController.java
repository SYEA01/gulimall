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
import java.util.concurrent.TimeUnit;

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
//        lock.lock();  // 阻塞式等待
        //- 1、锁的自动续期，如果业务超长，Redisson会在代码运行期间自动给锁续上新的30秒周期。不用担心业务时间长，锁自动过期被删掉
        //- 2、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁也会默认在30秒以后自动删除

        // 没有看门狗，推荐使用这个，需要把自动解锁时间设置的长一点
        lock.lock(10, TimeUnit.SECONDS);  // 还可以在加锁的同时设置自动解锁，设置成10秒以后自动解锁 【 自动解锁时间，一定要大于业务的执行时间 】
        // 问题：如果指定了过期时间，在锁时间到了以后，不会自动续期。
        // 1、如果我们传递了锁的超时时间，就发送给Redis，执行Lua脚本，进行占锁，默认超时时间就是我们指定的时间
        // 2、如果我们没有传递锁的超时时间，就使用 30 * 1000 ，【 LockWatchdogTimeout 看门狗的默认事件 也就是30秒 】;
        //  2.1、只要占锁成功，就会启动一个定时任务【 重新给锁设置过期时间 （每隔10秒自动再次续期） 】

        // 最佳实战: 一般使用lock.lock(10, TimeUnit.SECONDS); 省掉了整个续期操作。 手动解锁
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
