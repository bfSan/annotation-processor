# annotation-processor
练手annotation-Processor相关内容

## 一些APT、JCTree相关的资料
- [Javac黑客指南](https://developer.51cto.com/art/201305/392858.htm)
- [APT+JCTree的HelloWorld](https://blog.csdn.net/dap769815768/article/details/90448451)
- [JSR269及相关资料的传送门](https://blog.csdn.net/u012375207/article/details/70210111)
- [语法树Example](https://blog.csdn.net/a_zhenzhen/article/details/86065063#5%E3%80%81%E7%BB%99%E5%8F%98%E9%87%8F%E8%B5%8B%E5%80%BC)

## 已实现功能：根据注解所在的类，为Autowired对象对象生成工厂注入的代码。
例如：
  ``` 
  @Autowired
  private ApplicationContext applicationContext = (ApplicationContext)SpringUtils.getBean(ApplicationContext.class);
  ```