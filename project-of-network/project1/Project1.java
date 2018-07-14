    import javax.sound.sampled.*;
    import java.io.*;

    public class Project1 {

            File outputFile = new File("recoder.wav");

            TargetDataLine targetDataLine = null;
            AudioInputStream audioInputStream = null;
            static String inputfile = "INPUT.txt";
            static String file = "recoder.wav";
            SourceDataLine sourceDataLine = null;
            AudioFormat inaudioFormat = null;
            //this format is for part 1(not confirmed
            AudioFormat audioFormat = new AudioFormat(
                    44100.0F, 16, 1, true,
                    false);

            void recordstart( ) {
                try {
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                            audioFormat);
                    targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
                    targetDataLine.open(audioFormat);
                    targetDataLine.start();
                    //this is beginning record
                    System.out.println("record begin");
                    AudioInputStream ain = new AudioInputStream(targetDataLine);
                    //this is write the file
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

            void inaudio(String filename){
                try {
                    //read audio from filename
                    audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
                    //get its format
                    inaudioFormat = audioInputStream.getFormat();
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
                for (int i =0;i<50000;i++) {
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
                double angler=(2.0*Math.PI*1000*i)/(44100.0F/44000);
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

        public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException {

            int time = 10000;
            Project1 func = new Project1();
            String filename= "test.wav";
            byte[] input;
            //below is code for part 1 and 2

            //this is a new thread for record. sleep time is set from top of the project
//            func.recordstart();
//            Thread reco = new Thread( new Runnable() {
//                public void run() {
//                    try {
//                        //sleep time for record(but can be record when sleeping(i don't understand why
//                        Thread.sleep(time);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                    func.recorddone();
//                }
//            });
//            //begin the thread above
//            reco .start();
            //a new start of thread.this thread is for play a audio(pre prepared audio)
//            new Thread(){
//                public void run() {
//                    try {
//                        Project1 b = new Project1();
//                        b.inaudio(filename);
//                        b.playbegin();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } .start();

            //I don't know why, but this will run the function and the record can run correct
            //perhaps it can be set before the thread reco or not?
            //or not need?


            //this is part1  which is after record ,play the recorded file.
//            try {
//                Project1 a = new Project1();
//                a.inaudio(file);
//                a.playbegin();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //Project1 func = new Project1();
           // func.ndaudio();
            //below is a test for inputfile. they will get the inputfile array
//            input = readfile(inputfile);
//            System.out.print(input);
//            char[] inputch = new String(input).toCharArray();
//            System.out.print(inputch[0]);

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

