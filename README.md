#使用方法

进入代码目录下，该目录包含了src，.idea等多个目录，src中有所有的源代码和资源文件，.idea是使用IDEA时产生的配置文件，可用该ide导入。该目录使用mvn package会自动生成target目录，其中包含了可运行的jar文件。双击运行，使用空格键角斗开始，角斗的记录会生成在record.txt文档中，角斗开始前或结束后按L键可以从文件载入重放一场角斗。角斗过程中最好不要打断否则生成记录文件会不完整。

#代码架构

###战斗对象

战斗对象包括葫芦娃和妖怪们，二者享有许多公共的特征，如都有一个二维的坐标记录位置，都有自己的名字，都有各自的一幅图片用于显示。将二者公共的部分抽象为类Thing2D，并定义了一些公共的接口函数方便调用：
```
String getName();    //获取名字
Image getImage();    //获取图像
void setliveImage(Image img);     //设置对象活着时图像
void setdeadImage(Image img);    //设置遗像（死亡后显示）
int x(); int y();    //获取坐标
void setX(int _x); void setY(int y);     //设置坐标
```
类Huluwa和Monster均继承自抽象类Thing2D，在此基础上实现了线程接口Runnable，每一个对象都将独立运行在一个线程中，可以通过kill接口设置对象的isDead（用于标识对象是否死亡）域，标识对象是否死亡，死亡的线程会自动终止。
生物体的运动是我们需要关心的首要问题，每一个线程需要独立地判断下一步将向哪一个方向移动，这就需要对场地上所有敌方生物体的位置有一个判断。我在Huluwa和Monster的类中都添加了场地类Field的引用，每次移动前通过场地对敌方位置做出预判，根据预判结果确定前进方向。

###场地类

Field是定义的场地类，他继承自JPanel，也肩负了图像显示的功能。Field的是保证线程安全的重要一环，所有在场生物体都持有场地对象的引用（全局只有一个场地对象），Field类中也包含了所有生物体对象的引用，形成了association的关系。为了保证各个线程对资源的线性访问（此处的资源就是坐标位置），各线程的每一次移动请求都提交给Field，由Field判决并处理——将二维数组中的对像移动位置并设置对象的x和y。当位置产生冲突时，以某种随机策略选择存活的对象，将死亡的对象清理出二维数组。判决移动的接口是moveToPos，该接口由synchronize关键字修饰，保证资源访问过程中不会被打断从而保证线程安全性。

###文件记录与重放
**RecordBattle**
作业中要求实现文件记录和重放的功能，由于记录功能和对象移动坐标息息相关，通常的做法应该是直接在Field的moveToPos接口（处理所有对象的移动请求）中实现文件io，但从功能分解的角度，将记录功能拆分成单独的类，在Field中拿到该类的引用从而以调用接口的形式生成记录会更方便，代码复杂度也会更低。
RecordBattle类就用于生成对象运行过程中的记录，当对象移动时，假设初始坐标为(originalX, originalY), 结束坐标是(newX, newY), 则声称记录为：
0 originalX originalY newX newY
如果对象死亡，生成记录为：
1 orignalX originalY dead deadX deadY
后面的坐标为对象死亡坐标
写入文件时使用了java.io库中的FileWriter操作，生成记录文件名为record.txt，位于可执行文件.jar的同一级目录下。
**RepeatBattle**
与RecordBattle对应，RepeatBattle类用于处理从文件重放某次战斗。RepeatBattle稍显复杂因为涉及到图形界面，考虑到刷新的问题我将此功能单独实现到一个线程中。当用户输入了合法的路径之后启动线程，根据记录操作移动Field中的对象直到读到文件末尾。由于记录时用的时FileWriter，这里读出使用的是FileReader。

###图形框架

图形界面的使用参照了曹老师给的那个例子。最外部的Main类继承自JFrame类，在JFrame的框架中添加组件，也就是继承自JPanel的Field。该组件内置键盘中断的监视器，用于响应键盘输入。
Field类内部实现了类TAdapter（继承自KeyAdapter），采用适配器模式，重写了keyPressed方法用于响应输入。当用户按下空格，Field中所有生物对象被加入线程池，战斗开始。当用户按下L键，弹出文件选择窗口，选择文件进行重放，文件路径被传给RepeatBattle对象并将该对象加入线程池执行重放。

#面向对象的思想运用
+ 场地类Field与生物Huluwa,Monster的聚合：场地类中持有所有在场生物的引用，每一个生物类也持有场地对象的引用。两者存在着部分整体的依赖关系，以聚合的方式互相关联，符合合成/聚合服用原则。
+ SRP单一职责原则：在编码时我尽量遵循单一职责原则，每一个类都有特定且专一的功能，力图实现高内聚和低耦合。如文件记录和重放功能并没有直接在场地类中以新增方法的方式实现，而是用两个新类专门处理。
+ 开放封闭原则：Huluwa和Monster均继承自Thing2D，在Thing2D中规定了某些统一的方法并采用final关键字修饰防止修改，并定义了抽象接口名用于子类实现，即对修改封闭，对拓展开放。
+ 里氏替换法则：继承自Thing2D的Huluwa和Monster能在任何地方完全替换Thing2D，并执行Thing2D的所有方法。Field的二维数组（用于存储在场所有生物体）引用类型就是Thing2D，实际对象既有Huluwa也有Monster。


#引用资料和鸣谢

完成大作业过程中参考了CSDN上许多有关java线程，图形界面的博客，在此不一一赘述。曹老师给出的有关maven打包方法教程十分有用，减去了我不少麻烦。在此感谢曹老师余老师一个学期的教导，老师辛苦了，新年快乐！