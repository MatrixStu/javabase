# 基于JDBC的java数据库操作框架DB-helper(目前仅支持mysql)

> JDBC（Java Data Base Connectivity,java数据库连接）是一种用于执行SQL语句的Java API，可以为多种关系数据库提供统一访问，它由一组用Java语言编写的类和接口组成。
> 本框架是对jdbc的进一步简单封装，相较hibernate，mybaits等框架而言拥有良好的性能。

本文章的代码可在**文章最后的demo**中找到，demo是基于```idea开发环境```的。本框架是我花费近一周时间开发完成，希望能简化大家的数据库开发流程，目前```仅支持mysql```，时间仓促，如您发现问题，欢迎反馈至我邮箱```yanghy@youyicloud.com```。
### **JSObject**
   JSObject是基于```HashMap```的一个封装,内部使用HashMap存储数据，接受```int long double boolean String,List```等数据类型的数据，同时覆写了toString方法，可使用toString方法将内部数据转换为json字符串,可使用put(key,value)方法存储数据,同时提供```get(key)```方法来获取数据。
```java
  List<JSObject> userList = new ArrayList<JSObject>();
  for (int i = 0; i < 3; i++){
    JSObject user = new JSObject();
    user.put("userId",i+1);
    user.put("userName","test" + (i + 1));
    user.put("userPwd","pwdTest" + (i + 1));
    userList.add(user);
   }
  JSObject users = new JSObject();
  users.put("userList",userList);
  System.out.println(users);
```
**输出结果为**
```javascript
{"userList":[{"userPwd":"pwdTest1","userId":1,"userName":"test1"},{"userPwd":"pwdTest2","userId":2,"userName":"test2"},{"userPwd":"pwdTest3","userId":3,"userName":"test3"}]}
```
**此数据库框架目前查询后返回的结果为JSObject数据类型数据。**


### **DB-helper的初始化**
```此数据库框架可点击右侧链接```下载[点我下载](http://blog.youyicloud.com/data/DB_helper.jar)
下载完成后将此db-helper.jar加入到项目jar包依赖中。同时项目中应该加入```mysql的jar包，找不到的可通过右侧链接```下载[点我下载](http://blog.youyicloud.com/data/mysql-connector-java-5.1.6.jar)。

1 . 通过调用```DB.init(Sting userName,String userPwd,String dbUrl)```方法进行初始化。```使用本框架的其他方法前必须初始化，且必须传入合法的参数。```建议可只在程序第一次运行的地方进行初始化，如```servlet的init()方法```中进行。
```java
DB.init("root","","jdbc:mysql://localhost:3306/dbtest");
```
2 . 在初始化同时可以调用```DB.setDebuged(boolean flag)```来决定是否打印调试信息,默认为不显示。调试信息包括数据库sql语句与异常信息。

### **数据库基本操作**

**创建一个测试用数据库,数据库名为dbtest,通过下面的sql语句创建一个测试用表user表**
```sql
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `userPhone` varchar(255) NOT NULL,
  `userLevel` int(11) NOT NULL DEFAULT '1',
  `timeStamp` varchar(25) NOT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;
```


#### **插入**
#### **insert**
通过```put(key,value)```传入数据，最后excute执行,执行完毕返回一个flag表示执行成功与失败
```java
boolean flag = DB.insert(dbName)   //指定需要操作的表名String dbName="user",下同
            .put("userName","test")
            .put("userPhone","18888888888")
            .put("timeStamp",System.currentTimeMillis()+"")
            .put("userLevel",1)
            .execute();
```
#### **查找**
#### **scan**
scan查找返回的为```List<JSObject>，必须调用select方法与excute方法,其他方法根据情况选用```
```java
List<JSObject> userList = DB.scan(dbName)
                .select("userId","userName","userPhone")  //指定查询的字段,也可通过"*"来查询全部字段
                .where(DB.filter().whereEqualTo("userId",1))  //添加一个where筛选器
                .and(DB.filter().whereLessThanOrEqualTo("userName","test"))     //添加一个and语句,在此之前必须调where方法
                .or(DB.filter().whereGreaterThan("userLevel",0))  //添加一个or语句,在此之前必须调where方法
                .orderByDesc("timeStamp")   //指定排序字段，有先后顺序
                .orderByAsc("userName","userPhone")   //指定排序字段,有先后顺序
                .groupBy("userLevel") //指定分组字段
                .limit(10)     
                .start(2)      //指定起始位置，在此之前必须调用limit方法
                .execute();    //执行
```

#### **simpleScan**
scan查找返回的为```JSObject，只有单条记录，必须调用select方法与excute方法,其他方法根据情况选用，无limit与start方法```
```java
JSObject user = DB.simpleScan(dbName)
                .select("userId","userName","userPhone")  //指定查询的字段,也可通过"*"来查询全部字段
                .where(DB.filter().whereEqualTo("userId",1))  //添加一个where筛选器
                .and(DB.filter().whereLessThanOrEqualTo("userName","test"))  //添加一个and语句,在此之前必须调where方法
                .or(DB.filter().whereGreaterThan("userLevel",0))  //添加一个or语句,在此之前必须调where方法
                .orderByDesc("timeStamp")    //指定排序字段，有先后顺序
                .orderByAsc("userName","userPhone")  //指定排序字段,有先后顺序
                .groupBy("userLevel")   //指定分组字段
                .execute();   //执行
```
#### **修改**
#### **update**
update方法通过```put(key,value)```传入数据，最后excute执行,执行完毕返回一个flag表示执行成功与失败。```无groupBy方法,start方法```
```java
boolean flag = DB.update(dbName)
                .put("userName","test2")
                .where(DB.filter().whereEqualTo("userId",1))  //添加一个where筛选器
                .and(DB.filter().whereLessThanOrEqualTo("userName","test"))  //添加一个and语句,在此之前必须调where方法
                .or(DB.filter().whereGreaterThan("userLevel",0))  //添加一个or语句,在此之前必须调where方法
                .orderByDesc("timeStamp")  //指定排序字段，有先后顺序
                .orderByAsc("userName","userPhone")  //指定排序字段,有先后顺序
                .limit(2)
                .execute();   //执行
```
#### **删除**
#### **delete**
delete方法通过excute执行,执行完毕返回一个flag表示执行成功与失败。```无groupBy方法,start方法```
```java
boolean flag = DB.delete(dbName)
                .where(DB.filter().whereEqualTo("userId",1))  //添加一个where筛选器
                .and(DB.filter().whereLessThanOrEqualTo("userName","test"))  //添加一个and语句,在此之前必须调where方法
                .or(DB.filter().whereGreaterThan("userLevel",0))  //添加一个or语句,在此之前必须调where方法
                .orderByDesc("timeStamp")  //指定排序字段，有先后顺序
                .orderByAsc("userName","userPhone")  //指定排序字段,有先后顺序
                .limit(2)
                .execute();   //执行
```
#### **数据库函数**
数据库函数通常```配合select语句```来使用,目前```支持max,min,sum,distinct,count,avg等函数```
```java
List<JSObject> userList = DB.scan(dbName)
                .select(DB.func(DB.COUNT,"userId"),DB.func(DB.MAX,"userPhone"))  //通过DB.func(函数名,列)来调用
                .execute();   //执行
```
#### **其他方法**
同时可通过```DB.findLlistBySql(),DB.findOneBySql(),DB.updateBySql()```等方法来执行高级语句。
```java
List<JSObject> data = DB.findListBySql("select * from user where userName = ? and userPhone = ?","test2","18888888888");

JSObject singleData = DB.findOneBySql("select * from user where userName = ? and userPhone = ?","test2","18888888888");

boolean flag = DB.updateBySql("update user set userLevel = ? where userName = ? and userPhone = ?",1,"test2","18888888888");

boolean flag2 = DB.updateBySql("insert into user (userName,userPhone,userLevel,timeStamp) values (?,?,?,?)","test2","18888888888",1,System.currentTimeMillis()+"");

boolean flag3 = DB.updateBySql("delete from user where userName = ? and userPhone = ?","test2","18888888888");
```

### **各操作类型方法列表**
![](http://blog.youyicloud.com/wp-content/uploads/2016/07/J6R75K65KL79243.png)

[demo下载](http://blog.youyicloud.com/wp-content/uploads/2016/07/DBTest.zip)
