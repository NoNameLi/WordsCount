# WordsCount

实现思路：
	大文件则使用多线程并发读取文件

	1.本例中使用两线程读取 一从开始 另一从中间读取 使用RandomAccessFile类实现
	2.在线程中设置缓存，读取一定长度的数据，并将数据进行处理剔除特殊字符 以“ ”将字符串
	  分段 ，保存在HashMap中
	3.两线程执行结束，将两个线程中的HashMap保存在TreeMap（保证有序）
	4.将TreeMap 中的数据保存在文件中

测试：
	test.txt 保存着测试数据
	result.txt 保存着统计结果
	CountWords.java 程序源码
	（三个文件存放在同一目录下）