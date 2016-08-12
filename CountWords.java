import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;



/*统计一个英文文件（假设大小为：4G）中每个单词出现的次数，并把结果按照英文排序输出到一个文件中，结果如下：
apple 100
......
zoo 4
注意：由于文件过大，不重复单词总数有限，需要考虑到执行速度和内存使用情况。（提供可运行的程序及 README 使用文档）*/



class WordCount implements Runnable{
	
	private RandomAccessFile raf;
	private byte[] buff = new byte[3*1024];
	private long start;
	private long end;
	private HashMap<String, Integer> hm;
	
	public WordCount(File file, long start, long end){
		this.start = start;
		this.end = end;
		hm = new HashMap<String, Integer>();
		try {
			raf = new RandomAccessFile(file, "r");
			raf.seek(start);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean flag = true;
		
		try {
			
			while(flag){
				//判断文件是否可以全部读到缓存中
				if(raf.getFilePointer() + 3*1024 < end){
					raf.read(buff);
					
				}else{
					int length = (int) (end - raf.getFilePointer());
					raf.read(buff,(int) raf.getFilePointer(),length);
					flag =false;
				}
				String str = new String(buff);
				//去除特殊符号
				str = str.trim().replace(",", "").replace(".", "").replace(";", "");
				//以“ ” 分段
				String[] arr = str.split(" ");
				//存入HashMap中
				for(int i = 0; i < arr.length; i++){
					if(arr[i] != null && arr[i].length() != 0){
						if(null == hm.get(arr[i])){
							hm.put(arr[i], 1);
						}else{
							hm.put(arr[i], hm.get(arr[i]) + 1);
						}
						
					}
					
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//得到线程处理结果
	public HashMap<String, Integer> getMap(){
		return hm;
	}
	
	
}





public class CountWords {
	
	
	public final static void main(String[] args){
		
		File file = new File("test.txt");

		//WordCount 实现 Runnable
		WordCount wc1 = new WordCount(file, 0, file.length()/2);
		WordCount wc2 = new WordCount(file,file.length()/2,file.length());
		
		Thread t1 = new Thread(wc1);
		Thread t2 = new Thread(wc2);
		//开启线程
		t1.start();
		t2.start();
		while(true){
			//判断线程结束
			if(Thread.State.TERMINATED == t1.getState() &&Thread.State.TERMINATED == t2.getState()){
				
				Map<String,Integer> map1 = wc1.getMap();
				Map<String,Integer> map2 = wc2.getMap();
				//将结果存放在TreeMap中
				TreeMap<String, Integer> tMap = new TreeMap<String, Integer>();
				tMap.putAll(map1);
				tMap.putAll(map2);
 				//保存到文件中
				mapToFile(tMap, new File("result.txt"));
				return ;
			}
		}
	}
	
	private static void mapToFile(TreeMap<String, Integer> tMap, File file) {
		
		try {
		
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			
			for(Entry<String,Integer> entry : tMap.entrySet()){
				
				String str = entry.getKey() + "\t" + entry.getValue() + "\r\n";
				bfw.write(str);
				bfw.flush();
			}
			bfw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
}
