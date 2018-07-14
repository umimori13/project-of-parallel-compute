    import javax.sound.sampled.*;
    import java.io.*;
    import java.net.*;
    public class Project1 {

        private static boolean swi=false;
        File outputFile = new File("recoder.wav");

            TargetDataLine targetDataLine = null;
            TargetDataLine pkgtargetDataLine = null;
            SourceDataLine pkgsourceDataLine = null;
            AudioInputStream audioInputStream = null;
            static String inputfile = "INPUT.txt";
            static String file = "recoder.wav";
            SourceDataLine sourceDataLine = null;
            AudioFormat inaudioFormat = null;

            //this format is for part 1(not confirmed
            AudioFormat audioFormat = new AudioFormat(
                    44100.0F, 8, 1, true,
                    false);


        //below is the code from internet to know how to get the data from input

//        static class Capture implements Runnable {
//
//            TargetDataLine line;
//            Thread thread;
//            Socket s;
//            BufferedOutputStream captrueOutputStream;
//
//            Capture(){//构造器 取得socket以获得网络输出流
//
//            }
//
//            public void start() {
//
//                thread = new Thread(this);
//                thread.setName("Capture");
//                thread.start();
//            }
//
//            public void stop() {
//                thread = null;
//            }
//
//            public void run() {
//
////                try {
////                    captrueOutputStream=new BufferedOutputStream(s.getOutputStream());//建立输出流 此处可以加套压缩流用来压缩数据
////                }
////                catch (IOException ex) {
////                    return;
////                }
//
//                AudioFormat format =new AudioFormat(8000,16,2,true,true);//AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian）
//                DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
//
//                try {
//                    line = (TargetDataLine) AudioSystem.getLine(info);
//                    line.open(format, line.getBufferSize());
//                } catch (Exception ex) {
//                    return;
//                }
//
//                byte[] data = new byte[1024];//此处的1024可以情况进行调整，应跟下面的1024应保持一致
//                int numBytesRead=0;
//                line.start();
//
//                while (thread != null) {
//                    numBytesRead = line.read(data, 0,1024);//取数据（1024）的大小直接关系到传输的速度，一般越小越快，
//                    System.out.println(new String(data));
////                    try {
////                        captrueOutputStream.write(data, 0, numBytesRead);//写入网络流
////                    }
////                    catch (Exception ex) {
////                        break;
////                    }
//                }
//
//                line.stop();
//                line.close();
//                line = null;
//
////                try {
//////                    captrueOutputStream.flush();
//////                    captrueOutputStream.close();
////                } catch (IOException ex) {
////                    ex.printStackTrace();
////                }
//            }
//        }
            void recordstart( ) {
                try {
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                            audioFormat);
                    targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                    targetDataLine.open(audioFormat);
                    targetDataLine.start();
//                    byte[] data = new byte[1024];//此处的1024可以情况进行调整，应跟下面的1024应保持一致
//                    int numBytesRead=0;
//                    numBytesRead = targetDataLine.read(data, 0,1024);
                    //this is beginning record
                    System.out.println("record begin");



                    //this is write the file
//                    while (swi == false) {
//                            numBytesRead = targetDataLine.read(data, 0, 1024);
//                            System.out.println(byteArrayToDouble(data));
//                    }
                    AudioInputStream ain = new AudioInputStream(targetDataLine);
                    AudioSystem.write(ain, AudioFileFormat.Type.WAVE,outputFile);

                }catch (LineUnavailableException e){
                    e.printStackTrace();
                }catch (IOException ea) {
                    ea.printStackTrace();
                }
            }

            void recorddone(){
                //record over and make the record stop
                targetDataLine.stop();
                targetDataLine.close();
                System.out.println("record over");
            }


            public static double byteArrayToDouble(byte[] b) {
                long value = 0;
                for (int i = 0; i < 8; i++) {
                    value |= ((long) (b[i] & 0xff)) << (8 * i);
                }
                return Double.longBitsToDouble(value);
            }
        void inaudio(String filename){
                try {
                    //read audio from filename
                    audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
                    //get its format
                    inaudioFormat = audioInputStream.getFormat();
                    InputStream fileInput = new FileInputStream(filename);
                    BufferedInputStream bis = new BufferedInputStream(fileInput);
//                    data = new int[];
                    byte[] B= new byte[1024];
                    int readLength = 0;
                    int len=(int)audioInputStream.getFrameLength();
                    int[] data=new int[len];
                    String str;
//                    while((readLength = fileInput.read(B)) != -1){
//                        str=new String(B);
//                        char[] ch=str.toCharArray();
//                        long[] toInt = new long[ch.length];
//                        for(int i=0;i<ch.length;i++){
//                            toInt[i]=(long)ch[i];
                    for (int i =0;i<len;i++){
                        data[i]=bis.read();
                        System.out.println(data[i]);
                    }

//                            System.out.println(byteArrayToDouble(B));
//                        }

//                    }


                    System.out.println(" play begin");
                    System.out.println("frame per second："+inaudioFormat.getSampleRate());
                    System.out.println("total frame："+audioInputStream.getFrameLength());
                    System.out.println("total time(s)："+audioInputStream.getFrameLength()/inaudioFormat.getSampleRate());
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }

            }
            void playbegin() throws IOException {
                byte[] b = new byte[1024];
                int len = 0;
                try {
                    sourceDataLine.open(inaudioFormat, 1024);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                //this is actully play the audio.inaudio() is read the auido. The sourceDataline is the
                //auido data
                sourceDataLine.start();
                while ((len = audioInputStream.read(b)) > 0) {
                    sourceDataLine.write(b, 0, len);
                }
                audioInputStream.close();
                sourceDataLine.drain();
                sourceDataLine.close();
            }

            void ndaudio() throws LineUnavailableException {
                //this is part 2.play a set  sin audio
                //the format of the audio(not confirmed
                AudioFormat testfor=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F,
                        8, 1, 1, 44100.0F, false);
                SourceDataLine sdataline=AudioSystem.getSourceDataLine(testfor);
                sdataline.open(testfor);
                sdataline.start();
                byte[]buf=new byte[1];
                for (int i =0;i<500000;i++) {
                    //44100.0F/44000 is sample_Rate/frequency(not confirmed
                    double angle=(2.0*Math.PI*1000*i)/(44100.0F/44000);
                    buf[0]=(byte)(Math.sin(angle)+Math.sin(angle*10));
                    //the audio can be write and read. Means can be play when write
                    //the time of this audio is i'length.The sin is sin(2Pi*1000t)
                    sdataline.write(buf,0,1);
                }
            }

        static byte[] readfile(String fileName) {
            //this function is to read the input. It can read the things from INPUT.txt
            //byte is a buffer but not a array of string. the array of string is char in modulation()
            File file = new File(fileName);
            int filelength = Math.toIntExact(file.length());
            byte[] filecontent = new byte[filelength];
            try {
                FileInputStream infilestream = new FileInputStream(file);
                infilestream.read(filecontent);
                infilestream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return filecontent;
        }

        void modulation(String input) throws LineUnavailableException {
            //the audio format(not confirmed. If all of them are the same, they can be collected into a variable
            AudioFormat testfor2=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F,
                    8, 1, 1, 44100.0F, false);
            SourceDataLine sdataline=AudioSystem.getSourceDataLine(testfor2);
            sdataline.open(testfor2);
            sdataline.start();
            //inputch is the string array of input where will be like 01010101
            char[] inputch = input.toCharArray();
            byte[]buf=new byte[1];
            byte[]buf2=new byte[1];
            //the j is all the frame. Each frame is a 0 or 1 and they have a head(perhaps?
            for (int j =0;j<inputch.length;j++){
                double angler=(2.0*Math.PI*1000)/(44100.0F/44000);
                //perhaps be some error? this is the head of a frame data
                //the read and play is similar to the way upon
                buf2[0]=(byte)(Math.sin(angler));
                sdataline.write(buf2,0,1);
                for (int i =0;i<50000;i++) {
                    //sample_Rate/frequency
                    double angle=(2.0*Math.PI*1000*i)/(44100.0F/44000);
                    buf[0]=(byte)(Math.sin(angle+inputch[j]*Math.PI)+Math.sin(angle*10+inputch[j]*Math.PI));
                    sdataline.write(buf,0,1);
                }
            }

        }
//
//        void pkgrecordstart( ) {
//            try {
//                DataLine.Info info = new DataLine.Info(TargetDataLine.class,
//                        audioFormat);
//                pkgtargetDataLine = (TargetDataLine) AudioSystem.getLine(info);
//                pkgtargetDataLine.open(audioFormat);
//                pkgtargetDataLine.start();
//                //this is beginning record
//                System.out.println("record begin");
//                AudioInputStream ain = new AudioInputStream(pkgtargetDataLine);
//                //this is write the file
//
//                AudioSystem.write(ain, AudioFileFormat.Type.WAVE,outputFile);
//
//            }catch (LineUnavailableException e){
//                e.printStackTrace();
//            }catch (IOException ea) {
//                ea.printStackTrace();
//            }
//        }
//
//        void pkgrecorddone(){
//            //record over and make the record stop
//            pkgtargetDataLine.stop();
//            pkgtargetDataLine.close();
//            System.out.println("record over");
//        }
public static int getInt(byte[] arr, int index) {
    return  (0xff000000     & (arr[index+0] << 24))  |
            (0x00ff0000     & (arr[index+1] << 16))  |
            (0x0000ff00     & (arr[index+2] << 8))   |
            (0x000000ff     &  arr[index+3]);
}
        public static float getFloat(byte[] arr, int index) {
            return Float.intBitsToFloat(getInt(arr, index));
        }
        void packde(String filename){
            try {
                //read audio from filename
                audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
                //get its format
                inaudioFormat = audioInputStream.getFormat();
                InputStream fileInput = new FileInputStream(filename);
                BufferedInputStream bis = new BufferedInputStream(fileInput);
//                    data = new int[];
                byte[] B= new byte[(int) audioInputStream.getFrameLength()*inaudioFormat.getFrameSize()];
                int readLength = 0;
                int len=(int)audioInputStream.getFrameLength()*inaudioFormat.getFrameSize();
                int[] data=new int[len];
                boolean detect=false;
                String str;
                //below is my try to change byte to char.ignore it
//                    while((readLength = fileInput.read(B)) != -1){
//                        str=new String(B);
//                        char[] ch=str.toCharArray();
//                        long[] toInt = new long[ch.length];
//                        for(int i=0;i<ch.length;i++){
//                            toInt[i]=(long)ch[i];
//                bis.read(B);
                audioInputStream.read(B);
                for (int i =0;i<len;i+=4){
//                    data[i]=bis.read(B);
//                    System.out.println(data[i]);
                    System.out.println(getFloat(B,i)+"  "+i/4);
                    //here will be detect but the data is not correct
                    if (detect){
                        //begin receive
                    }

                }

//                            System.out.println(byteArrayToDouble(B));

                System.out.println(" play begin");
                System.out.println("frame per second："+inaudioFormat.getSampleRate());
                System.out.println("total frame："+audioInputStream.getFrameLength());
                System.out.println("total frame："+inaudioFormat.getFrameSize());
                System.out.println("total time(s)："+audioInputStream.getFrameLength()/inaudioFormat.getSampleRate());
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {

            int time = 10500;
            Project1 func = new Project1();
            String filename= "test.wav";
//            Capture ab = new Capture();
//            Thread ab1=  new Thread(ab);
//            ab.start();
            byte[] input;

//            new Thread(){
//                public void run() {
//                    Project1 b = new Project1();
//                    b.packde(filename);
//                }
//            } .start();



            //this is a new thread for record. sleep time is set from top of the project
            Thread reco = new Thread( new Runnable() {
                public void run() {
                    try {
                        //sleep time for record(but can be record when sleeping(i don't understand why
                        Thread.sleep(time);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    swi=true;
                    func.recorddone();
                }
            });
            //begin the thread above
            reco .start();
            func.recordstart();

//            new Thread(){
//                public void run() {
//                    try {
//                        func.ndaudio();
//                    } catch (LineUnavailableException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } .start();

            //below is code for part 1 and 2
            //I don't know why, but this will run the function and the record can run correct
            //perhaps it can be set before the thread reco or not?
            //or not need?

/*
            //a new start of thread.this thread is for play a audio(pre prepared audio)
            new Thread(){
                public void run() {
                    try {
                        Project1 b = new Project1();
                        b.inaudio(filename);
                        b.playbegin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } .start();



            //this is part1  which is after record ,play the recorded file.
            try {
                Project1 a = new Project1();
                a.inaudio(file);
                a.playbegin();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            //Project1 func = new Project1();

            //below is a test for inputfile. they will get the inputfile array
            /*
            input = readfile(inputfile);
            System.out.print(input);
            char[] inputch = new String(input).toCharArray();
            System.out.print(inputch[0]);
            */
            //the below is my first try of writing code.it is useless for project
            //but perhaps can get some code information for myself
            /*
            File outputFile = new File("recoder.wav");
            AudioFormat audioFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F,
                    false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                    audioFormat);
            TargetDataLine targetDataLine = null;
            try {
                targetDataLine = (TargetDataLine) AudioSystem
                        .getLine(info);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            try {
                targetDataLine.open(audioFormat);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
            targetDataLine.start();
            TargetDataLine finalTargetDataLine = targetDataLine;
            new Thread() {
                public void run() {
                    AudioInputStream cin = new AudioInputStream(finalTargetDataLine);
                    try {
                        AudioSystem.write(cin, AudioFileFormat.Type.WAVE,
                                outputFile);
                        System.out.println("over");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
            }.start();
            System.out.println("begin");
            System.in.read();
            targetDataLine.close();
            */
            }
        }

