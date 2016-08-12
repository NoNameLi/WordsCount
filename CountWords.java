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



/*ͳ��һ��Ӣ���ļ��������СΪ��4G����ÿ�����ʳ��ֵĴ��������ѽ������Ӣ�����������һ���ļ��У�������£�
apple 100
......
zoo 4
ע�⣺�����ļ����󣬲��ظ������������ޣ���Ҫ���ǵ�ִ���ٶȺ��ڴ�ʹ����������ṩ�����еĳ��� README ʹ���ĵ���*/



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
				//�ж��ļ��Ƿ����ȫ������������
				if(raf.getFilePointer() + 3*1024 < end){
					raf.read(buff);
					
				}else{
					int length = (int) (end - raf.getFilePointer());
					raf.read(buff,(int) raf.getFilePointer(),length);
					flag =false;
				}
				String str = new String(buff);
				//ȥ���������
				str = str.trim().replace(",", "").replace(".", "").replace(";", "");
				//�ԡ� �� �ֶ�
				String[] arr = str.split(" ");
				//����HashMap��
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
	
	//�õ��̴߳�����
	public HashMap<String, Integer> getMap(){
		return hm;
	}
	
	
}





public class CountWords {
	
	
	public final static void main(String[] args){
		
		File file = new File("test.txt");

		//WordCount ʵ�� Runnable
		WordCount wc1 = new WordCount(file, 0, file.length()/2);
		WordCount wc2 = new WordCount(file,file.length()/2,file.length());
		
		Thread t1 = new Thread(wc1);
		Thread t2 = new Thread(wc2);
		//�����߳�
		t1.start();
		t2.start();
		while(true){
			//�ж��߳̽���
			if(Thread.State.TERMINATED == t1.getState() &&Thread.State.TERMINATED == t2.getState()){
				
				Map<String,Integer> map1 = wc1.getMap();
				Map<String,Integer> map2 = wc2.getMap();
				//����������TreeMap��
				TreeMap<String, Integer> tMap = new TreeMap<String, Integer>();
				tMap.putAll(map1);
				tMap.putAll(map2);
 				//���浽�ļ���
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
