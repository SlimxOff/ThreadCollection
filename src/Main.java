import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int TEXT_LENGTH = 100000;
    private static final int NUM_TEXTS = 10000;
    private static final int QUEUE_CAPACITY = 100;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        Thread textGeneratorThread = new Thread(Main::generateTexts);
        Thread counterAThread = new Thread(() -> countMaxChar('a', queueA));
        Thread counterBThread = new Thread(() -> countMaxChar('b', queueB));
        Thread counterCThread = new Thread(() -> countMaxChar('c', queueC));

        textGeneratorThread.start();
        counterAThread.start();
        counterBThread.start();
        counterCThread.start();

        try {
            textGeneratorThread.join();
            counterAThread.join();
            counterBThread.join();
            counterCThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void generateTexts() {
        Random random = new Random();
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < NUM_TEXTS; i++) {
            text.setLength(0); // Clear StringBuilder
            for (int j = 0; j < TEXT_LENGTH; j++) {
                text.append(random.nextInt(3) == 0 ? 'a' : random.nextInt(3) == 1 ? 'b' : 'c');
            }

            try {
                queueA.put(text.toString());
                queueB.put(text.toString());
                queueC.put(text.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void countMaxChar(char targetChar, BlockingQueue<String> queue) {
        int maxCount = 0;
        String maxText = "";

        try {
            while (true) {
                String text = queue.take();
                int count = countOccurrences(text, targetChar);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Maximum occurrences of '" + targetChar + "' found: " + maxCount);
            System.out.println("Text with maximum occurrences of '" + targetChar + "': " + maxText);
        }
    }

    private static int countOccurrences(String text, char targetChar) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == targetChar) {
                count++;
            }
        }
        return count;
    }
}
