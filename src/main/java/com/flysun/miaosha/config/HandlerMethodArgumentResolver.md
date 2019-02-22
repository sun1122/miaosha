#### ## # SpringMVC 4.3 源码分析之 HandlerMethodArgumentResolver
**1. HandlerMethodArgumentResolver 概述**<br>
HandlerMethodArgumentResolver = HandlerMethod + Argument(参数) + Resolver(解析器), 其实就是HandlerMethod方法的解析器, 将 HttpServletRequest(header + body 中的内容)解析为HandlerMethod方法的参数, 主要的策略接口如下:

```
// HandlerMethod 方法中 参数解析器
public interface HandlerMethodArgumentResolver {

    // 判断 HandlerMethodArgumentResolver 是否支持 MethodParameter(PS: 一般都是通过 参数上面的注解|参数的类型)
    boolean supportsParameter(MethodParameter parameter);

    // 从 ModelAndViewContainer(被 @ModelAttribute), NativeWebRequest(其实就是HttpServletRequest) 中获取数据, 解决 方法上的参数
    Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception;
}
```
基于这个接口实现的处理器主要是如下几类:

```
1. 基于 Name 从 URI Template Variable, HttpServletRequest, HttpSession, Http 的 Header 中获取数据的 HandlerMethodArgumentResolver
2. 数据类型是 Map 的 HandlerMethodArgumentResolver(数据也是从 RI Template Variable, HttpServletRequest, HttpSession, Http 的 Header 中获取)
3. 固定参数类型的 HandlerMethodArgumentResolver, 这里的参数比如是 SessionStatus, ServletResponse, OutputStream, Writer, WebRequest, MultipartRequest, HttpSession, Principal, InputStream 等
4. 基于 ContentType 利用 HttpMessageConverter 将输入流转换成对应的参数
```
**2. 基于Name 的 HandlerMethodArgumentResolver**<br>
这类参数解决器都基于抽象类 AbstractNamedValueMethodArgumentResolver 实现的, 在抽象类中定义了解决参数的主逻辑, 而子类只需要实现对应的模版方法即可以(PS: 这里蕴含了 策略与模版模式), 主逻辑如下:

```
public final Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    // 创建 MethodParameter 对应的 NamedValueInfo
    NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
    MethodParameter nestedParameter = parameter.nestedIfOptional();     // Java 8 中支持的 java.util.Optional
    // 因为此时的 name 可能还是被 ${} 符号包裹, 则通过 BeanExpressionResolver 来进行解析
    Object resolvedName = resolveStringValue(namedValueInfo.name);
    if (resolvedName == null) throw new IllegalArgumentException("Specified name must not resolve to null: [" + namedValueInfo.name + "]");

    // 下面的数据大体通过 HttpServletRequest, Http Headers, URI template variables(URI 模版变量) 获取
    // @PathVariable     --> 通过前期对 uri 解析后得到的 decodedUriVariables 获得
    // @RequestParam     --> 通过 HttpServletRequest.getParameterValues(name) 获取
    // @RequestAttribute --> 通过 HttpServletRequest.getAttribute(name) 获取   <-- 这里的 scope 是 request
    // @RequestHeader    --> 通过 HttpServletRequest.getHeaderValues(name) 获取
    // @CookieValue      --> 通过 HttpServletRequest.getCookies() 获取
    // @SessionAttribute --> 通过 HttpServletRequest.getAttribute(name) 获取 <-- 这里的 scope 是 session
    // 通过 resolvedName 来解决参数的真实数据  <-- 模版方法
    Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);
    if (arg == null) {
        if (namedValueInfo.defaultValue != null) {
            // 若 arg == null, 则使用 defaultValue, 这里默认值可能也是通过占位符 ${...} 来进行查找
            arg = resolveStringValue(namedValueInfo.defaultValue);
        } else if (namedValueInfo.required && !nestedParameter.isOptional()) {
            // 若 arg == null && defaultValue == null && 非 optional 类型的参数 则通过 handleMissingValue 来进行处理, 一般是报异常
            handleMissingValue(namedValueInfo.name, nestedParameter, webRequest);
        }
        // 对 null 值的处理 一般还是报异常
        arg = handleNullValue(namedValueInfo.name, arg, nestedParameter.getNestedParameterType());
    } // 若得到的数据是 "", 则还是使用默认值
    else if ("".equals(arg) && namedValueInfo.defaultValue != null) {
        // 这里的默认值有可能也是 ${} 修饰的, 所以也需要通过 BeanExpressionResolver 来进行解析
        arg = resolveStringValue(namedValueInfo.defaultValue);
    }

    if (binderFactory != null) {
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, namedValueInfo.name);
        try { // 通过 WebDataBinder 中的 Converter 将 arg 转换成 parameter.getParameterType() 对应的类型
              // 将 arg 转换成 parameter.getParameterType() 类型, 这里就需要 SimpleTypeConverter
            arg = binder.convertIfNecessary(arg, parameter.getParameterType(), parameter);
        } catch (ConversionNotSupportedException ex) {
            throw new MethodArgumentConversionNotSupportedException(arg, ex.getRequiredType(), namedValueInfo.name, parameter, ex.getCause());
        } catch (TypeMismatchException ex) {
            throw new MethodArgumentTypeMismatchException(arg, ex.getRequiredType(), namedValueInfo.name, parameter, ex.getCause());
        }
    }
    // 这里的 handleResolvedValue 一般是空实现, 在PathVariableMethodArgumentResolver中也是存储一下数据到 HttpServletRequest 中
    handleResolvedValue(arg, namedValueInfo.name, parameter, mavContainer, webRequest);
    return arg;
}
```
上面代码的主要流程如下:

```
1. 基于 MethodParameter 构建 NameValueInfo <-- 主要有 name, defaultValue, required
2. 通过 BeanExpressionResolver(${}占位符解析器) 解析 name
3. 通过模版方法 resolveName 从 HttpServletRequest, Http Headers, URI template variables 中获取对应的属性值
4. 对 arg == null 这种情况的处理, 要么使用默认值, 若 required = true && arg == null, 则一般报出异常
5. 通过 WebDataBinder 将 arg 转换成 Methodparameter.getParameterType() 类型
```
子类主要需要完成如下操作:

```
1. 根据 MethodParameter 创建 NameValueInfo
2. 根据 name 从 HttpServletRequest, Http Headers, URI template variables 获取属性值
3. 对 arg == null 这种情况的处理
```
主要子类:

```
1. SessionAttributeMethodArgumentResolver
    针对 被 @SessionAttribute 修饰的参数起作用, 参数的获取一般通过 HttpServletRequest.getAttribute(name, RequestAttributes.SCOPE_SESSION)
2. RequestParamMethodArgumentResolver
    针对被 @RequestParam 注解修饰, 但类型不是 Map, 或类型是 Map, 并且 @RequestParam 中指定 name, 一般通过 MultipartHttpServletRequest | HttpServletRequest 获取数据
3. RequestHeaderMethodArgumentResolver
    针对 参数被 RequestHeader 注解, 并且 参数不是 Map 类型, 数据通过 HttpServletRequest.getHeaderValues(name) 获取
4. RequestAttributeMethodArgumentResolver
    针对 被 @RequestAttribute 修饰的参数起作用, 参数的获取一般通过 HttpServletRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST)
5. PathVariableMethodArgumentResolver
    解决被注解 @PathVariable 注释的参数 <- 这个注解对应的是 uri 中的数据, 在解析 URI 中已经进行解析好了 <- 在 RequestMappingInfoHandlerMapping.handleMatch -> getPathMatcher().extractUriTemplateVariables
6. MatrixVariableMethodArgumentResolver
    针对被 @MatrixVariable 注解修饰的参数起作用,  从 HttpServletRequest 中获取去除 ; 的 URI Template Variables 获取数据
7. ExpressionValueMethodArgumentResolver
    针对被 @Value 修饰, 返回 ExpressionValueNamedValueInfo
8. ServletCookieValueMethodArgumentResolver
    针对被 @CookieValue 修饰, 通过 HttpServletRequest.getCookies 获取对应数据
```
**3. 解决类型是Map 的 HandlerMethodArgumentResolver**
这个方法参数解析器需要结合上面的第一种参数解析器, 主要有如下的类型:

```
1. RequestParamMapMethodArgumentResolver
    针对被 @RequestParam注解修饰, 且参数类型是 Map 的, 且 @RequestParam 中没有指定 name, 从 HttpServletRequest 里面获取所有请求参数, 最后封装成 LinkedHashMap|LinkedMultiValueMap 的参数解析器
2. RequestHeaderMapMethodArgumentResolver
    解决被 @RequestHeader 注解修饰, 并且类型是 Map 的参数, HandlerMethodArgumentResolver会将 Http header 中的所有 name <--> value 都放入其中
3. PathVariableMapMethodArgumentResolver
    针对被 @PathVariable 注解修饰, 并且类型是 Map的, 且 @PathVariable.value ＝= null, 从 HttpServletRequest 中所有的 URI 模版变量 (PS: URI 模版变量的获取是通过 RequestMappingInfoHandlerMapping.handleMatch 获取)
4. MatrixVariableMapMethodArgumentResolver
    针对被 @MatrixVariable 注解修饰, 并且类型是 Map的, 且 MatrixVariable.name == null, 从 HttpServletRequest 中获取 URI 模版变量 <-- 并且是去除 ;
5. MapMethodProcessor
    针对被 参数是 Map, 数据直接从 ModelAndViewContainer 获取 ====Model====
```
**4. 解决固定类型的 HandlerMethodArgumentResolver<br>
主要的数据获取还是通过 HttpServletRequest, HttpServletResponse**
```
1. UriComponentsBuilderMethodArgumentResolver
    支持参数类型是 UriComponentsBuilder, 直接通过 ServletUriComponentsBuilder.fromServletMapping(request) 构建对象
2. SessionStatusMethodArgumentResolver
    支持参数类型是 SessionStatus, 直接通过 ModelAndViewContainer 获取 SessionStatus
3. ServletResponseMethodArgumentResolver
    支持 ServletResponse, OutputStream, Writer 类型, 数据的获取通过 HttpServletResponse
4. ServletRequestMethodArgumentResolver
    支持 WebRequest, ServletRequest, MultipartRequest, HttpSession, Principal, InputStream, Reader, HttpMethod, Locale, TimeZone, 数据通过 HttpServletRequest 获取
5. RedirectAttributesMethodArgumentResolver
    针对 RedirectAttributes及其子类的参数 的参数解决器, 主要还是基于 NativeWebRequest && DataBinder (通过 dataBinder 构建 RedirectAttributesModelMap)
6. ModelMethodProcessor
    针对 Model 及其子类的参数, 数据的获取一般通过 ModelAndViewContainer.getModel()
```
除了上面的几个类, 还有一个特别的 HandlerMethodArgumentResolver, 它就是 ModelAttributeMethodProcessor, 主要是针对 被 @ModelAttribute 注解修饰且不是普通类型(通过 !BeanUtils.isSimpleProperty来判断)的参数, 而参数的获取通过 从 ModelAndViewContainer.ModelMap 中获取数据值, 主逻辑如下:

```
public final Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    // 获取 @ModelAttribute 中指定 name
    String name = ModelFactory.getNameForParameter(parameter);
    // 从 ModelAndViewContainer.ModelMap 中获取数据值 | 通过构造函数创建一个
    Object attribute = (mavContainer.containsAttribute(name) ? mavContainer.getModel().get(name) : createAttribute(name, parameter, binderFactory, webRequest));
    // 检测 name 是否可以进行绑定
    if (!mavContainer.isBindingDisabled(name)) {
        ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
        if (ann != null && !ann.binding()) mavContainer.setBindingDisabled(name);
    }
    // 此处进行参数的绑定操作 (PS: 下面的 attribute 就是 DataBinder 的 target)
    WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
    if (binder.getTarget() != null) {
        if (!mavContainer.isBindingDisabled(name)) {  // 若可以进行参数的绑定
            bindRequestParameters(binder, webRequest); // 进行参数的绑定
        }
        // applicable: 合适 <-- 这里是进行参数的检查
        validateIfApplicable(binder, parameter);
        // 检查在校验的过程中是否出错
        if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) throw new BindException(binder.getBindingResult());
    }
    // 将 resolved 后的 Model 放入 ModelAndViewContainer 中
    // Add resolved attribute and BindingResult at the end of the model
    Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
    mavContainer.removeAttributes(bindingResultModel);
    mavContainer.addAllAttributes(bindingResultModel);
    // 通过 SimpleTypeConverter 进行参数的转换
    return binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
}
```
**5. 基于 ContentType 利用 HttpMessageConverter 将输入流转换成对应的参数的 HandlerMethodArgumentResolver**

这类参数解析器的基类是 AbstractMessageConverterMethodArgumentResolver, 如下是其主要的属性

```
// 解决 HandlerMethod 中的 argument 时使用到的 HttpMessageConverters
protected final List<HttpMessageConverter<?>> messageConverters;
// 支持的 MediaType  <-- 通过这里的 MediaType 来筛选对应的 HttpMessageConverter
protected final List<MediaType> allSupportedMediaTypes;
// Request/Response 的 Advice <- 这里的 Advice 其实就是 AOP 中 Advice 的概念
// RequestAdvice 在从 request 中读取数据之前|后
// ResponseAdvice 在 将数据写入 Response 之后
private final RequestResponseBodyAdviceChain advice;
```
对应的主逻辑如下:

```
protected <T> Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter,
        Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

    MediaType contentType;
    boolean noContentType = false;
    try { // 获取 Http 请求头中的 contentType
        contentType = inputMessage.getHeaders().getContentType();       
    } catch (InvalidMediaTypeException ex) {
        // 获取失败则报 HttpMediaTypeNotSupportedException, 根据 DefaultHandlerExceptionResolver, 则报出 Http.status = 415
        throw new HttpMediaTypeNotSupportedException(ex.getMessage());  
    }
    // 若 contentType == null, 则设置默认值, application/octet-stream
    if (contentType == null) {                                          
        noContentType = true;
        contentType = MediaType.APPLICATION_OCTET_STREAM;
    }
    // 获取 方法的声明类
    Class<?> contextClass = (parameter != null ? parameter.getContainingClass() : null);
    // 获取请求参数的类型
    Class<T> targetClass = (targetType instanceof Class ? (Class<T>) targetType : null);                
    if (targetClass == null) {   // 若 targetClass 是 null, 则通过工具类 ResolvableType 进行解析                                                                        
        ResolvableType resolvableType = (parameter != null ? ResolvableType.forMethodParameter(parameter) : ResolvableType.forType(targetType));
        targetClass = (Class<T>) resolvableType.resolve();                                              // 获取参数的类型
    }
    // 获取请求的类型 HttpMethod (GET, POST, INPUT, DELETE 等)
    HttpMethod httpMethod = ((HttpRequest) inputMessage).getMethod();                                   
    Object body = NO_VALUE;

    try {
        inputMessage = new EmptyBodyCheckingHttpInputMessage(inputMessage);
        // 循环遍历 HttpMessageConverter, 找出支持的 HttpMessageConverter
        for (HttpMessageConverter<?> converter : this.messageConverters) {                              
            Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
            // 下面分成两类 HttpMessageConverter 分别处理
            if (converter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
                // 判断 GenericHttpMessageConverter 是否支持 targetType + contextClass + contextType 这些类型
                if (genericConverter.canRead(targetType, contextClass, contentType)) {
                    logger.info("Read [" + targetType + "] as \"" + contentType + "\" with [" + converter + "]");
                    if (inputMessage.getBody() != null) { // 若处理后有 request 值
                        // 在通过 GenericHttpMessageConverter 处理前 过一下 Request 的 Advice <-- 其实就是个切面
                        inputMessage = getAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                        // 通过 GenericHttpMessageConverter 来处理请求的数据
                        body = genericConverter.read(targetType, contextClass, inputMessage);
                        // 在 GenericHttpMessageConverter 处理后在通过 Request 的 Advice 来做处理 <-- 其实就是个切面
                        body = getAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                    }
                    else { // 若处理后没有值, 则通过 Advice 的 handleEmptyBody 方法来处理
                        body = getAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                    }
                    break;
                }
            }
            else if (targetClass != null) {
                // 判断 HttpMessageConverter 是否支持 这种类型的数据
                if (converter.canRead(targetClass, contentType)) {
                    logger.info("Read [" + targetType + "] as \"" + contentType + "\" with [" + converter + "]");
                    if (inputMessage.getBody() != null) { // 若处理后有 request 值
                        // 在通过 HttpMessageConverter 处理前 过一下 Request 的 Advice <-- 其实就是个切面
                        inputMessage = getAdvice().beforeBodyRead(inputMessage, parameter, targetType, converterType);
                        // 通过 HttpMessageConverter 来处理请求的数据
                        body = ((HttpMessageConverter<T>) converter).read(targetClass, inputMessage);
                        // 在 HttpMessageConverter 处理后在通过 Request 的 Advice 来做处理 <-- 其实就是个切面
                        body = getAdvice().afterBodyRead(body, inputMessage, parameter, targetType, converterType);
                    }
                    else { // 若 Http 请求的 body 是 空, 则直接通过 Request/ResponseAdvice 来进行处理
                        body = getAdvice().handleEmptyBody(null, inputMessage, parameter, targetType, converterType);
                    }
                    break;
                }
            }
        }
    } catch (IOException ex) {
        throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
    }
    if (body == NO_VALUE) {  // 若 body 里面没有数据, 则
        if (httpMethod == null || !SUPPORTED_METHODS.contains(httpMethod) || (noContentType && inputMessage.getBody() == null)) return null;
        // 不满足以上条件, 则报出异常
        throw new HttpMediaTypeNotSupportedException(contentType, this.allSupportedMediaTypes);
    }
    return body;
}
```
对应流程如下:

```
1. 获取 Http 请求的 contentType
2. 获取请求参数的类型
3. 循环遍历 HttpMessageConverter, 通过 canRead 判断是否支持对应的参数类型解决
4. 循环遍历 ApplicationContext 中的 RequestBodyAdvice, 若支持的话, 则通过 RequestBodyAdvice 在 对 HttpServletRequest 读取的数据之前 进行一些增强操作
5. 通过 GenericHttpMessageConverter 来处理请求的数据
6. 在 GenericHttpMessageConverter 处理后在通过 Request 的 Advice 来做处理 <-- 其实就是个切面
7. 若请求的 body没有数据, 则通过 Advice 的 handleEmptyBody 方法来处理


```

对应的子类具有如下:

```
1. RequestPartMethodArgumentResolver
    参数被 @RequestPart 修饰, 参数是 MultipartFile | javax.servlet.http.Part 类型, 数据通过 HttpServletRequest 获取
2. HttpEntityMethodProcessor
    针对 HttpEntity|RequestEntity 类型的参数进行参数解决, 将 HttpServletRequest  里面的数据转换成 HttpEntity|RequestEntity   <-- HandlerMethodArgumentResolver
3. RequestResponseBodyMethodProcessor
    解决被 @RequestBody 注释的方法参数  <- 其间是用 HttpMessageConverter 进行参数的转换 
```
上面 RequestResponseBodyMethodProcessor 是最常用得, 主要是针对 @RequestBody 注解, 并且其也是个 HandlerMethodReturnValueHandler(PS: 这个后面说), 其主流程如下:

```
public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    // 获取嵌套参数 <- 有可能参数是用 Optional
    parameter = parameter.nestedIfOptional();   
    // 通过 HttpMessageConverter 来将数据转换成合适的类型
    Object arg = readWithMessageConverters(webRequest, parameter, parameter.getNestedGenericParameterType());
    // 获取参数的名字
    String name = Conventions.getVariableNameForParameter(parameter);
    // 构建 WebDataBinder, 参数中的第二个值 arg 其实就是 DataBinder 的 target
    WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);  
    if (arg != null) {
        // @Validated 进行参数的校验
        validateIfApplicable(binder, parameter);
        // 若有异常则直接暴出来
        if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
    }
    // 将绑定的结果保存在 ModelAndViewContainer 中
    mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
    // 对 Optional类型的参数的处理
    return adaptArgumentIfNecessary(arg, parameter);
}

@Override
protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
    // 从 NativeWebRequest 中获取  HttpServletRequest
    HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
    // 封装 ServletServerHttpRequest
    ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
    // 通过 InputMessage 中读取参数的内容, 并且 通过 HttpMessageConverter 来将数据转换成 paramType 类型的参数
    Object arg = readWithMessageConverters(inputMessage, parameter, paramType);
    if (arg == null) {
        // 检测参数是否是必需的
        if (checkRequired(parameter)) throw new HttpMessageNotReadableException("Required request body is missing: " + parameter.getMethod().toGenericString());
    }
   return arg; // 返回参数值
}
```
**6. HandlerMethodArgumentResolver 中的优秀设计**


```
1. 策略模式: 主接口HandlerMethodArgumentResolver定义解决参数得方法, 根据不同得策略实现对应的子类
2. 组合模式: 通过 HandlerMethodArgumentResolverComposite 将支持的HandlerMethodArgumentResolver放在 HandlerMethodArgumentResolverComposite中, 进行统一处理, 与之对应的有 Dubbo 中以 Delegate 为尾缀的类名(从字面我们知道起代理作用, 但其只代理一个类)
3. 模版模式: 在抽象类AbstractMessageConverterMethodArgumentResolver中 定义解析参数的主逻辑, 而子类 HttpEntityMethodProcessor|RequestResponseBodyMethodProcessor实现具体的逻辑 
4. 建造者模式: UriComponentsBuilder <-- 基于 URL 构建 UriComponents

```
