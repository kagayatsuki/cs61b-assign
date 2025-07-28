# markdown语法

#**标题**

##...多级标题

> 引用（>）用大于号表示

**有序列表**

1.

2.

**无序列表**（使用-）

- 1
- 2
- 3

**任务列表**

* [ ] -[ ] 1.没有完成
* [X] -[ x] 2.完成了


```cpp
class Solution {
public:
    void rotate(vector<int>& nums, int k) {
        k=k%nums.size();
       reverse(nums.begin(), nums.end());
        reverse(nums.begin(), nums.begin() + k);
        reverse(nums.begin() + k, nums.end());
      
    }
};
```

**代码块**

(```Java

内容

`` `)

**行内代码**``                             (`java`)

---

三个-表示横线

**链接[内容]                           ,(网址"注释")**

[我的GitHub仓库](https://github.com/kagayatsuki?tab=repositories)

[git仓库](https://github.com/kagayatsuki?tab=repositories "嘻嘻嘻")

图片在链接的基础上前面加了一个感叹号
![普瑞塞斯.jpg](普瑞塞斯.jpg)
       ^          ^
（图片名要保持一致）