# joey-solr

joey-solr是一个简化solr查询的java扩展库。

基于配置文件&代码方式进行配置，默认以配置文件（joey-solr.ini）方式加载配置。

## 查询例子
``` java
//查询条件
List<Condition> q = new ArrayList<>();
q.add(new Condition("company_name", new String[] {"厦门"}));
//过滤条件
List<Condition> fq = new ArrayList<>();
fq.add(new Condition("province", new String[] {"福建"}));
//排序条件
List<Sort> sort = new ArrayList<>();
sort.add(new Sort("id"));
//分页对象
Pagination pagination = new Pagination(1, 30);
//查询对象
Query<Contact> query = new Query<Contact>(q, fq, sort, pagination, Contact.class);	
//打印参数
System.out.println("q : " + query.getQStr());
System.out.println("fq : " + query.getFQStr());
System.out.println("sort : " + query.getSortStr());				
//查询
query.search();
//分页信息
System.out.println(pagination);
```

## 查询结果
``` text
q : (company_name:"厦门")
fq : (province:"福建")
sort : id DESC
Pagination [pageNo=1, pageSize=30, startNum=0, totalSize=557]
```

## ini配置文件
``` ini


#joey-Solr配置信息

#Collection（多个,号分隔）
collection=contact,nzjxm,exclusive_project

#contact配置#
#Solr请求基础地址
contact.baseSolrUrl=http://192.168.2.125:8983/solr/contact
#查询结果需要转换的实体类
contact.classpath=cn.joey.test.Contact
#启用大写转换（例：Java对象属性userName，solr字段user_name）
contact.upperConvertEnable=true
#数据导入实体名称
contact.importEntity=c_contact_new
#启用高亮
contact.highlightEnable=true
#高亮字段
contact.highlightFieldName=company_name
#高亮样式前缀
contact.highlightSimplePre=<font style=\"color:red\">
#高亮样式后缀
contact.highlightSimplePost=</font>

#nzjxm配置#
#Solr请求基础地址
nzjxm.baseSolrUrl=http://192.168.2.125:8983/solr/nzjxm
#查询结果需要转换的实体类
nzjxm.classpath=cn.joey.test.DesignedProject
#启用大写转换（例：Java对象属性userName，solr字段user_name）
nzjxm.upperConvertEnable=true
#启用高亮
nzjxm.highlightEnable=false

#exclusive_project配置#
#查询耗时
exclusive_project.showTime=true
#集群模式
exclusive_project.cluster=true
#zookpeer地址
exclusive_project.zkHost=192.168.2.131:2181
#Solr请求基础地址
exclusive_project.baseSolrUrl=http://192.168.2.131:8983/solr/exclusive_project
#查询结果需要转换的实体类
exclusive_project.classpath=cn.joey.test.ExclusiveProject
#启用大写转换（例：Java对象属性userName，solr字段user_name）
exclusive_project.upperConvertEnable=true

```

## pom.xml
``` xml
<dependency>
  <groupId>org.apache.solr</groupId>
  <artifactId>solr-solrj</artifactId>
  <version>7.1.0</version>
</dependency>	 	 	  	
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-slf4j-impl</artifactId>
  <version>2.10.0</version>
</dependency>	
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>javax.servlet-api</artifactId>
  <version>3.1.0</version>
  <scope>provided</scope>
</dependency> 	
```

## jar包
[joey-solr-1.0.0.jar](http://dunhanson.oss-cn-shenzhen.aliyuncs.com/file/joey-solr-1.0.0.jar)

[joey-solr-1.0.0-jdk7.jar](http://dunhanson.oss-cn-shenzhen.aliyuncs.com/file/joey-solr-1.0.0-jdk7.jar)




