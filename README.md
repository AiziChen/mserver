# SObj in Java Programming language
`SObj` is a serial-data-type like `JSON`.
This is the Java Language version.

JavaScript version is also here:
https://github.com/AiziChen/SObj-typescript.git

### Compare between SObj & JSON
> SObj
```scheme
(*obj
  (id 1)
  (name "DavidChen")
  (age 25)
  (birth "2019-01-16 01:08,30")
  (glasses ((id 1)
    (degree 203.3)
    (color "RED-BLACK")))
  (height 167.3)
  (goods (*list
    (*obj (name "火龙果") (price 2.3))
    (*obj (name "雪梨") (price 3.2))))
  (behaviors (*list "Shopping""Running""Football")))
```
> JSON
```json
{
  "id" : 1,
  "name" : "DavidChen",
  "age" : 25,
  "birth" : 1547630304332,
  "glasses" : {
    "id" : 1,
    "degree" : 203.3,
    "color" : "RED-BLACK"
  },
  "height" : 167.3,
  "goods" : [ {
    "name" : "火龙果",
    "price" : 2.3
  }, {
    "name" : "雪梨",
    "price" : 3.2
  } ],
  "behaviors" : [ "Shopping", "Running", "Football" ]
}
```
```shell
u1 = User{id=1, name=DavidChen, age=23, birth=Wed Jan 16 01:08:30 CST 2019, glasses=Glasses{id=1, degree=203.3, color=RED-BLACK}, height=167.3, goods=[Lquanye.lispparser.test.domain.Goods;@5451c3a8, behaviors=[Ljava.lang.String;@2626b418}
9999 objects total time: 1360ms
```
## Rules
* `(*obj ...)` - Object
* `(name value)` - Key-Value Pair
* `(*list ...)` - Array

## Usage
```java
// Java Objects to SObj
String sUser = LispParser.fromObject(user);
// SObj to Java Objects
User user = LispParser.toObject(sUser, User.class);
```
