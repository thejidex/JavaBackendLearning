# Java IO流

## 字节流

### 字节输出流(OutputStream)

```
close()		关闭输出流
flush()		刷新输出流并强制缓冲区的直接写入目的地
write(byte[] b)	将所有字节写入
write(byte[] b, int off, int len)	从指定位置开始写入制定长度的字节
```

#### FileOutputStream

* 创建：

  ```
  1.使用文件名创建
  String fileName = "example.txt";
  FileOutputStream fos = new FileOutputStream(fileName);
  
  2.使用File对象创建
  File file = new File("example.txt");
  FileOutputStream fos = new FileOutputStream(file);
  ```

* 写入数据

  ```
  write(int b)
  write(byte[] b)
  write(byte[] b,int off,int len)  //从`off`索引开始，`len`个字节
  ```

* 数据追加、换行

  ```
  1.使用文件名
  String fileName = "example.txt";
  boolean append = true;
  FileOutputStream fos = new FileOutputStream(fileName, append);
  
  2.使用File对象
  File file = new File("example.txt");
  boolean append = true;
  FileOutputStream fos = new FileOutputStream(file, append);
  ```

### 字节输入流(InputStream)

#### FileInputStream

* 创建

  ```
  和FileOutputStream一样
  ```

* 读出数据

  ```
  1.读取字节
  // 创建一个 FileInputStream 对象
  FileInputStream fis = new FileInputStream("test.txt");
  // 读取文件内容
  int data;
  while ((data = fis.read()) != -1) {
      System.out.print((char) data);
  }
  // 关闭输入流
  fis.close();
  
  
  2.使用字节数组读取
  // 创建一个 FileInputStream 对象
  FileInputStream fis = new FileInputStream("test.txt");
  // 读取文件内容到缓冲区
  byte[] buffer = new byte[1024];
  int count;
  while ((count = fis.read(buffer)) != -1) {
      System.out.println(new String(buffer, 0, count));
  }
  // 关闭输入流
  fis.close();
  ```

## 字符流

### 字符输入流(Reader)

```
close()	
read() 		// 从输入流读取一个字符并返回
read(char[] buf)	// 从输入流读取一些字符，并将它们存储到buf中
```

#### FileReader

* 创建

  ```
  // 使用File对象创建流对象
  File file = new File("a.txt");
  FileReader fr = new FileReader(file);
  
  // 使用文件名称创建流对象
  FileReader fr = new FileReader("b.txt");
  ```

* 读取

  ```
  1.读取字符：read()每次读取一个字符，返回读取的字符（转为int类型），当读取到文件末尾时，返回-1
  // 使用文件名称创建流对象
  FileReader fr = new FileReader("abc.txt");
  // 定义变量，保存数据
  int b;
  // 循环读取
  while ((b = fr.read())!=-1) {
      System.out.println((char)b);
  }
  // 关闭资源
  fr.close();
  
  2.读取指定长度的字符
  File textFile = new File("docs/约定.md");
  // 给一个 FileReader 的示例
  // try-with-resources FileReader
  try(FileReader reader = new FileReader(textFile);) {
      // read(char[] cbuf)
      char[] buffer = new char[1024];
      int len;
      while ((len = reader.read(buffer, 0, buffer.length)) != -1) {
          System.out.print(new String(buffer, 0, len));
      }
  }
  ```

### 字符输出流(Writer)

```
write(int c)	写入单个字符
write(char[] buf)	写入字符数组
write(char[] buf, int off, int len)		写入指定长度的字符数组
write(String str)	写入字符串
write(String str, int off, int len)		写入字符串的一部分
flush()		刷新
close()		关闭，关闭前会自动刷新
```

#### FileWrite

* 创建

  ```
  // 第一种：使用File对象创建流对象
  File file = new File("a.txt");
  FileWriter fw = new FileWriter(file);
  
  // 第二种：使用文件名称创建流对象
  FileWriter fw = new FileWriter("b.txt");
  ```

* 写入数据

  ```
  String str = "jide真的帅啊！";
  try (FileWriter fw = new FileWriter("output.txt")) {
      fw.write(str, 0, 5); // 将字符串的前 5 个字符写入文件
  } catch (IOException e) {
      e.printStackTrace();
  }
  ```

## 缓冲流

### 字节缓冲流

* 构造

  ```
  // 创建字节缓冲输入流，先声明字节流
  FileInputStream fps = new FileInputStream(b.txt);
  BufferedInputStream bis = new BufferedInputStream(fps)
  
  // 创建字节缓冲输入流（一步到位）
  BufferedInputStream bis = new BufferedInputStream(new FileInputStream("b.txt"));
  
  // 创建字节缓冲输出流（一步到位）
  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("b.txt"));
  ```

* 使用

  ```
  使用方法基本一致：
  
  // 记录开始时间
  long start = System.currentTimeMillis();
  // 创建流对象
  try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("py.mp4"));
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("copyPy.mp4"));){
      // 读写数据
      int b;
      while ((b = bis.read()) != -1) {
          bos.write(b);
      }
  }
  // 记录结束时间
  long end = System.currentTimeMillis();
  System.out.println("缓冲流复制时间:"+(end - start)+" 毫秒");
  ```

### 字符缓冲流

* 构造

  ```
  // 创建字符缓冲输入流
  BufferedReader br = new BufferedReader(new FileReader("b.txt"));
  // 创建字符缓冲输出流
  BufferedWriter bw = new BufferedWriter(new FileWriter("b.txt"));
  ```

* 使用

  ```
  基本一致，这里只写特有的方法：
  
  1.每次读取一行
  // 创建流对象
  BufferedReader br = new BufferedReader(new FileReader("a.txt"));
  // 定义字符串,保存读取的一行文字
  String line  = null;
  // 循环读取,读取到最后返回null
  while ((line = br.readLine())!=null) {
      System.out.print(line);
      System.out.println("------");
  }
  // 释放资源
  br.close();
  
  2.写出换行
  // 创建流对象
  BufferedWriter bw = new BufferedWriter(new FileWriter("b.txt"));
  // 写出数据
  bw.write("沉");
  // 写出换行
  bw.newLine();
  bw.write("默");
  bw.newLine();
  bw.write("王");
  bw.newLine();
  bw.write("二");
  bw.newLine();
  // 释放资源
  bw.close();
  ```

## 转换流

字节流和字符流之间的转换

```
InputStreamReader isr = new InputStreamReader(new FileInputStream("in.txt"));
InputStreamReader isr2 = new InputStreamReader(new FileInputStream("in.txt") , "GBK");

OutputStreamWriter isr = new OutputStreamWriter(new FileOutputStream("a.txt"));
OutputStreamWriter isr2 = new OutputStreamWriter(new FileOutputStream("b.txt") , "GBK");


try {
    // 从文件读取字节流，使用UTF-8编码方式
    FileInputStream fis = new FileInputStream("test.txt");
    // 将字节流转换为字符流，使用UTF-8编码方式
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
    // 使用缓冲流包装字符流，提高读取效率
    BufferedReader br = new BufferedReader(isr);
    // 创建输出流，使用UTF-8编码方式
    FileOutputStream fos = new FileOutputStream("output.txt");
    // 将输出流包装为转换流，使用UTF-8编码方式
    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
    // 使用缓冲流包装转换流，提高写入效率
    BufferedWriter bw = new BufferedWriter(osw);

    // 读取输入文件的每一行，写入到输出文件中
    String line;
    while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.newLine(); // 每行结束后写入一个换行符
    }

    // 关闭流
    br.close();
    bw.close();
} catch (IOException e) {
    e.printStackTrace();
}
```



## 序列

* 输出流

  ```
  Person x = new Person("jack", 20);
          try (ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream("c.txt"))) {
              ois.writeObject(x);
          } catch (IOException e) {
              e.printStackTrace();
          }
  ```

* 输入流

  ```
  try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("c.txt"))) {
              Person t = (Person) ois.readObject();
              System.out.println("=====");
              System.out.println(t);
              System.out.println("=====");
          } catch (IOException | ClassNotFoundException e) {
              e.printStackTrace();
          }
  ```

## 打印流

```
PrintWriter pw = new PrintWriter("output.txt");
pw.println("沉默王二");
pw.printf("他的年纪为 %d.\n", 18);
pw.close();

// 这是真牛逼，像打印到控制台一样
```

