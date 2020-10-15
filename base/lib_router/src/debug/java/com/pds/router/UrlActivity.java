package com.pds.router;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.data.UrlObj;

import java.util.List;
import java.util.Map;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 7:38 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 */
// 为每一个参数声明一个字段，并使用 @Autowired 标注
// URL中不能传递Parcelable类型数据，通过ARouter api可以传递Parcelable对象
@Route(path = "/url/activity")
public class UrlActivity extends Activity {
    // 需要注意的是，如果不使用自动注入，那么可以不写 ARouter.getInstance().inject(this)，
    // 但是需要取值的字段仍然需要标上 @Autowired 注解，因为 只有标上注解之后，ARouter才能知道以哪一种数据类型提取URL中的参数并放入Intent中，
    // 这样您才能在intent中获取到对应的参数
    @Autowired
    public String name;

    // 通过name来映射URL中的不同参数
    @Autowired
    int age;
    // 通过name来映射URL中的不同参数
    @Autowired(name = "girl")
    boolean boy;
    // 支持解析自定义对象，URL中使用json传递
    @Autowired
    UrlObj obj;
    // 使用 withObject 传递 List 和 Map 的实现了
    // Serializable 接口的实现类(ArrayList/HashMap)
    // 的时候，接收该对象的地方不能标注具体的实现类类型
    // 应仅标注为 List 或 Map，否则会影响序列化中类型
    // 的判断, 其他类似情况需要同样处理
    @Autowired
    List<UrlObj> list;
    @Autowired
    Map<String, List<UrlObj>> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        // ARouter会自动对字段进行赋值，无需主动获取
        Log.d("param", name + age + boy);
    }
}
