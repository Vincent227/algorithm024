##  **学习笔记**

## 一、HashMap学习总结

### 1. HashMap基本概念

HashMap底层数据结构为：数组+链表+红黑树，散列算法分为散列再探测和拉链式，HashMap采用的是拉链式，并在jdk1.8后使用红黑树优化长度大于等于8的链表；

可以接受null键和null值，null键的hash值时0；

元素无序，可以序列化，线程不安全；

添加，查询的时间复杂度基本都是O(1);

存储元素时，根据键的hash值找到对应的桶。如果出现不同的对象计算出来的hash值相同，也就是hash冲突。为了解决这个问题，使用单链表存储相同hash值的HashMap，数组中存放的相当于头节点；

当单链表长度大于等于8就会转化为红黑树，当红黑树长度小于等于6，就会转化为单链表。

### 2. HashMap源码学习
#### 2.1、一些变量的含义
```java
//初始容量是16
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 
//最大容量
static final int MAXIMUM_CAPACITY = 1 << 30;
//负载因子默认值0.75
static final float DEFAULT_LOAD_FACTOR = 0.75f;
//链表长度大于等于8时，链表转化成红黑树
static final int TREEIFY_THRESHOLD = 8;

//红黑树长度小于等于6时，红黑树转化成链表
static final int UNTREEIFY_THRESHOLD = 6;

//容量最小64时才会转会成红黑树
static final int MIN_TREEIFY_CAPACITY = 64;

//用于fail-fast的，记录HashMap结构发生变化(数量变化或rehash)的数目
transient int modCount;

//HashMap 的实际大小，不一定准确(因为当你拿到这个值的时候，可能又发生了变化)
transient int size;

// 阈值，当前HashMap所能容纳键值对数量的最大值，超过这个值，就需要扩容
// 计算方式：
// 阈值大小 = 数组容量 * 负载因子
int threshold;

//存放数据的数组
transient Node<K,V>[] table;
//链表的节点
static class Node<K,V> implements Map.Entry<K,V> {}
//红黑树的节点
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V>{} 
```
实现 Map 接口，允许 null 值，不同于 HashTable，是线程不安全的；

get() 和 put() 方法的实现达到了常数的时间复杂度，不过要达到好的迭代效果，initialCapacity 不要太高，loadFactor 不要太低；

loadFactor 默认值 0.75，是均衡了时间和空间损耗算出来的值，较高的值会减少空间开销(扩容减少)，但增加了查找成本(hash冲突增加，链表长度变长），不扩容的条件：数组容量> 需要的数组大小 / loadFactor；

如果有很多数据需要储存到 HashMap 中，建议 HashMap 的容量一开始就设置成足够的大小，这样可以防止在put过程中不断的扩容，影响性能；

HashMap 是非线程安全的，为了解决多线程安全问题，可以自己在外部加锁，或者通过Collections#synchronizedMap（在每个方法上加上了 synchronized 锁）来实现，`Map m = Collections.synchronizedMap(new HashMap(...))`;

在迭代过程中，如果 HashMap 的结构被修改，会快速失败。
#### 2.2、构造方法学习
```java
// 构造方法一：指定初始容量和负载因子
public HashMap(int initialCapacity, float loadFactor) {
  if (initialCapacity < 0)
    throw new IllegalArgumentException("Illegal initial capacity: " +
                                       initialCapacity);
  if (initialCapacity > MAXIMUM_CAPACITY)
    initialCapacity = MAXIMUM_CAPACITY;
  if (loadFactor <= 0 || Float.isNaN(loadFactor))
    throw new IllegalArgumentException("Illegal load factor: " +
                                       loadFactor);
  this.loadFactor = loadFactor;
  this.threshold = tableSizeFor(initialCapacity);
}

// 构造方法二：指定初始容量，此时负载因子使用默认值0.75
public HashMap(int initialCapacity) {
  this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
// 构造方法三：无参构造函数，初始容量是16，负载因子0.75，常用
public HashMap() {
  this.loadFactor = DEFAULT_LOAD_FACTOR; 
}

// 构造方法四：拷贝传入的Map，并且负载因子是默认值0.75
public HashMap(Map<? extends K, ? extends V> m) {
  this.loadFactor = DEFAULT_LOAD_FACTOR;
  putMapEntries(m, false);
}
```
HashMap的构造方法一共有4个，主要是对initialCapacity，loadFactor和threshold进行初始化，并没有涉及到数据结构的初始化，这部分是延迟到在插入数据时候才会进行。

当我们对时间和空间复杂度有要求的时候，可能就会采用第一种传入初始容量和负载因子的构造方式，这两个参数可以计算得出阈值.
#### 2.3、负载因子、阈值学习
```java
/**
     * Returns a power of two size for the given target capacity.
     */
static final int tableSizeFor(int cap) {
  int n = cap - 1;
  将n无符号右移1位，并将结果与右移前的n做按位或操作，结果赋给n；
  n |= n >>> 1;
  n |= n >>> 2;
  n |= n >>> 4;
  n |= n >>> 8;
  n |= n >>> 16;
  中间过程的目的就是使n的二进制数的低位全部变为1，比如10，11变为11，100，101，110，111变为111；
  return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```
默认情况下，初始容量是16，负载因子是0.75，而没有默认的阈值，这是因为数组的初始化是延迟放在了put方法中进行，而阈值也是在扩容的时候根据公式阈值大小 = 数组容量 * 负载因子来计算。

**负载因子loadFactor：**

loadFactor反应了HashMap桶数组的使用情况， 当我们调低负载因子时，HashMap 所能容纳的键值对数量变少。扩容时，重新将键值对存储新的桶数组里，键的键之间产生的碰撞会下降，链表长度变短。此时，HashMap 的增删改查等操作的效率将会变高，这里是典型的拿空间换时间。相反，如果增加负载因子（负载因子可以大于1），HashMap 所能容纳的键值对数量变多，空间利用率高，但碰撞率也高。这意味着链表长度变长，效率也随之降低，这种情况是拿时间换空间。

### 3. 新增
HashMap的put步骤，首先计算key的hash值，然后定位到这个hash值属于数组的哪个桶，然后判断桶是否为空，如果为空就将键值对存入即可。如果不为空，就根据那个桶的类型，决定是链表添加数据还是红黑树添加数据，最后还要根据key判断是否覆盖。在整个过程中还要时刻判断是否需要扩容，如果需要就要进行扩容操作。
#### 3.1、新增源码学习
```java
public V put(K key, V value) {
  // put方法实际上调用了putVal方法
  return putVal(hash(key), key, value, false, true);
}
// putVal方法
// 参数：
// hash ：通过hash算法计算出来的值
// onlyIfAbsent：默认为false，false表示如果key存在就用新值覆盖
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
  //n 表示数组的长度，i 为数组索引下标，p 为 i 下标位置的 Node 值
  Node<K,V>[] tab; Node<K,V> p; int n, i;
  //如果数组为空，调用resize初始化（数组被延长到插入新数据时再进行初始化）
  if ((tab = table) == null || (n = tab.length) == 0)
    n = (tab = resize()).length;
  // 如果当前索引位置是空的，则将新键值对节点的引用存入这个位置
  if ((p = tab[i = (n - 1) & hash]) == null)
    tab[i] = newNode(hash, key, value, null);
  // 如果当前索引上有值，则表示hash冲突，
  else {
    //e是当前节点的临时变量
    Node<K,V> e; K k;
    // 如果key的hash和值都相等，直接把当前下标位置的 Node 值赋值给临时变量
    if (p.hash == hash &&
        ((k = p.key) == key || (key != null && key.equals(k))))
      e = p;
    // 如果是红黑树，使用红黑树的方式新增
    else if (p instanceof TreeNode)
      e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
    // 如果是链表，进入下面逻辑
    else {
      //对链表进行遍历，并统计链表长度
      for (int binCount = 0; ; ++binCount) {
        // e = p.next 表示从头开始，遍历链表
        // p.next == null 表明 p 是链表的尾节点
        //链表不包含要插入的键值对节点时，则将该节点接入到链表的最后
        if ((e = p.next) == null) {
          //e和p.next都是持有对null的引用,即使p.next后来赋予了值只是改变了p.next指向的引用，和e没有关系
          p.next = newNode(hash, key, value, null);
          //新增时，当链表的长度大于等于树化阈值（8）时，调用treeifyBin进行树化
          if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
            treeifyBin(tab, hash);
          break;
        }
        //遍历过程中，发现链表中有元素和新增的元素相等，结束循环
        if (e.hash == hash &&
            ((k = e.key) == key || (key != null && key.equals(k))))
          break;
        //更改循环的当前元素，使p再遍历过程中，一直往后移动
        p = e;
      }
    }
    //判断要插入的键值对是否存在HashMap中
    if (e != null) {
      V oldValue = e.value;
      //onlyIfAbsent 表示是否仅在oldValue为null的情况下更新键值对的值
      if (!onlyIfAbsent || oldValue == null)
        e.value = value;
      // 当前节点移动到队尾
      afterNodeAccess(e);
      // 返回老值
      return oldValue;
    }
  }
  // 为了fail-fast，记录HashMap的数据结构变化
  ++modCount;
  //键值对数量大于阈值时，开始扩容
  if (++size > threshold)
    resize();
  // 删除不经常使用的元素
  afterNodeInsertion(evict);
  return null;
}

```
#### 3.2、扩容源码学习
数组的长度均时2的幂；

阈值大小 = 容量（数组长度） * 负载因子；

当HashMap中的键值对数量超过阈值时进行扩容；

扩容后，数组和阈值变为原来的2倍（如果计算过程中，阈值溢出归零，则按阈值公式重新计算）；

扩容之后，要重新计算键值对的位置，然后把它们移动到合适的位置上。
```java
final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
  			// 如果数组不为空（已经初始化过）
        if (oldCap > 0) {
            //老数组大小大于等于容量最大值，不扩容
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            //判断老数组大小2倍是否在最小值和最大值之间，如果是就可以扩容
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
          // 之前指定初始容量的构造函数使用threshold暂时保存initialCapacity 参数的值
          // 初始化时，将 threshold 的值赋值给 newCap
            newCap = oldThr;
        else {
            // zero initial threshold signifies using defaults
            //这部分为无参构造方法，数组容量为默认值
            newCap = DEFAULT_INITIAL_CAPACITY;
          	//计算阈值
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
  			// newThr 为 0 时，按阈值计算公式进行计算
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
  			// 创建新的数组，数组的初始化也是在这里完成的
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
          	// 如果旧的数组不为空，则遍历数组，并将键值对映射到新的数组中
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    //节点只有一个值，直接计算索引位置赋值
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    //红黑树
                    else if (e instanceof TreeNode)
                      	// 重新映射时，需要对红黑树进行拆分
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    //规避了8版本以下的成环问题
                    else { // preserve order
                        // loHead 表示老值,老值的意思是扩容后，该链表中计算出索引位置不变的元素
                        // hiHead 表示新值，新值的意思是扩容后，计算出索引位置发生变化的元素
                        // 举个例子，数组大小是 8 ，在数组索引位置是 1 的地方挂着两个值，两个值的 hashcode 是9和33。
                        // 当数组发生扩容时，新数组的大小是 16，此时 hashcode 是 33 的值计算出来的数组索引位置仍然是 1，我们称为老值
                        // hashcode 是 9 的值计算出来的数组索引位置是 9，就发生了变化，我们称为新值。
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        // java 7 是在 while 循环里面，单个计算好数组索引位置后，单个的插入数组中，在多线程情况下，会有成环问题
                        // java 8 是等链表整个 while 循环结束后，才给数组赋值，所以多线程情况下，也不会成环
                      // 遍历链表，并将链表节点按原顺序进行分组
                        do {
                            next = e.next;
                            // (e.hash & oldCap) == 0 表示老值链表
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            // (e.hash & oldCap) == 0 表示新值链表
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // 将分组后的链表映射到新桶中
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // 新值链表赋值到新的数组索引位置
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```
计算新数组的容量 newCap 和新阈值 newThr；

根据计算出的 newCap 创建新的数组，数组 table 也是延迟在这里进行初始化的；

将键值对节点重新映射到新的数组里。如果节点是 TreeNode 类型，则需要拆分红黑树。如果是普通节点，则节点按原顺序进行分组。

*后续待补充--*