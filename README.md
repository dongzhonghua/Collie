# collie
Java分布式调用链系统

todo:

-[ ] 方法有多个返回和出口，采取的方法是把原方法重命名，新增一个同名的。  

## 什么是分布式调用链追踪？

如果一个系统非常的庞大，那么会带来非常多的麻烦，首先是系统复杂度升高了，各个系统之间互相调用，使得查找问题等变得非常复杂。对于新接手项目的人来说也是非常不友好的。那一急需一个工具来使得复杂的系统变得更清晰。分布式调用链追踪就是这样的一个工具。

初次听到这样的系统，感觉非常的牛逼。秉持着想深入了解一个东西的话那就来实现它的理念，我决定自己动手实现一个简单的分布式调用链追踪系统。

调用链追踪无非就是把一个请求的历程中每一个步骤的信息记录并输出到一个地方，最终展示出来。按照这个方式的话我们其实可以简单的构想一下如何实现这种功能：

1. 直接在每个需要追踪的地方加上代码，这种最简单，但是也是最low的方式了，问题不言自明。
2. 使用厉害一点的技术，可以用aop，但是aop还是需要在代码里做一定的修改，对代码的侵入程度比较大。
3. 有没有更厉害的技术呢？当然有，那就是Java agent技术，也叫Java探针。具体的概念先略过了，之前也写过一些。通俗的理解就是可以做到jvm层面的aop，不需要在业务代码里做任何操作，仅使用一个jar包就可以完成代码的改造。

那就开始用Java探针技术来做一个分布式调用链追踪。仅使用Java探针技术还不足以完成这个项目，大致想了想。大概会用到一下技术：

1. Java agent技术

2. javasist字节码修改技术，可以对class进行更改，当然更牛逼的是ASM但是有学习的成本。

3. ThreadLocal，在一个线程中串联起各个函数的调用的话，我想到的最好的方法就是ThreadLocal了，而且目前确实有写调用链就是这个干的。

4. 数据存储，kafka，es？这个地方我也还没想好。

5. 前端数据展示，这个不知道要不要做，当然一个好的系统肯定是要有展示的东西，特别是调用链涉及到时间，有界面展示可能更直观。

     

6. ![调用链监控，架构师必须点亮的技能](https://gitee.com/dongzhonghua/zhonghua/raw/master/img/blog/%E8%B0%83%E7%94%A8%E9%93%BE%E7%9B%91%E6%8E%A7%E7%A4%BA%E6%84%8F%E5%9B%BE.png)

调研过程中遇到的困惑和一些技术细节

1. 分布式的场景下如何实现一个请求的标记和顺序？这个其实比较好解决，在请求到来时设置一个ID就可以，然后再请求过程中一直带着这个ID。暂且命名为TraceId吧。

2. 如果涉及到不同的系统，这个ID就比较关键，需要一直传递下去。也就是全链路追踪能力。其中vivo的一篇文章提到的非常具有参考性。

    > 全链路数据传递能力是 vivo 调用链系统功能完整性的基石，也是Agent最重要的基础设施，前面提到过的spanId、traceId及链路标志等很多数据传递都依赖于全链路数据传递能力，系统开发中途由于调用链系统定位更加具体，当前无实际功能依赖于链路标志，本文将不做介绍。项目之初全链路数据传递能力，仅用于Agent内部数据跨线程及跨进程传递，当前已开放给业务方来使用了。
    >
    > 一般 Java 研发同学都知道 JDK 中的ThreadLocal工具类用于多线程场景下的数据安全隔离，并且使用较为频繁，但是鲜有人使用过JDK 1.2即存在的InheritableThreadLocal，我也是从未使用过。
    >
    > InheritableThreadLocal用于在通过new Thread()创建线程时将ThreadLocalMap中的数据拷贝到子线程中，但是我们一般较少直接使用new Thread()方法创建线程，取而代之的是JDK1.5提供的线程池ThreadPoolExecutor，而InheritableThreadLocal在线程池场景下就无能为力了。你可以想象下，一旦跨线程或者跨线程池了，traceId及spanId等等重要的数据就丢失不能往后传递，导致一次请求调用的链路断开，不能通过traceId连起来，对调用链系统来说是多么沉重的打击。因此这个问题必须解决。
    >
    > 其实跨进程的数据传递是容易的，比如http请求我们可以将数据放到http请求的header中，Dubbo 调用可以放到RpcContext中往后传递，MQ场景可以放到消息头中。而跨线程池的数据传递是无法做到对业务代码无侵入的，vivo调用链Agent是通过拦截ThreadPoolExecutor的加载，通过字节码工具修改线程池ThreadPoolExecutor的字节码来实现的，这个也是一般开源的调用链系统不具备的能力。

3. 在一个系统中的请求一定是有一个入口和出口，这个入口和出口是接收ID或返回数据的地方。针对不同的系统需要做一下适配。

4. 不能对所有的类都做切面处理，而是要针对业务类，所以需要针对特定规则来进行过滤。

5. agent开发过程还是比较麻烦的，主要每次调试之前都得打一个jar包。当然jar包在idea里也可以调试，主要还是每次改动都得打包。

6. 数据的存储？数据结构的存储，一个是放在JVM里，一个是放在数据库里，或许ELK？这个还是得在过程中来确定，肯定也不止一个。

7. 不能每一次调用都做处理，需要采样。

8. 自定义classloader，打破双亲委派模型。为什么要自定义classloader呢，实现Agent与应用环境隔离。还有一个原因是父类加载器加载的类不能引用子类加载器加载的类

9. 也可以采集jvm相关信息

感谢以下文章：

> [1] https://segmentfault.com/a/1190000038254246 vivo写的，非常非常好
>
> [2] https://zhuanlan.zhihu.com/p/136855172 一个简单的字节码插桩
>
> [3] https://my.oschina.net/u/4598048/blog/4549854 一个初步实现的调用链工具，非常有建设性，提供了几个问题的解答
>
> [4] https://my.oschina.net/xiaominmin/blog/3153685 javasist获取系统不到类加载器中的类的问题



### ClassNotFound的问题

javasist添加一行代码可以解决。主要的原因可能是javasist能够获取的类只能是java自己的类加载器加载的类。自定义的就不行了。

```java
classPool.appendClassPath(spyJarPath);
```

还是不同的类加载器带来的问题

必须使用反射来解决，不过这种方式可以非常好的把实现代码解耦出去，然后针对我们这个场景，可以抽取出来一个接口，分别是before和complete，看代码的过程中非常有感触，牛逼的人写的代码总是很抽象，我目前的话能实现功能就不错了。

一个比较好的培养自己抽象思维的东西，实现一个功能，写代码之前，先写一个接口。在接口中定义自己需要的方法，是不是可以有多个实现类。还有是不是可以抽出来一个抽象类。

---

上面是在调研过程中总结的一些知识，现在这个功能差不多已经完成了，git地址：https://github.com/dongzhonghua/collie

大致可以总结一下，有这么几个点是非常需要总结的：

1. 自定义类加载器，拆分jar包，类隔离机制。
2. Javaagent技术
3. 如何业务无感知打点，会用到javasist技术
4. Java反射相关的，因为需要到类隔离目前好像只能用反射了。
5. 如何对某些框架进行修改，在出口入口出打点。
6. 前端的技术选择。

下面一条一条的说：

### 自定义类加载器，拆分jar包，类隔离机制。

<img src="https://gitee.com/dongzhonghua/zhonghua/raw/master/img/blog/peony%E7%B1%BB%E5%8A%A0%E8%BD%BD.png" alt="image-20210530214706546" style="zoom: 50%;" />

首先，collie的启动方式是这样的：

```
java -javaagent:xxx/collie-agent-1.0-SNAPSHOT-jar-with-dependencies.jar=xxx/collie-core-1.0-SNAPSHOT-jar-with-dependencies.jar,xxx/collie-spy-1.0-SNAPSHOT.jar -jar xxx/collie-test-1.0-SNAPSHOT.jar
```

这种方式非常的丑，为什么要这样呢。一条一条来看的话，首先是-javaagent:xxx/collie-agent-1.0-SNAPSHOT-jar-with-dependencies.jar，这个制定了agent jar包的地址，至于agent，资料比较多。=xxx/collie-core-1.0-SNAPSHOT-jar-with-dependencies.jar,xxx/collie-spy-1.0-SNAPSHOT.jar这个是制定了agent的参数。这里指定的有两个jar包地址，一个是spy，一个是core。后面的就是业务代码所在的jar包了。主要讲一下spy和jar包是什么作用。首先调用链的主要代码是在core里面，但是core不能用应用类加载器，否则可能污染业务代码，比如，业务用了spring 5，但是core用的是spring 4，那么加载的时候会出现冲突，所以core里需要用自定义的类加载器加载。

spy里只有三个类，最主要的是两个，为什么要单独分出来呢？因为调用链框架需要埋点，会用到Point类的方法，但是如果Point类用应用类加载器加载，则在core里会找不到，如果用自定义的加载器加载，则业务代码中的打点会获取不到这个类，因为一个类只能获取到他自己和其父加载器加载的类。所以我们干脆吧spy交给启动类加载器。目前我还不知道有没有更好地方式来解决这个问题。

### Javaagent技术

这里也用到了Javaagent技术，这里我不想说太多，资料很多，我用的也比较基础。

### 如何业务无感知打点，会用到javasist技术

前面说到了，我们希望这个调用链是对业务无感知的，所以说不用业务写一点代码，这里用到了字节码插桩的技术，就是在加载类的时候对类进行一系列的改造，把调用链需要用到的代码即时的编译到class文件中去，常见的字节码插桩有asm，Javasist等。比较简单的就是javasist了，举个例子，Javasist的用法：

```java
ClassPool classPool = ClassPool.getDefault();
// 必须要有这个，否则会报point找不到，搞了大半天 https://my.oschina.net/xiaominmin/blog/3153685
classPool.appendClassPath(spyJarPath);
String clazzname = className.replace("/", ".");
CtClass ctClass = classPool.get(clazzname);
// 排除掉注解，接口，枚举
if (!ctClass.isAnnotation() && !ctClass.isInterface() && !ctClass.isEnum()) {
    // 针对所有函数操作
    for (CtBehavior ctBehavior : ctClass.getDeclaredMethods()) {
        addMethodAspect(clazzname, ctBehavior, false);
    }
    // 所有构造函数
    for (CtBehavior ctBehavior : ctClass.getDeclaredConstructors()) {
        addMethodAspect(clazzname, ctBehavior, true);
    }
}


private void addMethodAspect(String clazzname, CtBehavior ctBehavior, boolean isConstructor) throws Exception {
        if (isNative(ctBehavior)
                || isAbstract(ctBehavior)
                || ctBehavior.getName().equals("toString")
                || ctBehavior.getName().equals("getClass")
                || ctBehavior.getName().equals("equals")
                || ctBehavior.getName().equals("hashCode")) {
            return;
        }
        // 方法前加强
        // before(String className, String methodName, String descriptor, Object[] params)
        // 如果是基本数据类型的话，传参为Object是不对的，需要转成封装类型
        // 转成封装类型的话非常方便，使用$w就可以，还是牛逼啊，而且也不影响其他的Object类型
        String methodName = isConstructor ? ctBehavior.getName() + "#" : ctBehavior.getName();
        ctBehavior.insertBefore(
                String.format("{xyz.dsvshx.collie.point.Point.before(\"%s\", \"%s\", \"%s\", %s);}",
                        clazzname, methodName, "还不知道传什么", "($w)$args")
        );
        // 打点加到最后
        // complete(String className, String methodName, String descriptor, Object returnValueOrThrowable)
        ctBehavior.insertAfter(
                String.format("{xyz.dsvshx.collie.point.Point.complete(\"%s\", \"%s\", \"%s\", %s);}",
                        clazzname, methodName, "还不知道传什么", "($w)$_")
        );
        // 捕获异常
        ctBehavior.addCatch(
                String.format("{xyz.dsvshx.collie.point.Point.complete(\"%s\", \"%s\", \"%s\", %s);"
                                + "throw $e;}",
                        clazzname, methodName, "还不知道传什么", "$e"),
                ClassPool.getDefault().get("java.lang.Throwable")
        );
    }
```

只需要用到insertBefore等方法就可以，还有很多地方需要注意的，比如获取返回值需要用占位符$_。不过总体上用起来非常方便，如果用ASM实现相同的功能，可能得几百行代码。

### Java反射相关的，因为需要到类隔离目前好像只能用反射了。

我们的调用链打点的地方是在Point类中，通过javasist植入到方法中，Point类如下图所以，可以发现在这个类里面没有具体的实现首先打点之后的操作：

```java
// 这两个在什么时候赋值呢？
public static Method BEFORE_METHOD;
public static Method COMPLETE_METHOD;

public static void before(String className, String methodName, String descriptor, Object[] params) {
    if (BEFORE_METHOD != null) {
        try {
            BEFORE_METHOD.invoke(null, className, methodName, descriptor, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

public static void complete(String className, String methodName, String descriptor, Object returnValueOrThrowable) {
    if (COMPLETE_METHOD != null) {
        try {
            COMPLETE_METHOD.invoke(null, className, methodName, descriptor, returnValueOrThrowable);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
```



### 如何对某些框架进行修改，在出口入口出打点。

在这一步，需要对框架进行修改，在请求到来时，获取traceId等，把这些信息放到ThreadLocal中：

```java
public static void enter(String traceId, String spanId, String parentSpanId) {
    try {
        // 好像也可以把这些放到CONTEXT_ENTRY里在做，但是这样的话传参什么的比较麻烦，还是这样比较方便。
        if (traceId == null || traceId.trim().length() == 0) {
            traceId = UUID.randomUUID().toString();
        }
        if (spanId == null || spanId.trim().length() == 0) {
            spanId = UUID.randomUUID().toString();
        }
        if (parentSpanId == null || parentSpanId.trim().length() == 0) {
            parentSpanId = "-1";
        }
        TRANSACTION_INFO = new TransactionInfo(traceId, spanId, parentSpanId);
        TRANSACTION_INFO_THREAD_LOCAL.set(TRANSACTION_INFO);
        System.out.printf(">>>>>>>>>Thread %s set, transaction info :%s%n", Thread.currentThread().getName(),
                TRANSACTION_INFO);
        if (CONTEXT_ENTRY != null) {
            //invoke方法的签名为：Object invoke(Object obj, Object... args)，第一个参数是调用方法的对象，其余的提供了调用方法所需要的参数。
            // 对于静态方法，第一个参数可以被忽略，即直接设置为null
            CONTEXT_ENTRY.invoke(null);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


public static void exit() {
    try {
        TRANSACTION_INFO_THREAD_LOCAL.remove();
        System.out.printf(">>>>>>>>>Thread %s exit, transaction info :%s%n", Thread.currentThread().getName(),
                TRANSACTION_INFO);
        if (CONTEXT_EXIT != null) {
            CONTEXT_EXIT.invoke(null);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
// Summer是我自己写的一个仿spring的项目，改造这个项目比较好调试。
public class SummerFrameworkAdaptorImpl implements FrameworkAdaptor {
    @Override
    public byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes, String spyJarPath) {
        try {
            if (className.equals("xyz/dsvshx/ioc/mvc/RequestHandler")) {
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(spyJarPath);
                String clazzname = className.replace("/", ".");
                CtClass ctClass = classPool.get(clazzname);
                CtMethod doHandlerMethod = ctClass.getDeclaredMethod("doHandler");
                // 没想到这么简单就成了？
                doHandlerMethod.insertBefore("{"
                        + "String traceId = fullHttpRequest.headers().get(\"collie-trace-id\");"
                        + "String parentSpanId = fullHttpRequest.headers().get(\"collie-span-id\");"
                        + "xyz.dsvshx.collie.point.FrameworkPoint.enter(traceId, \"\", parentSpanId);"
                        + "}");
                doHandlerMethod.insertAfter("{"
                        + "xyz.dsvshx.collie.point.FrameworkPoint.exit();"
                        + "}");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

最终的打印结果如下：

```
--before
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getConstructorArgs', event='null', descriptor='', params='null', paramList=[], throwable=null, result=null, startTime=1622441782696, finishTime=0, cntMs=0}
--after
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getConstructorArgs', event='null', descriptor='', params='null', paramList=[], throwable=null, result=null, startTime=1622441782696, finishTime=1622441782696, cntMs=0}
--before
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getPropertyValues', event='null', descriptor='', params='null', paramList=[], throwable=null, result=null, startTime=1622441782697, finishTime=0, cntMs=0}
--after
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getPropertyValues', event='null', descriptor='', params='null', paramList=[], throwable=null, result=null, startTime=1622441782697, finishTime=1622441782697, cntMs=0}
--before
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getBean', event='null', descriptor='', params='null', paramList=[], throwable=null, result=null, startTime=1622441782697, finishTime=0, cntMs=0}
--after
CallRecord{transactionInfo=traceId: trace123456, spanId: f2c637e7-e33e-4231-9397-553d2e2972c5, parentSpanId: span123abc, className='xyz.dsvshx.ioc.entity.BeanDefinition', methodName='getBean', event='null', descriptor='', params='null', paramList=[], throwable=null, result=xyz.dsvshx.collie.controller.HelloController$$EnhancerByCGLIB$$2347223e@6ab7a896,ime=1622441782697, finishTime=1622441782697, cntMs=0}

```



### 前端的技术选择

前端也比较重要，最合适这里的场景其实是甘特图，前期尝试自己写了写但是感觉要做出来还是十分的复杂，我的前端实在是太菜了。之后调研了很多框架，比如功能非常完善的有：gantt-elastic， dhtmlx-gantt等。但是这些框架一个最大的缺点就是他们的时间粒度最小是秒级别的，对于我们这种场景下基本都是毫秒级别的是不支持的。所以下面就贴出来官网的一个demo，最后我想要的效果也和这个差不多：

![image-20210531160021063](https://gitee.com/dongzhonghua/zhonghua/raw/master/img/blog/%E8%B0%83%E7%94%A8%E9%93%BE%E7%94%98%E7%89%B9%E5%9B%BE%E7%A4%BA%E6%84%8F%E5%9B%BE.png)

网页地址：https://neuronet.io/gantt-elastic/#/

