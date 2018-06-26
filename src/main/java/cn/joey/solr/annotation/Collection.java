package cn.joey.solr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collection {

    /**
     * collection名称
     * @return
     */
    public String value() default "";

    /**
     * Solr URL
     * @return
     */
    public String baseSolrUrl() default "";

    /**
     * zk地址
     * @return
     */
    public String zkHost() default "";

    /**
     * 集群模式
     * @return
     */
    public boolean cluster() default false;

    /**
     * zk客户端超时时间
     * @return
     */
    public int zkClientTimeout() default 0;

    /**
     * zk连接超时时间
     * @return
     */
    public int zkConnectTimeout() default 0;

    /**
     *
     * @return
     */
    public String entity() default "";

    /**
     * 下划线转换
     * @return
     */
    public boolean underlineConvertEnable() default true;

    /**
     * 启用高亮
     * @return
     */
    public boolean highlight() default false;

    /**
     * 高亮字段
     * @return
     */
    public String highlightFieldName() default "";

    /**
     * 高亮样式前缀
     * @return
     */
    public String highlightSimplePre() default "";

    /**
     * 高亮样式后缀
     * @return
     */
    public String highlightSimplePost() default "";

}
